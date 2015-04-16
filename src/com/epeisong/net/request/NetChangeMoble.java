package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.AccountReq;
import com.epeisong.logistics.proto.Eps.AccountResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 
 * @author 孙灵洁 更换手机号
 * 
 */
public abstract class NetChangeMoble extends NetRequestorAsync<AccountReq.Builder, AccountResp.Builder> {

    public NetChangeMoble(XBaseActivity a) {
        super(a);
    }

    @Override
    protected int getCommandCode() {
        // TODO Auto-generated method stub
        return CommandConstants.CHANGE_MOBLE_REQ;
    }

    @Override
    protected String getPendingMsg() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getResult(AccountResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getResult();
    }

    @Override
    protected String getDesc(AccountResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getDesc();
    }

    protected GeneratedMessage.Builder<AccountReq.Builder> getRequestBuilder() {
        AccountReq.Builder req = AccountReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(AccountReq.Builder req);

}
