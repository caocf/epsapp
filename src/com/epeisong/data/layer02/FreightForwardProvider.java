package com.epeisong.data.layer02;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.epeisong.data.dao.FreightForwardDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.layer03.FreightForwardNetProvider;
import com.epeisong.data.utils.Detector;
import com.epeisong.model.FreightForward;
import com.epeisong.utils.LogUtils;

@Deprecated
public class FreightForwardProvider {

    private boolean mJustLocal = true; // 只加载本地数据，不做同步

    public List<FreightForward> provideFirst(int requireSize) throws NetGetException {
        LogUtils.saveLog("FreightForwardProvider", "provideFirst.entry");

        List<FreightForward> data = FreightForwardDao.getInstance().queryFirst(requireSize);
        if (mJustLocal) {
            return data;
        }
        if (data == null || data.isEmpty()) {
            return null;
            // return new FreightForwardNetProvider().getNewest(requireSize);
        }
        List<Long> serialList = new ArrayList<Long>();
        for (FreightForward ff : data) {
            serialList.add(ff.getSerial());
        }
        // 检漏算法：将所有不连续的serial获取到（包括已被删除的），带优化处理：同com.epeisong.data.net.parser包中的解析联合处理
        List<Long> lost = Detector.detect(-1, requireSize, serialList);
        if (lost != null && lost.size() > 0) {
            List<FreightForward> lostData = new FreightForwardNetProvider().getLost(lost);

            if (lostData != null && !lostData.isEmpty()) {
                data.addAll(lostData);
            }
        }

        Collections.sort(data);
        Collections.reverse(data);

        int dSize = requireSize - data.size();
        if (dSize > 0) {
            long edgeTime = data.get(data.size() - 1).getForward_create_time();
            long serial = data.get(data.size() - 1).getSerial();
            FreightForwardNetProvider p = new FreightForwardNetProvider();
            List<FreightForward> netGetData = p.getOlder(edgeTime, serial, dSize);
            data.addAll(netGetData);
            Collections.sort(data);
            Collections.reverse(data);
        }

        LogUtils.saveLog("FreightForwardProvider", "provideFirst.return");
        return data;
    }

    public List<FreightForward> provideNewer(long last_time, long last_serial, int requireSize) throws NetGetException {

        List<FreightForward> data = FreightForwardDao.getInstance().queryNewer(last_time, last_serial, requireSize);
        if (mJustLocal) {
            return data;
        }
        if (data == null || data.isEmpty()) {
            return data;
        }

        List<Long> serialList = new ArrayList<Long>();
        for (FreightForward ff : data) {
            serialList.add(ff.getSerial());
        }
        List<Long> lost = Detector.detect(-1, requireSize, serialList);
        if (lost != null && lost.size() > 0) {
            List<FreightForward> lostData = new FreightForwardNetProvider().getLost(lost);

            if (lostData != null && !lostData.isEmpty()) {
                data.addAll(lostData);
            }
        }

        Collections.sort(data);
        Collections.reverse(data);
        return data;
    }

    public List<FreightForward> provideOlder(long last_time, long last_serial, int requireSize) throws NetGetException {

        List<FreightForward> data = FreightForwardDao.getInstance().queryOlder(last_time, last_serial, requireSize);

        if (mJustLocal) {
            return data;
        }
        if (data == null || data.isEmpty()) {
            if (last_serial > 0) {
                LogUtils.et("providerOld_all_from_net:" + requireSize);
                return new FreightForwardNetProvider().getOlder(last_time, last_serial, requireSize);
            } else {
                return new FreightForwardNetProvider().getNewest(requireSize);
            }
        }

        List<Long> serialList = new ArrayList<Long>();
        for (FreightForward ff : data) {
            serialList.add(ff.getSerial());
        }
        List<Long> lost = Detector.detect(-1, requireSize, serialList);
        if (lost != null && lost.size() > 0) {
            List<FreightForward> lostData = new FreightForwardNetProvider().getLost(lost);

            if (lostData != null && !lostData.isEmpty()) {
                data.addAll(lostData);
            }
        }

        Collections.sort(data);
        Collections.reverse(data);

        int dSize = requireSize - data.size();
        if (dSize > 0) {
            LogUtils.et("ProviderOld_dSize_from_net:" + dSize);
            long edgeTime = data.get(data.size() - 1).getForward_create_time();
            long serial = data.get(data.size() - 1).getSerial();
            FreightForwardNetProvider p = new FreightForwardNetProvider();
            List<FreightForward> netGetData = p.getOlder(edgeTime, serial, dSize);
            if (netGetData != null && netGetData.size() > 0) {
                data.addAll(netGetData);
                Collections.sort(data);
                Collections.reverse(data);
            }
        }
        return data;
    }
}
