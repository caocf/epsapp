package com.epeisong.data.net;

import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 搜索车源货源（改版之初，根据regionCode定位Market）
 * @author poet
 *
 */
public abstract class NetSearchFreightFromMarket extends
        NetRequestor<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder resp) {
        return resp.getResult();
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
}
