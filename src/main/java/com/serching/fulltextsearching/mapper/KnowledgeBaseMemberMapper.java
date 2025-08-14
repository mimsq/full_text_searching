package com.serching.fulltextsearching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serching.fulltextsearching.entity.KnowledgeBaseMember;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface KnowledgeBaseMemberMapper extends BaseMapper<KnowledgeBaseMember> {
    // 自定义批量插入方法
    void insertBatch(@Param("list") List<KnowledgeBaseMember> members);

    int updateMemberType(@Param("kbId") Long kbId, @Param("userId") Long userId, @Param("memberType") Integer memberType, @Param("updatedBy") Long updatedBy, @Param("updatedAt") LocalDateTime updatedAt);
}
