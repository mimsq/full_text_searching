package com.serching.fulltextsearching.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置类
 */
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadConfig {
    /**
     * 永久存储目录
     */
    private String permanentDir;
    // === 永久存储配置添加结束 ===
    
    public String getPermanentDir() {
        return permanentDir;
    }
    
    public void setPermanentDir(String permanentDir) {
        this.permanentDir = permanentDir;
    }
}