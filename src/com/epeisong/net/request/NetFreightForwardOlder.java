package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 获取较旧的车源、货源（别人发给我的）
 * 
 * @author poet
 * 
 */
public abstract class NetFreightForwardOlder extends NetRequestorAsync<FreightReq.Builder, FreightResp.Builder> {

    public NetFreightForwardOlder(XBaseActivity activity) {
        super(activity);
    }

    @Override
    protected int getCommandCode() {
        return CommandConstants.LIST_OLD_FREIGHT_DELIVERY_ADJOIN_LOCAL_REQ;
    }

    @Override
    protected String getPendingMsg() {
        return null;
    }

    @Override
    protected String getResult(FreightResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(FreightResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected GeneratedMessage.Builder<FreightReq.Builder> getRequestBuilder() {
        FreightReq.Builder req = FreightReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(FreightReq.Builder req);
}
