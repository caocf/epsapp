/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.payment.net.NetWallet.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年10月31日下午1:28:35
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.payment.net;

import com.epeisong.model.Wallet;
import com.epeisong.payment.net.utils.PaymentUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NetWallet {

    /**
     * 根据 钱包ID获取钱包
     * 
     * @param walletId
     * @return
     * @throws Exception
     */
    public Wallet getWallet(Integer walletId) throws Exception{
        String url = "WalletService/getWallet/"+walletId;
        
        String json =PaymentUtils.getJosnString(url);
        
        Gson gson = new Gson();
        
        Wallet  wallet = gson.fromJson(json, new TypeToken<Wallet>() {}.getType());
        
        return wallet;
    }
    
    /**
     * 验证支付密码是否正确
     * 
     * @param walletId
     * @param pwd
     * @return
     * @throws Exception
     */
    public boolean chkPaymentPwd(Integer walletId,String pwd) throws Exception{
        String url = "WalletService/chkPaymentPwd/"+walletId+"/"+pwd + "/"+PaymentUtils.WALLET_LOG_TYPE;
        
        String json =PaymentUtils.getJosnString(url);
        
        Gson gson = new Gson();
        
        boolean ret = gson.fromJson(json, new TypeToken<Boolean>() {}.getType());
        
        return ret;
    }
    
    /**
     * 支付
     * 
     * @param formWalletId
     * @param fromWalletName
     * @param toWalletName
     * @param amount
     * @param bizType
     * @param bizDetail
     * @param note
     * @return
     * @throws Exception
     */
    public int pay(Integer formWalletId,String fromWalletName,String toWalletName,
            Long amount,Integer bizType,String bizDetail,String note) throws Exception{
        String url = "WalletService/pay/"+formWalletId+"/"+fromWalletName+"/"
            +toWalletName+"/"+amount+"/"+bizType+"/"+bizDetail+"/"+PaymentUtils.WALLET_LOG_TYPE+"/"+note;
        
        String json =PaymentUtils.getJosnString(url);
        
        Gson gson = new Gson();
        
        int ret = gson.fromJson(json, new TypeToken<Integer>() {}.getType());
        
        return ret;
    }
}

