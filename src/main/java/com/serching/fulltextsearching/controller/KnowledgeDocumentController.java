package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.common.PageResult;
import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.entity.KnowledgeBase;
import com.serching.fulltextsearching.entity.KnowledgeDocument;
import com.serching.fulltextsearching.exception.BusinessException;
import com.serching.fulltextsearching.service.OperationLogService;
import com.serching.fulltextsearching.service.KnowledgeDocumentService;
import com.serching.fulltextsearching.service.KnowledgeBaseService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/document")
@Validated
@Api(tags = "知识文档管理", description = "知识文档的增删改查和文件上传接口")
public class KnowledgeDocumentController {

    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Autowired
    private OperationLogService operationLogService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "上传文件创建文档(绑定知识库/可选分组)", description = "file: 仅支持 .txt/.md; knowledgeBaseId: 知识库ID; categoryId: 可选")
    public Result<KnowledgeDocument> uploadDocument(
            @Parameter(description = "知识库ID", required = true)
            @RequestParam("knowledgeId")
            @NotNull(message = "知识库ID不能为空")Long knowledgeId,

            @Parameter(description = "知识库分组ID(可选)")
            @RequestParam(value = "categoryId", required = false) Long categoryId,

            @Parameter(description = "要上传的文件，仅支持 .txt/.md", required = true)
            @RequestPart("file")
            @NotNull(message = "上传文件不能为空") MultipartFile file
    )
    {
        try {
             //先查询知识库信息
            KnowledgeBase knowledgeBase = knowledgeBaseService.getKnowledgeDetail(knowledgeId);
            if (knowledgeBase == null) {
                throw new BusinessException("知识库不存在: " + knowledgeId);
            }

            KnowledgeDocument document = knowledgeDocumentService.uploadDocument(knowledgeBase, categoryId, file);
            // 上传成功后，创建操作日志
            operationLogService.addOperationLog(1,document.getId(),knowledgeId,1L);
            return Result.success(document);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新文档", description = "根据ID更新知识文档信息（部分字段可为空表示不更新）")
    public Result<KnowledgeDocument> updateDocument(
            @Parameter(description = "文档ID", required = true)
            @PathVariable
            @NotNull(message = "文档ID不能为空") Long id,

            @Parameter(description = "文档信息", required = true)
            @Valid @RequestBody KnowledgeDocument knowledgeDocument) {
        if (id == null) {
            throw new BusinessException("文档ID不能为空");
        }
        // 若请求体未带ID则补齐；若带了不一致则报错
        if (knowledgeDocument.getId() == null) {
            knowledgeDocument.setId(id);

        } else if (!id.equals(knowledgeDocument.getId())) {
            throw new BusinessException(400, "路径ID与请求体ID不一致");
        }

        KnowledgeDocument updatedDocument = knowledgeDocumentService.updateDocument(knowledgeDocument);
        operationLogService.addOperationLog(2,id, knowledgeDocument.getKbId(),1L);
        if (updatedDocument == null) {
            throw new BusinessException(404, "文档不存在或未更新");
        }
        return Result.success(updatedDocument);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文档详情", description = "根据ID获取知识文档详细信息")
    public Result<KnowledgeDocument> getDocument(
            @Parameter(description = "文档ID", required = true, example = "1")
            @PathVariable
            @NotNull(message = "文档ID不能为空") Long id) {
        KnowledgeDocument document = knowledgeDocumentService.getById(id);
        if (document == null) {
            throw new BusinessException(404, "文档不存在");
        }
        // 记录预览操作日志（operationType=4）
        try {
            operationLogService.addOperationLog(4, id, document.getKbId(), 1L);
        } catch (Exception e) {
            log.warn("记录预览日志失败, docId={}", id, e);
        }
        return Result.success(document);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "删除文档", description = "根据ID删除知识文档，不移入回收站，要删除然后移入回收站中，请使用回收站的移入回收站接口")
    public Result<Void> deleteDocument(
            @Parameter(description = "文档ID", required = true, example = "1")
            @PathVariable
            @NotNull(message = "文档ID不能为空") Long id,
            @Parameter(description = "知识库ID", required = true)
            @RequestParam("knowledgeId")
            @NotNull(message = "知识库ID不能为空") Long knowledgeId) {
        log.info("删除文档: id={}", id);
        boolean deleted = knowledgeDocumentService.deleteDocument(id);
        operationLogService.addOperationLog(3,id,knowledgeId,1L);
        if (!deleted) {
            throw new BusinessException(404, "文档不存在或删除失败");
        }
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询文档(按知识库)", description = "仅查询MySQL，根据 knowledgeId 查询 t_knowledge_document.kb_id，并进行分页")
    public Result<PageResult<KnowledgeDocument>> pageDocuments(
            @Parameter(description = "知识库ID(对应 t_knowledge_base.id)", required = true)
            @RequestParam("knowledgeId") Long knowledgeId,
            @Parameter(description = "当前页(从1开始)")
            @RequestParam(value = "page", defaultValue = "1")
            @NotNull(message = "当前页不能为空") long current,
            @Parameter(description = "每页大小")
            @RequestParam(value = "size", defaultValue = "10")
            @NotNull(message = "每页大小不能为空") long size
    ) {
        if (knowledgeId == null) {
            throw new BusinessException(400, "知识库ID不能为空");
        }
        PageResult<KnowledgeDocument> page = knowledgeDocumentService.pageByKbId(knowledgeId, current, size);
        return Result.success(page);
    }


    /**
     * 全文检索接口，使用Elasticsearch实现高效的文档全文检索
     * 支持关键词分词匹配、高亮显示和分页查询
     *
     * @param keyword 检索关键词，支持中文分词和模糊匹配
     * @param page 页码，从1开始，默认值为1
     * @param size 每页条数，默认值为10
     * @return 包含分页文档列表的Result对象
     * @see KnowledgeDocumentService#search(String, int, int)
     */
    @GetMapping("/search")
    @Operation(summary = "全文检索")
    public Result<PageResult<KnowledgeDocument>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1")
            @NotNull(message = "当前页不能为空") int page,
            @RequestParam(defaultValue = "10")
            @NotNull(message = "每页大小不能为空") int size
    ) {
        return Result.success(knowledgeDocumentService.search(keyword, page, size));
    }

    @GetMapping("/recent-edited")
    @Operation(summary = "最近编辑文档", description = "基于操作日志聚合，按最近编辑时间倒序返回文档列表；必须传 knowledgeId")
    public Result<PageResult<KnowledgeDocument>> recentEdited(
            @Parameter(description = "知识库ID(必填)")
            @RequestParam(value = "knowledgeId") Long knowledgeId,
            @RequestParam(defaultValue = "1")
            @NotNull(message = "当前页不能为空") int page,
            @RequestParam(defaultValue = "10")
            @NotNull(message = "每页大小不能为空") int size
    ) {
        if (knowledgeId == null) {
            throw new BusinessException(400, "knowledgeId 不能为空");
        }
        return Result.success(knowledgeDocumentService.pageRecentEdited(knowledgeId, page, size));
    }

    @DeleteMapping("/recent-edited/{id}")
    @Operation(summary = "从最近编辑中移除", description = "全局从最近编辑中移除，不影响其他数据，不删除文档")
    public Result<Void> removeFromRecentEdited(
            @Parameter(description = "文档ID", required = true)
            @PathVariable("id") Long documentId,
            @Parameter(description = "知识库ID(必填)")
            @RequestParam(value = "knowledgeId") Long knowledgeId
    ) {
        if (documentId == null || knowledgeId == null) {
            throw new BusinessException(400, "id 与 knowledgeId 均不能为空");
        }
        operationLogService.hideFromRecentEdited(documentId, knowledgeId);
        return Result.success();
    }

    @GetMapping("/recent-viewed")
    @Operation(summary = "最近预览文档", description = "基于操作日志聚合，按最近预览时间倒序返回文档列表")
    public Result<PageResult<KnowledgeDocument>> recentViewed(
            @Parameter(description = "知识库ID(可选)")
            @RequestParam(value = "knowledgeId", required = false) Long knowledgeId,
            @Parameter(description = "用户ID(可选，未接入鉴权前从请求传入)")
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(defaultValue = "1")
            @NotNull(message = "当前页不能为空") int page,
            @RequestParam(defaultValue = "10")
            @NotNull(message = "每页大小不能为空") int size
    ) {
        return Result.success(knowledgeDocumentService.pageRecentViewed(knowledgeId, userId, page, size));
    }

}