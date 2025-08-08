package com.serching.fulltextsearching.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.serching.fulltextsearching.entity.ESKnowledgeDocument;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.repository.TKnowledgeDocumentRepository;
import com.serching.fulltextsearching.service.TKnowledgeDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/document")
public class TKnowledgeDocumentController {

    @Autowired
    private TKnowledgeDocumentService tKnowledgeDocumentService;

    @Autowired
    private TKnowledgeDocumentRepository tKnowledgeDocumentRepository;


    private static final Logger logger = LoggerFactory.getLogger(TKnowledgeDocumentController.class);


    @PostMapping("/upload")
    public TKnowledgeDocument uploadDocument(@RequestParam("file") MultipartFile documentFile){

        try {
            return tKnowledgeDocumentService.uploadDocument(documentFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @PutMapping
    public TKnowledgeDocument updateDocument(@RequestBody TKnowledgeDocument tKnowledgeDocument){


        return tKnowledgeDocumentService.updateDocument(tKnowledgeDocument);
    }


    @GetMapping("/{id}")
    public TKnowledgeDocument getDocument(@PathVariable Long id){
        return tKnowledgeDocumentService.getById(id);
    }

    @PostMapping
    public TKnowledgeDocument createDocument(@RequestBody TKnowledgeDocument tKnowledgeDocument){
        logger.info("创建文档: title={}", tKnowledgeDocument.getTitle());
        return tKnowledgeDocumentService.createDocument(tKnowledgeDocument);
    }

    @DeleteMapping("/{id}")
    public boolean deleteDocument(@PathVariable Long id){
        logger.info("删除文档: id={}", id);
        return tKnowledgeDocumentService.deleteDocument(id);
    }

}