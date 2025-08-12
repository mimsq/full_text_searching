package com.serching.fulltextsearching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.serching.fulltextsearching.common.PageResult;
import com.serching.fulltextsearching.entity.TKnowledgeBase;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface TKnowledgeDocumentService extends IService<TKnowledgeDocument> {

    TKnowledgeDocument uploadDocument (TKnowledgeBase knowledgeBase, Long categoryId, MultipartFile file) throws IOException;

    TKnowledgeDocument updateDocument(TKnowledgeDocument tKnowledgeDocument);

    /**
     * 删除文档，同时删除MySQL和Elasticsearch中的数据
     * @param id 文档ID
     * @return 是否删除成功
     */
    boolean deleteDocument(Long id);

    /**
     * 分页查询指定知识库下的文档，仅查询 MySQL
     * @param kbId 知识库的数据库ID（即 `t_knowledge_base.id`）
     * @param current 当前页，从1开始
     * @param size 每页数量
     * @return 分页结果
     */
    PageResult<TKnowledgeDocument> pageByKbId(Long kbId, long current, long size);
}