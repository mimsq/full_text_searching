package com.serching.fulltextsearching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serching.fulltextsearching.entity.TKnowledgeBaseMember;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface KnowledgeBaseMemberMapper extends BaseMapper<TKnowledgeBaseMember> {
    // 自定义批量插入方法
    void insertBatch(@Param("list") List<TKnowledgeBaseMember> members);

    @Update("UPDATE t_knowledge_base_member SET member_type = #{memberType} WHERE id = #{id}")
    int updateMemberType(@Param("id") Long id, @Param("memberType") Integer memberType);
}
