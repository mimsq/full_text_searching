package com.serching.fulltextsearching.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.serching.fulltextsearching.entity.KnowledgeDocument;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface KnowledgeDocumentMapper extends BaseMapper<KnowledgeDocument> {

    List<KnowledgeDocument> selectByKbId(
        @Param("knowledgeBaseId") Long knowledgeBaseId,
        @Param("pageNum") Integer pageNum,
        @Param("pageSize") Integer pageSize
    );
}
