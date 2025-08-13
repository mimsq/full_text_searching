package com.serching.fulltextsearching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TKnowledgeDocumentMapper extends BaseMapper<TKnowledgeDocument> {
    @Select("select * from t_knowledge_document where kb_id = #{knowledgeBaseId} order by category_id,id limit #{pageNum},#{pageSize}")
    List<TKnowledgeDocument> selectByKbId(Long knowledgeBaseId, Integer pageNum, Integer pageSize);
}
