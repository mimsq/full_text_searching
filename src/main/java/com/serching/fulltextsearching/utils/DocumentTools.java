package com.serching.fulltextsearching.utils;

import org.apache.tika.Tika;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DocumentTools {

    private Tika tika;

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


}
