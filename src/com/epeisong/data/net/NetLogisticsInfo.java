package com.epeisong.data.net;

import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 获取用户的详情
 * @author poet
 *
 */
public abstract class NetLogisticsInfo extends
		NetRequestor<LogisticsReq.Builder, CommonLogisticsResp.Builder> {

	@Override
	protected int getCommandCode() {
		return CommandConstants.GET_LOGISTICS_REQ;
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
	protected Builder<LogisticsReq.Builder> getRequest() {
	    LogisticsReq.Builder req = LogisticsReq.newBuilder();
		if (onSetRequest(req)) {
			return req;
		}
		return null;
	}

	protected abstract boolean onSetRequest(LogisticsReq.Builder req);
}
