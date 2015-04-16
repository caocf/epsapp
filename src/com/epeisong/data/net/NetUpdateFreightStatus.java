package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetUpdateFreightStatus extends NetRequestor<FreightReq.Builder, FreightResp.Builder> {

	@Override
	protected int getCommandCode() {
		return CommandConstants.UPDATE_FREIGHT_STATUS_REQ;
	}

	@Override
	protected Builder<FreightReq.Builder> getRequest() {
		FreightReq.Builder req = FreightReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
	}

	@Override
	protected String getResult(FreightResp.Builder resp) {
		return resp.getResult();
	}

	@Override
	protected String getDesc(
			com.epeisong.logistics.proto.Eps.FreightResp.Builder resp) {
		return resp.getDesc();
	}
	
	protected abstract boolean onSetRequest(FreightReq.Builder req);

}
