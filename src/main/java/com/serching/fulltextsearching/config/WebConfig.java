package com.serching.fulltextsearching.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web配置类
 * Spring Boot 2.x 默认已经支持multipart/form-data文件上传
 * 不需要额外配置，保持简单
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Spring Boot 2.x 默认支持文件上传，无需额外配置

    @Value("${file.upload.image}")
    private String imageDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将/api/covers/** 映射到实际存储目录
        registry.addResourceHandler("/api/covers/**")
                .addResourceLocations("file:" + imageDir + File.separator);
    }
}
