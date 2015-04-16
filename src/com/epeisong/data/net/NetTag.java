package com.epeisong.data.net;

import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 给联系人添加标签
 * @author poet
 *
 */
public abstract class NetTag extends NetRequestor<ContactReq.Builder, CommonLogisticsResp.Builder> {

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

    @Override
    protected Builder<ContactReq.Builder> getRequest() {
        ContactReq.Builder req = ContactReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(ContactReq.Builder req);
}
