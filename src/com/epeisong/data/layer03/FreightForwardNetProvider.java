package com.epeisong.data.layer03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.epeisong.data.dao.FreightForwardDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetFreightForwardDetect;
import com.epeisong.data.net.NetFreightForwardNewest;
import com.epeisong.data.net.NetFreightForwardOlder;
import com.epeisong.data.net.parser.FreightForwardParser;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.model.FreightForward;
import com.epeisong.utils.LogUtils;

public class FreightForwardNetProvider {

	public List<FreightForward> getNewest(final int size) throws NetGetException {

		NetFreightForwardNewest net = new NetFreightForwardNewest() {
			@Override
			protected boolean onSetRequest(
					FreightReq.Builder req) {
				req.setLimitCount(size);
				return true;
			}
		};
		FreightResp.Builder resp = net.request();
		List<FreightForward> data = FreightForwardParser.parse(resp);
		Collections.sort(data);
		Collections.reverse(data);
		FreightForwardDao.getInstance().insertAll(data);
		return data;
	}

	public List<FreightForward> getOlder(long last_time,
			final long last_serial, final int size) throws NetGetException {

		NetFreightForwardOlder net = new NetFreightForwardOlder() {
			@Override
			protected boolean onSetRequest(
					FreightReq.Builder req) {
				if (last_serial != 0) {
					req.addIndex((int) last_serial);
				}
				req.setLimitCount(size);
				return true;
			}
		};
		FreightResp.Builder resp = net.request();
		List<FreightForward> data = FreightForwardParser.parse(resp);
		Collections.sort(data);
		Collections.reverse(data);
		
		StringBuilder sb = new StringBuilder("[");
		for(FreightForward ff : data) {
			sb.append(ff.getSerial() + ",");
		}
		LogUtils.et(sb.append("]").toString());
		
		FreightForwardDao.getInstance().insertAll(data);
		return data;
	}

	public List<FreightForward> getLost(final List<Long> lost)
			throws NetGetException {

		NetFreightForwardDetect net = new NetFreightForwardDetect() {
			@Override
			protected boolean onSetRequest(
			        FreightReq.Builder req) {
				List<Integer> _lost = new ArrayList<Integer>();
				for (long l : lost) {
					_lost.add((int) l);
				}
				req.addAllIndex(_lost);
				return true;
			}
		};
		FreightResp.Builder resp = net.request();
		List<FreightForward> data = FreightForwardParser.parse(resp);
		FreightForwardDao.getInstance().insertAll(data);
		return data;
	}
}
