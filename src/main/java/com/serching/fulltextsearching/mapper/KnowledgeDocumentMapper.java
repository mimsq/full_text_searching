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

	/**
	 * 最近编辑的文档（按操作日志中编辑时间倒序，去重文档）
	 */
	List<KnowledgeDocument> selectRecentEdited(
		@Param("kbId") Long kbId,
		@Param("userId") Long userId,
		@Param("pageNum") Integer pageNum,
		@Param("pageSize") Integer pageSize
	);

	/**
	 * 最近编辑文档总数（去重计数）
	 */
	Long countRecentEdited(
		@Param("kbId") Long kbId,
		@Param("userId") Long userId
	);

	/** 最近预览的文档（按操作日志中预览时间倒序，去重文档） */
	List<KnowledgeDocument> selectRecentViewed(
		@Param("kbId") Long kbId,
		@Param("userId") Long userId,
		@Param("pageNum") Integer pageNum,
		@Param("pageSize") Integer pageSize
	);

	/** 最近预览文档总数（去重计数） */
	Long countRecentViewed(
		@Param("kbId") Long kbId,
		@Param("userId") Long userId
	);
}
