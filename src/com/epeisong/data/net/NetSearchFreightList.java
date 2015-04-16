package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 搜索车源货车
 * @author poet
 *
 */
public abstract class NetSearchFreightList extends NetRequestor<FreightReq.Builder, FreightResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.SEARCH_FREIGHT_BY_LOCATION_REQ;
    }

    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.FreightResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.FreightResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected Builder<FreightReq.Builder> getRequest() {
        FreightReq.Builder req = FreightReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(FreightReq.Builder req);
}
