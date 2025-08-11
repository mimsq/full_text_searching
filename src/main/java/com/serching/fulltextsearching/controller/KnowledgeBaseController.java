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
    private KnowledgeBaseService knowledgeService;

    @PostMapping("/create")
    public Result createKnowledge(@RequestParam String name, @RequestParam(required = false) String coverImagePath, @RequestParam(defaultValue = "1") Integer scopeType, @RequestParam(required = false) String descriptionInfo){
        try {
            knowledgeService.createKnowledge(name, coverImagePath, scopeType, descriptionInfo);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    public Result deleteKnowledge(@RequestParam String id){
        try {
            knowledgeService.deleteKnowledge(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public Result<TKnowledgeBase> getKnowledgeDetail(@PathVariable String id){
        try {
            TKnowledgeBase tKnowledgeBase = knowledgeService.getKnowledgeDetail(id);
            return Result.success(tKnowledgeBase);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update/{id}")
    public Result updateKnowledge(@PathVariable String id, @RequestParam String name,@RequestParam(required = false) String coverImagePath, @RequestParam(defaultValue = "1") Integer scopeType, @RequestParam(required = false) String descriptionInfo){
        try {
            knowledgeService.updateKnowledge(id, name, coverImagePath, scopeType, descriptionInfo);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<PageResult<TKnowledgeBase>> getKnowledgeList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        try {
            PageResult<TKnowledgeBase> result = knowledgeService.getKnowledgeList(page, size);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

}
