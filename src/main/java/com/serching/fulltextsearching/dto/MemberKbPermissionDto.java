package com.serching.fulltextsearching.dto;

import lombok.Data;

import java.util.List;

@Data
public class MemberKbPermissionDto {
    private Long knowledgeBaseId;

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
