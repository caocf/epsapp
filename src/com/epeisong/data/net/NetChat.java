package com.epeisong.data.net;

import com.epeisong.logistics.proto.Eps.ChatReq;
import com.epeisong.logistics.proto.Eps.ChatResp;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 聊天
 * @author poet
 *
 */
public abstract class NetChat extends NetRequestor<ChatReq.Builder, ChatResp.Builder> {

    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.ChatResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.ChatResp.Builder resp) {
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
