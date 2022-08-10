package com.macro.mall.portal.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import java.security.Security;

@Component
public class SecurityConfig {

    static {
        /*
         * 加解密时，使用第三方BC库
         */
        Security.addProvider(new BouncyCastleProvider());
    }
}
