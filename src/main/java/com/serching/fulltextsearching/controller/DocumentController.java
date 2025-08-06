package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.entity.DocumentEntity;
import com.serching.fulltextsearching.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    
    @Autowired
    private DocumentService documentService;
    
    /**
     * 上传并索引文档
     * @param file 上传的文件
     * @return 响应结果
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file) {
        try {
            DocumentEntity savedDocument = documentService.saveDocument(file);
            return ResponseEntity.ok(savedDocument);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("处理文件时发生错误: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("保存文档时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 搜索文档内容
     * @param keyword 搜索关键字
     * @return 匹配的文档列表
     */
    @GetMapping("/search")
    public ResponseEntity<List<DocumentEntity>> searchDocuments(@RequestParam("keyword") String keyword) {
        List<DocumentEntity> documents = documentService.searchDocuments(keyword);
        return ResponseEntity.ok(documents);
    }
    
    /**
     * 根据ID获取文档
     * @param id 文档ID
     * @return 文档信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentEntity> getDocument(@PathVariable String id) {
        DocumentEntity document = documentService.getDocumentById(id);
        if (document != null) {
            return ResponseEntity.ok(document);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 获取所有文档
     * @return 所有文档列表
     */
    @GetMapping
    public ResponseEntity<Iterable<DocumentEntity>> getAllDocuments() {
        Iterable<DocumentEntity> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }
}