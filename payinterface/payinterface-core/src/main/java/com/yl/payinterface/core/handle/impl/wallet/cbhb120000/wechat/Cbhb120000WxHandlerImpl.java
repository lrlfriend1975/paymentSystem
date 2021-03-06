package com.yl.payinterface.core.handle.impl.wallet.cbhb120000.wechat;

import com.lefu.commons.utils.lang.AmountUtils;
import com.lefu.commons.utils.lang.JsonUtils;
import com.yl.payinterface.core.ExceptionMessages;
import com.yl.payinterface.core.service.InterfaceRequestService;
import com.yl.payinterface.core.enums.BusinessCompleteType;
import com.yl.payinterface.core.enums.InterfaceTradeStatus;
import com.yl.payinterface.core.enums.InterfaceType;
import com.yl.payinterface.core.handle.impl.wallet.cbhb120000.wechat.bean.CommonBean;
import com.yl.payinterface.core.handle.impl.wallet.cbhb120000.wechat.bean.WechatPayBean;
import com.yl.payinterface.core.handle.impl.wallet.cbhb120000.wechat.utils.CommonsUtil;
import com.yl.payinterface.core.handle.impl.wallet.cbhb120000.wechat.utils.WxSign;
import com.yl.payinterface.core.handle.impl.wallet.cbhb120000.wechat.utils.XmlUtils;
import com.yl.payinterface.core.handler.ChannelRecionHandler;
import com.yl.payinterface.core.handler.CloseOrderHandler;
import com.yl.payinterface.core.handler.RefundHandler;
import com.yl.payinterface.core.handler.WalletPayHandler;
import com.yl.payinterface.core.model.InterfaceRequest;
import com.yl.payinterface.core.utils.CodeBuilder;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 渤海银行 微信
 *
 * @author 聚合支付有限公司
 * @since 2017年10月7日
 * @version V1.0.0
 */
@Service("walletCbhbWx120000Handler")
public class Cbhb120000WxHandlerImpl implements WalletPayHandler, CloseOrderHandler, RefundHandler, ChannelRecionHandler {

	private static Logger logger = LoggerFactory.getLogger(Cbhb120000WxHandlerImpl.class);
	// 响应吗
	private static final String RESPCODE_USERPAYING = "USERPAYING";
	private static final String RESPCODE_NOTENOUGH = "NOTENOUGH";
	private static final String RESPCODE_AUTH_CODE_INVALID = "AUTH_CODE_INVALID";

	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.setProperty("pay_url", "https://api.mch.weixin.qq.com/pay/unifiedorder");
		properties.setProperty("query_url", "https://api.mch.weixin.qq.com/pay/orderquery");
		properties.setProperty("appid", "wx8dedd1af92f43abc");
		properties.setProperty("mch_id", "1461587802");
		properties.setProperty("sub_mch_id", "30573312");
		properties.setProperty("key", "8652ZXC358ASD85875Q8W9E25875Y65K");
		properties.setProperty("notify_url", "http://127.0.0.1");
		properties.setProperty("server_ip", "127.0.0.1");
		properties.setProperty("trade_type", "NATIVE");
		Map<String, String> params = new HashMap<>();
		params.put("tradeConfigs", JsonUtils.toJsonString(properties));
		params.put("interfaceType", "WXNATIVE");
		params.put("interfaceInfoCode", "CBHB120000-WX_NATIVE");
		params.put("productName", "测试");
		params.put("interfaceRequestID", CodeBuilder.build("TD", "yyyyMMdd"));
		params.put("amount", "1");
		System.out.println(new Cbhb120000WxHandlerImpl().pay(params));
	}

	@Resource
	private InterfaceRequestService interfaceRequestService;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> pay(Map<String, String> map) {
		// 查询交易配置信息
		Properties properties = JsonUtils.toObject(String.valueOf(map.get("tradeConfigs")), new TypeReference<Properties>(){});
		String url = properties.get("pay_url").toString();
		String key = properties.get("key").toString();

		String interfaceType = map.get("interfaceType");
		// 组装下单报文
		WechatPayBean payBean = new WechatPayBean();
		payBean.setAppid(properties.getProperty("appid"));
		payBean.setAttach(map.get("interfaceInfoCode"));
		payBean.setBody(map.get("productName"));
		payBean.setMch_id(properties.get("mch_id").toString());
		// 获取子商户号
		payBean.setSub_mch_id(properties.getProperty("sub_mch_id"));
		payBean.setNonce_str(CommonsUtil.getNonceStr());
		payBean.setNotify_url(properties.getProperty("notify_url"));
		payBean.setOut_trade_no(map.get("interfaceRequestID"));
		payBean.setSign_type("MD5");
		payBean.setTrade_type(properties.getProperty("trade_type"));
		payBean.setSpbill_create_ip(properties.getProperty("server_ip"));
		payBean.setTotal_fee(String.valueOf((int)AmountUtils.multiply(Double.valueOf(map.get("amount")), 100)));

		// 签名
		payBean.setSign(WxSign.createSign(CommonsUtil.getValue(payBean), key));
		String xml = XmlUtils.parseXML(CommonsUtil.getValue(payBean));
		logger.info("渤海银行 微信下单  接口编号:[{}],订单号:[{}],请求报文:[{}]", map.get("interfaceInfoCode"),map.get("interfaceRequestID"),xml);

		String res;
		Map<String, String> resMap;

		try {
			res = CommonsUtil.sendReq(url, xml, "POST");
			logger.info("渤海银行 微信下单  接口编号:[{}],订单号:[{}],响应报文:[{}]",  map.get("interfaceInfoCode"),map.get("interfaceRequestID"),res);
			resMap = XmlUtils.getMapFromXml(res);
			if (!resMap.get("return_code").equals("SUCCESS")) {
				throw new RuntimeException("return_code : " + resMap.get("return_code"));
			}
			if (!resMap.get("result_code").equals("SUCCESS")) {
				throw new RuntimeException("result_code : " + resMap.get("result_code"));
			}
		} catch (Exception e) {
			logger.error("渤海银行 微信下单异常   接口编号:[{}],订单号:[{}],异常信息：[{}]", map.get("interfaceInfoCode"),map.get("interfaceRequestID"),e);
			throw new RuntimeException(e.getMessage());
		}

		Map<String,String> wxResMap = new HashMap<String, String>();

		if(InterfaceType.WXJSAPI.name().equals(interfaceType)){
			wxResMap.put("pay_info", resMap.get("pay_info"));
			wxResMap.put("gateway", "wxJsapi");
		} else if(InterfaceType.WXNATIVE.name().equals(interfaceType)
				|| InterfaceType.ALIPAY.name().equals(interfaceType)){
			wxResMap.put("code_url", resMap.get("code_url"));
			wxResMap.put("prepay_id", resMap.get("prepay_id"));
			wxResMap.put("gateway", "native");
		} else if(InterfaceType.WXMICROPAY.name().equals(interfaceType)){
			String respCode = "";
			String respMsg = "";
			if (resMap.get("status").equals("0") && resMap.get("result_code").equals("0") && "0".equals(resMap.get("pay_result"))) {
				wxResMap.put("tranStat", "SUCCESS");
				respCode = "000000";
				respMsg = "SUCCESS";
				double amount = AmountUtils.divide(Double.valueOf(resMap.get("total_fee")).doubleValue(), 100.0D);
				wxResMap.put("amount", Double.toString(amount));
				wxResMap.put("channelClearDate", resMap.get("time_end"));
				wxResMap.put("completeTime", resMap.get("time_end"));
				wxResMap.put("interfaceOrderID", resMap.get("transaction_id"));
				wxResMap.put("openId", resMap.get("openid"));

				String bank_type = resMap.get("bank_type");
				// 借贷记标识
				if (bank_type != null && bank_type.contains("CREDIT")) {
					wxResMap.put("payType", "CREDIT_CARD");
				} else if (bank_type != null && bank_type.contains("DEBIT")) {
					wxResMap.put("payType", "DEBIT_CARD");
				} else {
					wxResMap.put("payType", "WALLET");
				}
			} else {
				respCode = resMap.get("err_code");
				if (respCode != null) respCode = respCode.trim();
				respMsg = resMap.get("err_msg");
				if ("Y".equals(resMap.get("need_query")) && !RESPCODE_USERPAYING.equals(respCode) && !RESPCODE_NOTENOUGH.equals(respCode)
						&& !RESPCODE_AUTH_CODE_INVALID.equals(respCode)) {
					respCode = "000001";
				}
				wxResMap.put("tranStat", "UNKNOWN");
			}
			wxResMap.put("responseCode", respCode);
			wxResMap.put("responseMessage", respMsg);
			wxResMap.put("businessCompleteType", BusinessCompleteType.NORMAL.toString());
			wxResMap.put("notifyDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			wxResMap.put("interfaceCode", map.get("interfaceInfoCode"));
		}
		wxResMap.put("interfaceInfoCode", map.get("interfaceInfoCode"));
		wxResMap.put("interfaceRequestID", map.get("interfaceRequestID"));
		return wxResMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> query(Map<String, String> map) {

		// 查询交易配置信息
		Properties properties = (Properties) JsonUtils.toObject((String) map.get("tradeConfigs"), Properties.class);

		logger.info("渤海银行 微信查询  接口编号:[{}],原始订单号:[{}]", map.get("payinterfaceCode"),map.get("originalInterfaceRequestID"));

		String url = properties.get("query_url").toString();
		String key = properties.get("key").toString();

		// 组装查询报文
		CommonBean wxQueryBean = new CommonBean();
		wxQueryBean.setAppid(properties.get("appid").toString());
		wxQueryBean.setMch_id(properties.get("mch_id").toString());
		wxQueryBean.setSub_mch_id(properties.get("sub_mch_id").toString());
		wxQueryBean.setNonce_str(CommonsUtil.getNonceStr());
		wxQueryBean.setOut_trade_no(map.get("originalInterfaceRequestID"));
		wxQueryBean.setSign(WxSign.createSign(CommonsUtil.getValue(wxQueryBean), key));
		String xml = XmlUtils.parseXML(CommonsUtil.getValue(wxQueryBean));
		logger.info("渤海银行 微信查询 接口编号:[{}],原始订单号:[{}],请求报文:[{}]", map.get("payinterfaceCode"), map.get("originalInterfaceRequestID"),xml);

		String res;
		Map<String, String> resMap;
		try {
			res = CommonsUtil.sendReq(url, xml, "POST");
			logger.info("渤海银行 微信查询 接口编号:[{}],原始订单号:[{}],响应报文:[{}]", map.get("payinterfaceCode"), map.get("originalInterfaceRequestID"),res);
			resMap = XmlUtils.getMapFromXml(res);
		} catch (Exception e) {
			logger.error("",e);
			throw new RuntimeException();
		}

		Map<String, String> queryParams = new HashMap<>();
		String responseCode = "";
		String responseMessage = "";

		if ("SUCCESS".equals(resMap.get("return_code")) && "SUCCESS".equals(resMap.get("result_code"))) {
			if ("SUCCESS".equals(resMap.get("trade_state"))) {
				queryParams.put("queryStatus", "SUCCESS");
				responseCode = "00";
				responseMessage = "成功";
			} else {

				// REFUND—转入退款 NOTPAY—未支付 CLOSED—已关闭 REVOKED—已撤销（刷卡支付） USERPAYING--用户支付中 PAYERROR--支付失败(其他原因，如银行返回失败)
				if ("PAYERROR".equals(resMap.get("trade_state"))) {
					responseCode = "01";
					responseMessage = "失败";
					queryParams.put("queryStatus", "FAILED");
					resMap.put("trade_state", "FAILED");
				} else if ("CLOSED".equals(resMap.get("trade_state"))) {
					responseCode = "02";
					responseMessage = "已关闭";
					queryParams.put("queryStatus", "CLOSED");
					resMap.put("trade_state", "CLOSED");
				} else {
					queryParams.put("queryStatus", "UNKNOWN");
				}

			}
		} else {
			queryParams.put("queryStatus", "UNKNOWN");
		}

		// 响应码
		queryParams.put("responseCode", responseCode);
		// 响应描述
		queryParams.put("responseMessage", responseMessage);
		// 支付结果
		queryParams.put("tranStat", resMap.get("trade_state"));
		// 金额
		queryParams.put("amount", resMap.get("cash_fee"));
		// 微信订单号
		queryParams.put("weChatOrderId", resMap.get("transaction_id"));
		// 通知时间
		queryParams.put("notifyDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		// 支付完成时间
		queryParams.put("orderFinishDate", resMap.get("time_end"));
		// 支付完成时间
		queryParams.put("channelClearDate", resMap.get("time_end"));
		// 接口编号
		queryParams.put("interfaceCode", map.get("payinterfaceCode"));
		// 接口请求订单号
		queryParams.put("interfaceRequestId", map.get("originalInterfaceRequestID"));
		// 业务完成方式
		queryParams.put("businessCompleteType", map.get("businessCompleteType"));
		return queryParams;
	}

	@Override
	public InterfaceRequest complete(Map<String, Object> map) {
		// 接口请求订单号
		String interfaceRequestId = map.get("interfaceRequestId").toString();
		// 根据支付流水号查询接口请求信息
		InterfaceRequest interfaceRequest = interfaceRequestService.queryByInterfaceRequestID(interfaceRequestId);
		logger.info("支付接口订单号为:{},完成时参数：{}", interfaceRequestId, map);

		if (interfaceRequest == null) {
			logger.error("支付接口订单号为:{},业务处理实体不存在", interfaceRequestId);
			throw new RuntimeException(ExceptionMessages.BUSINESS_HANDLER_ENTITY_NULL);
		}
		// 检验交易完成是否已处理成功 成功跳过,否则继续
		if ((!"".equals(interfaceRequest.getStatus())) && (InterfaceTradeStatus.SUCCESS.equals(interfaceRequest.getStatus()))) {
			logger.error("支付接口订单号为:{},接口已成功处理", interfaceRequest.getInterfaceRequestID());
			throw new RuntimeException(ExceptionMessages.INTERFACE_REQUEST_NOT_INIT);
		}
		// 交易结果
		String tranStat = map.get("tranStat").toString();
		logger.info("支付接口订单号为:{},钱包支付-支付订单完成时状态：{}", interfaceRequest.getInterfaceRequestID(), tranStat);

		if ("SUCCESS".equals(tranStat)) {
			interfaceRequest.setStatus(InterfaceTradeStatus.SUCCESS);
			// 交易金额
			double amount = AmountUtils.divide(Double.valueOf(map.get("amount").toString()).doubleValue(), 100.0D);
			// 校验订单金额是否与上送的一致
			if (!AmountUtils.eq(amount, interfaceRequest.getAmount())) {
				logger.error("支付接口订单号为:{},通知完成支付金额不一致", interfaceRequest.getInterfaceRequestID());
				throw new RuntimeException(ExceptionMessages.PAY_AMOUNT_NOT_ACCORDANCE);
			}
			interfaceRequest.setAmount(amount);
		} else if ("CLOSED".equals(tranStat)) {
			interfaceRequest.setStatus(InterfaceTradeStatus.CLOSED);
			map.put("responseMessage","已关闭");
		} else if ("FAIL".equals(tranStat)) {
			interfaceRequest.setStatus(InterfaceTradeStatus.FAILED);
			map.put("responseMessage","失败");
		} else {
			interfaceRequest.setStatus(InterfaceTradeStatus.UNKNOWN);
			map.put("responseMessage","未知");
		}
		// 响应码
		interfaceRequest.setResponseCode(map.get("responseCode") == null ? "" : map.get("responseCode").toString());
		// 交易结果描述
		interfaceRequest.setResponseMessage(map.get("responseMessage") == null ? "成功" : map.get("responseMessage").toString());
		// 交易完成时间
		if(map.get("completeTime") != null && !interfaceRequest.getStatus().equals(InterfaceTradeStatus.UNKNOWN)){
			try {
				interfaceRequest.setCompleteTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(map.get("completeTime").toString()));
			} catch (ParseException e) {
				logger.error("渤海银行 微信通知 完成时间参数异常 订单号:[{}],异常信息[{}]", interfaceRequest.getInterfaceRequestID(),e);
				interfaceRequest.setCompleteTime(new Date());
			}
		}
		// 业务处理完成方式
		interfaceRequest.setBusinessCompleteType(BusinessCompleteType.valueOf(map.get("businessCompleteType").toString()));
		// 记录渤海银行 微信订单号
		if (map.get("transaction_id") != null) {
			interfaceRequest.setInterfaceOrderID(map.get("transaction_id").toString());
		}
		return interfaceRequest;
	}

	@Override
	public Map<String, String> closeOrder(Map<String, String> closeOrderParams) {
		// 查询交易配置信息
		Properties properties = (Properties) JsonUtils.toObject((String) closeOrderParams.get("transConfig"), Properties.class);

		String url = properties.get("closeOrder_url").toString();
		String key = properties.get("key").toString();

		// 组装下单报文
		CommonBean closeOrderBean = new CommonBean();
		closeOrderBean.setAppid(properties.get("appid").toString());
		closeOrderBean.setMch_id(properties.get("mch_id").toString());
		closeOrderBean.setSub_mch_id(properties.get("sub_mch_id").toString());
		closeOrderBean.setNonce_str(CommonsUtil.getNonceStr());
		closeOrderBean.setOut_trade_no(closeOrderParams.get("interfaceRequestID"));

		// 签名
		closeOrderBean.setSign(WxSign.createSign(CommonsUtil.getValue(closeOrderBean), key));
		String xml = XmlUtils.parseXML(CommonsUtil.getValue(closeOrderBean));
		logger.info("微信订单关闭 订单号:[{}],请求报文:[{}]", closeOrderParams.get("interfaceRequestID"),xml);

		String res;
		Map<String, String> resMap;

		try {
			res = CommonsUtil.sendReq(url, xml, "POST");
			logger.info("微信订单关闭  订单号:[{}],响应报文:[{}]", closeOrderParams.get("interfaceRequestID"),res);
			resMap = XmlUtils.getMapFromXml(res);
			if (!resMap.get("return_code").equals("SUCCESS")) {
				throw new RuntimeException("return_code : " + resMap.get("return_code"));
			}
			if (!resMap.get("result_code").equals("SUCCESS")) {
				throw new RuntimeException("result_code : " + resMap.get("result_code"));
			}
		} catch (Exception e) {
			logger.error("", e);
			throw new RuntimeException(e.getMessage());
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("closeOrderStatus", "SUCCESS");
		map.put("interfaceRequestID", closeOrderParams.get("interfaceRequestID"));
		return map;
	}

	@Override
	public Map<String, String> refund(Map<String, String> refundParams) {
//		Map<String, String> requestParameters = tradeContext.getRequestParameters();
//		// 查询交易配置信息
//		Properties properties = (Properties) JsonUtils.toObject((String) requestParameters.get("transConfig"), Properties.class);
//		String url = properties.get ("refund_url").toString();
//		String key = properties.get("key").toString();
//		String certFilePath = properties.get("filePath").toString();
//		String certPassword = properties.get("mch_id").toString();
//
//		String appid = properties.get("appid").toString();
//		String mch_id = properties.get("mch_id").toString();
//		String transactionID = "";
//		// 交易订单号
//		String outTradeNo = requestParameters.get("originalInterfaceRequestID");
//		String deviceInfo = "";
//		// 退款订单号
//		String outRefundNo = requestParameters.get("interfaceRefundRequestID");
//		int totalFee = 0;
//		int refundFee = 0;
//		try {
//			totalFee = (int) AmountUtils.multiply(Double.parseDouble(requestParameters.get("tradeAmount")), 100);
//			refundFee = (int) AmountUtils.multiply(Double.parseDouble(requestParameters.get("refundAmount")), 100);
//		} catch (Exception e) {
//			throw new BusinessException(ExceptionMessages.TRADE_PARAMS_ERROR);
//		}
//		String opUserID = mch_id; // 操作人 默认商户编号
//		String refundFeeType = "CNY";
//
//		RefundTencentBean refundTencentData = new RefundTencentBean(appid, mch_id, transactionID, outTradeNo, deviceInfo, outRefundNo, totalFee, refundFee,
//				opUserID, refundFeeType);
//
//		refundTencentData.setSign(WxSign.createSign(CommonsUtil.getValue(refundTencentData), key));
//		String xml = XmlUtils.parseXML(CommonsUtil.getValue(refundTencentData));
//		logger.info("微信退款  退款订单号:[{}],请求报文:[{}]", requestParameters.get("interfaceRefundRequestID"),xml);
//
//		String res;
//		Map<String, String> resMap;
//		try {
//			res = CommonsUtil.sendHttpsReq(certFilePath, certPassword, url, xml, "POST");
//			logger.info("微信退款  退款订单号:[{}],响应报文:[{}]", requestParameters.get("interfaceRefundRequestID"),res);
//			resMap = XmlUtils.getMapFromXml(res);
//
//		} catch (Exception e) {
//			throw new BusinessException(ExceptionMessages.PAY_SUCCESS);
//		}
//
//		Map<String, Object> refundResultParams = new HashMap<String, Object>();
//		// 退款订单号
//		refundResultParams.put("interfaceRefundRequestID", outRefundNo);
//		// 交易订单号
//		refundResultParams.put("originalInterfaceRequestID", outTradeNo);
//		// 退款金额
//		refundResultParams.put("refundAmount", AmountUtils.divide(refundFee, 100));
//		if (resMap != null && "SUCCES".equals(resMap.get("SUCCES")) && "result_code".equals(resMap.get("result_code"))) {
//			refundResultParams.put("interfaceRefundStatus", InterfaceRefundStatus.REFUND_SUCCESS);
//		} else {
//			refundResultParams.put("interfaceRefundStatus", InterfaceRefundStatus.REFUND_FAILED);
//		}
//
//		return refundResultParams;
		return null;
	}

	@Override
	public Map<String, String> refundQuery(Map<String, String> refundParams) {
		return null;
	}

	@Override
	public Map<String, String> recion(Map<String, String> params) {
		return null;
	}
}
