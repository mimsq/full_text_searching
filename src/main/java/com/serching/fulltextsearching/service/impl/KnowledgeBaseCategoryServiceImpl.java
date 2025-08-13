package com.serching.fulltextsearching.service.impl;

import com.serching.fulltextsearching.entity.TKnowledgeBaseCategory;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.mapper.KnowledgeBaseCategoryMapper;
import com.serching.fulltextsearching.mapper.TKnowledgeDocumentMapper;
import com.serching.fulltextsearching.service.KnowledgeBaseCategoryService;
import com.serching.fulltextsearching.vo.DocumentGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        //进行一些校验
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        if (knowledgeBaseId == null) {
            throw new IllegalArgumentException("知识库不存在");
        }
        TKnowledgeBaseCategory tKnowledgeBaseCategory = new TKnowledgeBaseCategory();
        tKnowledgeBaseCategory.setKbId(knowledgeBaseId);
        tKnowledgeBaseCategory.setName(name);
        tKnowledgeBaseCategory.setCreatedBy(1L);
        tKnowledgeBaseCategory.setCreatedAt(LocalDateTime.now());
        tKnowledgeBaseCategory.setUpdatedBy(1L);
        tKnowledgeBaseCategory.setUpdatedAt(LocalDateTime.now());
        tKnowledgeBaseCategoryMapper.insert(tKnowledgeBaseCategory);
    }

    @Override
    public void removeById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("分类不存在");
        }
        tKnowledgeBaseCategoryMapper.deleteById(id);
    }

    @Override
    public void updateCategory(Long id, String name) {
        if (id == null) {
            throw new IllegalArgumentException("分组不存在");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("分组名称不能为空");
        }
        TKnowledgeBaseCategory tKnowledgeBaseCategory = tKnowledgeBaseCategoryMapper.selectById(id);
        tKnowledgeBaseCategory.setName(name);
        tKnowledgeBaseCategory.setUpdatedBy(1L);
        tKnowledgeBaseCategory.setUpdatedAt(LocalDateTime.now());
        tKnowledgeBaseCategoryMapper.updateById(tKnowledgeBaseCategory);
    }

    @Override
    public DocumentGroupVo getCategoryList(Long knowledgeBaseId, Integer pageNum, Integer pageSize) {
        if (knowledgeBaseId == null) {
            throw new IllegalArgumentException("知识库不存在");
        }
        DocumentGroupVo documentGroupVo = new DocumentGroupVo();
        documentGroupVo.setCategoryList(tKnowledgeBaseCategoryMapper.selectByKbId(knowledgeBaseId));
        documentGroupVo.setDocumentList(tKnowledgeDocumentMapper.selectByKbId(knowledgeBaseId, (pageNum-1)*pageSize, pageSize));
        return documentGroupVo;
    }
}
