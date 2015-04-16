package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 
 * @author 孙灵洁 我的车源详情
 * 
 */
public abstract class NetUpdateFreightStatus extends NetRequestorAsync<FreightReq.Builder, FreightResp.Builder> {

    public NetUpdateFreightStatus(XBaseActivity a) {
        super(a);
    }

    @Override
    protected int getCommandCode() {
        // TODO Auto-generated method stub
        return CommandConstants.UPDATE_FREIGHT_STATUS_REQ;
    }

    protected String getDesc(FreightResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getDesc();
    }

    @Override
    protected String getPendingMsg() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getResult(FreightResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getResult();
    }

    protected GeneratedMessage.Builder<FreightReq.Builder> getRequestBuilder() {
        FreightReq.Builder req = FreightReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(FreightReq.Builder req);

}
