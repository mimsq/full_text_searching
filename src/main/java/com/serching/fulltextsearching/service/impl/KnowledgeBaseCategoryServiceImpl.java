package com.serching.fulltextsearching.service.impl;

import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.mapper.TKnowledgeDocumentMapper;
import com.serching.fulltextsearching.vo.DocumentGroupVo;
import com.serching.fulltextsearching.entity.TKnowledgeBaseCategory;
import com.serching.fulltextsearching.mapper.KnowledgeBaseCategoryMapper;
import com.serching.fulltextsearching.service.KnowledgeBaseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<DocumentGroupVo> getCategoryList(Long knowledgeBaseId, Integer pageNum, Integer pageSize) {
        List<TKnowledgeBaseCategory> categoryList = tKnowledgeBaseCategoryMapper.selectByKbId(knowledgeBaseId);
        List<TKnowledgeDocument> documentList = tKnowledgeDocumentMapper.selectByKbId(knowledgeBaseId);
        List<DocumentGroupVo> documentGroupVoList = new ArrayList<>();
        int i = 0;
        for (TKnowledgeBaseCategory category : categoryList) {
            DocumentGroupVo documentGroupVo = new DocumentGroupVo();
            documentGroupVo.setId(category.getId());
            documentGroupVo.setTitle(category.getName());
            documentGroupVo.setType("GROUP");
            documentGroupVoList.add(documentGroupVo);
            while(i<documentList.size()) {
                TKnowledgeDocument document = documentList.get(i);
                if(document.getCategoryId().equals(category.getId())){
                    DocumentGroupVo documentGroupVo1 = new DocumentGroupVo();
                    documentGroupVo1.setId(document.getId());
                    documentGroupVo1.setTitle(document.getTitle());
                    documentGroupVo1.setType("DOCUMENT");
                    documentGroupVo1.setCategoryId(document.getCategoryId());
                    documentGroupVoList.add(documentGroupVo1);
                    i++;
                }else{
                    break;
                }
            }
        }

        return documentGroupVoList;
    }
}
