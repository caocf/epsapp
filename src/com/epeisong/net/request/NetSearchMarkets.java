package com.epeisong.net.request;

import android.R.integer;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.data.net.NetRequestor;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 搜索
 * 
 * @author Jack
 * 
 */
public abstract class NetSearchMarkets extends NetRequestor<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {

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
    protected Builder<SearchCommonLogisticsReq.Builder> getRequest() {
        SearchCommonLogisticsReq.Builder req = SearchCommonLogisticsReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(SearchCommonLogisticsReq.Builder req);

    protected abstract int getCommandCode();
}
