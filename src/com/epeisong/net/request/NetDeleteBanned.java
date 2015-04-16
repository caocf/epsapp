package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

public class NetDeleteBanned extends NetRequestorAsync<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {

    private int banndeId;

    public NetDeleteBanned(XBaseActivity activity, int banndeId) {
        this.banndeId = banndeId;
    }

    @Override
    protected GeneratedMessage.Builder<SearchCommonLogisticsReq.Builder> getRequestBuilder() {
        SearchCommonLogisticsReq.Builder req = SearchCommonLogisticsReq.newBuilder();
//        req.setMarketScreenBannedListId(banndeId);
        return req;
    }

    @Override
    protected int getCommandCode() {
        // TODO Auto-generated method stub
        return CommandConstants.DELETE_BANNED_LOGISTIC_REQ;
    }

    @Override
    protected String getPendingMsg() {
        // TODO Auto-generated method stub
        return "正在取消屏蔽";
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
