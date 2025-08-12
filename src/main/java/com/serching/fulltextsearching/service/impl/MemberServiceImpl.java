package com.serching.fulltextsearching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.serching.fulltextsearching.dto.MemberKbPermissionDto;
import com.serching.fulltextsearching.entity.TKnowledgeBaseMember;
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
        List<TKnowledgeBaseMember> members = dto.getAccessControls().stream().map(member -> {
            TKnowledgeBaseMember tKnowledgeBaseMember = new TKnowledgeBaseMember();
            tKnowledgeBaseMember.setKbId(kbId);
            tKnowledgeBaseMember.setUserId(Long.valueOf(member.getTargetId()));
            tKnowledgeBaseMember.setMemberType(Integer.valueOf(member.getTargetType()));
            return tKnowledgeBaseMember;
        }).collect(Collectors.toList());
        knowledgeBaseMemberMapper.insertBatch(members);
    }

    @Override
    public void delete(Long id) {
        knowledgeBaseMemberMapper.deleteById(id);
    }

    @Override
    public void update(Long id, Integer memberType) {
        knowledgeBaseMemberMapper.updateMemberType(id, memberType);
    }

    //给下面的sql查询加上分页查询的功能
    @Override
    public List<TKnowledgeBaseMember> getMemberList(Long kbId, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<TKnowledgeBaseMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TKnowledgeBaseMember::getKbId, kbId);
        queryWrapper.orderByDesc(TKnowledgeBaseMember::getId);
        queryWrapper.last("limit " + (pageNum - 1) * pageSize + "," + pageSize);
        return knowledgeBaseMemberMapper.selectList(queryWrapper);
    }
}
