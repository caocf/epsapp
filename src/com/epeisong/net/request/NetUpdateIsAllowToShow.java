package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 
 * @author 孙灵洁 搜索车源货源，朋友车源详情
 * 
 */
public class NetUpdateIsAllowToShow extends NetRequestorAsync<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {
    private int messageStatus;
    private int mId;

    public NetUpdateIsAllowToShow(XBaseActivity activity, int messageStatus, int mId) {
        this.messageStatus = messageStatus;
        this.mId = mId;
    }

    @Override
    protected int getCommandCode() {
        // TODO Auto-generated method stub
        return CommandConstants.UPDATE_IS_ALLOW_TO_SHOW_STATUS_ON_MARKET_SCREEN_REQ;
    }

    @Override
    protected GeneratedMessage.Builder<SearchCommonLogisticsReq.Builder> getRequestBuilder() {
        SearchCommonLogisticsReq.Builder req = SearchCommonLogisticsReq.newBuilder();
        req.setNewStatus(messageStatus);
        req.setMarketScreenId(mId);
        return req;
    }

    @Override
    protected String getPendingMsg() {
        // TODO Auto-generated method stub
        return "正在屏蔽此消息";
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

    // protected abstract boolean
    // onSetRequest(UpdateIsAllowToShowStatusOnMarketScreenReq.Builder req);

}
