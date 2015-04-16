/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.payment.net.NetWalletAccount.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年10月31日下午1:15:02
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.payment.net;

import com.epeisong.payment.net.utils.PaymentUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NetWalletAccount {

    /**
     * 登录钱包
     * 
     * @param uname
     * @param pwd
     * @return
     * @throws Exception
     */
    public int login(String uname,String pwd) throws Exception{
        
        String url = "AccountService/login/"+uname+"/"+pwd+"/"+PaymentUtils.WALLET_LOG_TYPE;
        
        String json =PaymentUtils.getJosnString(url);
        
        Gson gson = new Gson();
        
        int walletId = gson.fromJson(json, new TypeToken<Integer>() {}.getType());
        
        return walletId;
    }
    
    /**
     * 登录钱包
     * 
     * @param uname
     * @param pwd
     * @return
     * @throws Exception
     */
    public int signUp(String uname,String pwd) throws Exception{
        
        String url = "AccountService/signUp/"+uname+"/"+pwd+"/"+PaymentUtils.WALLET_LOG_TYPE;
        
        String json =PaymentUtils.getJosnString(url);
        
        Gson gson = new Gson();
        
        int walletId = gson.fromJson(json, new TypeToken<Integer>() {}.getType());
        
        return walletId;
    }
}

