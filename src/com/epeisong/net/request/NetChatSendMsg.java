package com.epeisong.net.request;

import com.epeisong.logistics.proto.Eps.ChatReq;
import com.epeisong.logistics.proto.Eps.ChatResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 发送聊天消息（多种类型的消息统一发送）
 * 
 * @author poet
 * 
 */
public abstract class NetChatSendMsg extends NetRequestorAsync<ChatReq.Builder, ChatResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.CHAT_SEND_MULTI_TYPE_REQ;
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
