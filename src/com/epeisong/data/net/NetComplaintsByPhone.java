package com.epeisong.data.net;
/**
 * 通过手机号获得投诉记录 gnn
 */
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetComplaintsByPhone extends NetRequestor<LogisticsReq.Builder, CommonLogisticsResp.Builder> {

	@Override
	protected int getCommandCode() {
		return CommandConstants.GET_COMPLAINT_BY_PHONE_NUMBER_REQ;
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
