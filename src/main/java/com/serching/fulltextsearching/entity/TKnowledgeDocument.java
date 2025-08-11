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
     * 关联知识库 id
     */
    private Long kbId;

    /**
     * 文档分类 id
     */
    private Long categoryId;

    /**
     * 文档内容
     */
    private String content;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 概述描述
     */
    private String summary;

    /**
     * 文件后缀
     */
    private String docSuffix;

    /**
     * 字数
     */
    private Integer wordCount;

    /**
     * 处理状态：0 未开始、1 完成、2 处理中
     */
    private Integer processingStatus;

    /**
     * 文档状态：0 不可用、1 可用
     */
    private Integer docStatus;

    /**
     * 删除状态：0 未删除、1 已删除
     */
    private Integer delStatus;

    /**
     * 文档类型：0 文本、1 图片、2 音频、3 视频、4 其他
     */
    private Integer docType;

    /**
     * 文档其他信息（建议 JSON）
     */
    private String docMetadata;

    /**
     * 关联文件 id（t_knowledge_file.id）
     */
    private Long fileId;

    /**
     * 预览信息 / 地址
     */
    private String previewInfo;

    /**
     * Dify 中的文档 ID
     *
     * ALTER TABLE t_knowledge_document
     * ADD COLUMN dify_document_id VARCHAR(255) COMMENT 'Dify 中的文档 ID';
     *
     * CREATE INDEX idx_dify_document_id ON t_knowledge_document(dify_document_id);
     */
    private String difyDocumentId;

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
     * 更新人ID
     */
    private Long updatedBy;

    /**
     * 最后更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;


}