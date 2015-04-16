package com.epeisong.data.layer02;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.epeisong.base.db.Condition;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.dao.helper.InfoFeeDbHelper;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.parser.InfoFeeParser;
import com.epeisong.data.utils.Detector;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.InfoFee.InfoFeeReq;
import com.epeisong.logistics.proto.InfoFee.InfoFeeResp;
import com.epeisong.logistics.proto.Transaction.ProtoInfoFee;
import com.epeisong.model.InfoFee;
import com.epeisong.net.request.NetInfoFee;
import com.epeisong.utils.HandlerUtils;

public class InfoFeeManager {

    /**
     * 查询列表，第一次
     * @param size
     * @param status
     * @return
     */
    public List<InfoFee> queryFirst(int size, int status) {
        List<InfoFee> list = InfoFeeDbHelper.getInstance().queryList(InfoFee.class,
                new Condition().equal("status", status).orderBy("syncIndex desc limit 0,1"));
        if (list.isEmpty()) {
            return netNewest(size, status, 0);
        } else {
            int edgeIndex = list.get(0).getSyncIndex();
            List<InfoFee> netNewest = netNewest(size, status, edgeIndex);
            if (netNewest == null) {
                netNewest = new ArrayList<InfoFee>();
            } else if (!netNewest.isEmpty()) {
                edgeIndex = netNewest.get(netNewest.size() - 1).getSyncIndex();
            }
            listOlder(size - netNewest.size(), status, edgeIndex + 1, netNewest);
            return netNewest;
        }
    }

    /**
     * 查询列表，上拉获取更多
     * @param size
     * @param status
     * @param edgeIndex
     * @return
     */
    public List<InfoFee> queryOlder(int size, int status, int edgeIndex) {
        List<InfoFee> result = new ArrayList<InfoFee>();
        listOlder(size, status, edgeIndex, result);
        return result;
    }

    /**
     * 循环获取列表数据，确保本地数据的完整性
     * @param size
     * @param status
     * @param edgeIndex
     * @param result
     */
    private void listOlder(int size, int status, int edgeIndex, List<InfoFee> result) {
        if (size < 1 || edgeIndex <= 1) {
            return;
        }
        List<InfoFee> list = InfoFeeDbHelper.getInstance().queryList(InfoFee.class,
                new Condition().less("syncIndex", edgeIndex).orderBy("syncIndex desc limit 0," + size));
        List<Integer> indexList = new ArrayList<Integer>();
        for (InfoFee infoFee : list) {
            indexList.add(infoFee.getSyncIndex());
        }
        boolean isBreak = Detector.isBreak(edgeIndex, indexList);
        if (isBreak || list.size() < size) {
            List<InfoFee> netOlder = netOlder(size, status, edgeIndex);
            if (netOlder != null) {
                result.addAll(netOlder);
            }
        } else {
            int addedCount = 0;
            for (InfoFee infoFee : list) {
                if (infoFee.getStatus() == status) {
                    result.add(infoFee);
                    addedCount++;
                }
                edgeIndex = infoFee.getSyncIndex();
            }
            if (addedCount < size && edgeIndex > 1) {
                listOlder(size - addedCount, status, edgeIndex, result);
            }
        }
    }

    public List<InfoFee> netNewest(final int size, final int status, final int edgeIndex) {
        NetInfoFee net = new NetInfoFee() {
            @Override
            protected int getCommandCode() {
                return CommandConstants.LIST_INFO_FEE_REQ;
            }

            @Override
            protected void setRequest(InfoFeeReq.Builder req) {
                req.setLogisticsId(Integer.parseInt(UserDao.getInstance().getUser().getId()));
                req.setCount(size);
                req.setStatus(status);
                req.setThanSyncIndex(edgeIndex);
            }
        };
        return net(net);
    }

    public List<InfoFee> netOlder(final int size, final int status, final int edgeIndex) {
        NetInfoFee net = new NetInfoFee() {
            @Override
            protected int getCommandCode() {
                return CommandConstants.LIST_INFO_FEE_REQ;
            }

            @Override
            protected void setRequest(InfoFeeReq.Builder req) {
                req.setLogisticsId(Integer.parseInt(UserDao.getInstance().getUser().getId()));
                req.setCount(size);
                req.setStatus(status);
                req.setLessSyncIndex(edgeIndex);
            }
        };
        return net(net);
    }

    private List<InfoFee> net(NetInfoFee net) {
        InfoFeeResp.Builder resp;
        try {
            resp = net.request();
        } catch (NetGetException e) {
            e.printStackTrace();
            return null;
        }

        if (net.isSuccess(resp)) {
            List<InfoFee> parseList = InfoFeeParser.parseList(resp);
            for (InfoFee infoFee : parseList) {
                InfoFee queryById = InfoFeeDbHelper.getInstance().queryById(InfoFee.class, infoFee.getId());
                if (queryById != null) {
                    infoFee.setLocalStatus(queryById.getLocalStatus());
                }
                InfoFeeDbHelper.getInstance().replace(infoFee, null);
            }
            return parseList;
        }
        return null;
    }

    public InfoFee getInfoFeeFromNet(final String id) {
        NetInfoFee net = new NetInfoFee() {
            @Override
            protected int getCommandCode() {
                return CommandConstants.GET_INFO_FEE_REQ;
            }

            @Override
            protected void setRequest(InfoFeeReq.Builder req) {
                req.setInfoFeeId(id);
            }
        };

        InfoFeeResp.Builder resp = null;
        try {
            resp = net.request();
        } catch (NetGetException e) {
            e.printStackTrace();
            return null;
        }
        InfoFee infoFee = null;
        if (net.isSuccess(resp)) {
            ProtoInfoFee eInfoFee = resp.getInfoFee();
            if (null == eInfoFee) {
                return null;
            }
            infoFee = InfoFeeParser.parser(eInfoFee);
            InfoFee queryById = InfoFeeDbHelper.getInstance().queryById(InfoFee.class, infoFee.getId());
            if (queryById != null) {
                infoFee.setLocalStatus(queryById.getLocalStatus());
            }
            InfoFeeDbHelper.getInstance().replace(infoFee, null);
        }
        return infoFee;
    }

    public void readInfoFee(String id) {
        InfoFee infoFee = InfoFeeDbHelper.getInstance().queryById(InfoFee.class, id);
        if (infoFee != null) {
            infoFee.setLocalStatus(InfoFee.READ);
            if (InfoFeeDbHelper.getInstance().replace(infoFee, null)) {
                notify(infoFee);
            }
        }
    }

    public boolean changeDbAndList(InfoFee infoFee, Condition condition) {
        if (InfoFeeDbHelper.getInstance().replace(infoFee, condition)) {
            notify(infoFee);
            return true;
        }
        return false;
    }

    static List<InfoFeeObserver> sObservers;

    public static void addObserver(InfoFeeObserver ob) {
        if (sObservers == null) {
            sObservers = new ArrayList<InfoFeeObserver>();
        }
        sObservers.add(ob);
    }

    public static void removeObserver(InfoFeeObserver ob) {
        if (sObservers != null) {
            Iterator<InfoFeeObserver> it = sObservers.iterator();
            while (it.hasNext()) {
                InfoFeeObserver next = it.next();
                if (next == null || next == ob) {
                    it.remove();
                }
            }
        }
    }

    void notify(final InfoFee infoFee) {
        if (sObservers != null) {
            HandlerUtils.post(new Runnable() {
                @Override
                public void run() {
                    for (InfoFeeObserver ob : sObservers) {
                        ob.onInfoFeeChange(infoFee);
                    }
                }
            });
        }
    }

    public static interface InfoFeeObserver {
        void onInfoFeeChange(InfoFee infoFee);
    }
}
