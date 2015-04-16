package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetListMineFreight extends NetRequestor<FreightReq.Builder, FreightResp.Builder> {

	@Override
	protected int getCommandCode() {
		return CommandConstants.LIST_FREIGHTS_ON_BLACK_BOARD_BY_CREATE_TIME_REQ;
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
	protected String getDesc(FreightResp.Builder resp) {
		return resp.getDesc();
	}
	
	protected abstract boolean onSetRequest(FreightReq.Builder req);

}
