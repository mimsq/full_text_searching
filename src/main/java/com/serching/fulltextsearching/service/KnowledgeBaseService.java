package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.common.PageResult;
import com.serching.fulltextsearching.entity.TKnowledgeBase;

import java.util.Map;

public interface KnowledgeBaseService {

    void createKnowledge(String name, String coverImagePath, Integer scopeType, String descriptionInfo);

    void deleteKnowledge(String id);

    TKnowledgeBase getKnowledgeDetail(String id);

    void updateKnowledge(String id, String name, String coverImagePath, Integer scopeType, String descriptionInfo);

    PageResult<TKnowledgeBase> getKnowledgeList(int page, int size);

    void setPermission(String id, int scopeType);

    Integer getPermission(String knowledgeBaseId);
}
