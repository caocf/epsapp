package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.proto.Eps.FreightResp.Builder;
import com.google.protobuf.GeneratedMessage;

/**
 * 
 * @author gnn
 * 小黑板已发信息条数
 */
public class NetGetPublishBlack extends NetRequestorAsync<FreightReq.Builder, FreightResp.Builder> {
	private int id;
	
	public NetGetPublishBlack(XBaseActivity activity , int id) {
		super(activity);
		this.id = id;
	}

	@Override
	protected int getCommandCode() {
		// TODO Auto-generated method stub
		return CommandConstants.GET_FREIGHT_COUNT_ON_BLACK_BOARD_REQ;
	}

	@Override
	protected String getDesc(Builder resp) {
		// TODO Auto-generated method stub
		return resp.getDesc();
	}

	@Override
	protected String getPendingMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getResult(Builder resp) {
		// TODO Auto-generated method stub
		return resp.getResult();
	}
	
	@Override
	protected GeneratedMessage.Builder<FreightReq.Builder> getRequestBuilder() {
		FreightReq.Builder req = FreightReq.newBuilder();
		req.setLogisticId(id);
		return req;
	}

}
