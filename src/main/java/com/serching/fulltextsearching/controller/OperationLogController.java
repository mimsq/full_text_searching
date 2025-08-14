package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.service.OperationLogService;
import com.serching.fulltextsearching.vo.OperationLogVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/operationLogs")
@Validated
@Api(tags = "操作日志管理接口")
public class OperationLogController {
    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/list")
    @ApiOperation("获取知识库操作日志列表")
    public Result<List<OperationLogVo>> getOperationLogList(
            @ApiParam(value = "知识库ID", required = true)
            @RequestParam @NotNull(message = "知识库ID不能为空") Long kbId,
            @ApiParam(value = "页码(默认1)", example = "1")
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @ApiParam(value = "每页条数(默认10)", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) Integer pageSize) {
        List<OperationLogVo> operationLogList = operationLogService.getOperationLogList(kbId, pageNum, pageSize);
        return Result.success(operationLogList);
    }
}
