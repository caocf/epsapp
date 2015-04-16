package com.epeisong.data.net;

import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetLoadContacts extends
		NetRequestor<ContactReq.Builder, CommonLogisticsResp.Builder> {

	@Override
	protected int getCommandCode() {
		return CommandConstants.LIST_CONTACTS_REQ;
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
	protected Builder<ContactReq.Builder> getRequest() {
		ContactReq.Builder req = ContactReq.newBuilder();
		if (onSetRequest(req)) {
			return req;
		}
		return null;
	}

	protected abstract boolean onSetRequest(ContactReq.Builder req);
}
