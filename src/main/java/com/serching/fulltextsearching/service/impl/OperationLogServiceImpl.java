package com.serching.fulltextsearching.service.impl;

import com.serching.fulltextsearching.entity.OperationLog;
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
        OperationLog operationLog = new OperationLog();
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

    @Override
    public void hideFromRecentEdited(Long documentId, Long kbId) {
        OperationLog hideLog = new OperationLog();
        hideLog.setOperationType(5); // 5=最近编辑隐藏（全局）
        hideLog.setObjectId(documentId);
        hideLog.setKbId(kbId);
        hideLog.setCreatedAt(LocalDateTime.now());
        operationLogMapper.insert(hideLog);
    }
}
