package com.yl.payinterface.core.handle.impl.quick.pengrui310102.bean;

import net.sf.json.JSONObject;

/**
 * 全时优服 消费短信Bean
 * @author 聚合支付有限公司
 * @version V1.0.0
 * @since 2017/12/28
 */
public class PengRui310102SmsBean {

    /** 请求头 */
    private JSONObject head;

    /** TOKEN */
    private String token;

    /** 订单号 */
    private String consumeOrderId;

    /** 渠道商户号 */
    private String platMerchantCode;

    /** 商户费率编号 */
    private String merchantRateCode;

    /** 支付金额 */
    private String payAmount;

    /** 银行卡户名 */
    private String accountName;

    /** 银行卡卡号 */
    private String cardNo;

    /** 银行卡类型 */
    private String cardType;

    /** 证件类型 */
    private String certType;

    /** 身份证号 */
    private String certNo;

    /** 银行预留手机号 */
    private String phoneno;

    /** 银行代码 */
    private String bankCode;

    /** 银行代号 */
    private String bankAbbr;

    public JSONObject getHead() {
        return head;
    }

    public void setHead(JSONObject head) {
        this.head = head;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getConsumeOrderId() {
        return consumeOrderId;
    }

    public void setConsumeOrderId(String consumeOrderId) {
        this.consumeOrderId = consumeOrderId;
    }

    public String getPlatMerchantCode() {
        return platMerchantCode;
    }

    public void setPlatMerchantCode(String platMerchantCode) {
        this.platMerchantCode = platMerchantCode;
    }

    public String getMerchantRateCode() {
        return merchantRateCode;
    }

    public void setMerchantRateCode(String merchantRateCode) {
        this.merchantRateCode = merchantRateCode;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankAbbr() {
        return bankAbbr;
    }

    public void setBankAbbr(String bankAbbr) {
        this.bankAbbr = bankAbbr;
    }
}