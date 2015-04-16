package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

public class NetLatestFreightsOnMarket extends NetRequestorAsync<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {

    private int marketId;
    private int limitCount;
    private int edgeFreightId;
    private int fType;
    private int startCode;
    private int endCode;

    public NetLatestFreightsOnMarket(XBaseActivity activity, int mId, int limitNum, int edgeFreightId, int fType ,int startCode, int endCode) {
        super(activity);
        marketId = mId;
        limitCount = limitNum;
        this.edgeFreightId = edgeFreightId;
        this.fType = fType;
        this.startCode = startCode;
        this.endCode = endCode;
    }

    @Override
    protected int getCommandCode() {
        // TODO Auto-generated method stub
        return CommandConstants.LIST_LATEST_FREIGHTS_ON_MARKET_SCREEN_REQ;
    }

    @Override
    protected GeneratedMessage.Builder<SearchCommonLogisticsReq.Builder> getRequestBuilder() {
        SearchCommonLogisticsReq.Builder req = SearchCommonLogisticsReq.newBuilder();
        if (edgeFreightId > 0) {
            req.setId(edgeFreightId);
        }
        req.setMarketId(marketId);
        req.setCurrentTime(System.currentTimeMillis());
        req.setLimitCount(limitCount);
        req.setFreightType(fType);
        req.setRouteCodeA(startCode);
        req.setRouteCodeB(endCode);
        return req;
    }

    @Override
    protected String getPendingMsg() {
        // TODO Auto-generated method stub
        return "正在搜索...";
    }

    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getResult();
    }

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getDesc();
    }

}
