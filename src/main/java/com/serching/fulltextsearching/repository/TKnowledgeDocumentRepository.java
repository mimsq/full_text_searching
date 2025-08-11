package com.serching.fulltextsearching.repository;

import com.serching.fulltextsearching.entity.ESKnowledgeDocument;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TKnowledgeDocumentRepository extends ElasticsearchRepository<ESKnowledgeDocument, String> {
    
    /**
     * 根据内容搜索文档
     * @param content 内容关键字
     * @return 匹配的文档列表
     */
    List<TKnowledgeDocument> findByContentContaining(String content);
    
    /**
     * 根据标题搜索文档
     * @param title 标题
     * @return 匹配的文档列表
     */
    List<ESKnowledgeDocument> findByTitle(String title);
}