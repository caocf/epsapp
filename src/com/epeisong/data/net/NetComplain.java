package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetComplain extends NetRequestor<LogisticsReq.Builder, CommonLogisticsResp.Builder> {

	@Override
	protected int getCommandCode() {
		return CommandConstants.COMPLAIN_REQ;
	}

	@Override
	protected Builder<LogisticsReq.Builder> getRequest() {
		LogisticsReq.Builder req = LogisticsReq.newBuilder();
		if (onSetRequest(req)) {
            return req;
        }
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
	
	protected abstract boolean onSetRequest(LogisticsReq.Builder req);

}
