package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 搜索车源货源
 * 
 * @author gnn
 * 
 */

public  class NetSearchFreight
		extends
		NetRequestorAsync<FreightReq.Builder, FreightResp.Builder> {

	
	private int eStartPlace;
	private int eEndPlace;
	private int freighttype;
	private int elimitiCount;
	private int id;
	private int logistics_id;

	public NetSearchFreight(XBaseActivity activity, int start, int end,
			int freighttype, int limitiCount, int id, int logistics_id) {
		super(activity);
		eStartPlace = start;
		eEndPlace = end;
		elimitiCount = limitiCount;
		this.id = id;
		this.freighttype = freighttype;
		this.logistics_id = logistics_id;

	}

	@Override
	protected int getCommandCode() {
		// TODO Auto-generated method stub
		return CommandConstants.SEARCH_FREIGHT_BY_LOCATION_REQ;
	}

	@Override
	protected String getPendingMsg() {
		return "正在搜索...";
	}

	@Override
	protected GeneratedMessage.Builder<FreightReq.Builder> getRequestBuilder() {
		FreightReq.Builder req = FreightReq
				.newBuilder();
		req.setStartPointCode(eStartPlace);
		req.setDestinationCode(eEndPlace);
		req.setLimitCount(elimitiCount);
		req.setId(id);
		req.setFreightType(freighttype);
		req.setLogisticId(logistics_id);
		return req;
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
	

}
