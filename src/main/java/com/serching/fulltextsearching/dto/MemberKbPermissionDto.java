package com.serching.fulltextsearching.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(description = "成员知识库权限DTO")
public class MemberKbPermissionDto {
    @ApiModelProperty(value = "知识库ID", required = true)
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeBaseId;

    @ApiModelProperty(value = "用户ID列表", required = true)
    @NotNull(message = "用户列表不能为空")
    private List<MemberKbPermissionInfoDTO> accessControls;

    @Data
    public static class MemberKbPermissionInfoDTO {
        private String avatar;

        private String name;

        private String targetId;

        private String targetType;

        private String permission;
    }
}
