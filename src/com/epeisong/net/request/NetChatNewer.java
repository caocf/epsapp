package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.ChatReq;
import com.epeisong.logistics.proto.Eps.ChatResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 获取较新的消息
 * 
 * @author poet
 * 
 */
public abstract class NetChatNewer extends NetRequestorAsync<ChatReq.Builder, ChatResp.Builder> {

    public NetChatNewer(XBaseActivity activity) {
        super(activity);
    }

    @Override
    protected int getCommandCode() {
        return CommandConstants.LIST_LATEST_CHATS_REQ;
    }

    @Override
    protected String getPendingMsg() {
        return null;
    }

    @Override
    protected String getResult(ChatResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(ChatResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected GeneratedMessage.Builder<ChatReq.Builder> getRequestBuilder() {
        ChatReq.Builder req = ChatReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(ChatReq.Builder req);
}
