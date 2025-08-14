package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.dto.MemberKbPermissionDto;
import com.serching.fulltextsearching.entity.TKnowledgeBaseMember;
import com.serching.fulltextsearching.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/member")
@Validated
@Api(tags = "知识库成员管理接口")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/batch")
    @ApiOperation("批量添加知识库成员")
    public Result<Void> saveBatch(
            @ApiParam(value = "成员权限信息", required = true)
            @RequestBody @Valid MemberKbPermissionDto dto) {
        memberService.saveBatch(dto);
        return Result.success();
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除知识库成员")
    public Result<Void> delete(
            @ApiParam(value = "成员权限信息", required = true)
            @RequestBody @Valid MemberKbPermissionDto dto) {
        memberService.delete(dto);
        return Result.success();
    }

    @PostMapping("/update")
    @ApiOperation("更新成员权限")
    public Result<Void> update(
            @ApiParam(value = "成员权限信息", required = true)
            @RequestBody @Valid MemberKbPermissionDto dto) {
        memberService.update(dto);
        return Result.success();
    }

    @GetMapping("/list/{kbId}")
    @ApiOperation("获取知识库成员列表")
    public Result<List<TKnowledgeBaseMember>> getMemberList(
            @ApiParam(value = "知识库ID", required = true)
            @PathVariable @NotNull(message = "知识库ID不能为空") Long kbId,
            @ApiParam(value = "页码(默认1)", example = "1")
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @ApiParam(value = "每页条数(默认10)", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) Integer pageSize) {
        List<TKnowledgeBaseMember> memberList = memberService.getMemberList(kbId, pageNum, pageSize);
        return Result.success(memberList);
    }
}
