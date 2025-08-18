package com.serching.fulltextsearching.dto;

import lombok.Data;

@Data
public class FileCallResult {
    private Integer ret;
    private String msg;
    private String id;
    private String storeId;
    private String fileName;
    private Long fileSize;
    private String fileSuffix;
    private String fileType;
    private String fileExtInfo;

    public static FileCallResult error(String message) {
        FileCallResult result = new FileCallResult();
        result.setRet(1);
        result.setMsg(message);
        return result;
    }

    public boolean isSuccess() {
        return ret != null && ret == 0;
    }
}