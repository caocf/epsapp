/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.model.InfoFee.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年10月16日下午4:18:05
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.model;

import java.io.Serializable;

import com.epeisong.base.db.Property;

public class InfoFee implements Serializable, Comparable<InfoFee> {

    private static final long serialVersionUID = 1L;

    @Property(primaryKey = true)
    private String id;
    @Property
    private Integer type;
    @Property
    private Integer freightId;
    @Property
    private String freightAddr;
    @Property
    private String freightInfo;
    @Property
    private Long infoAmount;
    @Property
    private Integer payerGuaranteeId;
    @Property
    private String payerGuaranteeName;
    @Property
    private Integer payerGuaranteeProductId;
    @Property
    private Integer payerGuaranteeProductType;
    @Property
    private String payerGuaranteeProductOwnerLogo;
    @Property
    private String payerGuaranteeProductOtherLogo;
    @Property
    private Integer payeeGuaranteeId;
    @Property
    private String payeeGuaranteeName;
    @Property
    private Integer payeeGuaranteeProductId;
    @Property
    private Integer payeeGuaranteeProductType;
    @Property
    private String payeeGuaranteeProductOwnerLogo;
    @Property
    private String payeeGuaranteeProductOtherLogo;
    @Property
    private Integer payerGuaranteeAmount;
    @Property
    private Integer payeeGuaranteeAmount;
    @Property
    private Integer tradingPlatformId;
    @Property
    private Integer payerId;
    @Property
    private String payerName;
    @Property
    private Integer payeeId;
    @Property
    private String payeeName;
    @Property
    private String result; // 临时用于投诉处理结果显示
    @Property
    private Integer payerFlowStatus;
    @Property
    private Integer payeeFlowStatus;
    @Property
    private Integer status;
    @Property
    private long createDate;
    @Property
    private long updateDate;
    @Property
    private Integer syncIndex;
    @Property
    private Integer percentageType; // 1：绝对值，单位：分；2：百分比
    @Property
    private Integer percentageValue;
    @Property
    private int localStatus; // 本地状态：1已读，2未读

    public static final int READ = 1;
    public static final int UNREAD = 2;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getFreightId() {
        return freightId;
    }

    public void setFreightId(Integer freightId) {
        this.freightId = freightId;
    }

    public String getFreightAddr() {
        return freightAddr;
    }

    public String getPayerGuaranteeProductOtherLogo() {
        return payerGuaranteeProductOtherLogo;
    }

    public void setPayerGuaranteeProductOtherLogo(String payerGuaranteeProductOtherLogo) {
        this.payerGuaranteeProductOtherLogo = payerGuaranteeProductOtherLogo;
    }

    public String getPayeeGuaranteeProductOtherLogo() {
        return payeeGuaranteeProductOtherLogo;
    }

    public void setPayeeGuaranteeProductOtherLogo(String payeeGuaranteeProductOtherLogo) {
        this.payeeGuaranteeProductOtherLogo = payeeGuaranteeProductOtherLogo;
    }

    public String getPayerGuaranteeProductOwnerLogo() {
        return payerGuaranteeProductOwnerLogo;
    }

    public void setPayerGuaranteeProductOwnerLogo(String payerGuaranteeProductOwnerLogo) {
        this.payerGuaranteeProductOwnerLogo = payerGuaranteeProductOwnerLogo;
    }

    public String getPayeeGuaranteeProductOwnerLogo() {
        return payeeGuaranteeProductOwnerLogo;
    }

    public void setPayeeGuaranteeProductOwnerLogo(String payeeGuaranteeProductOwnerLogo) {
        this.payeeGuaranteeProductOwnerLogo = payeeGuaranteeProductOwnerLogo;
    }

    public void setFreightAddr(String freightAddr) {
        this.freightAddr = freightAddr;
    }

    public String getFreightInfo() {
        return freightInfo;
    }

    public void setFreightInfo(String freightInfo) {
        this.freightInfo = freightInfo;
    }

    public Long getInfoAmount() {
        return infoAmount;
    }

    public void setInfoAmount(Long infoAmount) {
        this.infoAmount = infoAmount;
    }

    public Integer getPayerGuaranteeId() {
        return payerGuaranteeId;
    }

    public void setPayerGuaranteeId(Integer payerGuaranteeId) {
        this.payerGuaranteeId = payerGuaranteeId;
    }

    public Integer getPayerGuaranteeProductId() {
        return payerGuaranteeProductId;
    }

    public void setPayerGuaranteeProductId(Integer payerGuaranteeProductId) {
        this.payerGuaranteeProductId = payerGuaranteeProductId;
    }

    public Integer getPayerGuaranteeProductType() {
        return payerGuaranteeProductType;
    }

    public void setPayerGuaranteeProductType(Integer payerGuaranteeProductType) {
        this.payerGuaranteeProductType = payerGuaranteeProductType;
    }

    public Integer getPayeeGuaranteeProductType() {
        return payeeGuaranteeProductType;
    }

    public void setPayeeGuaranteeProductType(Integer payeeGuaranteeProductType) {
        this.payeeGuaranteeProductType = payeeGuaranteeProductType;
    }

    public Integer getPayeeGuaranteeId() {
        return payeeGuaranteeId;
    }

    public void setPayeeGuaranteeId(Integer payeeGuaranteeId) {
        this.payeeGuaranteeId = payeeGuaranteeId;
    }

    public Integer getPayeeGuaranteeProductId() {
        return payeeGuaranteeProductId;
    }

    public Integer getPercentageType() {
        return percentageType;
    }

    public void setPercentageType(Integer percentageType) {
        this.percentageType = percentageType;
    }

    public Integer getPercentageValue() {
        return percentageValue;
    }

    public void setPercentageValue(Integer percentageValue) {
        this.percentageValue = percentageValue;
    }

    public void setPayeeGuaranteeProductId(Integer payeeGuaranteeProductId) {
        this.payeeGuaranteeProductId = payeeGuaranteeProductId;
    }

    public Integer getTradingPlatformId() {
        return tradingPlatformId;
    }

    public void setTradingPlatformId(Integer tradingPlatformId) {
        this.tradingPlatformId = tradingPlatformId;
    }

    public Integer getPayerId() {
        return payerId;
    }

    public void setPayerId(Integer payerId) {
        this.payerId = payerId;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public Integer getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(Integer payeeId) {
        this.payeeId = payeeId;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getPayeeFlowStatus() {
        return payeeFlowStatus;
    }

    public void setPayeeFlowStatus(Integer payeeFlowStatus) {
        this.payeeFlowStatus = payeeFlowStatus;
    }

    public Integer getPayerFlowStatus() {
        return payerFlowStatus;
    }

    public void setPayerFlowStatus(Integer payerFlowStatus) {
        this.payerFlowStatus = payerFlowStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public String getPayerGuaranteeName() {
        return payerGuaranteeName;
    }

    public void setPayerGuaranteeName(String payerGuaranteeName) {
        this.payerGuaranteeName = payerGuaranteeName;
    }

    public String getPayeeGuaranteeName() {
        return payeeGuaranteeName;
    }

    public void setPayeeGuaranteeName(String payeeGuaranteeName) {
        this.payeeGuaranteeName = payeeGuaranteeName;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPayerGuaranteeAmount() {
        return payerGuaranteeAmount;
    }

    public void setPayerGuaranteeAmount(Integer payerGuaranteeAmount) {
        this.payerGuaranteeAmount = payerGuaranteeAmount;
    }

    public Integer getPayeeGuaranteeAmount() {
        return payeeGuaranteeAmount;
    }

    public void setPayeeGuaranteeAmount(Integer payeeGuaranteeAmount) {
        this.payeeGuaranteeAmount = payeeGuaranteeAmount;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getSyncIndex() {
        return syncIndex;
    }

    public void setSyncIndex(Integer syncIndex) {
        this.syncIndex = syncIndex;
    }

    public int getLocalStatus() {
        return localStatus;
    }

    public void setLocalStatus(int localStatus) {
        this.localStatus = localStatus;
    }

    @Override
    public int compareTo(InfoFee another) {
        if (another == null) {
            return -1;
        }
        long d = another.getCreateDate() - getCreateDate();
        if (d == 0) {
            return 0;
        }
        if (d > 0) {
            return 1;
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof InfoFee)) {
            return false;
        }
        InfoFee infoFee = (InfoFee) o;
        return getId().equals(infoFee.getId());
    }
}
