/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.payment.net.utils.EnumBizType.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年10月31日下午3:44:16
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.payment.net.utils;

public enum EnumBizType {
    GUARANTEE_FEE(1),INFO_FEE(2), FREIGHT(3);

    private int value;

    private EnumBizType(int value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }
}

