package com.serching.fulltextsearching.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serching.fulltextsearching.client.DifyApiClient;
import com.serching.fulltextsearching.entity.KnowledgeBase;
import com.serching.fulltextsearching.service.DifySyncBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class DifySyncBaseServiceImpl implements DifySyncBaseService {
    @Autowired
    private DifyApiClient difyApiClient;

    @Value("${dify.sync-enabled:true}")
    private boolean syncEnabled;

    @Override
    public String createKnowledgeInDify(KnowledgeBase knowledgeBase) {
        if (!syncEnabled) {
            log.info("Dify 同步已禁用，跳过创建知识库操作");
            return null;
        }
        try {
            log.info("开始同步创建知识库到 Dify，名称: {}", knowledgeBase.getTitle());

            String response = difyApiClient.createDataset(knowledgeBase.getTitle(), knowledgeBase.getDescriptionInfo());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);
            String difyBaseId = jsonNode.get("id").asText();
            log.info("知识库同步创建到 Dify 成功，名称: {}, Dify 知识库ID: {}", knowledgeBase.getTitle(), difyBaseId);
            return difyBaseId;

        } catch (IOException e) {
            log.error("知识库同步创建到 Dify 失败，名称: {}, 错误: {}", knowledgeBase.getTitle(), e.getMessage(), e);
            throw new RuntimeException("创建知识库失败: " + e.getMessage(), e);
        }
    }
    @Override
    public boolean deleteKnowledgeFromDify(String difyBaseId) {
        if (!syncEnabled) {
            log.info("Dify 同步已禁用，跳过删除知识库操作，Dify知识库ID: {}", difyBaseId);
            return true;
        }
        try {
            log.info("开始同步删除知识库从 Dify，Dify 知识库ID: {}", difyBaseId);

            difyApiClient.deleteDataset(difyBaseId);
            log.info("知识库同步删除从 Dify 成功，Dify 知识库ID: {}", difyBaseId);
            return true;

        } catch (IOException e) {
            log.error("知识库同步删除从 Dify 失败，Dify 知识库ID: {}, 错误: {}", difyBaseId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateKnowledgeInDify(KnowledgeBase knowledgeBase) {
        if (!syncEnabled) {
            log.info("Dify 同步已禁用，跳过更新知识库操作，Dify知识库ID: {}", knowledgeBase.getBaseId());
            return true;
        }
        try {
            log.info("开始同步更新知识库到 Dify，Dify 知识库ID: {}", knowledgeBase.getBaseId());
            difyApiClient.updateDataset(knowledgeBase.getBaseId(), knowledgeBase.getTitle(), knowledgeBase.getDescriptionInfo());
            log.info("知识库同步更新到 Dify 成功，Dify 知识库ID: {}", knowledgeBase.getBaseId());
            return true;

        } catch (IOException e) {
            log.error("知识库同步更新到 Dify 失败，Dify 知识库ID: {}, 错误: {}", knowledgeBase.getBaseId(), e.getMessage(), e);
            return false;
        }
    }
}
