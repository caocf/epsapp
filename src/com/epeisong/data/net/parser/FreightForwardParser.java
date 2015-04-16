package com.epeisong.data.net.parser;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.logistics.proto.Base.ProtoEFreight;
import com.epeisong.logistics.proto.Base.ProtoRFreightDelivery;
import com.epeisong.logistics.proto.Eps.DeliverFreightServerPushReq;
import com.epeisong.logistics.proto.Eps.FreightAndFreightDelivery;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.model.Freight;
import com.epeisong.model.FreightForward;

public class FreightForwardParser {

	public static FreightForward parse(DeliverFreightServerPushReq.Builder req) {
		String user_show_name = req.getSenderName();
		ProtoRFreightDelivery delivery = req.getFreightDelivery();
		FreightForward ff = new FreightForward();
		ff.setId(String.valueOf(delivery.getId()));
		ff.setSerial(delivery.getSyncIndex());
		ff.setForward_create_time(delivery.getCreateDate());
		ff.setForward_update_time(delivery.getUpdateDate());
		ff.setUser_id(String.valueOf(delivery.getSenderId()));
		ff.setUser_show_name(user_show_name);

		ProtoEFreight protoEFreight = req.getFreight();
		Freight freight = FreightParser.parse(protoEFreight);
		ff.setFreight(freight);
		return ff;
	}

	public static FreightForward parse(FreightAndFreightDelivery faf) {
		ProtoEFreight freight = faf.getFreight();
		ProtoRFreightDelivery forward = faf.getFreightDelivery();

		FreightForward ff = new FreightForward();
		ff.setFreight(FreightParser.parse(freight));

		ff.setId(String.valueOf(forward.getId()));
		ff.setSerial(forward.getSyncIndex());
		ff.setUser_id(String.valueOf(forward.getSenderId()));
		ff.setForward_create_time(forward.getCreateDate());
		ff.setForward_update_time(forward.getUpdateDate());

		return ff;
	}

	public static List<FreightForward> parse(
			FreightResp.Builder resp) {
		List<FreightAndFreightDelivery> list = resp
				.getFreightAndFreightDeliveryList();
		List<Integer> missedSerialList = resp.getMissedSyncIndexList();

		List<FreightForward> result = new ArrayList<FreightForward>();
		for (FreightAndFreightDelivery item : list) {
			result.add(parse(item));
		}
		if (missedSerialList != null && missedSerialList.size() > 0) {
			for (Integer serial : missedSerialList) {
				FreightForward ff = new FreightForward();
				ff.setSerial(serial);
				result.add(ff);
			}
		}
		return result;
	}

}
