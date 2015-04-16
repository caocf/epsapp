package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetCreateRoleByCostomerService extends NetRequestor<LogisticsReq.Builder, CommonLogisticsResp.Builder> {

	@Override
	protected int getCommandCode() {
		// TODO Auto-generated method stub
		return CommandConstants.CREATE_SUB_ROLE_BY_PLAT_FORM_CUSTOMER_SERVICE_REQ;
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
		// TODO Auto-generated method stub
		return resp.getResult();
	}

	@Override
	protected String getDesc(CommonLogisticsResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getDesc();
	}
	
	protected abstract boolean onSetRequest(LogisticsReq.Builder req);

}
