package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.entity.TKnowledgeBase;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.exception.BusinessException;
import com.serching.fulltextsearching.service.TKnowledgeDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/document")
@Validated
@Tag(name = "知识文档管理", description = "知识文档的增删改查和文件上传接口")
public class TKnowledgeDocumentController {

    @Autowired
    private TKnowledgeDocumentService tKnowledgeDocumentService;

    @PostMapping(value = "/upload",consumes = "multipart/form-data")
    @Operation(summary = "上传文件创建文档(绑定知识库/可选分组)", description = "file: 仅支持 .txt/.md; knowledgeBase: JSON; categoryId: 可选")
    public Result<TKnowledgeDocument> uploadDocument(
            @Parameter(description = "知识库实体(JSON)，至少包含 id", required = true)
            @RequestPart("knowledgeBase") @Valid TKnowledgeBase knowledgeBase,
            @Parameter(description = "知识库分组ID(可选)")
            @RequestPart(value = "categoryId", required = false) Long categoryId,
            @Parameter(description = "要上传的文件，仅支持 .txt/.md", required = true)
            @RequestPart("file") MultipartFile file
    )
    {
        try {
            TKnowledgeDocument document = tKnowledgeDocumentService.uploadDocument(knowledgeBase,categoryId,file);
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

}