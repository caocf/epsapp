package com.epeisong.data.layer02;

import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetLogisticsInfo;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticsReq;
import com.epeisong.model.Contacts;
import com.epeisong.model.User;

public class UserProvider {

	public static User provideById(final String id) {
		Contacts c = ContactsDao.getInstance().queryById(id);
		if (c != null) {
			return c.convertToUser();
		}
		NetLogisticsInfo net = new NetLogisticsInfo() {
			@Override
			protected boolean onSetRequest(LogisticsReq.Builder req) {
				req.setLogisticsId(Integer.parseInt(id));
				return true;
			}
		};
		try {
			CommonLogisticsResp.Builder resp = net.request();
			return UserParser.parseSingleUser(resp);
		} catch (NetGetException e) {
			e.printStackTrace();
			return null;
		}
	}
}
