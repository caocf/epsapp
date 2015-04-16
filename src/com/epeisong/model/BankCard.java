/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.model.BankCard.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年11月6日下午2:54:48
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.model;

import java.io.Serializable;

public class BankCard implements Serializable{

    /**
     * 
     */
    @Deprecated
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer walletId;
    private Integer cardType;
    private String bankCode;
    private String bankName;
    private String cardNumber;
    private String realName;
    private String personname;
    private String cityname;
    private String identityNumber;
    private Long createDate;
    private Long updateDate;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getWalletId() {
        return walletId;
    }
    public void setWalletId(Integer walletId) {
        this.walletId = walletId;
    }
    public String getPersonName() {
        return personname;
    }
    public void setPersonName(String personname) {
        this.personname = personname;
    }
    public String getCityName() {
        return cityname;
    }
    public void setCityName(String cityname) {
        this.cityname = cityname;
    }
    public Integer getCardType() {
        return cardType;
    }
    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }
    public String getBankCode() {
        return bankCode;
    }
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }
    public String getBankName() {
        return bankName;
    }
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    public String getRealName() {
        return realName;
    }
    public void setRealName(String realName) {
        this.realName = realName;
    }
    public String getIdentityNumber() {
        return identityNumber;
    }
    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }
    public Long getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }
    public Long getUpdateDate() {
        return updateDate;
    }
    public void setUpdateDate(Long updateDate) {
        this.updateDate = updateDate;
    }
}

