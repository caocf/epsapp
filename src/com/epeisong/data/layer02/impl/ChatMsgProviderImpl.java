package com.epeisong.data.layer02.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.epeisong.data.dao.ChatMsgDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.layer02.abs.ChatMsgProvider;
import com.epeisong.data.layer03.ChatMsgNetProvider;
import com.epeisong.data.utils.ChatUtils;
import com.epeisong.data.utils.Detector;
import com.epeisong.model.ChatMsg;
import com.epeisong.utils.LogUtils;

/**
 * 聊天消息提供者的原始实现
 * @author poet
 *
 */
public class ChatMsgProviderImpl implements ChatMsgProvider {

    @Override
    public List<ChatMsg> providerFirst(final String remote_id, final int size, final int business_type,
            final String business_id) {
        String tableName = ChatUtils.getChatMsgTableName(remote_id, business_type, business_id);
        List<ChatMsg> result = ChatMsgDao.getInstance().queryFirst(tableName, size);
        if (result == null || result.isEmpty()) {
            return null;
        }
        List<Long> serialList = new ArrayList<Long>();
        for (ChatMsg msg : result) {
            long serial = msg.getSerial();
            if (serial > 0) {
                serialList.add(serial);
            }
        }
        ChatMsgNetProvider netProvider = new ChatMsgNetProvider();
        // 检漏算法：将所有不连续的serial获取到（包括已被删除的），带优化处理：同com.epeisong.data.net.parser包中的解析联合处理
        List<Long> lost = Detector.detect(-1, size, serialList);
        if (lost != null && !lost.isEmpty()) {
            try {
                List<ChatMsg> lostData = netProvider.getLost(remote_id, business_type, business_id, lost);
                if (lostData != null && !lostData.isEmpty()) {
                    result.addAll(lostData);
                }
            } catch (NetGetException e) {
                e.printStackTrace();
                return null;
            }
        }
        Collections.sort(result);
        if (result.size() > size) {
            return result.subList(result.size() - size, result.size());
        }
        return result;
    }

    @Override
    public List<ChatMsg> provideNewer(final String remote_id, final int size, final int business_type,
            final String business_id, long last_time, long last_serial) {
        String tableName = ChatUtils.getChatMsgTableName(remote_id, business_type, business_id);
        List<ChatMsg> result = ChatMsgDao.getInstance().queryNewer(tableName, last_time, last_serial, size);
        if (result == null || result.isEmpty()) {
            return null;
        }
        List<Long> serialList = new ArrayList<Long>();
        for (ChatMsg msg : result) {
            long serial = msg.getSerial();
            if (serial > 0) {
                serialList.add(serial);
            }
        }
        ChatMsgNetProvider netProvider = new ChatMsgNetProvider();
        // 检漏算法：将所有不连续的serial获取到（包括已被删除的），带优化处理：同com.epeisong.data.net.parser包中的解析联合处理
        List<Long> lost = Detector.detect(-1, size, serialList);
        if (lost != null && !lost.isEmpty()) {
            try {
                List<ChatMsg> lostData = netProvider.getLost(remote_id, business_type, business_id, lost);
                if (lostData != null && !lostData.isEmpty()) {
                    result.addAll(lostData);
                }
            } catch (NetGetException e) {
                e.printStackTrace();
                return null;
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public List<ChatMsg> provideOlder(final String remote_id, final int size, final int business_type,
            final String business_id, long last_time, long last_serial) {

        ChatMsgNetProvider netProvider = new ChatMsgNetProvider();

        String tableName = ChatUtils.getChatMsgTableName(remote_id, business_type, business_id);
        List<ChatMsg> result = ChatMsgDao.getInstance().queryOlder(tableName, last_time, last_serial, size);
        if (result == null || result.isEmpty()) {
            LogUtils.et("provideOld_all_from_net:" + size);
            try {
                return netProvider.getOlder(remote_id, size, business_type, business_id, last_serial);
            } catch (NetGetException e) {
                e.printStackTrace();
                return null;
            }
        }
        List<Long> serialList = new ArrayList<Long>();
        for (ChatMsg msg : result) {
            long serial = msg.getSerial();
            if (serial > 0) {
                serialList.add(serial);
            }
        }

        // 检漏算法：将所有不连续的serial获取到（包括已被删除的），带优化处理：同com.epeisong.data.net.parser包中的解析联合处理
        List<Long> lost = Detector.detect(last_serial, size, serialList);
        if (lost != null && !lost.isEmpty()) {
            try {
                List<ChatMsg> lostData = netProvider.getLost(remote_id, business_type, business_id, lost);
                if (lostData != null && !lostData.isEmpty()) {
                    result.addAll(lostData);
                }
            } catch (NetGetException e) {
                e.printStackTrace();
                return null;
            }
        }
        Collections.sort(result);

        int dSize = size - result.size();
        if (dSize > 0 && result.get(0).getSerial() > 1) {
            LogUtils.et("ProvideOld_dSize_from_net:" + dSize);
            long lastSerial = result.get(0).getSerial();
            try {
                List<ChatMsg> netGetData = netProvider.getOlder(remote_id, dSize, business_type, business_id,
                        lastSerial);
                if (netGetData != null && !netGetData.isEmpty()) {
                    result.addAll(netGetData);
                    Collections.sort(result);
                }
            } catch (NetGetException e) {
                e.printStackTrace();
                return null;
            }
        }
        return result;
    }
}
