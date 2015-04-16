package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.proto.InfoFee.InfoFeeReq;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 订货、配货
 * @author poet
 *
 */
public abstract class NetCreateInfoFee extends NetRequestor<InfoFeeReq.Builder, FreightResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.CREATE_INFO_FEE_REQ;
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
    protected Builder<InfoFeeReq.Builder> getRequest() {
        InfoFeeReq.Builder req = InfoFeeReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(InfoFeeReq.Builder req);
}
