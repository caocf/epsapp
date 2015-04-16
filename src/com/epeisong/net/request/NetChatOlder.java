package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.ChatReq;
import com.epeisong.logistics.proto.Eps.ChatResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 获取历史消息
 * 
 * @author poet
 * 
 */
public abstract class NetChatOlder
		extends
		NetRequestorAsync<ChatReq.Builder, ChatResp.Builder> {

	public NetChatOlder(XBaseActivity activity) {
		super(activity);
	}

	@Override
	protected int getCommandCode() {
		return CommandConstants.LIST_OLD_CHATS_ADJOIN_LOCAL_REQ;
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
	protected Builder<ChatReq.Builder> getRequestBuilder() {
	    ChatReq.Builder req = ChatReq.newBuilder();
		if(onSetRequest(req)) {
			return req;
		}
		return null;
	}
	
	protected abstract boolean onSetRequest(ChatReq.Builder req);
}
