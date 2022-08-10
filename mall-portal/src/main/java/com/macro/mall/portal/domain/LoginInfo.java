package com.macro.mall.portal.domain;

import lombok.Data;

@Data
public class LoginInfo {
    private String code;
    private String encryptedData;
    private String iv;
}
