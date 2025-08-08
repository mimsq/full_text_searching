package com.serching.fulltextsearching.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_knowledge_file")
public class TKnowledgeFile {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name; // 文件名
    private String filePath; // 存储路径
    private String suffix; // 后缀名
    private Integer fileSzie; // 文件大小
    private String md5; // 文件md5值
    private Integer encryption; // 文件是否加密
    private Long createdBy; // 创建人
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}