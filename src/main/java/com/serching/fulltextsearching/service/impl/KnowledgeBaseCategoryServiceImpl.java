package com.serching.fulltextsearching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.serching.fulltextsearching.entity.KnowledgeBaseCategory;
import com.serching.fulltextsearching.entity.KnowledgeDocument;
import com.serching.fulltextsearching.mapper.KnowledgeBaseCategoryMapper;
import com.serching.fulltextsearching.mapper.KnowledgeDocumentMapper;
import com.serching.fulltextsearching.service.KnowledgeBaseCategoryService;
import com.serching.fulltextsearching.service.KnowledgeDocumentService;
import com.serching.fulltextsearching.vo.DocumentGroupVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class KnowledgeBaseCategoryServiceImpl implements KnowledgeBaseCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(KnowledgeBaseCategoryServiceImpl.class);

    @Autowired
    private KnowledgeBaseCategoryMapper knowledgeBaseCategoryMapper;

    @Autowired
    private KnowledgeDocumentMapper knowledgeDocumentMapper;

    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;

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
        knowledgeBaseCategoryMapper.insert(knowledgeBaseCategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        logger.info("开始删除分类，分类ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }

        // 1. 查询分类是否存在
        KnowledgeBaseCategory category = knowledgeBaseCategoryMapper.selectById(id);
        if (category == null) {
            throw new IllegalArgumentException("分类不存在");
        }

        // 2. 查询该分类下的所有文档
        QueryWrapper<KnowledgeDocument> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", id)
               .eq("del_status", 0); // 只查询未删除的文档
        
        List<KnowledgeDocument> documents = knowledgeDocumentService.list(wrapper);
        logger.info("分类下共有 {} 个文档需要移入回收站", documents.size());

        // 3. 将该分类下的所有文档移入回收站
        // 使用默认用户ID 1L，后续由负责该模块的处理逻辑来获取真实用户ID
        Long defaultUserId = 1L;
        for (KnowledgeDocument document : documents) {
            try {
                boolean moved = knowledgeDocumentService.moveToRecycleBin(document.getId(), defaultUserId);
                if (moved) {
                    logger.info("文档 {} 成功移入回收站", document.getId());
                } else {
                    logger.warn("文档 {} 移入回收站失败", document.getId());
                }
            } catch (Exception e) {
                logger.error("文档 {} 移入回收站异常", document.getId(), e);
                // 继续处理其他文档，不中断整个流程
            }
        }

        // 4. 删除分类
        int deleted = knowledgeBaseCategoryMapper.deleteById(id);
        if (deleted > 0) {
            logger.info("分类 {} 删除成功", id);
        } else {
            logger.warn("分类 {} 删除失败", id);
            throw new RuntimeException("分类删除失败");
        }
    }

    @Override
    public void updateCategory(Long id, String name) {
        if (id == null) {
            throw new IllegalArgumentException("分组不存在");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("分组名称不能为空");
        }
        KnowledgeBaseCategory knowledgeBaseCategory = knowledgeBaseCategoryMapper.selectById(id);
        knowledgeBaseCategory.setName(name);
        knowledgeBaseCategory.setUpdatedBy(1L);
        knowledgeBaseCategory.setUpdatedAt(LocalDateTime.now());
        knowledgeBaseCategoryMapper.updateById(knowledgeBaseCategory);
    }

    @Override
    public DocumentGroupVo getCategoryList(Long knowledgeBaseId, Integer pageNum, Integer pageSize) {
        if (knowledgeBaseId == null) {
            throw new IllegalArgumentException("知识库不存在");
        }
        DocumentGroupVo documentGroupVo = new DocumentGroupVo();
        documentGroupVo.setCategoryList(knowledgeBaseCategoryMapper.selectByKbId(knowledgeBaseId));
        documentGroupVo.setDocumentList(knowledgeDocumentMapper.selectByKbId(knowledgeBaseId, (pageNum-1)*pageSize, pageSize));
        return documentGroupVo;
    }
}
