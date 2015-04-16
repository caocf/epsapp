package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.google.protobuf.GeneratedMessage;

/**
 * 添加会员
 * 
 * @author gnn
 * 
 */
public class NetAddMembers extends
		NetRequestorAsync<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {

	private String user_id;

	public NetAddMembers(XBaseActivity activity, String user_id) {
		super(activity);
		this.user_id = user_id;
	}

	@Override
	protected int getCommandCode() {
		return CommandConstants.ADD_MEMBER_REQ;
	}

	@Override
	protected String getPendingMsg() {
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

	@Override
	protected GeneratedMessage.Builder<SearchCommonLogisticsReq.Builder> getRequestBuilder() {
		SearchCommonLogisticsReq.Builder req = SearchCommonLogisticsReq.newBuilder();
		req.setLogisticId(Integer.parseInt(user_id));
		return req;
	}
}
