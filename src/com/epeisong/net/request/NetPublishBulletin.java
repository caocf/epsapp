package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.BulletinReq;
import com.epeisong.logistics.proto.Eps.BulletinResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 发出公告
 * 
 * @author poet
 * 
 */
public class NetPublishBulletin extends
		NetRequestorAsync<BulletinReq.Builder, BulletinResp.Builder> {

	private String content;

	public NetPublishBulletin(XBaseActivity activity, String content) {
		super(activity);
		this.content = content;
	}

	@Override
	protected int getCommandCode() {
		return CommandConstants.CREATE_BULLETIN_REQ;
	}

	@Override
	protected String getPendingMsg() {
		return null;
	}

	@Override
	protected String getResult(BulletinResp.Builder resp) {
		return resp.getResult();
	}

	@Override
	protected String getDesc(BulletinResp.Builder resp) {
		return resp.getDesc();
	}

	@Override
	protected GeneratedMessage.Builder<BulletinReq.Builder> getRequestBuilder() {
		BulletinReq.Builder req = BulletinReq.newBuilder();
		req.setContent(content);
		return req;
	}
}
