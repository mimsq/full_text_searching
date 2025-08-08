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
    @PostMapping
    public TKnowledgeDocument saveDocument(@RequestBody TKnowledgeDocument tKnowledgeDocument){


        return tKnowledgeDocumentService.saveDocument(tKnowledgeDocument);
    }

    @GetMapping("/{id}")
    public TKnowledgeDocument getTKnowledgeDocument(@PathVariable Long id){
        return tKnowledgeDocumentService.getById(id);
    }


}
