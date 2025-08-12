package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.common.PageResult;
import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.entity.TKnowledgeBase;
import com.serching.fulltextsearching.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/base")
public class KnowledgeBaseController {

    @Autowired
    private KnowledgeBaseService difyKnowledgeService;

    @PostMapping("/create")
    public Result createKnowledge(@RequestParam String name, @RequestParam(required = false) String coverImagePath, @RequestParam(defaultValue = "1") Integer scopeType, @RequestParam(required = false) String descriptionInfo){
        try {
            difyKnowledgeService.createKnowledge(name, coverImagePath, scopeType, descriptionInfo);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    public Result deleteKnowledge(@RequestParam String id){
        try {
            difyKnowledgeService.deleteKnowledge(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<TKnowledgeBase> getKnowledgeDetail(@PathVariable Long id){
        try {
            TKnowledgeBase tKnowledgeBase = difyKnowledgeService.getKnowledgeDetail(id);
            return Result.success(tKnowledgeBase);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update/{id}")
    public Result updateKnowledge(@PathVariable String id, @RequestParam String name,@RequestParam(required = false) String coverImagePath, @RequestParam(defaultValue = "1") Integer scopeType, @RequestParam(required = false) String descriptionInfo){
        try {
            difyKnowledgeService.updateKnowledge(id, name, coverImagePath, scopeType, descriptionInfo);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<PageResult<TKnowledgeBase>> getKnowledgeList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortBy
    ){
        try {
            PageResult<TKnowledgeBase> result = difyKnowledgeService.getKnowledgeList(page, size, keyword, sortBy);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


}
