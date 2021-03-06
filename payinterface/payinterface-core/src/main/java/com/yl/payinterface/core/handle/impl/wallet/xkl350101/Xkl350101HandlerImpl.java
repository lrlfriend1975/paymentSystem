package com.yl.payinterface.core.handle.impl.wallet.xkl350101;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.lefu.commons.utils.lang.AmountUtils;
import com.lefu.commons.utils.lang.JsonUtils;
import com.yl.payinterface.core.handle.impl.wallet.xkl350101.utils.XklSignUtil;
import com.yl.payinterface.core.handler.WalletPayHandler;
import com.yl.payinterface.core.model.InterfaceRequest;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 新快乐 QQ扫码
 *
 * @author AnLin
 * @version V1.0.0
 * @since 2018/4/8
 */
@Service("walletXkl350101Handler")
public class Xkl350101HandlerImpl implements WalletPayHandler {

    private static final Logger logger = LoggerFactory.getLogger(Xkl350101HandlerImpl.class);

    @Override
    public Map<String, String> pay(Map<String, String> map) {
        Map<String,String> resParams = new HashMap<>();
        Properties properties = JsonUtils.toObject(String.valueOf(map.get("tradeConfigs")), new TypeReference<Properties>(){});
        String interfaceRequestID = map.get("interfaceRequestID");
        String amount = String.valueOf((int) AmountUtils.multiply(Double.valueOf(map.get("amount")), 100));
        String productName = map.get("productName");
        try {
            Map<String,String> pay = new HashMap<>();
            pay.put("merNo", properties.getProperty("merNo"));
            pay.put("orderNo", interfaceRequestID);
            pay.put("transAmt", amount);
            pay.put("commodityName", productName);
            pay.put("notifyUrl", properties.getProperty("notifyUrl"));
            pay.put("sign", XklSignUtil.signData(pay, properties.getProperty("key")));
            Map<String, String> res = XklSignUtil.httpGet(pay, properties.getProperty("url"));
            if("S".equals(res.get("respType"))) {
                resParams.put("code_url", res.get("codeUrl"));
                resParams.put("code_img_url", "https://api.zk1006.cn/qrCode?s=" + res.get("codeUrl"));
                resParams.put("interfaceInfoCode", map.get("interfaceInfoCode"));
                resParams.put("interfaceRequestID", interfaceRequestID);
                resParams.put("gateway", "native");
                resParams.put("merchantNo", properties.getProperty("merNo"));
            }
        } catch (Exception e) {
            logger.error("异常信息:{}", e);
        }
        return resParams;
    }

    @Override
    public Map<String, String> query(Map<String, String> map) {
        return null;
    }

    @Override
    public InterfaceRequest complete(Map<String, Object> map) {
        return null;
    }

    public static void main(String[] args) {
        Map<String,String> resParams = new HashMap<>();
        try {
            Map<String,String> pay = new HashMap<>();
            pay.put("merNo", "888688812147");
            pay.put("orderNo", System.currentTimeMillis()+"JQKA");
            pay.put("transAmt", "1000");
            pay.put("commodityName", "小李飞刀");
            pay.put("province", "110000");
            pay.put("city", "110100");
            pay.put("areaId", "110101");
            pay.put("notifyUrl", "http://www.sunboyang.com:8080/myshop/complete_get");
            pay.put("sign", XklSignUtil.signData(pay, "2d72979be0de4f77a15ca25d17e77ab1"));
            Map<String, String> res = XklSignUtil.httpGet(pay, "http://api.kuailefpay.cn/interface/aliNativePay");
            System.out.println(res);
        } catch (Exception e) {
            logger.error("异常信息:{}", e);
        }
    }
}