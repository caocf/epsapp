/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.payment.net.utils.EnumWalletType.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年10月31日下午3:45:11
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.payment.net.utils;

public enum EnumWalletType {
    EPS(1), ALIPAY(2), WEIXIN(3), UNIONPAY(4);

    private int value;

    private EnumWalletType(int value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }
}

