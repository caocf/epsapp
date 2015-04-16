package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 搜索联系人
 * 
 * @author poet
 * 
 */
public abstract class NetSearchContacts extends
        NetRequestorAsync<ContactReq.Builder, CommonLogisticsResp.Builder> {

    public NetSearchContacts(XBaseActivity activity) {
        super(activity);
    }

    @Override
    protected int getCommandCode() {
        return CommandConstants.SEARCH_CONTACT_REQ;
    }

    @Override
    protected String getPendingMsg() {
        return "正在搜索联系人...";
    }

    @Override
    protected String getResult(CommonLogisticsResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(CommonLogisticsResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected GeneratedMessage.Builder<ContactReq.Builder> getRequestBuilder() {
        ContactReq.Builder req = ContactReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(ContactReq.Builder req);
}
