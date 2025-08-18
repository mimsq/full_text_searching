package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.vo.OperationLogVo;

import java.util.List;

public interface OperationLogService {
    void addOperationLog(int operationType,Long objectId,Long kbId,Long createBy);

    List<OperationLogVo> getOperationLogList(Long kbId, int pageNum, int pageSize);

	/**
	 * 将某文档从“最近编辑”列表中移除（全局隐藏，通过写入隐藏日志）
	 */
	void hideFromRecentEdited(Long documentId, Long kbId);
}
