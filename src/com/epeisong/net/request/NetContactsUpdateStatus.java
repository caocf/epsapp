package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

public abstract class NetContactsUpdateStatus extends
        NetRequestorAsync<ContactReq.Builder, CommonLogisticsResp.Builder> {

    public NetContactsUpdateStatus(XBaseActivity a) {
        super(a);
    }

    @Override
    protected int getCommandCode() {
        // TODO Auto-generated method stub
        return CommandConstants.UPDATE_CONTACT_STATUS_REQ;
    }

    @Override
    protected String getPendingMsg() {
        // TODO Auto-generated method stub
        return null;
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

    protected GeneratedMessage.Builder<ContactReq.Builder> getRequestBuilder() {
        ContactReq.Builder req = ContactReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;

    }

    protected abstract boolean onSetRequest(ContactReq.Builder req);

}
