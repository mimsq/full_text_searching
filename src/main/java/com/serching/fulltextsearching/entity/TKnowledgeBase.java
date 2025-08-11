package com.serching.fulltextsearching.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_knowledge_base")
public class TKnowledgeBase {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title; // 知识库名称
    private String baseId; // 知识库ID
    private String coverImagePath; // 封面图标
    private Integer scopeType; // 权限可见范围:0:私密、1:公开
    private String descriptionInfo; // 知识库描述信息
    private Integer kbType; // 知识库类型:0:默认，1：同步外部
    private Integer indexingType; // 索引类型
    private Long createdBy; // 创建人
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
    private String owner; // 归属，组织、小组、外部关联id比如IM某个群
    private Integer permission; // 成员内容权限:0:可查看1：可编辑
    private Long tenantId; // 租户id
}