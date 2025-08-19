package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.common.PageResult;
import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.entity.KnowledgeBase;
import com.serching.fulltextsearching.exception.BusinessException;
import com.serching.fulltextsearching.service.KnowledgeBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/knowledge")
@Validated
@Api(tags = "知识库管理接口")
public class KnowledgeBaseController {

    @Autowired
    private KnowledgeBaseService knowledgeService;

    @PostMapping("/create")
    @ApiOperation("创建知识库")
    public Result<Void> createKnowledge(
            @ApiParam(value = "知识库名称", required = true)
            @RequestParam @NotBlank(message = "知识库名称不能为空") String name,
            @ApiParam(value = "封面图片路径(可选)")
            @RequestParam(required = false) String coverImagePath,
            @ApiParam(value = "权限类型(1-私有,2-公开)", required = true, example = "1")
            @RequestParam(defaultValue = "1") @NotNull(message = "权限类型不能为空")
            @Min(value = 1, message = "权限类型只能是1或2") @Max(value = 2, message = "权限类型只能是1或2") Integer scopeType,
            @ApiParam(value = "描述信息(可选)")
            @RequestParam(required = false) String descriptionInfo){
        try {
            knowledgeService.createKnowledge(name, coverImagePath, scopeType, descriptionInfo);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除知识库")
    public Result<Void> deleteKnowledge(
            @ApiParam(value = "知识库ID", required = true)
            @PathVariable @NotNull(message = "知识库ID不能为空") Long id){
        try {
            knowledgeService.deleteKnowledge(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("获取知识库详情")
    public Result<KnowledgeBase> getKnowledgeDetail(
            @ApiParam(value = "知识库ID", required = true)
            @PathVariable @NotNull(message = "知识库ID不能为空") Long id){
        try {
            KnowledgeBase knowledgeBase = knowledgeService.getKnowledgeDetail(id);
            return Result.success(knowledgeBase);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update/{id}")
    @ApiOperation("更新知识库")
    public Result<Void> updateKnowledge(
            @ApiParam(value = "知识库ID", required = true)
            @PathVariable @NotBlank(message = "知识库ID不能为空") String id,
            @ApiParam(value = "新知识库名称", required = true)
            @RequestParam @NotBlank(message = "知识库名称不能为空") String name,
            @ApiParam(value = "封面图片路径(可选)")
            @RequestParam(required = false) String coverImagePath,
            @ApiParam(value = "权限类型(1-私有,2-公开)", required = true)
            @RequestParam(defaultValue = "1") @NotNull(message = "权限类型不能为空")
            @Min(1) @Max(2) Integer scopeType,
            @ApiParam(value = "描述信息(可选)")
            @RequestParam(required = false) String descriptionInfo){
        try {
            knowledgeService.updateKnowledge(id, name, coverImagePath, scopeType, descriptionInfo);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    @ApiOperation("获取知识库列表")
    public Result<PageResult<KnowledgeBase>> getKnowledgeList(
            @ApiParam(value = "页码(默认1)", example = "1")
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码不能小于1") Integer page,
            @ApiParam(value = "每页条数(默认10)", example = "10")
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "每页条数不能小于1") Integer size
    ){
        try {
            PageResult<KnowledgeBase> result = knowledgeService.getKnowledgeList(page, size);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/setPermission")
    @ApiOperation("设置知识库权限")
    public Result<Void> setPermission(
            @ApiParam(value = "知识库ID", required = true)
            @RequestParam @NotBlank(message = "知识库ID不能为空") String id,
            @ApiParam(value = "权限类型(1-私有,2-公开)", required = true)
            @RequestParam @NotNull(message = "权限类型不能为空")
            @Min(value = 1, message = "权限类型只能是1或2") @Max(value = 2, message = "权限类型只能是1或2") Integer scopeType){
        try {
            knowledgeService.setPermission(id, scopeType);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/getPermission")
    @ApiOperation("获取知识库权限")
    public Result<Integer> getPermission(
            @ApiParam(value = "知识库ID", required = true)
            @RequestParam @NotBlank(message = "知识库ID不能为空") String knowledgeBaseId){
        try {
            Integer permission = knowledgeService.getPermission(knowledgeBaseId);
            return Result.success(permission);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}/dict")
    @ApiOperation("获取知识库数据字典")
    public Result<Map<String, Object>> getDict(
            @ApiParam(value = "知识库ID", required = true)
            @PathVariable Long id) {
        try {
            Map<String, Object> dict = knowledgeService.getDict(id);
            return Result.success(dict);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/dict")
    @ApiOperation("更新知识库数据字典")
    public Result<Void> updateDict(
            @ApiParam(value = "知识库ID", required = true)
            @PathVariable Long id,
            @ApiParam(value = "数据字典(JSON)", required = true)
            @RequestBody Map<String, Object> dict) {
        try {
            knowledgeService.updateDict(id, dict);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    //上传封面图片接口
    @PostMapping(value = "/cover/upload",consumes = "multipart/form-data")
    @ApiOperation("上传知识库封面图片")
    public Result<Map<String,String>> uploadCoverImage(
            @ApiParam(value = "封面图片文件", required = true)
            @RequestPart("file")MultipartFile file){
        try{
            //1.基础验证
            if (file.isEmpty()){
                return Result.error("上传文件不能为空");
            }

            if (!file.getContentType().startsWith("image/")){
                return Result.error("请上传图片类型文件");
            }
            if (file.getSize() > 1024 * 1024){
                return Result.error("图片不能超过1MB");
            }

            //2.调用业务层处理业务逻辑
            String imagePath = knowledgeService.uploadCoverImage(file);

            //3.构建响应
            Map<String,String> result = new HashMap<>(1);
            result.put("coverImagePath",imagePath);
            return Result.success(result);

        }catch (BusinessException e){
            return Result.error(e.getMessage());
        }catch (Exception e){
            return Result.error("系统异常，请稍后重试");
        }
    }



}
