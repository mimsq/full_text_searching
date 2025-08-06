package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.entity.DocumentEntity;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface DocumentService {
    
    /**
     * 上传并保存文档
     * @param file 上传的文件
     * @return 保存的文档实体
     * @throws IOException IO异常
     */
    DocumentEntity saveDocument(MultipartFile file) throws IOException;
    
    /**
     * 根据关键字搜索文档
     * @param keyword 搜索关键字
     * @return 匹配的文档列表
     */
    List<DocumentEntity> searchDocuments(String keyword);
    
    /**
     * 根据ID获取文档
     * @param id 文档ID
     * @return 文档实体
     */
    DocumentEntity getDocumentById(String id);
    
    /**
     * 获取所有文档
     * @return 所有文档列表
     */
    Iterable<DocumentEntity> getAllDocuments();
    
    /**
     * 使用 Apache Tika 提取文档中的文本内容
     * @param inputStream 文档输入流
     * @return 提取的文本内容
     * @throws IOException IO异常
     */
    String extractText(InputStream inputStream) throws IOException;
    
    /**
     * 使用 Apache Tika 提取文档中的文本内容（带元数据）
     * @param inputStream 文档输入流
     * @return 提取的文本内容和元数据
     * @throws IOException IO异常
     * @throws TikaException Tika异常
     * @throws SAXException SAX异常
     */
    DocumentContent extractTextWithMetadata(InputStream inputStream) 
            throws IOException, TikaException, SAXException;
    
    /**
     * 文档内容和元数据的封装类
     */
    class DocumentContent {
        private final String content;
        private final Metadata metadata;
        
        public DocumentContent(String content, Metadata metadata) {
            this.content = content;
            this.metadata = metadata;
        }
        
        public String getContent() {
            return content;
        }
        
        public Metadata getMetadata() {
            return metadata;
        }
    }
}