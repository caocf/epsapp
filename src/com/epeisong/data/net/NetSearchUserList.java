package com.epeisong.data.net;

import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 搜索用户
 * @author poet
 *
 */
public abstract class NetSearchUserList extends
        NetRequestor<SearchCommonLogisticsReq.Builder, CommonLogisticsResp.Builder> {

    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder resp) {
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
}
