package com.serching.fulltextsearching.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.serching.fulltextsearching.entity.TKnowledgeFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TKnowledgeFileService extends IService<TKnowledgeFile> {
    /**
     * 保存文件信息并返回文件记录
     */
    TKnowledgeFile saveFileInfo(MultipartFile file, String filePath, Long userId) throws IOException;
}