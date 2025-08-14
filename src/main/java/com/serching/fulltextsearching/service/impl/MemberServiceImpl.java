package com.serching.fulltextsearching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.serching.fulltextsearching.dto.MemberKbPermissionDto;
import com.serching.fulltextsearching.entity.KnowledgeBaseMember;
import com.serching.fulltextsearching.mapper.KnowledgeBaseMemberMapper;
import com.serching.fulltextsearching.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private KnowledgeBaseMemberMapper knowledgeBaseMemberMapper;

    @Override
    public void saveBatch(MemberKbPermissionDto dto) {
        Long kbId = dto.getKnowledgeBaseId();
        List<KnowledgeBaseMember> members = dto.getAccessControls().stream().map(member -> {
            KnowledgeBaseMember knowledgeBaseMember = new KnowledgeBaseMember();
            knowledgeBaseMember.setKbId(kbId);
            knowledgeBaseMember.setUserId(Long.valueOf(member.getTargetId()));
            knowledgeBaseMember.setMemberType(Integer.valueOf(member.getPermission()));
            knowledgeBaseMember.setCreatedBy(1L);
            knowledgeBaseMember.setUpdatedBy(1L);
            knowledgeBaseMember.setCreatedAt(LocalDateTime.now());
            knowledgeBaseMember.setUpdatedAt(LocalDateTime.now());
            return knowledgeBaseMember;
        }).collect(Collectors.toList());
        knowledgeBaseMemberMapper.insertBatch(members);
    }

    @Override
    public void delete(MemberKbPermissionDto dto) {
        knowledgeBaseMemberMapper.delete(new LambdaQueryWrapper<KnowledgeBaseMember>()
                .eq(KnowledgeBaseMember::getKbId, dto.getKnowledgeBaseId())
                .eq(KnowledgeBaseMember::getUserId, Long.valueOf(dto.getAccessControls().get(0).getTargetId())));
    }

    @Override
    public void update(MemberKbPermissionDto dto) {
        knowledgeBaseMemberMapper.updateMemberType(dto.getKnowledgeBaseId(), Long.valueOf(dto.getAccessControls().get(0).getTargetId()), Integer.valueOf(dto.getAccessControls().get(0).getPermission()), 1L, LocalDateTime.now());
    }

    //给下面的sql查询加上分页查询的功能
    @Override
    public List<KnowledgeBaseMember> getMemberList(Long kbId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<KnowledgeBaseMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(KnowledgeBaseMember::getKbId, kbId);
        queryWrapper.orderByDesc(KnowledgeBaseMember::getId);
        queryWrapper.last("limit " + (pageNum - 1) * pageSize + "," + pageSize);
        return knowledgeBaseMemberMapper.selectList(queryWrapper);
    }
}
