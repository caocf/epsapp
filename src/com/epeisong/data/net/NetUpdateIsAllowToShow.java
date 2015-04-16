package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetUpdateIsAllowToShow extends NetRequestor<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {

	@Override
	protected int getCommandCode() {
		return CommandConstants.UPDATE_IS_ALLOW_TO_SHOW_STATUS_ON_MARKET_SCREEN_REQ;
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
