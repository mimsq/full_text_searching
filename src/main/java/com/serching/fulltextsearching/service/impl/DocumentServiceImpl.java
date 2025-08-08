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

import java.io.File;
import java.io.FileInputStream;
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
        //生成唯一文件名
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        //定义保存路径(可根据需要修改)
        String filePath = System.getProperty("java.io.tmpdir") + "/" + filename;
        //保存文件到本地路径
        File localFile = new File(filePath);
        file.transferTo(localFile);
        try{
            //使用tika提取指定路径下的文件
            String content = extractTextFromPath(filePath);

            //创建文档实体...
            DocumentEntity document = new DocumentEntity();

            return documentRepository.save(document);
        }finally {
            //可选:删除临时文件
            if (localFile.exists()){
                localFile.delete();
            }
        }
        


    }
    /**
     * 从文件路径提取文本内容
     * @param filePath 文件路径
     * @return 提取的文本内容
     * @throws IOException IO异常
     */
    public String extractTextFromPath(String filePath) throws IOException {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            return tika.parseToString(inputStream);
        } catch (Exception e) {
            throw new IOException("解析文档时发生错误: " + filePath, e);
        }
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