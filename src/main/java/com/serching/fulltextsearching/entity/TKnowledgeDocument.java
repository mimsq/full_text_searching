package com.serching.fulltextsearching.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_knowledge_document")
public class TKnowledgeDocument {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文档内容
     */
    private String content;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文件后缀
     */
    private String docSuffix;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 处理状态 (0: 处理中, 1: 已完成, 2: 失败)
     */
    private Integer processingStatus;

    /**
     * 文档状态 (0: 禁用, 1: 启用)
     */
    private Integer docStatus;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 文档来源路径
     */
    private String sourcePath;

    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;


}