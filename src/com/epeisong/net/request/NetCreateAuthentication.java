package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.AuthenticationReq;
import com.epeisong.logistics.proto.Eps.AuthenticationResp;
import com.google.protobuf.GeneratedMessage;

public abstract class NetCreateAuthentication extends NetRequestorAsync<AuthenticationReq.Builder, AuthenticationResp.Builder> {

	public NetCreateAuthentication(XBaseActivity a){
		super(a);
	}
	
	@Override
	protected int getCommandCode() {
		// TODO Auto-generated method stub
		return CommandConstants.AUTHENTICATE_SOME_ONE_REQ;
	}

	@Override
	protected String getDesc(
			com.epeisong.logistics.proto.Eps.AuthenticationResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getDesc();
	}

	@Override
	protected String getPendingMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getResult(
			com.epeisong.logistics.proto.Eps.AuthenticationResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getResult();
	}
	
	@Override
    protected GeneratedMessage.Builder<AuthenticationReq.Builder> getRequestBuilder() {
		AuthenticationReq.Builder req = AuthenticationReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(AuthenticationReq.Builder req);

}
