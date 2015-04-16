package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.data.net.NetRequestor;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.AccountReq;
import com.epeisong.logistics.proto.Eps.QuestionReq;
import com.epeisong.logistics.proto.Eps.QuestionResp;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Descriptors.FieldDescriptor;

public abstract class NetQuestion extends
		NetRequestor<QuestionReq.Builder, QuestionResp.Builder> {


	@Override
	protected int getCommandCode() {
		// TODO Auto-generated method stub
		return CommandConstants.LIST_QUESTIONS_REQ;
	}

	@Override
	protected String getDesc(QuestionResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getDesc();
	}

	@Override
	protected String getResult(QuestionResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getResult();
	}

	protected GeneratedMessage.Builder<QuestionReq.Builder> getRequest() {
		QuestionReq.Builder req = QuestionReq.newBuilder();
		if (onSetRequest(req)) {

			return req;
		}
		return null;
	}

	protected abstract boolean onSetRequest(QuestionReq.Builder req);

}
