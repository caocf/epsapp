package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 搜索信息
 * 
 * @author Jack
 * 
 */
public class NetSearchNetInfor extends NetRequestorAsync<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {

//	private int eStartPlace;
//	private int elimitiCount;
////	private int eLogisticId;
	
	int marketId;
	int limitCount;
	int id;
	int logitiscTypeCode;
	
	int serveRegionCode;
	int routeCodeA;
	int routeCodeB;
	int periodOfValidity;
	int loadType;
	double weightScore;
	
	public NetSearchNetInfor(XBaseActivity activity, int markid, int limitcount, 
			int id, int logitisctype,
			int serveRegionCode, int routeCodeA, int routeCodeB,
			int periodOfValidity, int loadType, double weightScore){
//		eStartPlace = start;
//		elimitiCount = limitiCount;
////		eLogisticId = logisticId;
		
		this.marketId = markid;
		this.limitCount = limitcount;
		this.id = id;
		this.logitiscTypeCode = logitisctype;
		
		this.serveRegionCode = serveRegionCode;
		this.routeCodeA = routeCodeA;
		this.routeCodeB = routeCodeB;
		this.periodOfValidity = periodOfValidity;
		this.loadType = loadType;
		this.weightScore = weightScore;
	}
	
	@Override
	protected GeneratedMessage.Builder<SearchCommonLogisticsReq.Builder> getRequestBuilder() {
		SearchCommonLogisticsReq.Builder req = SearchCommonLogisticsReq.newBuilder();
//		req.setRegionCode(eStartPlace);
//		req.setLogisticId(0);
//		req.setLimitCount(elimitiCount);
		
		req.setMarketId(marketId);
		req.setLimitCount(limitCount);
		req.setId(id);
		req.setLogisticTypeCode(logitiscTypeCode);
		return req;
	}
	
	@Override
	protected String getResult(
			com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getResult();
	}

	@Override
	protected String getDesc(
			com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getDesc();
	}

	@Override
	protected int getCommandCode() {
		// TODO Auto-generated method stub
		return CommandConstants.GET_MEMBERS_REQ;
	}

	@Override
	protected String getPendingMsg() {
		// TODO Auto-generated method stub
		return "正在搜索...";
	}

}
