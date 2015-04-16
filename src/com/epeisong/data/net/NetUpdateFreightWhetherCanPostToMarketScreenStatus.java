package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.google.protobuf.GeneratedMessage.Builder;
/**
 * 
 * @author gnn 我的车源货源详情，是否发送到配货市场
 * 
 */
public abstract class NetUpdateFreightWhetherCanPostToMarketScreenStatus extends NetRequestor<FreightReq.Builder, FreightResp.Builder>{

	@Override
	protected int getCommandCode() {
		return CommandConstants.UPDATE_FREIGHT_WHETHER_POST_TO_MARKET_SCREEN_STATUS_REQ;
	}

	@Override
	protected Builder<FreightReq.Builder> getRequest() {
		FreightReq.Builder req = FreightReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
	}

	@Override
	protected String getResult(FreightResp.Builder resp) {
		return resp.getResult();
	}

	@Override
	protected String getDesc(FreightResp.Builder resp) {
		return resp.getDesc();
	}
	
	protected abstract boolean onSetRequest(FreightReq.Builder req);

}
