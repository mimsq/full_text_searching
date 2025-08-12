package com.serching.fulltextsearching.service.impl;

import com.serching.fulltextsearching.entity.TKnowledgeBaseCategory;
import com.serching.fulltextsearching.mapper.KnowledgeBaseCategoryMapper;
import com.serching.fulltextsearching.service.KnowledgeBaseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KnowledgeBaseCategoryServiceImpl implements KnowledgeBaseCategoryService {

    @Autowired
    private KnowledgeBaseCategoryMapper tKnowledgeBaseCategoryMapper;


    @Override
    public void createCategory(String name, Long knowledgeBaseId) {
        TKnowledgeBaseCategory tKnowledgeBaseCategory = new TKnowledgeBaseCategory();
        tKnowledgeBaseCategory.setName(name);
        tKnowledgeBaseCategory.setKbId(knowledgeBaseId);
        tKnowledgeBaseCategoryMapper.insert(tKnowledgeBaseCategory);
    }

    @Override
    public void removeById(Long id) {

        tKnowledgeBaseCategoryMapper.deleteById(id);
    }

    @Override
    public void updateCategory(Long id, String name) {
        TKnowledgeBaseCategory tKnowledgeBaseCategory = tKnowledgeBaseCategoryMapper.selectById(id);
        tKnowledgeBaseCategory.setName(name);
        tKnowledgeBaseCategoryMapper.updateById(tKnowledgeBaseCategory);
    }
}
