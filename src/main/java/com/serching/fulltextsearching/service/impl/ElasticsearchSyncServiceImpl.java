package com.serching.fulltextsearching.service.impl;

import com.serching.fulltextsearching.client.ElasticsearchApiClient;
import com.serching.fulltextsearching.dto.EsSearchResult;
import com.serching.fulltextsearching.entity.ESKnowledgeDocument;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import com.serching.fulltextsearching.service.ElasticsearchSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class ElasticsearchSyncServiceImpl implements ElasticsearchSyncService {

    @Autowired
    private ElasticsearchApiClient elasticsearchClient;

    @Value("${elasticsearch.sync-enabled:true}")
    private boolean syncEnabled;

    /**
     * 同步文档到 Elasticsearch
     */
    @Override
    public boolean syncDocumentToEs(ESKnowledgeDocument document) {
        if (!syncEnabled) {
            log.info("Elasticsearch 同步已禁用，跳过同步操作，documentId: {}", document.getId());
            return true;
        }

        if (document.getId() == null || document.getContent() == null) {
            log.warn("文档ID或内容为空，跳过同步操作，documentId: {}", document.getId());
            return false;
        }
        try {
            log.info("开始同步文档到 Elasticsearch，documentId: {}", document.getId());
            boolean result = elasticsearchClient.syncDocument(document);

            if (result) {
                log.info("文档同步到 Elasticsearch 成功，documentId: {}", document.getId());
            } else {
                log.error("文档同步到 Elasticsearch 失败，documentId: {}", document.getId());
            }

            return result;
        } catch (Exception e) {
            log.error("文档同步到 Elasticsearch 发生未知异常，documentId: {},错误: {}", document.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * 从 Elasticsearch 删除文档
     */
    @Override
    public boolean deleteDocumentFromEs(String documentId) {
        if (!syncEnabled) {
            log.info("Elasticsearch 同步已禁用，跳过删除操作，documentId: {}", documentId);
            return true;
        }

        if (documentId == null) {
            log.warn("文档ID为空，跳过删除操作");
            return false;
        }
        try {
            log.info("开始从 Elasticsearch 删除文档，documentId: {}", documentId);
            boolean result = elasticsearchClient.deleteDocument(documentId);
            log.info("文档从 Elasticsearch 删除成功，documentId: {}", documentId);
            return result;
        } catch (Exception e) {
            log.error("文档从 Elasticsearch 删除发生未知异常，documentId: {},错误: {}", documentId, e.getMessage());
            return false;
        }
    }

    @Override
    public EsSearchResult searchDocumentIds(String keyword, int page, int size) {
        try {
            return elasticsearchClient.searchDocuments(keyword, page, size);
        } catch (Exception e) {
            log.error("ES 搜索失败: {}", e.getMessage(), e);
            throw new RuntimeException("ES 搜索失败: " + e.getMessage(), e);
        }
    }
}
