package com.macro.mall.portal.dto.pay;

public class PayOrderDto {
    private String appid;
    private Amount amount;
    private String mchid;
    private String description;
    private String notify_url;
    private Payer payer;
    private String out_trade_no;

    public PayOrderDto() {
        this(new Builder());
    }

    public PayOrderDto(Builder builder){
        this.appid = builder.appid;
        this.amount = builder.amount;
        this.mchid = builder.mchid;
        this.description = builder.description;
        this.notify_url = builder.notify_url;
        this.payer = builder.payer;
        this.out_trade_no = builder.out_trade_no;
    }

    public  Builder newBuilder(){
        return new Builder(this);
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getMchid() {
        return mchid;
    }

    public void setMchid(String mchid) {
        this.mchid = mchid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public Payer getPayer() {
        return payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public static class Builder {
        private String appid;
        private Amount amount;
        private String mchid;
        private String description;
        private String notify_url;
        private Payer payer;
        private String out_trade_no;

        public Builder() {
        }

        public PayOrderDto build(){
            return new PayOrderDto(this);
        }

        public Builder(PayOrderDto payOrderDto) {
            this.appid = payOrderDto.appid;
            this.amount = payOrderDto.amount;
            this.mchid = payOrderDto.mchid;
            this.description = payOrderDto.description;
            this.notify_url = payOrderDto.notify_url;
            this.payer = payOrderDto.payer;
        }

        public Builder buildAppid(String appid){
            this.appid = appid;
            return this;
        }

        public Builder buildAmount(int total,String currency){
            this.amount = new Amount();
            amount.setTotal(total);
            amount.setCurrency(currency);
            return this;
        }

        public Builder buildMchid(String mchid){
            this.mchid = mchid;
            return this;
        }

        public Builder buildDescription(String description){
            this.description = description;
            return this;
        }

        public Builder buildNotifyUrl(String notify_url){
            this.notify_url = notify_url;
            return this;
        }

        public Builder buildPayer(String openid){
            this.payer = new Payer();
            this.payer.setOpenid(openid);
            return this;
        }

        public Builder buildOutTradeNo(String out_trade_no){
            this.out_trade_no = out_trade_no;
            return this;
        }
    }
}

class Payer {
    private String openid;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }
}

class Amount {
    private int total;
    private String currency;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
