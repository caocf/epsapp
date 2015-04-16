package com.epeisong.net.request;

import java.util.List;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.common.CommandConstants;
import com.google.protobuf.GeneratedMessage;

/**
 * 车源、货源的通知（转发）
 * 
 * @author poet
 * 
 */
public class NetNoticeOrRelayDispatch extends
		NetRequestorAsync<FreightReq.Builder, FreightResp.Builder> {

	private String dispatch_id;
	private List<Integer> contacts_id_list;

	public NetNoticeOrRelayDispatch(XBaseActivity activity, String dispatch_id,
			List<Integer> contacts_id_list) {
		super(activity);
		this.dispatch_id = dispatch_id;
		this.contacts_id_list = contacts_id_list;
	}

	@Override
	protected int getCommandCode() {
		return CommandConstants.DELIVER_FREIGHT_REQ;
	}

	@Override
	protected String getPendingMsg() {
		return null;
	}

	@Override
	protected String getResult(FreightResp.Builder resp) {
		return resp.getResult();
	}

	@Override
	protected String getDesc(FreightResp.Builder resp) {
		return resp.getDesc();
	}

	@Override
	protected GeneratedMessage.Builder<FreightReq.Builder> getRequestBuilder() {
	    FreightReq.Builder req = FreightReq.newBuilder();
		req.setFreightId(Integer.parseInt(dispatch_id));
		req.addAllReceiverId(contacts_id_list);
		req.setContactId(Integer.parseInt(UserDao.getInstance().getUser().getId()));
		return req;
	}
}
