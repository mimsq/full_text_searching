package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.dto.MemberKbPermissionDto;
import com.serching.fulltextsearching.entity.KnowledgeBaseMember;

import java.util.List;

public interface MemberService {
    void saveBatch(MemberKbPermissionDto dto);

    void delete(MemberKbPermissionDto dto);

    void update(MemberKbPermissionDto dto);

    List<KnowledgeBaseMember> getMemberList(Long kbId, Integer pageNum, Integer pageSize);
}
