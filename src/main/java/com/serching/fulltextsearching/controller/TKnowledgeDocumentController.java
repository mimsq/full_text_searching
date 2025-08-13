package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.common.PageResult;
import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.entity.TKnowledgeBase;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.exception.BusinessException;
import com.serching.fulltextsearching.service.TKnowledgeDocumentService;
import com.serching.fulltextsearching.service.KnowledgeBaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/document")
@Validated
@Tag(name = "知识文档管理", description = "知识文档的增删改查和文件上传接口")
public class TKnowledgeDocumentController {

    @Autowired
    private TKnowledgeDocumentService tKnowledgeDocumentService;
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(summary = "上传文件创建文档(绑定知识库/可选分组)", description = "file: 仅支持 .txt/.md; knowledgeBaseId: 知识库ID; categoryId: 可选")
    public Result<TKnowledgeDocument> uploadDocument(
            @Parameter(description = "知识库ID", required = true)
            @RequestParam("knowledgeId") Long knowledgeId,
            @Parameter(description = "知识库分组ID(可选)")
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @Parameter(description = "要上传的文件，仅支持 .txt/.md", required = true)
            @RequestPart("file") MultipartFile file
    )
    {
        try {
             //先查询知识库信息
            TKnowledgeBase knowledgeBase = knowledgeBaseService.getKnowledgeDetail(knowledgeId);
            if (knowledgeBase == null) {
                throw new BusinessException("知识库不存在: " + knowledgeId);
            }

            TKnowledgeDocument document = tKnowledgeDocumentService.uploadDocument(knowledgeBase, categoryId, file);
            return Result.success(document);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新文档", description = "根据ID更新知识文档信息（部分字段可为空表示不更新）")
    public Result<TKnowledgeDocument> updateDocument(
            @Parameter(description = "文档ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "文档信息", required = true)
            @Valid @RequestBody TKnowledgeDocument tKnowledgeDocument) {
        if (id == null) {
            throw new BusinessException("文档ID不能为空");
        }
        // 若请求体未带ID则补齐；若带了不一致则报错
        if (tKnowledgeDocument.getId() == null) {
            tKnowledgeDocument.setId(id);

        } else if (!id.equals(tKnowledgeDocument.getId())) {
            throw new BusinessException(400, "路径ID与请求体ID不一致");
        }

        TKnowledgeDocument updatedDocument = tKnowledgeDocumentService.updateDocument(tKnowledgeDocument);
        if (updatedDocument == null) {
            throw new BusinessException(404, "文档不存在或未更新");
        }
        return Result.success(updatedDocument);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文档详情", description = "根据ID获取知识文档详细信息")
    public Result<TKnowledgeDocument> getDocument(
            @Parameter(description = "文档ID", required = true, example = "1")
            @PathVariable Long id) {
        TKnowledgeDocument document = tKnowledgeDocumentService.getById(id);
        if (document == null) {
            throw new BusinessException(404, "文档不存在");
        }
        return Result.success(document);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "删除文档", description = "根据ID删除知识文档")
    public Result<Void> deleteDocument(
            @Parameter(description = "文档ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("删除文档: id={}", id);
        boolean deleted = tKnowledgeDocumentService.deleteDocument(id);
        if (!deleted) {
            throw new BusinessException(404, "文档不存在或删除失败");
        }
        return Result.success();
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询文档(按知识库)", description = "仅查询MySQL，根据 knowledgeId 查询 t_knowledge_document.kb_id，并进行分页")
    public Result<PageResult<TKnowledgeDocument>> pageDocuments(
            @Parameter(description = "知识库ID(对应 t_knowledge_base.id)", required = true)
            @RequestParam("knowledgeId") Long knowledgeId,
            @Parameter(description = "当前页(从1开始)")
            @RequestParam(value = "page", defaultValue = "1") long current,
            @Parameter(description = "每页大小")
            @RequestParam(value = "size", defaultValue = "10") long size
    ) {
        if (knowledgeId == null) {
            throw new BusinessException(400, "知识库ID不能为空");
        }
        PageResult<TKnowledgeDocument> page = tKnowledgeDocumentService.pageByKbId(knowledgeId, current, size);
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
     * @see com.serching.fulltextsearching.service.TKnowledgeDocumentService#search(String, int, int)
     */
    @GetMapping("/search")
    @Operation(summary = "全文检索")
    public Result<PageResult<TKnowledgeDocument>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Result.success(tKnowledgeDocumentService.search(keyword, page, size));
    }

}