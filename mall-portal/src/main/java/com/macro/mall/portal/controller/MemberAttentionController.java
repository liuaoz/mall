package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.domain.MemberBrandAttention;
import com.macro.mall.portal.service.MemberAttentionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 会员关注品牌管理Controller
 */
@RestController
@Api(tags = "MemberAttentionController")
@RequestMapping("/member/brand/attention")
public class MemberAttentionController {
    @Autowired
    private MemberAttentionService memberAttentionService;

    @ApiOperation("添加品牌关注")
    @PostMapping
    public CommonResult<Integer> add(@RequestBody MemberBrandAttention memberBrandAttention) {
        int count = memberAttentionService.add(memberBrandAttention);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("取消品牌关注")
    @DeleteMapping("/{brandId}")
    public CommonResult<Integer> delete(@PathVariable Long brandId) {
        int count = memberAttentionService.delete(brandId);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("显示当前用户品牌关注列表")
    @GetMapping("/list")
    public CommonResult<CommonPage<MemberBrandAttention>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                                               @RequestParam(defaultValue = "5") Integer pageSize) {
        Page<MemberBrandAttention> page = memberAttentionService.list(pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(page));
    }

    @ApiOperation("显示品牌关注详情")
    @GetMapping("/{brandId}")
    public CommonResult<MemberBrandAttention> detail(@PathVariable Long brandId) {
        MemberBrandAttention memberBrandAttention = memberAttentionService.detail(brandId);
        return CommonResult.success(memberBrandAttention);
    }

    @ApiOperation("清空当前用户品牌关注列表")
    @DeleteMapping("/clear")
    public CommonResult clear() {
        memberAttentionService.clear();
        return CommonResult.success(null);
    }
}
