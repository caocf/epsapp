package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.model.Freight;
import com.google.protobuf.GeneratedMessage;

/**
 * 转发到小黑板
 * @author poet
 *
 */
public abstract class NetRepasteFreight extends
		NetRequestorAsync<FreightReq.Builder, FreightResp.Builder> {

	public NetRepasteFreight(XBaseActivity a) {
		super(a);
	}

	
	
	@Override
	protected int getCommandCode() {
		// TODO Auto-generated method stub
		return CommandConstants.REPASTE_FREIGHT_REQ;
	}

	@Override
	protected String getPendingMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getResult(FreightResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getResult();
	}

	@Override
	protected String getDesc(FreightResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getDesc();
	}
	
	
	@Override
	protected GeneratedMessage.Builder<FreightReq.Builder> getRequestBuilder() {
		FreightReq.Builder req = FreightReq
				.newBuilder();
		if (onSetRequest(req)) {
			return req;
		}
		return null;
	}

	protected abstract boolean onSetRequest(
			FreightReq.Builder req);


}
