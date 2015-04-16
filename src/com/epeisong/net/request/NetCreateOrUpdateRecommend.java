package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.RecommendReq;
import com.epeisong.logistics.proto.Eps.RecommendResp;
import com.epeisong.logistics.proto.Eps.RecommendResp.Builder;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;
/**
 * 创建或者更新推荐数
 * @author gnn
 *
 */
public abstract class NetCreateOrUpdateRecommend extends NetRequestorAsync<RecommendReq.Builder, RecommendResp.Builder> {
	
	public NetCreateOrUpdateRecommend(XBaseActivity a){
		super(a);
	}
	
	@Override
	protected int getCommandCode() {
		// TODO Auto-generated method stub
		return CommandConstants.CREATE_OR_UPDATE_RECOMMEND_STATUS_REQ;
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
    protected GeneratedMessage.Builder<RecommendReq.Builder> getRequestBuilder() {
		RecommendReq.Builder req = RecommendReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(RecommendReq.Builder req);

}
