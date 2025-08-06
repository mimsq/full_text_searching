package com.serching.fulltextsearching.service.impl;

import com.serching.fulltextsearching.entity.DocumentEntity;
import com.serching.fulltextsearching.repository.DocumentRepository;
import com.serching.fulltextsearching.service.DocumentService;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {
    
    private Tika tika;
    
    @Autowired
    private DocumentRepository documentRepository;
    
    public DocumentServiceImpl() {
        this.tika = new Tika();
    }
    
    // 为了测试目的，提供一个可以注入Tika实例的构造函数
    public DocumentServiceImpl(Tika tika, DocumentRepository documentRepository) {
        this.tika = tika;
        this.documentRepository = documentRepository;
    }
    
    @Override
    public DocumentEntity saveDocument(MultipartFile file) throws IOException {
        // 使用 Tika 提取文本内容
        String content = extractText(file.getInputStream());
        
        // 创建文档实体
        DocumentEntity document = new DocumentEntity();
        document.setId(UUID.randomUUID().toString());
        document.setContent(content);
        document.setFilename(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setCreatedDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // 保存到 Elasticsearch
        return documentRepository.save(document);
    }
    
    @Override
    public List<DocumentEntity> searchDocuments(String keyword) {
        return documentRepository.findByContentContaining(keyword);
    }
    
    @Override
    public DocumentEntity getDocumentById(String id) {
        return documentRepository.findById(id).orElse(null);
    }
    
    @Override
    public Iterable<DocumentEntity> getAllDocuments() {
        return documentRepository.findAll();
    }
    
    @Override
    public String extractText(InputStream inputStream) throws IOException {
        try {
            return tika.parseToString(inputStream);
        } catch (Exception e) {
            throw new IOException("解析文档时发生错误", e);
        }
    }
    
    @Override
    public DocumentContent extractTextWithMetadata(InputStream inputStream) 
            throws IOException, TikaException, SAXException {
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        Parser parser = new AutoDetectParser();
        ParseContext context = new ParseContext();
        
        parser.parse(inputStream, handler, metadata, context);
        
        return new DocumentContent(handler.toString(), metadata);
    }
}