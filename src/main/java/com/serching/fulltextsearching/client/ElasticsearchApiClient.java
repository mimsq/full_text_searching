package com.serching.fulltextsearching.client;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.serching.fulltextsearching.entity.ESKnowledgeDocument;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Objects;

/**
 * Elasticsearch 8 官方客户端实现
 */
@Slf4j
@Component
public class ElasticsearchApiClient {

    @Value("${spring.elasticsearch.uris}")
    private String esUris;

    @Value("${spring.elasticsearch.username:}")
    private String username;

    @Value("${spring.elasticsearch.password:}")
    private String password;

    private ElasticsearchClient esClient;
    private ElasticsearchTransport transport;
    private final String indexName = "knowledge_document"; // 索引名称

    /**
     * 初始化 Elasticsearch 客户端
     */
    @PostConstruct
    public void initClient() {
        // 解析ES连接地址
        String[] hosts = esUris.split(",");
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            String host = hosts[i].replace("http://", "").replace("https://", "");
            String[] parts = host.split(":");
            httpHosts[i] = new HttpHost(parts[0], Integer.parseInt(parts[1]));
        }

        // 创建低级REST客户端
        RestClient restClient = RestClient.builder(httpHosts)
                // 如需认证可添加如下配置
                // .setHttpClientConfigCallback(httpClientBuilder ->
                //     httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                // )
                .build();

        // 创建传输层
        transport = new RestClientTransport(restClient, new JacksonJsonpMapper());

        // 创建高级客户端
        esClient = new ElasticsearchClient(transport);
        log.info("Elasticsearch 8 客户端初始化完成，连接地址: {}", esUris);
    }

    /**
     * 同步文档到 Elasticsearch
     */
    public boolean syncDocument(ESKnowledgeDocument document) {
        if (Objects.isNull(esClient)) {
            log.error("Elasticsearch 客户端未初始化");
            return false;
        }
        try {
            // 创建索引请求（对应官方示例的 IndexRequest）
            IndexRequest<ESKnowledgeDocument> request = IndexRequest.of(i -> i
                    .index(indexName)
                    .id(document.getId())
                    .document(document)
            );
            // 执行索引操作
            IndexResponse response = esClient.index(request);

            log.info("文档同步到 Elasticsearch 成功，documentId: {}, version: {}",
                    document.getId(), response.version());
            return true;

        } catch (IOException e) {
            log.error("文档同步到 Elasticsearch 失败，documentId: {}", document.getId(), e);
            return false;
        }
    }

    /**
     * 从 Elasticsearch 删除文档
     */
    public boolean deleteDocument(String documentId) {
        if (Objects.isNull(esClient)) {
            log.error("Elasticsearch 客户端未初始化");
            return false;
        }

        try {
            // 创建删除请求
            DeleteRequest request = DeleteRequest.of(d -> d
                    .index(indexName)
                    .id(documentId)
            );

            // 执行删除操作
            DeleteResponse response = esClient.delete(request);

            log.info("文档从 Elasticsearch 删除成功，documentId: {}, result: {}",
                    documentId, response.result());
            return true;

        } catch (IOException e) {
            log.error("文档从 Elasticsearch 删除失败，documentId: {}", documentId, e);
            return false;
        }
    }

    /**
     * 销毁客户端资源
     */
    @PreDestroy
    public void closeClient() throws IOException {
        if (transport != null) {
            transport.close();
            log.info("Elasticsearch 客户端资源已释放");
        }
    }
}
