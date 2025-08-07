package com.serching.fulltextsearching;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.serching.fulltextsearching.entity.TestEntity;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import co.elastic.clients.elasticsearch.core.IndexResponse; // 使用新版 IndexResponse
import org.elasticsearch.client.RestClient;
import java.util.logging.Logger;
import java.io.IOException;


public class TestClient {
    public static void main(String[] args) {
        // URL and API key
        String serverUrl = "http://localhost:9200";

// Create the low-level client
        RestClient restClient = RestClient
                .builder(HttpHost.create(serverUrl))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey ")
                })
                .build();

// Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());

// And create the API client
        ElasticsearchClient esClient = new ElasticsearchClient(transport);

        TestEntity testEntity = new TestEntity("1","这是一篇测试文档","测试");

        try {
            IndexResponse response = esClient.index(i ->
                            i.index("测试")
                                    .id(testEntity.getId())
                                    .document(testEntity)
                    );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Logger logger = Logger.getLogger(TestClient.class.getName());
        try {
            GetResponse getResponse = esClient.get(g ->
                    g.index("测试")
                            .id("1"),
                            TestEntity.class);
            if (getResponse.found()) {
                TestEntity product = (TestEntity) getResponse.source();
                logger.info("Product name " + product.getContent());
            } else {
                logger.info ("Product not found");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
}
