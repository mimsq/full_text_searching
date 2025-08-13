package com.serching.fulltextsearching.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.serching.fulltextsearching.entity.TOperationLog;
import com.serching.fulltextsearching.mapper.OperationLogMapper;
import com.serching.fulltextsearching.service.OperationLogService;
import com.serching.fulltextsearching.vo.OperationLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public void addOperationLog(int operationType, Long objectId, Long kbId,Long createdBy) {
        TOperationLog operationLog = new TOperationLog();
        operationLog.setOperationType(operationType);
        operationLog.setObjectId(objectId);
        operationLog.setKbId(kbId);
        operationLog.setCreatedBy(createdBy);
        operationLog.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(operationLog);
    }

    @Override
    public List<OperationLogVo> getOperationLogList(Long kbId, int pageNum, int pageSize) {
        List<OperationLogVo> operationLogList = operationLogMapper.selectOperationLogWithDetails(kbId, (pageNum-1)*pageSize, pageSize);
        return operationLogList;
    }
}
