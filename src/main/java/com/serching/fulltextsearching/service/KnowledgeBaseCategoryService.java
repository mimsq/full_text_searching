package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.vo.DocumentGroupVo;

import java.util.List;

public interface KnowledgeBaseCategoryService {
    void createCategory(String name, Long knowledgeBaseId);

    /**
     * 删除分类，同时将该分类下的所有文档移入回收站
     * @param id 分类ID
     */
    void removeById(Long id);

    void updateCategory(Long id, String name);

    DocumentGroupVo getCategoryList(Long knowledgeBaseId, Integer pageNum, Integer pageSize);
}
