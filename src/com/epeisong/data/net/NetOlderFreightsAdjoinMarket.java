package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetOlderFreightsAdjoinMarket extends NetRequestor<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {

	@Override
	protected int getCommandCode() {
		return CommandConstants.LIST_OLDER_FREIGHTS_ADJOIN_LOCAL_FROM_MARKET_SCREEN_REQ;
	}

	@Override
	protected Builder<com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder> getRequest() {
		SearchCommonLogisticsReq.Builder req = SearchCommonLogisticsReq.newBuilder();
		if (onSetRequest(req)) {
            return req;
        }
        return null;
	}

	@Override
	protected String getResult(
			com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder resp) {
		return resp.getResult();
	}

	@Override
	protected String getDesc(
			com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder resp) {
		return resp.getDesc();
	}
	
	protected abstract boolean onSetRequest(SearchCommonLogisticsReq.Builder req);

}
