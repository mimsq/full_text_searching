package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.vo.DocumentGroupVo;

import java.util.List;

public interface KnowledgeBaseCategoryService {
    void createCategory(String name, Long knowledgeBaseId);

    void removeById(Long id);

    void updateCategory(Long id, String name);

    DocumentGroupVo getCategoryList(Long knowledgeBaseId, Integer pageNum, Integer pageSize);
}
