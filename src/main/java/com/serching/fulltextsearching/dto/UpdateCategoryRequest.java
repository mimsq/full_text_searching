package com.serching.fulltextsearching.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "更新知识库分类请求")
public class UpdateCategoryRequest {
    
    @ApiModelProperty(value = "新分类名称", required = true, example = "更新后的分类名称")
    @NotBlank(message = "分类名称不能为空")
    private String name;
}
