package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.service.KnowledgeBaseCategoryService;
import com.serching.fulltextsearching.vo.DocumentGroupVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/category")
@Validated
@Api(tags = "知识库分类管理接口") // Swagger文档标签
public class KnowledgeBaseCategoryController {
    @Autowired
    private KnowledgeBaseCategoryService knowledgeBaseCategoryService;

    @PostMapping("/create")
    @ApiOperation("创建知识库分类") // 接口描述
    public Result<Void> createCategory(
            @ApiParam(value = "分类名称", required = true) // 参数说明
            @RequestParam @NotBlank(message = "分类名称不能为空") String name,
            @ApiParam(value = "知识库ID", required = true)
            @RequestParam @NotNull(message = "知识库ID不能为空") Long knowledgeBaseId){
        try {
            knowledgeBaseCategoryService.createCategory(name, knowledgeBaseId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除知识库分类")
    public Result<Void> deleteCategory(
            @ApiParam(value = "分类ID", required = true)
            @PathVariable @NotNull(message = "分类ID不能为空") Long id){
        try {
            knowledgeBaseCategoryService.removeById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update/{id}")
    @ApiOperation("更新知识库分类")
    public Result<Void> updateCategory(
            @ApiParam(value = "分类ID", required = true)
            @PathVariable @NotNull(message = "分类ID不能为空") Long id,
            @ApiParam(value = "新分类名称", required = true)
            @RequestParam @NotBlank(message = "分类名称不能为空") String name){
        try {
            knowledgeBaseCategoryService.updateCategory(id, name);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    @ApiOperation("获取知识库分类列表")
    public Result<DocumentGroupVo> getCategoryList(
            @ApiParam(value = "知识库ID", required = true)
            @RequestParam @NotNull(message = "知识库ID不能为空") Long knowledgeBaseId,
            @ApiParam(value = "页码(默认1)", example = "1")
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于1") Integer pageNum,
            @ApiParam(value = "每页条数(默认20)", example = "20")
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "每页条数不能小于1") Integer pageSize){
        try {
            return Result.success(knowledgeBaseCategoryService.getCategoryList(knowledgeBaseId, pageNum, pageSize));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

