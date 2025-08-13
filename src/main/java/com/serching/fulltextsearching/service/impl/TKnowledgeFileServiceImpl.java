package com.serching.fulltextsearching.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serching.fulltextsearching.entity.TKnowledgeFile;
import com.serching.fulltextsearching.mapper.TKnowledgeFileMapper;
import com.serching.fulltextsearching.service.TKnowledgeFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class TKnowledgeFileServiceImpl extends ServiceImpl<TKnowledgeFileMapper, TKnowledgeFile> implements TKnowledgeFileService {

    @Override
    public TKnowledgeFile saveFileInfo(MultipartFile file, String filePath, Long userId) throws IOException {
        TKnowledgeFile knowledgeFile = new TKnowledgeFile();
        knowledgeFile.setName(file.getOriginalFilename());
        knowledgeFile.setFilePath(filePath);
        knowledgeFile.setSuffix(getFileSuffix(file.getOriginalFilename()));
        knowledgeFile.setFileSzie((int) file.getSize());
        knowledgeFile.setMd5(calculateMd5(file));
        knowledgeFile.setEncryption(0); // 默认不加密
        knowledgeFile.setCreatedBy(userId);
        knowledgeFile.setCreatedAt(LocalDateTime.now());
        knowledgeFile.setUpdatedBy(userId);
        knowledgeFile.setUpdatedAt(LocalDateTime.now());

        this.save(knowledgeFile);
        return knowledgeFile;
    }

    private String getFileSuffix(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private String calculateMd5(MultipartFile file) throws IOException {
        // 这里可以集成MD5计算工具类
        return ""; // 暂时返回空，后续可集成实际MD5计算
    }
}