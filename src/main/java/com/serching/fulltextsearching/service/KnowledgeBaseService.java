package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.common.PageResult;
import com.serching.fulltextsearching.entity.KnowledgeBase;

public interface KnowledgeBaseService {

    void createKnowledge(String name, String coverImagePath, Integer scopeType, String descriptionInfo);

    void deleteKnowledge(Long id);

    KnowledgeBase getKnowledgeDetail(Long id);

    void updateKnowledge(String id, String name, String coverImagePath, Integer scopeType, String descriptionInfo);

    PageResult<KnowledgeBase> getKnowledgeList(int page, int size);

    void setPermission(String id, int scopeType);

    Integer getPermission(String knowledgeBaseId);
}
