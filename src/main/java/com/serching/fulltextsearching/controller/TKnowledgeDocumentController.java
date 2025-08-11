package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.exception.BusinessException;
import com.serching.fulltextsearching.service.TKnowledgeDocumentService;
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
public class TKnowledgeDocumentController {

    @Autowired
    private TKnowledgeDocumentService tKnowledgeDocumentService;

    @PostMapping("/upload")
    public Result<TKnowledgeDocument> uploadDocument(@RequestParam("file") @NotNull(message = "文件不能为空") MultipartFile documentFile) {
        try {
            TKnowledgeDocument document = tKnowledgeDocumentService.uploadDocument(documentFile);
            return Result.success(document);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @PutMapping
    public Result<TKnowledgeDocument> updateDocument(@Valid @RequestBody TKnowledgeDocument tKnowledgeDocument) {
        if (tKnowledgeDocument.getId() == null) {
            throw new BusinessException("文档ID不能为空");
        }
        TKnowledgeDocument updatedDocument = tKnowledgeDocumentService.updateDocument(tKnowledgeDocument);
        return Result.success(updatedDocument);
    }

    @GetMapping("/{id}")
    public Result<TKnowledgeDocument> getDocument(@PathVariable Long id) {
        TKnowledgeDocument document = tKnowledgeDocumentService.getById(id);
        if (document == null) {
            throw new BusinessException(404, "文档不存在");
        }
        return Result.success(document);
    }

    @PostMapping
    public Result<TKnowledgeDocument> createDocument(@Valid @RequestBody TKnowledgeDocument tKnowledgeDocument) {
        log.info("创建文档: title={}", tKnowledgeDocument.getTitle());
        TKnowledgeDocument createdDocument = tKnowledgeDocumentService.createDocument(tKnowledgeDocument);
        return Result.success(createdDocument);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDocument(@PathVariable Long id) {
        log.info("删除文档: id={}", id);
        boolean deleted = tKnowledgeDocumentService.deleteDocument(id);
        if (!deleted) {
            throw new BusinessException(404, "文档不存在或删除失败");
        }
        return Result.success();
    }

}