package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CmsSubject;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductCategory;
import com.macro.mall.portal.domain.HomeContentResult;
import com.macro.mall.portal.service.HomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页内容管理Controller
 */
@RestController
@Api(tags = "HomeController")
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private HomeService homeService;

    @ApiOperation("首页内容信息展示")
    @GetMapping("/content")
    public CommonResult<HomeContentResult> content() {
        HomeContentResult contentResult = homeService.content();
        return CommonResult.success(contentResult);
    }

    @ApiOperation("分页获取推荐商品")
    @GetMapping("/recommendProductList")
    public CommonResult<List<PmsProduct>> recommendProductList(@RequestParam(defaultValue = "4") Integer pageSize,
                                                               @RequestParam(defaultValue = "1") Integer pageNum) {
        List<PmsProduct> productList = homeService.recommendProductList(pageSize, pageNum);
        return CommonResult.success(productList);
    }

    @ApiOperation("获取首页商品分类")
    @GetMapping("/productCateList/{parentId}")
    public CommonResult<List<PmsProductCategory>> getProductCateList(@PathVariable Long parentId) {
        List<PmsProductCategory> productCategoryList = homeService.getProductCateList(parentId);
        return CommonResult.success(productCategoryList);
    }

    @ApiOperation("根据分类获取专题")
    @GetMapping("/subjectList")
    public CommonResult<List<CmsSubject>> getSubjectList(@RequestParam(required = false) Long cateId,
                                                         @RequestParam(defaultValue = "4") Integer pageSize,
                                                         @RequestParam(defaultValue = "1") Integer pageNum) {
        List<CmsSubject> subjectList = homeService.getSubjectList(cateId, pageSize, pageNum);
        return CommonResult.success(subjectList);
    }

    @ApiOperation("分页获取人气推荐商品")
    @GetMapping("/hotProductList")
    public CommonResult<List<PmsProduct>> hotProductList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                         @RequestParam(defaultValue = "6") Integer pageSize) {
        List<PmsProduct> productList = homeService.hotProductList(pageNum, pageSize);
        return CommonResult.success(productList);
    }

    @ApiOperation("分页获取新品推荐商品")
    @GetMapping("/newProductList")
    public CommonResult<List<PmsProduct>> newProductList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                         @RequestParam(defaultValue = "6") Integer pageSize) {
        List<PmsProduct> productList = homeService.newProductList(pageNum, pageSize);
        return CommonResult.success(productList);
    }
}
