package com.serching.fulltextsearching.service.impl;

import com.serching.fulltextsearching.entity.KnowledgeBaseCategory;
import com.serching.fulltextsearching.mapper.KnowledgeBaseCategoryMapper;
import com.serching.fulltextsearching.mapper.KnowledgeDocumentMapper;
import com.serching.fulltextsearching.service.KnowledgeBaseCategoryService;
import com.serching.fulltextsearching.vo.DocumentGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KnowledgeBaseCategoryServiceImpl implements KnowledgeBaseCategoryService {

    @Autowired
    private KnowledgeBaseCategoryMapper tKnowledgeBaseCategoryMapper;

    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Override
    public void createCategory(String name, Long knowledgeBaseId) {
        //进行一些校验
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        if (knowledgeBaseId == null) {
            throw new IllegalArgumentException("知识库不存在");
        }
        KnowledgeBaseCategory knowledgeBaseCategory = new KnowledgeBaseCategory();
        knowledgeBaseCategory.setKbId(knowledgeBaseId);
        knowledgeBaseCategory.setName(name);
        knowledgeBaseCategory.setCreatedBy(1L);
        knowledgeBaseCategory.setCreatedAt(LocalDateTime.now());
        knowledgeBaseCategory.setUpdatedBy(1L);
        knowledgeBaseCategory.setUpdatedAt(LocalDateTime.now());
        tKnowledgeBaseCategoryMapper.insert(knowledgeBaseCategory);
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
        KnowledgeBaseCategory knowledgeBaseCategory = tKnowledgeBaseCategoryMapper.selectById(id);
        knowledgeBaseCategory.setName(name);
        knowledgeBaseCategory.setUpdatedBy(1L);
        knowledgeBaseCategory.setUpdatedAt(LocalDateTime.now());
        tKnowledgeBaseCategoryMapper.updateById(knowledgeBaseCategory);
    }

    @Override
    public DocumentGroupVo getCategoryList(Long knowledgeBaseId, Integer pageNum, Integer pageSize) {
        if (knowledgeBaseId == null) {
            throw new IllegalArgumentException("知识库不存在");
        }
        DocumentGroupVo documentGroupVo = new DocumentGroupVo();
        documentGroupVo.setCategoryList(tKnowledgeBaseCategoryMapper.selectByKbId(knowledgeBaseId));
        documentGroupVo.setDocumentList(knowledgeDocumentMapper.selectByKbId(knowledgeBaseId, (pageNum-1)*pageSize, pageSize));
        return documentGroupVo;
    }
}
