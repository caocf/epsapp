package com.epeisong.data.net;

import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 添加联系人
 * @author poet
 *
 */
public abstract class NetAddContacts extends NetRequestor<ContactReq.Builder, CommonLogisticsResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.ADD_CONTACT_REQ;
    }

    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder resp) {
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
