package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetUserConfig extends NetRequestorAsync<LogisticsReq.Builder, CommonLogisticsResp.Builder> {

    public NetUserConfig(XBaseActivity a) {
        super(a);
    }

    @Override
    protected int getCommandCode() {
        return CommandConstants.UPDATE_LOGISTIC_SUBSCRIBE_REQ;
    }

    @Override
    protected String getPendingMsg() {
        return null;
    }

    @Override
    protected String getResult(CommonLogisticsResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(CommonLogisticsResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected Builder<LogisticsReq.Builder> getRequestBuilder() {
        LogisticsReq.Builder req = LogisticsReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return super.getRequestBuilder();
    }

    protected abstract boolean onSetRequest(LogisticsReq.Builder req);
}
