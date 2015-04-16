package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetAddRegionToMarket  extends NetRequestor<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {

	@Override
	protected int getCommandCode() {
		return CommandConstants.ADD_REGION_TO_MARKET_REQ;
	}

	@Override
	protected Builder<SearchCommonLogisticsReq.Builder> getRequest() {
		SearchCommonLogisticsReq.Builder req = SearchCommonLogisticsReq.newBuilder();
		if (onSetRequest(req)) {
			return req;
		}
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
	
	protected abstract boolean onSetRequest(SearchCommonLogisticsReq.Builder req);

}
