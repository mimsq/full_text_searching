package com.serching.fulltextsearching.vo;

import com.serching.fulltextsearching.entity.TOperationLog;
import lombok.Data;

@Data
public class OperationLogVo extends TOperationLog {
    // 文档标题
    private String documentTitle;
    // 操作用户名
    private String username;
    // 图片链接
    private String avatarUrl;
}
