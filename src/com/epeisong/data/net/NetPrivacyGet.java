package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 获取隐私
 * @author poet
 *
 */
public abstract class NetPrivacyGet extends NetRequestor<LogisticsReq.Builder, CommonLogisticsResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.GET_PRIVACY_REQ;
    }

    @Override
    protected String getResult(CommonLogisticsResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getResult();
    }

    @Override
    protected String getDesc(CommonLogisticsResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getDesc();
    }

    @Override
    protected Builder<LogisticsReq.Builder> getRequest() {
        LogisticsReq.Builder req = LogisticsReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(LogisticsReq.Builder req);

}
