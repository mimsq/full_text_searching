package com.serching.fulltextsearching.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_knowledge_base_category")
public class TKnowledgeBaseCategory {
    @TableId(type = IdType.AUTO)
    //知识库分组id（前端传值可选）
    private Long id;
    //知识库分组名称
    private String name;
    //知识库id
    private Long kbId;
    //创建人
    private Long createdBy;
    //创建时间
    private LocalDateTime createdAt;
    //更新人
    private Long updatedBy;
    //更新时间
    private LocalDateTime updatedAt;
}