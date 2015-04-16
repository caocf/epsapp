/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.net.request.NeInfoFee.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年10月21日上午10:17:18
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.net.request;

import com.epeisong.data.dao.UserDao;
import com.epeisong.data.net.NetRequestor;
import com.epeisong.logistics.proto.InfoFee.InfoFeeReq;
import com.epeisong.logistics.proto.InfoFee.InfoFeeResp;
import com.epeisong.utils.LogUtils;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 已自动添加：logistics_id
 * @author poet
 *
 */
public abstract class NetInfoFee extends NetRequestor<InfoFeeReq.Builder, InfoFeeResp.Builder> {

    @Override
    protected abstract int getCommandCode();

    @Override
    protected String getResult(InfoFeeResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(InfoFeeResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected Builder<InfoFeeReq.Builder> getRequest() {
        InfoFeeReq.Builder req = InfoFeeReq.newBuilder();
        setRequest(req);
        req.setLogisticsId(Integer.parseInt(UserDao.getInstance().getUser().getId()));
        return req;
    }

    protected abstract void setRequest(InfoFeeReq.Builder req);
}
