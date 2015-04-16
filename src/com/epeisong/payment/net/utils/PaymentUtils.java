/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.payment.net.utils.PaymentUtils.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年10月31日下午1:38:07
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.payment.net.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.epeisong.EpsNetConfig;

public class PaymentUtils {
    public static final String WALLET_TYPE_JSON = "application/json";
    public static final Integer WALLET_LOG_TYPE = 1;
    
    public static String getJosnString(String url) throws Exception{
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(EpsNetConfig.WALLET_SERVE_URL + url);
        request.addHeader("Accept",PaymentUtils.WALLET_TYPE_JSON);
        
        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String json =EntityUtils.toString(entity,"utf-8");
        
        return json;
    }
}

