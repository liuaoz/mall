package com.macro.mall.portal.service;

import com.macro.mall.common.util.SafeUtil;
import com.macro.mall.common.util.XmlUtil;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.config.WxPayConfig;
import com.macro.mall.portal.dto.pay.UnifiedOrderDto;
import com.macro.mall.portal.dto.pay.UnifiedOrderRespDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * 1. 小程序中的统一下单和jsapi有什么区别？
 */
@Service
public class WxPayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WxPayService.class);
    @Autowired
    private WxPayConfig wxPayConfig;

    @Autowired
    private UmsMemberService memberService;

    private static final String url = "https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi";
    public static final String url_unified_order = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //    private static final String notify_url = "https://www.sunoribt.com/health/pay/" + Const.NOTIFY_URL;
    public static final String spbill_create_ip = "183.195.43.193";


    /**
     * 统一下单
     */
    public UnifiedOrderRespDto unifiedOrder(BigDecimal totalFee, String orderNo) {

        UnifiedOrderRespDto respDto;

        UmsMember currentMember = memberService.getCurrentMember();

        String body = assembleBody(new BigDecimal("100").multiply(totalFee).intValue()
                , orderNo, currentMember.getOpenid());

        LOGGER.info("body={}", body);

        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url_unified_order))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("content-type", "text/xml")
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            if (HttpStatus.valueOf(statusCode).is2xxSuccessful()) {
                LOGGER.info("unified order response: {}", response.body());
                respDto = XmlUtil.convertXmlStrToObject(UnifiedOrderRespDto.class, response.body());
            } else {
                LOGGER.error("unified order response code:" + statusCode);
                return null;
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.error("unified order error.", e);
            return null;
        }
        return respDto;
    }

    private String assembleBody(int totalFee, String orderNo, String userOpenId) {

        String goodDesc = "海鲜";
        String tradeType = "JSAPI";

        String temp = String.join("&",
                "appid=" + wxPayConfig.getAppid(),
                "body=" + goodDesc,
                "mch_id=" + wxPayConfig.getMchId(),
                "nonce_str=" + orderNo,
                "notify_url=" + wxPayConfig.getNotifyUrl(),
                "openid=" + userOpenId,
                "out_trade_no=" + orderNo,
                "spbill_create_ip=" + spbill_create_ip,
                "total_fee=" + totalFee,
                "trade_type=" + tradeType,
                "key=" + wxPayConfig.getKey()
        );


        String sign = SafeUtil.md5(temp).toUpperCase();

        return "<xml>"
                + "<appid>" + wxPayConfig.getAppid() + "</appid>"
                + "<body>" + goodDesc + "</body>"
                + "<mch_id>" + wxPayConfig.getMchId() + "</mch_id>"
                + "<nonce_str>" + orderNo + "</nonce_str>"
                + "<notify_url>" + wxPayConfig.getNotifyUrl() + "</notify_url>"
                + "<openid>" + userOpenId + "</openid>"
                + "<out_trade_no>" + orderNo + "</out_trade_no>"
                + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
                + "<total_fee>" + totalFee + "</total_fee>"
                + "<trade_type>" + tradeType + "</trade_type>"
                + "<sign>" + sign + "</sign>"
                + "</xml>";
    }


}
