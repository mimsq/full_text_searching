package com.serching.fulltextsearching.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Dify API 客户端
 * 用于与 Dify 知识库进行文档同步操作
 */
@Slf4j
@Component
public class DifyApiClient {
    
    private final String baseUrl;
    private final String apiKey;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public DifyApiClient(@Value("${dify.base-url}") String baseUrl,
                        @Value("${dify.api-key}") String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
        log.info("DifyApiClient 初始化完成，baseUrl: {}", baseUrl);
    }
    //通过文件创建文档，返回 dify 文档ID
    public String createDocumentByFile(String kbId, File file,Map<String,Object> dataConfig) throws Exception{
        String url = baseUrl + "/v1/datasets/" + kbId + "/document/create-by-file";
        String dataJson = (dataConfig == null) ? "{}" : objectMapper.writeValueAsString(dataConfig);

        HttpPost post = new HttpPost(url);
        post.setHeader("Authorization", "Bearer " + apiKey);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("data", dataJson, ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
        builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName());
        post.setEntity(builder.build());

        log.info("POST {}", url);
        log.debug("data={}", dataJson);

        try (CloseableHttpClient http = HttpClients.createDefault();
             CloseableHttpResponse resp = http.execute(post)) {
            String body = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
            int code = resp.getStatusLine().getStatusCode();
            log.info("Dify create-by-file status={}, bodyLength={}", code, body.length());
            if (code < 200 || code >= 300) {
                throw new RuntimeException("Dify 通过文件创建文档失败: " + body);
            }
            JsonNode root = objectMapper.readTree(body);
            JsonNode doc = root.path("document");
            String difyDocumentId = doc.path("id").asText(null);
            if (difyDocumentId == null) {
                throw new RuntimeException("Dify 响应中缺少 document.id: " + body);
            }
            return difyDocumentId;
        }
    }
    
    /**
     * 通过文本更新 Dify 知识库中的文档
     * @param datasetId 知识库ID
     * @param documentId 文档ID
     * @param name 文档名称（可选）
     * @param text 文档内容（可选）
     * @return API响应结果
     */
    public String updateDocumentByText(String datasetId, String documentId, String name, String text) throws IOException {
        String url = baseUrl + "/v1/datasets/" + datasetId + "/documents/" + documentId + "/update-by-text";
        
        log.info("开始通过文本更新 Dify 文档，datasetId: {}, documentId: {}, url: {}", datasetId, documentId, url);
        
        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        if (name != null && !name.isEmpty()) {
            requestBody.put("name", name);
        }
        if (text != null && !text.isEmpty()) {
            requestBody.put("text", text);
        }
        
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        log.debug("更新文档请求体: {}", jsonBody);
        
        // 创建HTTP请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization", "Bearer " + apiKey);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
        
        // 执行请求
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            
            log.info("Dify 文档更新完成，状态码: {}, documentId: {}", statusCode, documentId);
            log.debug("Dify 响应体: {}", responseBody);
            
            if (statusCode >= 200 && statusCode < 300) {
                log.info("Dify 文档更新成功，documentId: {}", documentId);
            } else {
                log.error("Dify 文档更新失败，状态码: {}, documentId: {}, 响应: {}", statusCode, documentId, responseBody);
            }
            
            return responseBody;
        } catch (Exception e) {
            log.error("Dify 文档更新异常，documentId: {}, 错误: {}", documentId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 删除 Dify 知识库中的文档
     * @param datasetId 知识库ID
     * @param documentId 文档ID
     * @return API响应结果
     */
    public String deleteDocument(String datasetId, String documentId) throws IOException {
        String url = baseUrl + "/v1/datasets/" + datasetId + "/documents/" + documentId;
        
        log.info("开始删除 Dify 文档，datasetId: {}, documentId: {}, url: {}", datasetId, documentId, url);
        
        // 创建HTTP请求
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.setHeader("Authorization", "Bearer " + apiKey);
        
        // 执行请求
        try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            
            log.info("Dify 文档删除完成，状态码: {}, documentId: {}", statusCode, documentId);
            log.debug("Dify 响应体: {}", responseBody);
            
            if (statusCode >= 200 && statusCode < 300) {
                log.info("Dify 文档删除成功，documentId: {}", documentId);
            } else {
                log.error("Dify 文档删除失败，状态码: {}, documentId: {}, 响应: {}", statusCode, documentId, responseBody);
            }
            
            return responseBody;
        } catch (Exception e) {
            log.error("Dify 文档删除异常，documentId: {}, 错误: {}", documentId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 关闭HTTP客户端
     */
    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
            log.info("DifyApiClient HTTP客户端已关闭");
        }
    }
}
