package com.epeisong.data.net;

import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 搜索整车运输
 * 
 * @author poet
 * 
 */
public abstract class NetLogisticsSearch extends

NetRequestor<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {

    @Override
    protected String getResult(CommonLogisticsResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(CommonLogisticsResp.Builder resp) {
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
