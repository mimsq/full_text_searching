package com.serching.fulltextsearching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.mapper.TKnowledgeDocumentMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface TKnowledgeDocumentService extends IService<TKnowledgeDocument> {

    TKnowledgeDocument uploadDocument (MultipartFile file) throws IOException;

    TKnowledgeDocument updateDocument(TKnowledgeDocument tKnowledgeDocument);
}
