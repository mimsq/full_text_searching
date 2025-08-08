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
     * 根据文件名搜索文档
     * @param filename 文件名
     * @return 匹配的文档列表
     */
    List<TKnowledgeDocument> findByFilename(String filename);
}