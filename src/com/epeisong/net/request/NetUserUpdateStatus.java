package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.epeisong.model.User;
import com.google.protobuf.GeneratedMessage;

public abstract class NetUserUpdateStatus extends NetRequestorAsync<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {
    /*
     * private int freightId; private int newStatus;
     */

    public NetUserUpdateStatus(XBaseActivity a) {
        super(a);
    }

    @Override
	protected int getCommandCode() {
		// TODO Auto-generated method stub
		return CommandConstants.UPDATE_MEMBER_STATUS_REQ;
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
    protected GeneratedMessage.Builder<SearchCommonLogisticsReq.Builder> getRequestBuilder() {
		SearchCommonLogisticsReq.Builder req = SearchCommonLogisticsReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

	protected abstract boolean onSetRequest(SearchCommonLogisticsReq.Builder req);
}
