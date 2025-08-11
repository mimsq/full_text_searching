package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.entity.TKnowledgeDocument;

/**
 * Dify 同步服务接口
 * 用于处理文档与 Dify 知识库的同步操作
 */
public interface DifySyncService {
    
    /**
     * 更新 Dify 知识库中的文档
     * @param document 要更新的文档
     * @return 是否更新成功
     */
    boolean updateDocumentInDify(TKnowledgeDocument document);
    
    /**
     * 从 Dify 知识库中删除文档
     * @param document 要删除的文档
     * @return 是否删除成功
     */
    boolean removeDocumentFromDify(TKnowledgeDocument document);
}
