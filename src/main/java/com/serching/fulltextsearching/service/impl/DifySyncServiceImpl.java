package com.serching.fulltextsearching.service.impl;

import com.serching.fulltextsearching.client.DifyApiClient;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.service.DifySyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Dify 同步服务实现类
 */
@Slf4j
@Service
public class DifySyncServiceImpl implements DifySyncService {
    
    @Autowired
    private DifyApiClient difyApiClient;
    
    @Value("${dify.sync-enabled:true}")
    private boolean syncEnabled;
    
    @Override
    public boolean updateDocumentInDify(TKnowledgeDocument document) {
        if (!syncEnabled) {
            log.info("Dify 同步已禁用，跳过更新操作，documentId: {}", document.getId());
            return true;
        }
        //检查是否有Dify 文档ID
        if (document.getDifyDocumentId() == null || document.getDifyDocumentId().isEmpty()) {
            log.warn("文档未关联 Dify 文档ID，跳过更新操作，documentId: {}", document.getId());
            return false;
        }
        //检查是否有知识库ID(用于确定Dify中的哪个知识库)
        if (document.getKbId() == null) {
            log.warn("文档未关联知识库ID，跳过更新操作，documentId: {}", document.getId());
            return false;
        }
        
        try {
            log.info("开始同步更新文档到 Dify，documentId: {}, difyDocumentId: {}, kbId: {}", 
                    document.getId(), document.getDifyDocumentId(), document.getKbId());
            
            String response = difyApiClient.updateDocumentByText(
                    document.getKbId().toString(),    //知识库ID
                    document.getDifyDocumentId(),    //Dify 文档ID
                    document.getTitle(),            //文档标题
                    document.getContent()           //文档内容
            );
            
            log.info("文档同步更新到 Dify 成功，本地文档ID: {}, 知识库文档ID: {}", 
                    document.getId(), document.getDifyDocumentId());
            return true;
            
        } catch (IOException e) {
            log.error("文档同步更新到 Dify 失败，documentId: {}, difyDocumentId: {}, 错误: {}", 
                    document.getId(), document.getDifyDocumentId(), e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("文档同步更新到 Dify 发生未知异常，documentId: {}, difyDocumentId: {}, 错误: {}", 
                    document.getId(), document.getDifyDocumentId(), e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean removeDocumentFromDify(TKnowledgeDocument document) {
        if (!syncEnabled) {
            log.info("Dify 同步已禁用，跳过删除操作，documentId: {}", document.getId());
            return true;
        }
        
        if (document.getDifyDocumentId() == null || document.getDifyDocumentId().isEmpty()) {
            log.warn("文档未关联 Dify 文档ID，跳过删除操作，documentId: {}", document.getId());
            return false;
        }
        
        if (document.getKbId() == null) {
            log.warn("文档未关联知识库ID，跳过删除操作，documentId: {}", document.getId());
            return false;
        }
        
        try {
            log.info("开始同步删除文档从 Dify，documentId: {}, difyDocumentId: {}, kbId: {}", 
                    document.getId(), document.getDifyDocumentId(), document.getKbId());
            
            String response = difyApiClient.deleteDocument(
                    document.getKbId().toString(),
                    document.getDifyDocumentId()
            );
            
            log.info("文档同步删除从 Dify 成功，documentId: {}, difyDocumentId: {}", 
                    document.getId(), document.getDifyDocumentId());
            return true;
            
        } catch (IOException e) {
            log.error("文档同步删除从 Dify 失败，documentId: {}, difyDocumentId: {}, 错误: {}", 
                    document.getId(), document.getDifyDocumentId(), e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("文档同步删除从 Dify 发生未知异常，documentId: {}, difyDocumentId: {}, 错误: {}", 
                    document.getId(), document.getDifyDocumentId(), e.getMessage(), e);
            return false;
        }
    }
}
