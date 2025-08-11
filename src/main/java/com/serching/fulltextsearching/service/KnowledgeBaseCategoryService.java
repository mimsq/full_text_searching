package com.serching.fulltextsearching.service;

public interface KnowledgeBaseCategoryService {
    void createCategory(String name, Long knowledgeBaseId);

    void removeById(Long id);

    void updateCategory(Long id, String name);
}
