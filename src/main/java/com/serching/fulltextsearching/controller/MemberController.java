package com.serching.fulltextsearching.controller;

import com.serching.fulltextsearching.common.Result;
import com.serching.fulltextsearching.dto.MemberKbPermissionDto;
import com.serching.fulltextsearching.entity.TKnowledgeBaseMember;
import com.serching.fulltextsearching.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;


    @PostMapping("/batch")
    public Result<Void> saveBatch(@RequestBody MemberKbPermissionDto dto) {
        memberService.saveBatch(dto);
        return Result.success();
    }

    //删除某个用户的权限信息
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestBody MemberKbPermissionDto dto) {
        memberService.delete(dto);
        return Result.success();
    }

    //更新某个用户的权限信息
    @PostMapping("/update")
    public Result<Void> update(@RequestBody MemberKbPermissionDto dto) {
        memberService.update(dto);
        return Result.success();
    }

    //获取知识库的成员列表
    //在下面的代码的基础上加上分页查询的功能
    @GetMapping("/list/{kbId}")
    public Result<List<TKnowledgeBaseMember>> getMemberList(@PathVariable Long kbId,
                                                            @RequestParam(defaultValue = "1") Integer pageNum,
                                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<TKnowledgeBaseMember> memberList = memberService.getMemberList(kbId, pageNum, pageSize);
        return Result.success(memberList);
    }
}
