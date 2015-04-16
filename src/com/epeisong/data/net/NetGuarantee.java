package com.epeisong.data.net;

import com.epeisong.logistics.proto.Eps.GuaranteeReq;
import com.epeisong.logistics.proto.Eps.GuaranteeResp;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 担保
 * @author poet
 *
 */
public abstract class NetGuarantee extends NetRequestor<GuaranteeReq.Builder, GuaranteeResp.Builder> {
    


    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.GuaranteeResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getResult();
    }

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.GuaranteeResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getDesc();
    }

    @Override
    protected Builder<GuaranteeReq.Builder> getRequest() {
        GuaranteeReq.Builder req = GuaranteeReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(GuaranteeReq.Builder req);
    protected abstract int getCommandCode();
}
