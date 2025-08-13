package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.common.PageResult;
import com.serching.fulltextsearching.entity.TKnowledgeBase;

public interface KnowledgeBaseService {

    void createKnowledge(String name, String coverImagePath, Integer scopeType, String descriptionInfo);

    void deleteKnowledge(Long id);

    TKnowledgeBase getKnowledgeDetail(Long id);

    void updateKnowledge(String id, String name, String coverImagePath, Integer scopeType, String descriptionInfo);

    PageResult<TKnowledgeBase> getKnowledgeList(int page, int size);

    void setPermission(String id, int scopeType);

    Integer getPermission(String knowledgeBaseId);
}
