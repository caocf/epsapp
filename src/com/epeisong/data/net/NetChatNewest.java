package com.epeisong.data.net;

import com.epeisong.logistics.proto.Eps.ChatReq;
import com.epeisong.logistics.proto.Eps.ChatResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetChatNewest extends NetRequestor<ChatReq.Builder, ChatResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.LIST_LATEST_CHATS_REQ;
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
    protected Builder<ChatReq.Builder> getRequest() {
        ChatReq.Builder req = ChatReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(ChatReq.Builder req);
}
