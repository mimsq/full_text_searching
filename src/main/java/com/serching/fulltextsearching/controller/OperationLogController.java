package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.entity.TOperationLog;
import com.serching.fulltextsearching.service.OperationLogService;
import com.serching.fulltextsearching.vo.OperationLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operationLogs")
public class OperationLogController {
    @Autowired
    private OperationLogService operationLogService;
    //查看操作日志列表
    @GetMapping("/list")
    public Result<List<OperationLogVo>> getOperationLogList(@RequestParam Long kbId,
                                                            @RequestParam(defaultValue = "1") Integer pageNum,
                                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<OperationLogVo> operationLogList = operationLogService.getOperationLogList(kbId, pageNum, pageSize);
        return Result.success(operationLogList);
    }

}
