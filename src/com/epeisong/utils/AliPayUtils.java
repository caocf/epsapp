package com.epeisong.utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

 

//支付宝支付常用方法
public class AliPayUtils {
	private static final String ALGORITHM = "RSA";

	private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	private static final String DEFAULT_CHARSET = "UTF-8";

	//商户PID
	public static final String PARTNER  = "2088912258924679";
	//商户收款账号
	public static final String SELLER = "18112950006";
	//商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJ" +
			"dAgEAAoGBALmZbuxf8arAzUR7"+
			"Qj3JGbZdjak4v+ilShZXu1oqS5E1ou6v0ShUUDjRP3vte5ZOVfRTpyQgEh153JhP" +
			"qYyjl5aap/9T3Ptw79wFUT4rpGZf4q43CwngAH4KNrBlJpuUYVLXNBQIYImVi0GI" +
			"zM/6Npo+WFqcVjLem92uvyKtZgr9AgMBAAECgYBvqjaolJkLyPA3EYmrLsD/jY3b" +
			"kD82M6w9gQ9Bkkzg0spynOlUQFX7uTle0GUY64yfcW0QORNtFJJ6OQOjkBF1DgJd" +
			"NJ33JBAr28epdR3w6Ir0VMG42jiT+o59nJlXglLShxMOOIJDbx834PX7NcHBN8CX" +
			"1wo4paSGrSe3aUj/aQJBAOvBRKjDidPtk+UydzyF2nHjhcpnE1EaTG9lBcAp+9w/" +
			"y50LoBhGjJrFcl5dEKNfdkWBXZRTNNx0KwSCApuRbVMCQQDJiZEO1HFhIFsIMSFA" +
			"v0Z04tubVZaFb9Ik9rmLUnryo69JZGV5s2gi1xyezy+zUEWHUBbpsPRtJOdwMW9g" +
			"MUxvAkEAlqPDz2JCa1xfKBB7B/0ve546Gv6J5US8mmEer48kE7Pf0tVe0qQhq7OG" +
			"RK2KuBmDZR4oQWJ5YHQKGLNKH0VpaQJASnMpRQMxxyF+v232zMZcLr3HRoC9lBmU" +
			"1dbXACK+DHhvPnt6CRU/eO9iQUBkbKdQqAXsEPQvt0oUvRtl+3D6vQJBALRSDEqu" +
			"9oEcKeBeIt4QulxL6X+OxCJkGxvR1g+W1gForeDeyqPBsqqE2kLla+tLe4u1MjRX" +
			"688eAa4tS6gdXtk=";
	//支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2" +
			"W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/Vs" +
			"W8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
    //public static String NOTIFY_URL = "https://transaction.epeisong.com:9998/transaction-ws/notify_url";
	
	//服务器异步通知订单付款路径
 	public static final String ORDER_NOTIFY_URL =  "http://notify.epeisong.com/alipay/notify/order/async";
    
 	//服务器异步通知充值付款路径
 	public static final String WALLET_NOTIFY_URL =  "http://notify.epeisong.com/alipay/notify/deposit/async";
 	
 	
 	//服务器异步通知保证金付款路径
 	public static final String BOND_NOTIFY_URL =  "http://notify.epeisong.com/alipay/notify/bond/async";
	
	/**
	 * create the order info. 创建订单信息
	 * orderId:订单唯一号；subject：订单标题(不超过128汉字)；body:详情；price：金额
	 */
	public  static String getOrderInfo(String orderId,String subject, String body,String price ) {
		return getCommInfo( orderId,  subject,  body,price,ORDER_NOTIFY_URL);
	}
	/**
	 * create the order info. 创建充值付款信息
	 */
	public  static String getWalletInfo(String orderId,String subject, String body, 
			String price)  {
		return getCommInfo( orderId,  subject,  body,price,WALLET_NOTIFY_URL);
	}
 	
	/**
	 * create the order info. 创建保证金付款信息
	 */
	public  static String getBondInfo(String orderId,String subject, String body, 
			String price)  {
		return getCommInfo( orderId,  subject,  body,price,BOND_NOTIFY_URL);
	}
 	
	/**
	 * create the order info. 创建通用付款信息
	 */
	public  static String getCommInfo(String orderId,String subject, String body, 
			String price,String norify_url) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderId + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" +  norify_url//"http://notify.msp.hk/notify.htm"//
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";
		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";
		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"2m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		 //orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}
	
	
	 
	
   //对订单信息进行签名
	public static String sign(String content ) throws Exception{
		 
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
					Base64.decode(RSA_PRIVATE));
			KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(content.getBytes(DEFAULT_CHARSET));

			byte[] signed = signature.sign();

			return Base64.encode(signed);
		 
 
	}
	
	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public static String getSignType() {
		return "sign_type=\"RSA\"";
	}
	
	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public  static String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);
		StringBuilder sb = new StringBuilder();

	//	Random r = new Random();
		sb.append(key).append(String.valueOf((int)(Math.random()*10000) ));
 
		return sb.toString();
	}

}
