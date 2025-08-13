package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.entity.TKnowledgeBase;

public interface DifySyncBaseService {
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
}
