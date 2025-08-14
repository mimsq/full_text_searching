package com.serching.fulltextsearching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serching.fulltextsearching.entity.KnowledgeBase;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface KnowledgeBaseMapper extends BaseMapper<KnowledgeBase> {

}