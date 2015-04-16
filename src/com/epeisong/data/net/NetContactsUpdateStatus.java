package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 更改联系人的状态（黑名单-联系人，联系人-黑名单，删除）
 * @author poet
 *
 */
public abstract class NetContactsUpdateStatus extends NetRequestor<ContactReq.Builder, CommonLogisticsResp.Builder> {

    @Override
    protected int getCommandCode() {
        // TODO Auto-generated method stub
        return CommandConstants.UPDATE_CONTACT_STATUS_REQ;
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

    @Override
    protected Builder<com.epeisong.logistics.proto.Eps.ContactReq.Builder> getRequest() {
        ContactReq.Builder req = ContactReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(ContactReq.Builder req);
}
