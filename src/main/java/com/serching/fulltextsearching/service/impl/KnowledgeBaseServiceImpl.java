package com.serching.fulltextsearching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serching.fulltextsearching.common.PageResult;
import com.serching.fulltextsearching.entity.TKnowledgeBase;
import com.serching.fulltextsearching.mapper.TKnowledgeBaseMapper;
import com.serching.fulltextsearching.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Autowired
    private DifySyncServiceImpl difySyncService;

    @Autowired
    private TKnowledgeBaseMapper knowledgeBaseMapper;
    @Value("${dify.url}")
    private String difyApiUrl;

    @Value("${dify.api-key}")
    private String difyApiKey;

    @Override
    public void createKnowledge(String name, String coverImagePath, Integer scopeType, String descriptionInfo) {
        TKnowledgeBase knowledgeBase = new TKnowledgeBase();
        knowledgeBase.setTitle(name);
        knowledgeBase.setCoverImagePath(coverImagePath);
        knowledgeBase.setScopeType(scopeType);
        knowledgeBase.setDescriptionInfo(descriptionInfo);
        try {
            String difyBaseId = difySyncService.createKnowledgeInDify(knowledgeBase);
            knowledgeBase.setBaseId(difyBaseId); // 关联Dify知识库ID
            knowledgeBaseMapper.insert(knowledgeBase);
        } catch (Exception e) {
            throw new RuntimeException("创建知识库失败", e);
        }
    }

    @Override
    public void deleteKnowledge(String id) {
        // 1. 查询数据库获取Dify知识库ID
        TKnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在");
        }

        // 2. 调用DifySyncService执行Dify平台删除操作
        try {
            difySyncService.deleteKnowledgeFromDify(knowledgeBase.getBaseId());
        } catch (Exception e) {
            throw new RuntimeException("删除知识库失败", e);
        }

        // 3. 执行数据库删除操作
        knowledgeBaseMapper.deleteById(id);
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
        // 1. 查询数据库获取知识库信息
        TKnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在");
        }
        knowledgeBase.setTitle(name);
        knowledgeBase.setUpdatedAt(LocalDateTime.now());
        knowledgeBase.setCoverImagePath(coverImagePath);
        knowledgeBase.setScopeType(scopeType);
        knowledgeBase.setDescriptionInfo(descriptionInfo);
        // 2. 调用DifySyncService执行Dify平台更新操作
        try
        {
            difySyncService.updateKnowledgeInDify(knowledgeBase);
        } catch (Exception e) {
            throw new RuntimeException("更新知识库失败", e);
        }
        // 3. 执行数据库更新操作
        knowledgeBaseMapper.updateById(knowledgeBase);
    }

    @Override
    public PageResult<TKnowledgeBase> getKnowledgeList(int page, int size) {
        // 创建分页对象
        Page<TKnowledgeBase> pageParam = new Page<>(page, size);

        // 构建查询条件
        LambdaQueryWrapper<TKnowledgeBase> queryWrapper = new LambdaQueryWrapper<>();

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

    @Override
    public void setPermission(String id, int scopeType) {
        //操作数据库，将scopeType更新为scopeType
        TKnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在");
        }
        knowledgeBase.setScopeType(scopeType);
        knowledgeBaseMapper.updateById(knowledgeBase);
    }
}
