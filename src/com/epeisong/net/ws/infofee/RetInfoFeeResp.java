/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.logistics.ws.resp.InfoFeeResp.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2015年2月8日下午12:07:03
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.net.ws.infofee;

import com.epeisong.model.InfoFee;

public class RetInfoFeeResp {
    public static int SUCC = 1;
    public static int FAIL = -1;

    int result = FAIL;
    String desc = "";
    InfoFee infoFee;
    
    int payerSyncIndex=0;
    int payeeSyncIndex=0;
    
    public int getResult() {
        return result;
    }
    public void setResult(int result) {
        this.result = result;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public InfoFee getInfoFee() {
        return infoFee;
    }
    public void setInfoFee(InfoFee infoFee) {
        this.infoFee = infoFee;
    }
    public int getPayerSyncIndex() {
        return payerSyncIndex;
    }
    public void setPayerSyncIndex(int payerSyncIndex) {
        this.payerSyncIndex = payerSyncIndex;
    }
    public int getPayeeSyncIndex() {
        return payeeSyncIndex;
    }
    public void setPayeeSyncIndex(int payeeSyncIndex) {
        this.payeeSyncIndex = payeeSyncIndex;
    }
    
}

