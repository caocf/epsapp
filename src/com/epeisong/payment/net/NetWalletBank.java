/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.payment.net.NetWalletBank.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年11月6日下午3:21:38
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.payment.net;

import java.util.List;

import com.epeisong.model.BankCard;
import com.epeisong.payment.net.utils.PaymentUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NetWalletBank {

    public List<BankCard> getAllBankCards(Integer walletId) throws Exception{
        
        String url = "BankCardService/getAllBankCards/"+walletId+"/"+PaymentUtils.WALLET_LOG_TYPE;
        
        String json =PaymentUtils.getJosnString(url);
        
        Gson gson = new Gson();
        
        List<BankCard> list = gson.fromJson(json, new TypeToken<List<BankCard>>() {}.getType());
        
        return list;
    }
    
    public int addBankCard(Integer walletId,String walletName,Integer cardType,String bankCode
            ,String bankName,String cardNumber,String realName,String cityName) throws Exception{
        String url = "BankCardService/createBankCard/"+walletId+"/"+walletName+"/"+cardType+"/"+bankCode
        //        +"/"+bankName+"/"+cardNumber+"/"+realName+"/"+cityName+"/"+PaymentUtils.WALLET_LOG_TYPE;
                        +"/"+bankName+"/"+cardNumber+"/"+realName+"/"+PaymentUtils.WALLET_LOG_TYPE;
        
        String json =PaymentUtils.getJosnString(url);
        
        Gson gson = new Gson();
        
        Integer ret = gson.fromJson(json, new TypeToken<Integer>() {}.getType());
        return ret;
    }
    
    public int deleteBankCard(Integer id,Integer walletId) throws Exception{
        String url = "BankCardService/deleteBankCard/"+id+"/"+walletId+"/"+PaymentUtils.WALLET_LOG_TYPE;
        
        String json =PaymentUtils.getJosnString(url);
        
        Gson gson = new Gson();
        
        Integer ret = gson.fromJson(json, new TypeToken<Integer>() {}.getType());
        return ret;
    }
    
    public boolean checkBankCardIsExist(Integer walletId,String cardNumber) throws Exception {
        
        String url = "BankCardService/checkBankCardIsExist/"+walletId+"/"+cardNumber+"/"+PaymentUtils.WALLET_LOG_TYPE;
        
        String json =PaymentUtils.getJosnString(url);
        
        Gson gson = new Gson();
        
        boolean ret = gson.fromJson(json, new TypeToken<Boolean>() {}.getType());
        return ret;
    }
}

