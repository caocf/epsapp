package com.epeisong.net.request;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.ChatReq;
import com.epeisong.logistics.proto.Eps.ChatResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 聊天消息检漏
 * 
 * @author poet
 * 
 */
public class NetChatDetect extends
		NetRequestorAsync<ChatReq.Builder, ChatResp.Builder> {

	private String remoteId;
	private List<Long> lostSerial;
	private int business_type;
	private String business_id;

	public NetChatDetect(XBaseActivity activity, String remoteId,
			List<Long> lostSerial, int business_type, String business_id) {
		super(activity);
		this.remoteId = remoteId;
		this.lostSerial = lostSerial;
		this.business_type = business_type;
		this.business_id = business_id;
	}

	@Override
	protected int getCommandCode() {
		return CommandConstants.LIST_MISSED_CHATS_REQ;
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
		if (lostSerial == null || lostSerial.isEmpty()) {
			return null;
		}
		req.setOppsiteId(Integer.parseInt(remoteId));
		List<Integer> lost = new ArrayList<Integer>();
		for (long l : lostSerial) {
			lost.add((int) l);
		}
		req.addAllIndex(lost);
		req.setTableId(business_type);
		req.setBizId(Integer.parseInt(business_id));
		return req;
	}
}
