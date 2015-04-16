package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 
 * @author Jack 删除所有无效的车源货源
 * 
 */
public abstract class NetDeleteAllInvalidFreight extends NetRequestorAsync<FreightReq.Builder, FreightResp.Builder> {

	//private int logisticId;
    public NetDeleteAllInvalidFreight(XBaseActivity a) {
        super(a);
    }

    @Override
    protected int getCommandCode() {
        // TODO Auto-generated method stub
        return CommandConstants.DELETE_ALL_INVALID_FREIGHTS_ON_BLACK_BOARD_REQ;
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
