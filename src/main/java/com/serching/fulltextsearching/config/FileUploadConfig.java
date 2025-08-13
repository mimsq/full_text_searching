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
     * 临时文件目录
     */
    private String tempDir;
    
    /**
     * 是否启用清理
     */
    private boolean cleanupEnabled = true;
    
    public String getTempDir() {
        return tempDir;
    }
    
    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }
    
    public boolean isCleanupEnabled() {
        return cleanupEnabled;
    }
    
    public void setCleanupEnabled(boolean cleanupEnabled) {
        this.cleanupEnabled = cleanupEnabled;
    }
}
