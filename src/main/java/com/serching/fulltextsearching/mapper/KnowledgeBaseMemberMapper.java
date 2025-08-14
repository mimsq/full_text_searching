package com.serching.fulltextsearching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serching.fulltextsearching.entity.TKnowledgeBaseMember;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

public interface KnowledgeBaseMemberMapper extends BaseMapper<TKnowledgeBaseMember> {
    // 自定义批量插入方法
    void insertBatch(@Param("list") List<TKnowledgeBaseMember> members);

    int updateMemberType(@Param("kbId") Long kbId, @Param("userId") Long userId, @Param("memberType") Integer memberType, @Param("updatedBy") Long updatedBy, @Param("updatedAt") LocalDateTime updatedAt);
}
