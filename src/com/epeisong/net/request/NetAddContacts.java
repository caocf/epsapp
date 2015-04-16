package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 添加联系人
 * 
 * @author poet
 * 
 */
public class NetAddContacts extends
		NetRequestorAsync<ContactReq.Builder, CommonLogisticsResp.Builder> {

	private String contacts_id;

	public NetAddContacts(XBaseActivity activity, String contacts_id) {
		super(activity);
		this.contacts_id = contacts_id;
	}

	@Override
	protected int getCommandCode() {
		return CommandConstants.ADD_CONTACT_REQ;
	}

	@Override
	protected String getPendingMsg() {
		return null;
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
		req.setContactId(Integer.parseInt(contacts_id));
		return req;
	}
}
