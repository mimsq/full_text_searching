package com.serching.fulltextsearching.utils;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class DocumentTools {

    private static final Logger logger = LoggerFactory.getLogger(DocumentTools.class);
    private final Tika tika = new Tika();

    //从文档中提取文本
    public String extractTextFromPath(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("文件不存在: " + filePath);
        }
        
        logger.info("开始提取文本，文件: {}, 大小: {} bytes", filePath, file.length());
        
        try (InputStream inputStream = new FileInputStream(file)) {
            String content = tika.parseToString(inputStream);
            logger.info("文本提取成功，文件: {}, 内容长度: {} 字符", filePath, content != null ? content.length() : 0);
            return content;
        } catch (TikaException e) {
            logger.error("Tika解析文档失败: {}", filePath, e);
            throw new IOException("解析文档时发生错误: " + filePath + ", 错误: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("提取文本时发生未知错误: {}", filePath, e);
            throw new IOException("解析文档时发生错误: " + filePath + ", 错误: " + e.getMessage(), e);
        }
    }

    //从文件名中获取扩展名
    public String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 检查文件格式是否支持
     * @param filename 文件名
     * @return 是否支持
     */
    public boolean isSupportedFormat(String filename) {
        String extension = getFileExtension(filename);
        return "txt".equals(extension) || "md".equals(extension) || 
               "doc".equals(extension) || "docx".equals(extension) || 
               "pdf".equals(extension);
    }

    /**
     * 获取支持的文件格式列表
     * @return 支持的文件格式列表
     */
    public String[] getSupportedFormats() {
        return new String[]{"txt", "md", "doc", "docx", "pdf"};
    }

    /**
     * 创建文本文件并写入内容
     * @param filePath 文件路径
     * @param content 文件内容
     * @throws IOException IO异常
     */
    public void createTextFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content != null ? content : "");
        }
    }

    /**
     * 获取文件名后缀
     * @param filename 文件名
     * @return 文件名后缀
     */
    private String getFileSuffix(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

}
