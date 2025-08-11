package com.serching.fulltextsearching.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    
    public DifyApiClient(@Value("${dify.url}") String baseUrl,
                        @Value("${dify.api-key}") String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
        log.info("DifyApiClient 初始化完成，baseUrl: {}", baseUrl);
    }

    /**
     * 创建知识库
     * @param name 知识库名称
     * @param description 知识库描述
     * @return API响应结果
     */
    public String createDataset(String name, String description) throws IOException {
        String url = baseUrl + "/v1/datasets";

        log.info("开始创建 Dify 知识库，名称: {}", name);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", name);
        if (description != null && !description.isEmpty()) {
            requestBody.put("description", description);
        }

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        log.debug("创建知识库请求体: {}", jsonBody);

        // 创建HTTP请求
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Authorization", "Bearer " + apiKey);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));

        // 执行请求
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();

            log.info("Dify 知识库创建完成，状态码: {}, 名称: {}", statusCode, name);
            log.debug("Dify 响应体: {}", responseBody);

            if (statusCode >= 200 && statusCode < 300) {
                log.info("Dify 知识库创建成功，名称: {}", name);
            } else {
                log.error("Dify 知识库创建失败，状态码: {}, 名称: {}, 响应: {}", statusCode, name, responseBody);
            }

            return responseBody;
        } catch (Exception e) {
            log.error("Dify 知识库创建异常，名称: {}, 错误: {}", name, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 删除知识库
     * @param datasetId 知识库ID
     * @return API响应结果
     */
    public String deleteDataset(String datasetId) throws IOException {
        String url = baseUrl + "/v1/datasets/" + datasetId;

        log.info("开始删除 Dify 知识库，datasetId: {}", datasetId);

        // 创建HTTP请求
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.setHeader("Authorization", "Bearer " + apiKey);

        // 执行请求
        try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();

            log.info("Dify 知识库删除完成，状态码: {}, datasetId: {}", statusCode, datasetId);
            log.debug("Dify 响应体: {}", responseBody);

            if (statusCode >= 200 && statusCode < 300) {
                log.info("Dify 知识库删除成功，datasetId: {}", datasetId);
            } else {
                log.error("Dify 知识库删除失败，状态码: {}, datasetId: {}, 响应: {}", statusCode, datasetId, responseBody);
            }

            return responseBody;
        } catch (Exception e) {
            log.error("Dify 知识库删除异常，datasetId: {}, 错误: {}", datasetId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 更新知识库
     * @param datasetId 知识库ID
     * @param name 知识库名称(可选)
     * @param description 知识库描述(可选)
     * @return API响应结果
     */
    public String updateDataset(String datasetId, String name, String description) throws IOException {
        String url = baseUrl + "/v1/datasets/" + datasetId;

        log.info("开始更新 Dify 知识库，datasetId: {}", datasetId);

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        if (name != null && !name.isEmpty()) {
            requestBody.put("name", name);
        }
        if (description != null && !description.isEmpty()) {
            requestBody.put("description", description);
        }

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        log.debug("更新知识库请求体: {}", jsonBody);

        // 创建HTTP请求
        HttpPatch httpPatch = new HttpPatch(url);
        httpPatch.setHeader("Authorization", "Bearer " + apiKey);
        httpPatch.setHeader("Content-Type", "application/json");
        httpPatch.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));

        // 执行请求
        try (CloseableHttpResponse response = httpClient.execute(httpPatch)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();

            log.info("Dify 知识库更新完成，状态码: {}, datasetId: {}", statusCode, datasetId);
            log.debug("Dify 响应体: {}", responseBody);

            if (statusCode >= 200 && statusCode < 300) {
                log.info("Dify 知识库更新成功，datasetId: {}", datasetId);
            } else {
                log.error("Dify 知识库更新失败，状态码: {}, datasetId: {}, 响应: {}", statusCode, datasetId, responseBody);
            }

            return responseBody;
        } catch (Exception e) {
            log.error("Dify 知识库更新异常，datasetId: {}, 错误: {}", datasetId, e.getMessage(), e);
            throw e;
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
