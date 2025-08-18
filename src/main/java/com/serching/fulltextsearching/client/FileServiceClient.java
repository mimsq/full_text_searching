package com.serching.fulltextsearching.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serching.fulltextsearching.dto.FileCallResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class FileServiceClient {

    @Value("${file-service.service.url:http://localhost:8080}")
    private String fileServiceUrl;

    @Value("${file-service.service.uid:1}")
    private String defaultUid;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 上传文件到文件服务
     * @param file 要上传的文件
     * @param sourceType 来源类型：1-租户人员上传，0-前端用户上传
     * @param uid 租户人员ID（当sourceType=1时必传）
     * @return FileCallResult 上传结果
     */
    public FileCallResult uploadFile(File file, String sourceType, String uid) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String uploadUrl = fileServiceUrl + "/upload";
            HttpPost httpPost = new HttpPost(uploadUrl);

            // 设置请求头
            if ("1".equals(sourceType) && uid != null) {
                httpPost.setHeader("uid", uid);
            }

            // 构建multipart请求体
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
            builder.addTextBody("st", sourceType, ContentType.TEXT_PLAIN);

            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);

            log.info("开始上传文件到文件服务: {}, 文件: {}", uploadUrl, file.getName());

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                log.info("文件服务响应: {}", responseBody);

                if (response.getStatusLine().getStatusCode() == 200) {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    return parseFileCallResult(jsonNode);
                } else {
                    log.error("文件服务上传失败，状态码: {}, 响应: {}",
                            response.getStatusLine().getStatusCode(), responseBody);
                    return FileCallResult.error("文件服务上传失败: " + response.getStatusLine().getStatusCode());
                }
            }
        } catch (Exception e) {
            log.error("调用文件服务上传接口异常: {}", e.getMessage(), e);
            return FileCallResult.error("文件服务调用异常: " + e.getMessage());
        }
    }

    private FileCallResult parseFileCallResult(JsonNode jsonNode) {
        FileCallResult result = new FileCallResult();
        result.setRet(jsonNode.get("ret").asInt());
        result.setMsg(jsonNode.get("msg").asText());

        if (result.getRet() == 0) {
            result.setId(jsonNode.get("id").asText());
            result.setStoreId(jsonNode.get("storeId").asText());
            result.setFileName(jsonNode.get("fileName").asText());
            result.setFileSize(jsonNode.get("fileSize").asLong());
            result.setFileSuffix(jsonNode.get("fileSuffix").asText());
            result.setFileType(jsonNode.get("fileType").asText());
            if (jsonNode.has("fileExtInfo")) {
                result.setFileExtInfo(jsonNode.get("fileExtInfo").asText());
            }
        }

        return result;
    }
}