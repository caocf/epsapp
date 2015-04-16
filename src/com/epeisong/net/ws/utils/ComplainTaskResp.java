/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.transaction.model.ComplainTaskResp.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2015年3月9日下午3:15:55
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.net.ws.utils;

import com.epeisong.model.ComplainTask;


public class ComplainTaskResp {

    public static int SUCC = 1;

    int result = -1;

    String desc = "";
    
    ComplainTask task;

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

    public ComplainTask getTask() {
        return task;
    }

    public void setTask(ComplainTask task) {
        this.task = task;
    }
    
    
}

