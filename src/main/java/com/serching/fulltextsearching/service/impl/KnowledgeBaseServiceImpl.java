package com.serching.fulltextsearching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serching.fulltextsearching.common.PageResult;
import com.serching.fulltextsearching.entity.KnowledgeBase;
import com.serching.fulltextsearching.exception.BusinessException;
import com.serching.fulltextsearching.mapper.KnowledgeBaseMapper;
import com.serching.fulltextsearching.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    @Autowired
    private DifySyncBaseServiceImpl difySyncBaseService;

    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;

    @Value("${file.upload.image}")
    private String imageDir;

    @Override
    public void createKnowledge(String name, String coverImagePath, Integer scopeType, String descriptionInfo) {
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.setTitle(name);
        knowledgeBase.setCoverImagePath(coverImagePath);
        knowledgeBase.setScopeType(scopeType);
        knowledgeBase.setDescriptionInfo(descriptionInfo);
        knowledgeBase.setKbType(0);
        knowledgeBase.setIndexingType(0);
        knowledgeBase.setCreatedBy(1L);
        knowledgeBase.setCreatedAt(LocalDateTime.now());
        knowledgeBase.setUpdatedBy(1L);
        knowledgeBase.setUpdatedAt(LocalDateTime.now());
        try {
            String difyBaseId = difySyncBaseService.createKnowledgeInDify(knowledgeBase);
            knowledgeBase.setBaseId(difyBaseId);
            knowledgeBaseMapper.insert(knowledgeBase);
        } catch (Exception e) {
            throw new RuntimeException("创建知识库失败", e);
        }
    }

    @Override
    public void deleteKnowledge(Long id) {
        // 1. 查询数据库获取Dify知识库ID
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在");
        }

        // 2. 调用DifySyncService执行Dify平台删除操作
        try {
            difySyncBaseService.deleteKnowledgeFromDify(knowledgeBase.getBaseId());
        } catch (Exception e) {
            throw new RuntimeException("删除知识库失败", e);
        }

        // 3. 执行数据库删除操作
        knowledgeBaseMapper.deleteById(id);
    }


    @Override
    public KnowledgeBase getKnowledgeDetail(Long id) {
        //从数据库中查出有关信息并返回
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在");
        }
        return knowledgeBase;
    }

    @Override
    public void updateKnowledge(String id, String name, String coverImagePath, Integer scopeType, String descriptionInfo) {
        // 1. 查询数据库获取知识库信息
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在");
        }
        knowledgeBase.setTitle(name);
        knowledgeBase.setUpdatedAt(LocalDateTime.now());
        knowledgeBase.setUpdatedBy(1L);
        if (coverImagePath != null) {
            knowledgeBase.setCoverImagePath(coverImagePath);
        }
        if (scopeType != null) {
            knowledgeBase.setScopeType(scopeType);
        }
        if (descriptionInfo != null) {
            knowledgeBase.setDescriptionInfo(descriptionInfo);
        }
        // 2. 调用DifySyncService执行Dify平台更新操作
        try
        {
            difySyncBaseService.updateKnowledgeInDify(knowledgeBase);
        } catch (Exception e) {
            throw new RuntimeException("更新知识库失败", e);
        }
        // 3. 执行数据库更新操作
        knowledgeBaseMapper.updateById(knowledgeBase);
    }

    @Override
    public PageResult<KnowledgeBase> getKnowledgeList(int page, int size) {
        // 创建分页对象
        Page<KnowledgeBase> pageParam = new Page<>(page, size);

        // 构建查询条件
        LambdaQueryWrapper<KnowledgeBase> queryWrapper = new LambdaQueryWrapper<>();

        // 执行分页查询
        IPage<KnowledgeBase> resultPage = knowledgeBaseMapper.selectPage(pageParam, queryWrapper);

        // 封装返回结果
        PageResult<KnowledgeBase> pageResult = new PageResult<>();
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
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在");
        }
        knowledgeBase.setScopeType(scopeType);
        knowledgeBaseMapper.updateById(knowledgeBase);
    }

    @Override
    public Integer getPermission(String knowledgeBaseId) {
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(knowledgeBaseId);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在");
        }
        return knowledgeBase.getScopeType();
    }

    @Override
    public void updateDict(Long id, Map<String, Object> dict) {
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在");
        }
        knowledgeBase.setDict(dict);
        knowledgeBase.setUpdatedAt(LocalDateTime.now());
        knowledgeBase.setUpdatedBy(1L);
        knowledgeBaseMapper.updateById(knowledgeBase);
    }

    @Override
    public Map<String, Object> getDict(Long id) {
        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
        if (knowledgeBase == null) {
            throw new RuntimeException("知识库不存在");
        }
        return knowledgeBase.getDict();
    }

    @Override
    public String uploadCoverImage(MultipartFile file) throws IOException{
        //1.确保上传目录存在
        File dir = new File(imageDir);
        if (!dir.exists() && !dir.mkdirs()){
            throw new BusinessException("图片保存目录不存在且无法自动创建，请联系管理员");
        }

        //2.生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else if (file.getContentType() != null && file.getContentType().startsWith("image/")) {
            // fallback by content type
            suffix = "." + file.getContentType().substring("image/".length());
        } else {
            suffix = ".img";
        }
        String fileName = "cover_" + UUID.randomUUID()+suffix;

        //3.保存文件
        try {
            File dest = new File(dir, fileName);
            log.info("[uploadCoverImage] save to: {} (existsDir={})", dest.getAbsolutePath(), dir.exists());
            file.transferTo(dest);
            log.info("[uploadCoverImage] saved ok: {} bytes", dest.length());
        } catch (IOException ioe) {
            log.error("[uploadCoverImage] save failed: {}", ioe.getMessage(), ioe);
            throw new BusinessException("图片保存失败，请稍后再试");
        }

        //4.构建访问路径
        return "/api/covers/"+fileName;
    }

}
