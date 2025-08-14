package com.serching.fulltextsearching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.serching.fulltextsearching.entity.KnowledgeFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface KnowledgeFileService extends IService<KnowledgeFile> {
    /**
     * 保存文件信息并返回文件记录
     */
    KnowledgeFile saveFileInfo(MultipartFile file, String filePath, Long userId) throws IOException;
}