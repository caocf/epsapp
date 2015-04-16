package com.epeisong.data.net;

import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.model.User;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetFreightUpdateStatus extends NetRequestor<FreightReq.Builder, FreightResp.Builder> {

	@Override
	protected int getCommandCode() {
		return CommandConstants.UPDATE_FREIGHT_STATUS_REQ;
	}

	@Override
	protected Builder<FreightReq.Builder> getRequest() {
		FreightReq.Builder req = FreightReq.newBuilder();
        if (onSetRequest(req)) {
            User u = UserDao.getInstance().getUser();
            req.setLogisticId(Integer.parseInt(u.getId()));
            req.setLogisticName(u.getShow_name());
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
