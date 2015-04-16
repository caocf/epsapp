package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 同步联系人
 * @author poet
 *
 */
public abstract class NetContactsSync extends NetRequestor<ContactReq.Builder, CommonLogisticsResp.Builder> {

    @Override
    protected boolean isShowToast() {
        return false;
    }

    @Override
    protected int getCommandCode() {
        return CommandConstants.SYNC_CONTACT_REQ;
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
