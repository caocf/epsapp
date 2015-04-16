/**
 * Copyright(C) 2009-2019 EPeiSong NanJing Information Service LTD. All Rights Reserved.   
 * 版权所有(C) 2009-2019 南京易配送信息技术有限公司
 * 公司名称：南京易配送信息技术有限公司
 * 公司地址：中国，江苏省南京市雨花台区花神大道23号3号楼309室
 * 网址:http://www.epeisong.com
 * <p>
 * 文件名：com.epeisong.data.layer02.InfoFeeProvider.java
 * <p>
 * 作者: 刘林
 * <p>
 * 创建时间: 2014年10月23日上午9:59:11
 * <p>
 * 部门: 产品部
 * <p>
 * 描述: TODO
 * <p>
 */
package com.epeisong.data.layer02;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.epeisong.data.dao.InfoFeeDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.parser.InfoFeeParser;
import com.epeisong.data.utils.Detector;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.InfoFee.InfoFeeReq;
import com.epeisong.logistics.proto.InfoFee.InfoFeeReq.Builder;
import com.epeisong.logistics.proto.InfoFee.InfoFeeResp;
import com.epeisong.logistics.proto.Transaction.ProtoInfoFee;
import com.epeisong.model.InfoFee;
import com.epeisong.net.request.NetInfoFee;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.java.JavaUtils;

public class InfoFeeProvider_old {


    // 网络获取数据，某一点新的，thanIndex；某一点旧的，lessIndex
    private List<InfoFee> providerFromNet(final int status, final int size, final int thanIndex, final int lessIndex) {
        NetInfoFee net = new NetInfoFee() {

            @Override
            protected int getCommandCode() {
                return CommandConstants.LIST_INFO_FEE_REQ;
            }

            @Override
            protected void setRequest(Builder req) {
                req.setCount(size);
                req.setStatus(status);
                req.setThanSyncIndex(thanIndex);
                req.setLessSyncIndex(lessIndex);
            }
        };
        try {
            InfoFeeResp.Builder resp = net.request();
            if (net.isSuccess(resp)) {
                List<InfoFee> result = InfoFeeParser.parseList(resp);
                if (!result.isEmpty()) {
                    InfoFeeDao.getInstance().replace(result);
                }
                return result;
            }
        } catch (NetGetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<InfoFee> getInfoFeeList(Integer logisticsId, Integer lessSyncIndex, int count, int status) {

        List<InfoFee> result = new ArrayList<InfoFee>();

        listInfoFee(logisticsId, count, lessSyncIndex, status, result);

        if (result != null && result.size() > 0) {
            Collections.sort(result);
        }

        return result;
    }

    /**
     * 刷新取最新的数据
     * 
     * @param logisticsId
     * @param syncIndex
     * @param updateDate
     * @param count
     * @return
     */
    public List<InfoFee> getNewestInfoFeeList(Integer logisticsId, Integer thanSyncIndex, int count, int status) {
        List<InfoFee> result = netInfoFeeList(logisticsId, 0, thanSyncIndex, count, status);

        int dSize = count;
        if (null != result && result.size() > 0) {
            dSize = count - result.size();
        } else {
            result = new ArrayList<InfoFee>();
        }

        listInfoFee(logisticsId, dSize, thanSyncIndex + 1, status, result);

        if (null != result && result.size() > 0) {
            Collections.sort(result);
        }

        return result;
    }

    private void listInfoFee(Integer logisticsId, int count, int currentSyncIndex, int status, List<InfoFee> result) {
        List<InfoFee> list = InfoFeeDao.getInstance().queryList(currentSyncIndex, 0, count);

        List<Integer> syncIndexList = new ArrayList<Integer>();
        if (list != null && list.size() > 0) {
            for (InfoFee infoFee : list) {
                syncIndexList.add(infoFee.getSyncIndex());
            }
        }

//        List<Integer> lostIndexList = Detector.detect(currentSyncIndex, count, syncIndexList);
//        if ((lostIndexList == null || lostIndexList.size() <= 0)
//                && ((list != null && list.size() == count) || (list != null && list.size() > 0 && list.get(
//                        list.size() - 1).getSyncIndex() == 1))) {
        boolean isBreak = Detector.isBreak(currentSyncIndex, syncIndexList);
        if ((!isBreak)
                && ((list != null && list.size() == count) || (list != null && list.size() > 0 && list.get(
                        list.size() - 1).getSyncIndex() == 1))) {
            int icount = 0;
            for (InfoFee infoFee : list) {
                if (status == infoFee.getStatus()) {
                    result.add(infoFee);
                    icount++;
                }
                currentSyncIndex = infoFee.getSyncIndex();
            }

            if (icount < count && currentSyncIndex > 1) {
                listInfoFee(logisticsId, count - icount, currentSyncIndex, status, result);
            }
        } else {
            list = netInfoFeeList(logisticsId, currentSyncIndex, 0, count, status);
            if (list != null && list.size() > 0) {
                result.addAll(list);
            }
        }
    }

    public InfoFee getInfoFee(final String infoFeeId) {
        NetInfoFee net = new NetInfoFee() {

            @Override
            protected int getCommandCode() {
                return CommandConstants.GET_INFO_FEE_REQ;
            }

            @Override
            protected void setRequest(InfoFeeReq.Builder req) {
                req.setInfoFeeId(infoFeeId);
            }
        };

        InfoFeeResp.Builder resp = null;
        try {
            resp = net.request();
        } catch (NetGetException e) {
            e.printStackTrace();
        }

        InfoFee infoFee = null;
        if (net.isSuccess(resp)) {
            ProtoInfoFee eInfoFee = resp.getInfoFee();
            if (null == eInfoFee) {
                return null;
            }

            infoFee = InfoFeeParser.parser(eInfoFee);
            infoFee.setLocalStatus(1);
            InfoFeeDao.getInstance().replace(infoFee);
            LogUtils.d(this, JavaUtils.getString(infoFee));
        }
        return infoFee;
    }

    private List<InfoFee> netInfoFeeList(final Integer logisticsId, final Integer lessSyncIndex,
            final Integer thanSyncIndex, final int count, final int status) {
        NetInfoFee net = new NetInfoFee() {

            @Override
            protected int getCommandCode() {
                return CommandConstants.LIST_INFO_FEE_REQ;
            }

            @Override
            protected void setRequest(InfoFeeReq.Builder req) {
                req.setLogisticsId(logisticsId);
                req.setLessSyncIndex(lessSyncIndex);
                req.setThanSyncIndex(thanSyncIndex);
                req.setStatus(status);
                req.setCount(count);
            }
        };

        InfoFeeResp.Builder resp = null;
        try {
            resp = net.request();

        } catch (NetGetException e) {
            e.printStackTrace();
        }

        List<InfoFee> result = new ArrayList<InfoFee>();
        if (net.isSuccess(resp)) {
            List<ProtoInfoFee> eInfoFees = resp.getInfoFeesList();
            if (null == eInfoFees || eInfoFees.isEmpty()) {
                return null;
            }

            for (ProtoInfoFee eInfoFee : eInfoFees) {
                InfoFee infoFee = InfoFeeParser.parser(eInfoFee);
                infoFee.setLocalStatus(1);
                result.add(infoFee);
                InfoFee localInfoFee = InfoFeeDao.getInstance().queryById(infoFee.getId());
                if (null == localInfoFee) {
                    InfoFeeDao.getInstance().replace(infoFee);
                } else {
                    InfoFeeDao.getInstance().update(infoFee);
                }
            }
        }

        return result;
    }
}
