package com.macro.mall.portal.domain.wx;

import lombok.Data;

@Data
public class SessionInfo {
    private String openid;
    private String session_key;
    private String unionid;
    private Integer errcode;
    private String errmsg;
}
