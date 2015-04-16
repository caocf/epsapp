package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.GeneralResp;
import com.epeisong.logistics.proto.Eps.SendVerificationCodeReq;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetVerificationCodeLogined extends
        NetRequestor<SendVerificationCodeReq.Builder, GeneralResp.Builder> {

    @Override
    protected int getCommandCode() {
        // TODO Auto-generated method stub
        return CommandConstants.SEND_VERIFICATION_CODE_REQ;
    }

    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.GeneralResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getResult();
    }

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.GeneralResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getDesc();
    }

    @Override
    protected Builder<com.epeisong.logistics.proto.Eps.SendVerificationCodeReq.Builder> getRequest() {
        SendVerificationCodeReq.Builder req = SendVerificationCodeReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(SendVerificationCodeReq.Builder req);
}
