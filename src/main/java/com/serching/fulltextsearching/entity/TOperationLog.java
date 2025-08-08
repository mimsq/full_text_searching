package com.serching.fulltextsearching.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_operation_log")
public class TOperationLog {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title; // 操作描述
    private Integer operationType; // 操作类型
    private Integer objectId; // 操作对象id:如知识库id、知识文档id
    private String objectType; // 知识库、知识、成员
    private Long createdBy; // 创建人
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}