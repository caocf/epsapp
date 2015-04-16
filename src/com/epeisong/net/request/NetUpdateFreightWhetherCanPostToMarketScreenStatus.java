package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.common.CommandConstants;

/**
 * 
 * @author 孙灵洁 我的车源货源详情，是否发送到配货市场
 * 
 */
public abstract class NetUpdateFreightWhetherCanPostToMarketScreenStatus extends
        NetRequestorAsync<FreightReq.Builder, FreightResp.Builder> {

    public NetUpdateFreightWhetherCanPostToMarketScreenStatus(XBaseActivity a) {
        super(a);
    }

    @Override
    protected int getCommandCode() {
        // TODO Auto-generated method stub
        return CommandConstants.UPDATE_FREIGHT_WHETHER_POST_TO_MARKET_SCREEN_STATUS_REQ;
    }

    @Override
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

    @Override
    protected com.google.protobuf.GeneratedMessage.Builder<com.epeisong.logistics.proto.Eps.FreightReq.Builder> getRequestBuilder() {
        FreightReq.Builder req = FreightReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(FreightReq.Builder req);

}
