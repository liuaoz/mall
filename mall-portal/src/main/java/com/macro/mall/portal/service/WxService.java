package com.macro.mall.portal.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.macro.mall.portal.config.WxConfig;
import com.macro.mall.portal.domain.wx.SessionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WxService {

    @Autowired
    private WxConfig wxConfig;

    public static final String BASE_URL
            = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    public SessionInfo code2Session(String code) {
        String url = String.format(BASE_URL, wxConfig.getAppid(), wxConfig.getAppSecret(), code);
        String resp = HttpUtil.get(url);
        return JSONUtil.toBean(resp, SessionInfo.class);
    }
}
