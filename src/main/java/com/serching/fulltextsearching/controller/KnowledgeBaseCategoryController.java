package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.entity.TKnowledgeBaseCategory;
import com.serching.fulltextsearching.service.KnowledgeBaseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
}

