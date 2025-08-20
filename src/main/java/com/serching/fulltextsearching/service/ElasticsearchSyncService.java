package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.dto.EsSearchResult;
import com.serching.fulltextsearching.entity.ESKnowledgeDocument;

public interface ElasticsearchSyncService {
    /**
     * 同步文档到 Elasticsearch
     */
    boolean syncDocumentToEs(ESKnowledgeDocument document);

    /**
     * 从 Elasticsearch 删除文档
     */
    boolean deleteDocumentFromEs(String documentId);


    /**
     * 从 Elasticsearch 全文检索文档
     */
    EsSearchResult searchDocumentIds(String keyword, int page, int size);

    /**
     * 从 Elasticsearch 全文检索指定知识库下的文档
     */
    EsSearchResult searchDocumentIdsByKbId(String keyword, Long kbId, int page, int size);
}
