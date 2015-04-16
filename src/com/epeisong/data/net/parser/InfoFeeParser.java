/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.data.net.parser.InfoFeeParser.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年10月19日上午10:03:11
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.data.net.parser;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.logistics.proto.InfoFee.InfoFeeResp;
import com.epeisong.logistics.proto.Transaction.ProtoInfoFee;
import com.epeisong.model.InfoFee;

public class InfoFeeParser {

    public static InfoFee parser(ProtoInfoFee eInfoFee) {
        InfoFee infoFee = new InfoFee();

        infoFee.setId(eInfoFee.getId());
        infoFee.setType(eInfoFee.getType());
        infoFee.setFreightId(eInfoFee.getFreightId());
        infoFee.setFreightAddr(eInfoFee.getFreightAddr());
        infoFee.setFreightInfo(eInfoFee.getFreightInfo());
        infoFee.setInfoAmount(eInfoFee.getInfoAmount());
        infoFee.setPayerGuaranteeId(eInfoFee.getPayerGuaranteeId());
        infoFee.setPayerGuaranteeName(eInfoFee.getPayerGuaranteeName());
        infoFee.setPayerGuaranteeProductId(eInfoFee.getPayerGuaranteeProductId());
        infoFee.setPayerGuaranteeProductType(eInfoFee.getPayerGuaranteeProductType());
        infoFee.setPayerGuaranteeProductOtherLogo(eInfoFee.getPayerGuaranteeProductOtherLogo());
        infoFee.setPayerGuaranteeProductOwnerLogo(eInfoFee.getPayerGuaranteeProductOwnerLogo());
        infoFee.setPayeeGuaranteeId(eInfoFee.getPayeeGuaranteeId());
        infoFee.setPayeeGuaranteeName(eInfoFee.getPayeeGuaranteeName());
        infoFee.setPayeeGuaranteeProductId(eInfoFee.getPayeeGuaranteeProductId());
        infoFee.setPayeeGuaranteeProductType(eInfoFee.getPayeeGuaranteeProductType());
        infoFee.setPayeeGuaranteeProductOtherLogo(eInfoFee.getPayeeGuaranteeProductOtherLogo());
        infoFee.setPayeeGuaranteeProductOwnerLogo(eInfoFee.getPayeeGuaranteeProductOwnerLogo());
        infoFee.setPayerGuaranteeAmount(eInfoFee.getPayerGuaranteeAmount());
        infoFee.setPayeeGuaranteeAmount(eInfoFee.getPayeeGuaranteeAmount());
        infoFee.setTradingPlatformId(eInfoFee.getTradingPlatformId());
        infoFee.setPayerId(eInfoFee.getPayerId());
        infoFee.setPayerName(eInfoFee.getPayerName());
        infoFee.setPayeeId(eInfoFee.getPayeeId());
        infoFee.setPayeeName(eInfoFee.getPayeeName());
        infoFee.setResult(eInfoFee.getResult());
        infoFee.setPayeeFlowStatus(eInfoFee.getPayeeFlowStatus());
        infoFee.setPayerFlowStatus(eInfoFee.getPayerFlowStatus());
        infoFee.setStatus(eInfoFee.getStatus());
        infoFee.setCreateDate(eInfoFee.getCreateDate());
        infoFee.setUpdateDate(eInfoFee.getUpdateDate());
        infoFee.setSyncIndex(eInfoFee.getSyncIndex());
        infoFee.setPercentageType(eInfoFee.getPercentageType());
        infoFee.setPercentageValue(eInfoFee.getPercentageValue());
        return infoFee;
    }

    public static List<InfoFee> parseList(InfoFeeResp.Builder resp) {
        List<InfoFee> result = new ArrayList<InfoFee>();
        List<ProtoInfoFee> list = resp.getInfoFeesList();
        for (ProtoInfoFee item : list) {
            result.add(parser(item));
        }
        return result;
    }
}
