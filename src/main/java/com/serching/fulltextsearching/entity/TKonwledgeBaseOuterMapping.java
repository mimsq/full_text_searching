package com.serching.fulltextsearching.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_konwledge_base_outer_mapping")
public class TKonwledgeBaseOuterMapping {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long kbId;
    private String outId; // 外部关联对象id
    private String outType; // 外部系统类型:比如dify、ragflow
    private String extendedInfo; // 外部额外配置信息
    private Long createdBy; // 创建人
    private LocalDateTime createdAt;
    private Long updatedBy;
    private LocalDateTime updatedAt;
}