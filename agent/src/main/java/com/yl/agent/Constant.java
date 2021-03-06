package com.yl.agent;

import java.util.Map;

import com.yl.sms.SmsUtils;
import net.sf.json.JSONObject;

/**
 * 常量
 * 
 * @author 聚合支付有限公司
 * @since 2016年7月13日
 * @version V1.0.0
 */

public class Constant {
	
	public static final String CUSTOMERSERVICE = "agent";
	public static final String BOOLEAN_TRUE = "TRUE";
	public static final String BOOLEAN_FALSE = "FALSE";
	public static final String PROPERTIES_SYS = "system";	
	
	
	
	public static final String SESSION_AUTH = "auth";
	public static final String SESSION_CODE = "code";		
	public static final String SESSION_CODE_STATUS = "codestatus";
	
	//存放数据字典的Cache Region名称和存储Key
	public static final String DICT_REGION = "pos_dict_cache";
	public static final String CACHE_DICTS = "dicts";
	public static final String DEFAULT_REGION = "default";

	//临时
	public static final String NOPERMIT="noPermit";  //返回无操作此权限
	
	public static final String OPERATOR_RESOUSE = "agent.operator.resource";
	
	public static final int OPERATOR_RESOURCE_CACHE_DB = 1;
	public static final int IP_DB=12;
	
	public static final String SMS_AUDIT_TYPE0 = "【千付宝】您的验证码是：[%s]，您正在进行提现操作，请勿泄露。";
	public static final String SMS_OPEN_TYPE1 = "【千付宝】您的验证码是：[%s]，您正在开通提现手机验证码校验功能，请勿泄露，感谢您的使用。";
	public static final String SMS_CLOSE_TYPE2 = "【千付宝】您的验证码是：[%s]，您正在关闭提现手机验证码校验功能，请勿泄露，感谢您的使用。";
	public static final String SMS_ADD_OPER = "【千付宝】尊敬的：[%s]您好，您的登陆密码是：[%s]，请登录pay.feiyijj.com/Agent进行操作!";
	public static final String SMS_AGENT_FORGOTPASSWORD = "【千付宝】尊敬的：[%s]您好，您正在重置密码，您的验证码是[%s]，请勿泄露。";
	public static final String AGENT_RESETPASSWORD = "【千付宝】您登录密码已重置为：[%s]请妥善保管，切勿泄露。";

	//字典管理
	public static final String DICTIONARY = "DICTIONARY.";
	//字典类型
	public static final String DICTIONARY_TYPE = "DICTIONARY_TYPE.";
	//字典
	public static Map<String,JSONObject> DICTS;

    public static void main(String[] args) throws Exception {
        SmsUtils.sendCustom(String.format(Constant.AGENT_RESETPASSWORD, "123123"), "17607114151");
    }
}