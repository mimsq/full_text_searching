package com.serching.fulltextsearching.service;

import com.serching.fulltextsearching.vo.OperationLogVo;

import java.util.List;

public interface OperationLogService {
    void addOperationLog(int operationType,Long objectId,Long kbId,Long createBy);

    List<OperationLogVo> getOperationLogList(Long kbId, int pageNum, int pageSize);
}
