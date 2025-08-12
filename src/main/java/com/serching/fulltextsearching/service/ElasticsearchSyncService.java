package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.entity.ESKnowledgeDocument;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;

public interface ElasticsearchSyncService {
    /**
     * 同步文档到 Elasticsearch
     */
    boolean syncDocumentToEs(ESKnowledgeDocument document);

    /**
     * 从 Elasticsearch 删除文档
     */
    boolean deleteDocumentFromEs(String documentId);
}
