package com.serching.fulltextsearching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serching.fulltextsearching.entity.TKnowledgeBaseCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KnowledgeBaseCategoryMapper extends BaseMapper<TKnowledgeBaseCategory> {
    @Select("select * from t_knowledge_base_category where kb_id = #{knowledgeBaseId} order by id")
    List<TKnowledgeBaseCategory> selectByKbId(Long knowledgeBaseId);
}
