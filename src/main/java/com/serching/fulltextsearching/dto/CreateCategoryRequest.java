package com.serching.fulltextsearching.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "创建知识库分类请求")
public class CreateCategoryRequest {
    
    @ApiModelProperty(value = "分类名称", required = true, example = "测试文档分类")
    @NotBlank(message = "分类名称不能为空")
    private String name;
    
    @ApiModelProperty(value = "知识库ID", required = true, example = "1")
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeBaseId;
}
