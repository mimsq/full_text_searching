package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.service.KnowledgeBaseCategoryService;
import com.serching.fulltextsearching.vo.DocumentGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class KnowledgeBaseCategoryController {
    @Autowired
    private KnowledgeBaseCategoryService knowledgeBaseCategoryService;

    @PostMapping("/create")
    public Result<Void> createCategory(@RequestParam String name, @RequestParam Long knowledgeBaseId){
        try {
            knowledgeBaseCategoryService.createCategory(name, knowledgeBaseId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id){
        try {
            knowledgeBaseCategoryService.removeById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestParam String name){
        try {
            knowledgeBaseCategoryService.updateCategory(id, name);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<DocumentGroupVo>> getCategoryList(@RequestParam Long knowledgeBaseId, @RequestParam Integer pageNum, @RequestParam Integer pageSize){
        try {
            List<DocumentGroupVo> categories = knowledgeBaseCategoryService.getCategoryList(knowledgeBaseId, pageNum, pageSize);
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

