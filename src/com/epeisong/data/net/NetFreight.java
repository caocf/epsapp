package com.epeisong.data.net;

import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 车源货源
 * 
 * @author Jack
 * 
 */
public abstract class NetFreight extends
		NetRequestor<FreightReq.Builder, FreightResp.Builder> {

	@Override
	protected String getResult(FreightResp.Builder resp) {
		return resp.getResult();
	}

	@Override
	protected String getDesc(FreightResp.Builder resp) {
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
	protected abstract int getCommandCode();
}
