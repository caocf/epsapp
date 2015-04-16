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
import com.epeisong.logistics.net.NetServiceFactory;
import com.epeisong.model.ChatMsg;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SysUtils;

/**
 * 聊天消息提供者的新算法实现
 * @author poet
 *
 */
public class ChatMsgProviderImplNew implements ChatMsgProvider {

    @Override
    public List<ChatMsg> providerFirst(String remote_id, int size, int business_type, String business_id) {
        String tableName = ChatUtils.getChatMsgTableName(remote_id, business_type, business_id);
        if (!SysUtils.isNetworkConnected() || !NetServiceFactory.getInstance().isAvailable()) {
            return ChatMsgDao.getInstance().queryFirst(tableName, size);
        }
        ChatMsg newest = ChatMsgDao.getInstance().queryNewest(tableName);

        long startIndex = 0;
        if (newest != null) {
            startIndex = newest.getSerial();
        }
        ChatMsgNetProvider net = new ChatMsgNetProvider();
        try {
            List<ChatMsg> netList = net.getNewest(remote_id, startIndex, size, business_type, business_id);
            if (netList != null && !netList.isEmpty()) {
                return netList;
            }
        } catch (NetGetException e) {
            e.printStackTrace();
        }
        List<ChatMsg> result = new ArrayList<ChatMsg>();
        List<ChatMsg> newestList = ChatMsgDao.getInstance().queryFirst(tableName, 10);
        Collections.reverse(newestList);
        LogUtils.d(this, newestList.toString());
        if (newestList != null && !newestList.isEmpty()) {
            ChatMsg last = null;
            for (ChatMsg msg : newestList) {
                if (last == null) {
                    last = msg;
                    result.add(msg);
                } else {
                    if (last.getSerial() - msg.getSerial() > 1) {
                        break;
                    } else {
                        result.add(msg);
                        last = msg;
                    }
                }
            }
            Collections.reverse(result);
            return result;
        }
        return null;
    }

    @Override
    public List<ChatMsg> provideNewer(String remote_id, int size, int business_type, String business_id,
            long last_time, long last_serial) {
        String tableName = ChatUtils.getChatMsgTableName(remote_id, business_type, business_id);
        return ChatMsgDao.getInstance().queryNewer(tableName, last_time, last_serial, size);
    }

    @Override
    public List<ChatMsg> provideOlder(String remote_id, int size, int business_type, String business_id,
            long last_time, long last_serial) {
        String tableName = ChatUtils.getChatMsgTableName(remote_id, business_type, business_id);
        List<ChatMsg> result = ChatMsgDao.getInstance().queryOlder(tableName, last_time, last_serial, size);
        if (result == null || result.isEmpty()) {
            return getOlderFromNet(remote_id, size, business_type, business_id, last_time, last_serial);
        }
        List<Long> serialList = new ArrayList<Long>();
        for (ChatMsg msg : result) {
            long serial = msg.getSerial();
            if (serial > 0) {
                serialList.add(serial);
            }
        }
        if (Detector.isBreak(last_serial, serialList)) {
            result = getOlderFromNet(remote_id, size, business_type, business_id, last_time, last_serial);
        }
        if (result != null && !result.isEmpty()) {
            Collections.sort(result);
        }
        return result;
    }

    private List<ChatMsg> getOlderFromNet(String remote_id, int size, int business_type, String business_id,
            long last_time, long last_serial) {
        ChatMsgNetProvider net = new ChatMsgNetProvider();
        try {
            return net.getOlder(remote_id, size, business_type, business_id, last_serial);
        } catch (NetGetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<ChatMsg> getFirstFromDb(String remote_id, int size, int business_type, String business_id) {
        String tableName = ChatUtils.getChatMsgTableName(remote_id, business_type, business_id);
        List<ChatMsg> result = ChatMsgDao.getInstance().queryFirst(tableName, size);
        if (result == null || result.isEmpty()) {
            // ChatMsgNetProvider p = new ChatMsgNetProvider();
            // try {
            // return p.getNewest(remote_id, size, business_type, business_id);
            // } catch (NetGetException e) {
            // e.printStackTrace();
            // }
            return result;
        }
        List<Long> serialList = new ArrayList<Long>();
        for (ChatMsg msg : result) {
            long serial = msg.getSerial();
            if (serial > 0) {
                serialList.add(serial);
            }
        }
        if (serialList.size() < size || Detector.isBreak(0, serialList)) {
            // 网络取
            ChatMsgNetProvider net = new ChatMsgNetProvider();
            try {
                List<ChatMsg> newest = net.getNewest(remote_id, 0, size, business_type, business_id);
                if (!newest.isEmpty()) {
                    return newest;
                }
            } catch (NetGetException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
