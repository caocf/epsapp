package com.epeisong.data.net;

import com.epeisong.EpsNetConfig;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.net.NetServiceFactory;
import com.epeisong.logistics.proto.Eps.SendVerificationCodeReq;
import com.epeisong.logistics.proto.Eps.SendVerificationCodeResp;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 获取验证码
 * @author poet
 *
 */
public abstract class NetGetCode extends
        NetRequestor<SendVerificationCodeReq.Builder, SendVerificationCodeResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.SEND_VERIFICATION_CODE_REQ;
    }

    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.SendVerificationCodeResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.SendVerificationCodeResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected Builder<SendVerificationCodeReq.Builder> getRequest() {
        return null;
    }

    @Override
    protected boolean isRequestSelf() {
        return true;
    }

    @Override
    protected SendVerificationCodeResp.Builder requestSelf() throws Exception {
        return NetServiceFactory.getInstance().sendVerificationCode(EpsNetConfig.getHost(), EpsNetConfig.PORT,
                getPhone(), getPurpose(), (int) getTimeout());
    }

    protected abstract String getPhone();

    protected abstract int getPurpose();
}
