package com.serching.fulltextsearching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serching.fulltextsearching.entity.KnowledgeBaseCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KnowledgeBaseCategoryMapper extends BaseMapper<KnowledgeBaseCategory> {
    List<KnowledgeBaseCategory> selectByKbId(Long knowledgeBaseId);
}
