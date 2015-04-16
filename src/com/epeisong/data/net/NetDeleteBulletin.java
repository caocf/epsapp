package com.epeisong.data.net;

import com.epeisong.logistics.proto.Eps.BulletinReq;
import com.epeisong.logistics.proto.Eps.BulletinReq.Builder;
import com.epeisong.logistics.proto.Eps.BulletinResp;
import com.epeisong.logistics.common.CommandConstants;

public abstract class NetDeleteBulletin
		extends
		NetRequestor<BulletinReq.Builder, BulletinResp.Builder> {

	@Override
	protected int getCommandCode() {
		return CommandConstants.UPDATE_BULLETIN_STATUS_REQ;
	}

	@Override
	protected com.google.protobuf.GeneratedMessage.Builder<Builder> getRequest() {
		BulletinReq.Builder req = BulletinReq
				.newBuilder();
		if (onSetRequest(req)) {
			return req;
		}
		return null;
	}

	protected abstract boolean onSetRequest(BulletinReq.Builder req);

	@Override
	protected String getResult(BulletinResp.Builder resp) {
		return resp.getResult();
	}

	@Override
	protected String getDesc(BulletinResp.Builder resp) {
		return resp.getDesc();
	}

}
