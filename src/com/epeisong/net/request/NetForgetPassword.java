package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.AccountReq;
import com.epeisong.logistics.proto.Eps.AccountResp;
import com.google.protobuf.GeneratedMessage;

public abstract class NetForgetPassword extends
		NetRequestorAsync<AccountReq.Builder, AccountResp.Builder> {

	 public NetForgetPassword(XBaseActivity a) {
	        super(a);
	    }
	@Override
	protected int getCommandCode() {
		// TODO Auto-generated method stub
		return CommandConstants.FORGET_PASSWORD_REQ;
	}

	@Override
	protected String getDesc(AccountResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getDesc();
	}

	@Override
	protected String getPendingMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getResult(AccountResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getResult();
	}

	@Override
	protected GeneratedMessage.Builder<AccountReq.Builder> getRequestBuilder() {
		AccountReq.Builder req = AccountReq.newBuilder();
		if (onSetRequest(req)) {
			return req;
		}
		return null;
	}

	protected abstract boolean onSetRequest(AccountReq.Builder req);

}
