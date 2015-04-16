/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.transaction.model.WithdrawTaskResp.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2015年2月12日下午2:42:56
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.net.ws.utils;

import java.util.List;

public class WithdrawTaskResp {
    public static int SUCC = 1;
    public static int FAIL = -1;

    int result = -1;

    String desc = "";
    List<WithdrawTask> withdrawTaskList;
    WithdrawTask withdrawTask;
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
    public List<WithdrawTask> getWithdrawTaskList() {
        return withdrawTaskList;
    }
    public void setWithdrawTaskList(List<WithdrawTask> withdrawTaskList) {
        this.withdrawTaskList = withdrawTaskList;
    }
    public WithdrawTask getWithdrawTask() {
        return withdrawTask;
    }
    public void setWithdrawTask(WithdrawTask withdrawTask) {
        this.withdrawTask = withdrawTask;
    }
    
}

