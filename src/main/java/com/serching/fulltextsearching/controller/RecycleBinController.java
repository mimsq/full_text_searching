package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.common.PageResult;
import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.entity.KnowledgeDocument;
import com.serching.fulltextsearching.service.KnowledgeDocumentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 回收站控制器
 */
@RestController
@RequestMapping("/api/recycle-bin")
@Api(tags = "回收站管理")
public class RecycleBinController {

    private static final Logger logger = LoggerFactory.getLogger(RecycleBinController.class);

    @Autowired
    private KnowledgeDocumentService knowledgeDocumentService;

    @PostMapping("/move/{documentId}")
    @ApiOperation(value = "将文档移动到回收站",notes = "将指定文档移动到回收站，用户可以从回收站恢复文档")
    public Result<Boolean> moveToRecycleBin(
            @ApiParam(value = "文档ID", required = true) @PathVariable Long documentId,
            @ApiParam(value = "操作用户ID", required = true) @RequestParam Long userId) {
        
        logger.info("接收到移动文档到回收站请求，文档ID: {}, 用户ID: {}", documentId, userId);
        
        try {
            boolean result = knowledgeDocumentService.moveToRecycleBin(documentId, userId);
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("移动文档到回收站失败");
            }
        } catch (Exception e) {
            logger.error("移动文档到回收站异常，文档ID: {}", documentId, e);
            return Result.error("移动文档到回收站失败: " + e.getMessage());
        }
    }

    @PostMapping("/restore/{documentId}")
    @ApiOperation("从回收站恢复文档")
    public Result<Boolean> restoreFromRecycleBin(
            @ApiParam(value = "文档ID", required = true) @PathVariable Long documentId,
            @ApiParam(value = "操作用户ID", required = true) @RequestParam Long userId) {
        
        logger.info("接收到从回收站恢复文档请求，文档ID: {}, 用户ID: {}", documentId, userId);
        
        try {
            boolean result = knowledgeDocumentService.restoreFromRecycleBin(documentId, userId);
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("从回收站恢复文档失败");
            }
        } catch (Exception e) {
            logger.error("从回收站恢复文档异常，文档ID: {}", documentId, e);
            return Result.error("从回收站恢复文档失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    @ApiOperation("获取回收站列表")
    public Result<PageResult<KnowledgeDocument>> getRecycleBinList(
            @ApiParam(value = "知识库ID", required = true) @RequestParam Long kbId,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") int page,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") int size) {
        
        logger.info("接收到获取回收站列表请求，知识库ID: {}, 页码: {}, 每页大小: {}", kbId, page, size);
        
        try {
            PageResult<KnowledgeDocument> result = knowledgeDocumentService.getRecycleBinList(kbId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("获取回收站列表异常，知识库ID: {}", kbId, e);
            return Result.error("获取回收站列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    @ApiOperation("测试回收站功能")
    public Result<String> testRecycleBin() {
        return Result.success("回收站功能已实现，包含以下接口：\n" +
                "1. POST /api/recycle-bin/move/{documentId} - 将文档移动到回收站\n" +
                "2. POST /api/recycle-bin/restore/{documentId} - 从回收站恢复文档\n" +
                "3. GET /api/recycle-bin/list - 获取回收站列表");
    }
}
