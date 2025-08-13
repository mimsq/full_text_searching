package com.serching.fulltextsearching.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_knowledge_document_segment")
public class TKnowledgeDocumentSegment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long kbId; // 知识库id
    private Long docId; // 文档id
    private String content; // 文档切片内容
    private Long createdBy; // 创建人
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}