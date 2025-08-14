package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.entity.KnowledgeBase;

public interface DifySyncBaseService {
    /**
     * 创建 Dify 知识库
     * @param knowledgeBase 知识库参数
     * @return 创建的知识库实体
     */
    String createKnowledgeInDify(KnowledgeBase knowledgeBase);

    /**
     * 删除 Dify 知识库
     * @param difyBaseId Dify 知识库ID
     * @return 是否删除成功
     */
    boolean deleteKnowledgeFromDify(String difyBaseId);

    /**
     * 更新 Dify 知识库
     * @param knowledgeBase 知识库参数
     * @return 是否更新成功
     */
    boolean updateKnowledgeInDify(KnowledgeBase knowledgeBase);
}
