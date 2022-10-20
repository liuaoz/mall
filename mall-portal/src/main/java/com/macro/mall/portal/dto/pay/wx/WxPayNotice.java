package com.macro.mall.portal.dto.pay.wx;

public enum WxPayNotice {

    SUCCESS,
    FAIL;

    public static String noticeSuccess() {
        return "<xml>"
                + "<return_code>SUCCESS</return_code>"
                + "<return_msg>OK</return_msg>"
                + "</xml>";
    }

    public static String noticeFail() {
        return "<xml>"
                + "<return_code>FAIL</return_code>"
                + "<return_msg>支付通知失败</return_msg>"
                + "</xml>";
    }
}
