package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.entity.TKnowledgeBase;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import org.apache.poi.ss.formula.functions.T;

/**
 * Dify 同步服务接口
 * 用于处理文档与 Dify 知识库的同步操作
 */
public interface DifySyncService {
    /**
     * 创建 Dify 知识库
     * @param tKnowledgeBase 知识库参数
     * @return 创建的知识库实体
     */
    String createKnowledgeInDify(TKnowledgeBase tKnowledgeBase);

    /**
     * 删除 Dify 知识库
     * @param difyBaseId Dify 知识库ID
     * @return 是否删除成功
     */
    boolean deleteKnowledgeFromDify(String difyBaseId);

    /**
     * 更新 Dify 知识库
     * @param tKnowledgeBase 知识库参数
     * @return 是否更新成功
     */
    boolean updateKnowledgeInDify(TKnowledgeBase tKnowledgeBase);

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
