package com.serching.fulltextsearching.utils;

import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class DocumentTools {

    private final Tika tika = new Tika();

    //从文档中提取文本
    public String extractTextFromPath(String filePath) throws IOException {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            return tika.parseToString(inputStream);
        } catch (Exception e) {
            throw new IOException("解析文档时发生错误: " + filePath, e);
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
