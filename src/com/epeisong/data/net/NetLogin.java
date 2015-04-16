package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.UserLoginReq;
import com.epeisong.logistics.proto.Eps.UserLoginResp;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 登录
 * @author poet
 *
 */
public abstract class NetLogin extends NetRequestor<UserLoginReq.Builder, UserLoginResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.USER_LOGIN_REQ;
    }

    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.UserLoginResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.UserLoginResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected Builder<UserLoginReq.Builder> getRequest() {
        UserLoginReq.Builder req = UserLoginReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(UserLoginReq.Builder req);
}
