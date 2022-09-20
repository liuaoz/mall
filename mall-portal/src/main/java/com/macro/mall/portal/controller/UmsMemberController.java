package com.macro.mall.portal.controller;

import cn.hutool.Hutool;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.util.SafeUtil;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.LoginInfo;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.service.WxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 会员登录注册管理Controller
 */
@RestController
@Api(tags = "UmsMemberController")
@RequestMapping("/sso")
public class UmsMemberController {
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private WxService wxService;

    @ApiOperation("登录/注册,微信一键登录")
    @PostMapping("loginByCode")
    public CommonResult login(@RequestBody LoginInfo req) {
        String token = memberService.login(req.getCode(), req.getEncryptedData(), req.getIv());
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        if (Objects.nonNull(token)) {
            var member = memberService.getCurrentMember();
            tokenMap.put("username", member.getUsername());
        }
        return CommonResult.success(tokenMap);
    }

    @ApiOperation("会员注册")
    @PostMapping("/register")
    public CommonResult register(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String telephone,
                                 @RequestParam String authCode) {
        memberService.register(username, password, telephone, authCode);
        return CommonResult.success(null, "注册成功");
    }

    @ApiOperation("会员登录")
    @PostMapping("/login")
    public CommonResult<Map<String, String>> login(@RequestParam String username,
                                                   @RequestParam String password) {
        String token = memberService.login(username, password);
        if (token == null) {
            return CommonResult.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation("获取会员信息")
    @GetMapping("/info")
    public CommonResult<UmsMember> info(Principal principal) {
        if (principal == null) {
            return CommonResult.unauthorized(null);
        }
        UmsMember member = memberService.getCurrentMember();
        return CommonResult.success(member);
    }

    @ApiOperation("获取验证码")
    @GetMapping("/getAuthCode")
    public CommonResult<String> getAuthCode(@RequestParam String telephone) {
        String authCode = memberService.generateAuthCode(telephone);
        return CommonResult.success(authCode, "获取验证码成功");
    }

    @ApiOperation("会员修改密码")
    @PostMapping("/updatePassword")
    public CommonResult updatePassword(@RequestParam String telephone,
                                       @RequestParam String password,
                                       @RequestParam String authCode) {
        memberService.updatePassword(telephone, password, authCode);
        return CommonResult.success(null, "密码修改成功");
    }


    @ApiOperation(value = "刷新token")
    @GetMapping("/refreshToken")
    public CommonResult<Map<String, String>> refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = memberService.refreshToken(token);
        if (refreshToken == null) {
            return CommonResult.failed("token已经过期！");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }
}
