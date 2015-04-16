package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 获取较旧的车源货源(配货站的车源货源信息)
 * 
 * @author gnn
 * 
 */
public class NetOlderFreightsAdjoinMarket extends NetRequestorAsync<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {

    private int marketId;
    private int limitCount;
    private long createDate; // 下滑前最后一条的时间
    private int fType;
    private int marketFreightIndex;
    private int startCode;
    private int endCode;

    public NetOlderFreightsAdjoinMarket(XBaseActivity activity, int mId, long cDate, int marketFreightIndex,
            int limitNum, int fType ,int startCode, int endCode) {
        super(activity);
        marketId = mId;
        limitCount = limitNum;
        createDate = cDate;
        this.fType = fType;
        this.marketFreightIndex = marketFreightIndex;
        this.startCode = startCode;
        this.endCode = endCode;
   }

    @Override
    protected int getCommandCode() {
        // TODO Auto-generated method stub
        return CommandConstants.LIST_OLDER_FREIGHTS_ADJOIN_LOCAL_FROM_MARKET_SCREEN_REQ;
    }

    @Override
    protected String getPendingMsg() {
        // TODO Auto-generated method stub
        return "正在加载...";
    }

    @Override
    protected GeneratedMessage.Builder<SearchCommonLogisticsReq.Builder> getRequestBuilder() {
        SearchCommonLogisticsReq.Builder req = SearchCommonLogisticsReq.newBuilder();
        req.setMarketId(marketId);
        req.setLimitCount(limitCount);
        req.setCreateDate(createDate);
        req.setFreightType(fType);
        req.setId(marketFreightIndex);
        req.setRouteCodeA(startCode);
        req.setRouteCodeB(endCode);
        return req;
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
