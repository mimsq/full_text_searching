package com.serching.fulltextsearching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serching.fulltextsearching.entity.TKnowledgeDocument;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TKnowledgeDocumentMapper extends BaseMapper<TKnowledgeDocument> {

    List<TKnowledgeDocument> selectByKbId(
        @Param("knowledgeBaseId") Long knowledgeBaseId,
        @Param("pageNum") Integer pageNum,
        @Param("pageSize") Integer pageSize
    );
}
