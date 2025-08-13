package com.serching.fulltextsearching.service.impl;

import com.serching.fulltextsearching.entity.TKnowledgeBaseCategory;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.mapper.KnowledgeBaseCategoryMapper;
import com.serching.fulltextsearching.mapper.TKnowledgeDocumentMapper;
import com.serching.fulltextsearching.service.KnowledgeBaseCategoryService;
import com.serching.fulltextsearching.vo.DocumentGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KnowledgeBaseCategoryServiceImpl implements KnowledgeBaseCategoryService {

    @Autowired
    private KnowledgeBaseCategoryMapper tKnowledgeBaseCategoryMapper;

    @Autowired
    private TKnowledgeDocumentMapper tKnowledgeDocumentMapper;

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

    @Override
    public DocumentGroupVo getCategoryList(Long knowledgeBaseId, Integer pageNum, Integer pageSize) {
        DocumentGroupVo documentGroupVo = new DocumentGroupVo();
        documentGroupVo.setCategoryList(tKnowledgeBaseCategoryMapper.selectByKbId(knowledgeBaseId));
        documentGroupVo.setDocumentList(tKnowledgeDocumentMapper.selectByKbId(knowledgeBaseId));
        return documentGroupVo;
    }
}
