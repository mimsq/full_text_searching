package com.serching.fulltextsearching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serching.fulltextsearching.common.PageResult;
import com.serching.fulltextsearching.entity.TKnowledgeBase;
import com.serching.fulltextsearching.mapper.TKnowledgeBaseMapper;
import com.serching.fulltextsearching.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Autowired
    private TKnowledgeBaseMapper knowledgeBaseMapper;
    @Value("${dify.url}")
    private String difyApiUrl;

    @Value("${dify.api-key}")
    private String difyApiKey;

    private final RestTemplate restTemplate;

    // 构造函数注入RestTemplate
    public KnowledgeBaseServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createKnowledge(String name, String coverImagePath, Integer scopeType, String descriptionInfo) {
        // 构建请求URL
        String url = difyApiUrl + "/v1/datasets";
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + difyApiKey);
        // 构建请求体
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("description", descriptionInfo);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        try {
            // 发送POST请求
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            // 打印响应结果到控制台
            System.out.println("知识库创建成功，响应结果：");
            System.out.println(response.getBody());
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            TKnowledgeBase knowledgeBase = new TKnowledgeBase();
            knowledgeBase.setTitle(name);
            knowledgeBase.setBaseId(jsonNode.get("id").asText());
            knowledgeBase.setCoverImagePath(coverImagePath);
            knowledgeBase.setScopeType(scopeType);
            knowledgeBase.setDescriptionInfo(descriptionInfo);
            knowledgeBase.setCreatedAt(LocalDateTime.now());
            knowledgeBase.setUpdatedAt(LocalDateTime.now());
            knowledgeBaseMapper.insert(knowledgeBase);
        } catch (HttpStatusCodeException e) {
            // 处理HTTP请求异常
            throw new RuntimeException("创建知识库失败: " + e.getResponseBodyAsString(), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteKnowledge(String id) {
        TKnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在");
        }
        String url = difyApiUrl + "/v1/datasets/" + knowledgeBase.getBaseId();
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + difyApiKey);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        try {
            // 发送DELETE请求
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    request,
                    Void.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                knowledgeBaseMapper.deleteById(id);
                System.out.println("知识库删除成功，ID: " + id);
            } else {
                throw new RuntimeException("删除知识库失败，状态码: " + response.getStatusCodeValue());
            }
        } catch (HttpStatusCodeException e) {
            // 处理HTTP请求异常
            throw new RuntimeException("删除知识库失败: " + e.getResponseBodyAsString(), e);
        }
    }

    @Override
    public TKnowledgeBase getKnowledgeDetail(String id) {
//        TKnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
//        if (knowledgeBase == null) {
//            throw new RuntimeException("知识库不存在");
//        }
//        String url = difyApiUrl + "/v1/datasets/" + knowledgeBase.getBaseId();
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + difyApiKey);
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        try {
//            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//            if (response.getStatusCode().is2xxSuccessful()) {
//                ObjectMapper objectMapper = new ObjectMapper();
//                return objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
//                });
//            } else {
//                throw new RuntimeException("获取知识库详情失败: " + response.getStatusCode());
//            }
//        } catch (HttpClientErrorException e) {
//            throw new RuntimeException("获取知识库详情失败: " + e.getResponseBodyAsString());
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("解析知识库详情失败");
//        } catch (Exception e) {
//            throw new RuntimeException("获取知识库详情时发生异常");
//        }
        //从数据库中查出有关信息并返回
        TKnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在");
        }
        return knowledgeBase;
    }

    @Override
    public void updateKnowledge(String id, String name, String coverImagePath, Integer scopeType, String descriptionInfo) {
        TKnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在");
        }
        String url = difyApiUrl + "/v1/datasets/" + knowledgeBase.getBaseId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + difyApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("description", descriptionInfo);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                knowledgeBase.setTitle(name);
                knowledgeBase.setUpdatedAt(LocalDateTime.now());
                knowledgeBase.setCoverImagePath(coverImagePath);
                knowledgeBase.setScopeType(scopeType);
                knowledgeBase.setDescriptionInfo(descriptionInfo);
                knowledgeBaseMapper.updateById(knowledgeBase);
            } else {
                throw new RuntimeException("更新知识库失败: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("更新知识库失败: " + e.getResponseBodyAsString());
        }
    }

    @Override
    public PageResult<TKnowledgeBase> getKnowledgeList(int page, int size, String keyword, String sortBy) {
        // 创建分页对象
        Page<TKnowledgeBase> pageParam = new Page<>(page, size);

        // 构建查询条件
        LambdaQueryWrapper<TKnowledgeBase> queryWrapper = new LambdaQueryWrapper<>();

//        // 添加关键词模糊查询条件（支持标题和描述）
//        if (StringUtils.hasText(keyword)) {
//            queryWrapper.and(wrapper -> wrapper
//                    .like(TKnowledgeBase::getTitle, keyword)
//                    .or()
//                    .like(TKnowledgeBase::getDescriptionInfo, keyword));
//        }
//
//        // 添加排序条件
//        if (StringUtils.hasText(sortBy)) {
//            switch (sortBy.toLowerCase()) {
//                case "title":
//                    queryWrapper.orderByAsc(TKnowledgeBase::getTitle);
//                    break;
//                case "created":
//                    queryWrapper.orderByDesc(TKnowledgeBase::getCreatedAt);
//                    break;
//                case "updated":
//                    queryWrapper.orderByDesc(TKnowledgeBase::getUpdatedAt);
//                    break;
//                default:
//                    queryWrapper.orderByDesc(TKnowledgeBase::getUpdatedAt);
//                    break;
//            }
//        } else {
//            // 默认按更新时间降序排序
//            queryWrapper.orderByDesc(TKnowledgeBase::getUpdatedAt);
//        }

        // 执行分页查询
        IPage<TKnowledgeBase> resultPage = knowledgeBaseMapper.selectPage(pageParam, queryWrapper);

        // 封装返回结果
        PageResult<TKnowledgeBase> pageResult = new PageResult<>();
        pageResult.setRecords(resultPage.getRecords());
        pageResult.setTotal(resultPage.getTotal());
        pageResult.setSize(resultPage.getSize());
        pageResult.setCurrent(resultPage.getCurrent());
        pageResult.setPages(resultPage.getPages());

        return pageResult;
    }
}
