package com.serching.fulltextsearching.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_knowledge_base_member")
public class TKnowledgeBaseMember {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long kbId; // 知识库id
    private Long userId; // 用户id
    private Integer memberType; // 成员类型:0：所有者(可管理)、1管理员(可编辑)3、普通成员(仅查看)
    private Long createdBy; // 创建人
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}