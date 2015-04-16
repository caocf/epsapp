package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 所有的屏蔽会员
 * 
 * @author gnn
 * 
 */

public class NetMarketScreenBannedList extends NetRequestorAsync<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {
    private int limitCount;
    private int edgeId;

    public NetMarketScreenBannedList(XBaseActivity activity, int limitCount, int edgeId) {
        this.limitCount = limitCount;
        this.edgeId = edgeId;
    }

    @Override
    protected GeneratedMessage.Builder<SearchCommonLogisticsReq.Builder> getRequestBuilder() {
        SearchCommonLogisticsReq.Builder req = SearchCommonLogisticsReq.newBuilder();
        req.setLimitCount(limitCount);
        if (edgeId > 0) {
            req.setId(edgeId);
        }
        return req;
    }

    @Override
    protected int getCommandCode() {
        // TODO Auto-generated method stub
        return CommandConstants.GET_MARKET_SCREEN_BANNED_LIST_REQ;
    }

    @Override
    protected String getPendingMsg() {
        // TODO Auto-generated method stub
        return "正在加载";
    }

    @Override
    protected String getResult(CommonLogisticsResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getResult();
    }

    @Override
    protected String getDesc(CommonLogisticsResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getDesc();
    }

}
