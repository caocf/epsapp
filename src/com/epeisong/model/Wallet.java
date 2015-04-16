/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.model.Wallet.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年10月31日下午12:05:41
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.model;

import java.io.Serializable;

public class Wallet implements Serializable {

    /**
     * 
     */
    @Deprecated
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer accountId;
    private String walletName;
    private Integer walletType;
    private Long amount;
    private String paymentPwd;
    private String realName;
    private Integer idType;
    private Integer idStatus;
    private String identityNumber;
    private Integer status;
    private Long createDate;
    private Long updateDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public Integer getWalletType() {
        return walletType;
    }

    public void setWalletType(Integer walletType) {
        this.walletType = walletType;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getPaymentPwd() {
        return paymentPwd;
    }

    public void setPaymentPwd(String paymentPwd) {
        this.paymentPwd = paymentPwd;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Integer getIdType() {
        return idType;
    }

    public void setIdType(Integer idType) {
        this.idType = idType;
    }

    public Integer getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(Integer idStatus) {
        this.idStatus = idStatus;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
