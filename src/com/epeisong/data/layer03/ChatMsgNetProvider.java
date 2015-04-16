package com.epeisong.data.layer03;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.epeisong.data.dao.ChatMsgDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetChatDetect;
import com.epeisong.data.net.NetChatNewer;
import com.epeisong.data.net.NetChatNewest;
import com.epeisong.data.net.NetChatOlder;
import com.epeisong.data.net.parser.ChatMsgParser;
import com.epeisong.data.utils.ChatUtils;
import com.epeisong.logistics.proto.Eps.ChatReq;
import com.epeisong.logistics.proto.Eps.ChatResp;
import com.epeisong.model.ChatMsg;
import com.epeisong.utils.LogUtils;

public class ChatMsgNetProvider {

    public List<ChatMsg> getNewest(final String remote_id, final long startIndex, final int size,
            final int business_type, final String business_id) throws NetGetException {
        NetChatNewest net = new NetChatNewest() {
            @Override
            protected boolean onSetRequest(ChatReq.Builder req) {
                req.setOppsiteId(Integer.parseInt(remote_id));
                req.setLimitCount(size);
                req.setTableId(business_type);
                // req.setBizId(Integer.parseInt(business_id));
                if (business_id != null) {
                    req.setBizIdStr(business_id);
                }
                if (startIndex > 0) {
                    List<Integer> indexList = new ArrayList<Integer>();
                    indexList.add((int) startIndex);
                    req.addAllIndex(indexList);
                }
                return true;
            }
        };
        ChatResp.Builder resp = net.request();
        // 根据对已删除数据的处理策略不同，解析时需要修改处理
        List<ChatMsg> result = ChatMsgParser.parse(remote_id, resp);
        if (result != null && !result.isEmpty()) {
            String tableName = ChatUtils.getChatMsgTableName(remote_id, business_type, business_id);
            Collections.sort(result);
            boolean bool = ChatMsgDao.getInstance().replaceAll(tableName, result);
            LogUtils.d(this, "replaceAll:" + bool);
        }
        return result;
    }

    public List<ChatMsg> getNewer(final String remote_id, final int size, final int business_type,
            final String business_id, final long last_serial) throws NetGetException {
        NetChatNewer net = new NetChatNewer() {
            @Override
            protected boolean onSetRequest(ChatReq.Builder req) {
                req.setOppsiteId(Integer.parseInt(remote_id));
                req.setLimitCount(size);
                req.setTableId(business_type);
                // req.setBizId(Integer.parseInt(business_id));
                if (business_id != null) {
                    req.setBizIdStr(business_id);
                }
                req.addIndex((int) last_serial);
                return true;
            }
        };
        ChatResp.Builder resp = net.request();
        // 根据对已删除数据的处理策略不同，解析时需要修改处理
        List<ChatMsg> result = ChatMsgParser.parse(remote_id, resp);
        if (result != null && !result.isEmpty()) {
            String tableName = ChatUtils.getChatMsgTableName(remote_id, business_type, business_id);
            ChatMsgDao.getInstance().insertAll(tableName, result);
        }
        Collections.sort(result);
        return result;
    }

    public List<ChatMsg> getOlder(final String remote_id, final int size, final int business_type,
            final String business_id, final long last_serial) throws NetGetException {
        NetChatOlder net = new NetChatOlder() {
            @Override
            protected boolean onSetRequest(ChatReq.Builder req) {
                req.setOppsiteId(Integer.parseInt(remote_id));
                req.setTableId(business_type);
                // req.setBizId(Integer.parseInt(business_id));
                if (business_id != null) {
                    req.setBizIdStr(business_id);
                }
                req.setLimitCount(size);
                req.addIndex((int) last_serial);
                return true;
            }
        };
        ChatResp.Builder resp = net.request();
        // 根据对已删除数据的处理策略不同，解析时需要修改处理
        List<ChatMsg> result = ChatMsgParser.parse(remote_id, resp);
        if (result != null && !result.isEmpty()) {
            String tableName = ChatUtils.getChatMsgTableName(remote_id, business_type, business_id);
            // ChatMsgDao.getInstance().insertAll(tableName, result);
            ChatMsgDao.getInstance().replaceAll(tableName, result);
        }
        Collections.sort(result);
        return result;
    }

    public List<ChatMsg> getLost(final String remote_id, final int business_type, final String business_id,
            final List<Long> serialList) throws NetGetException {
        NetChatDetect net = new NetChatDetect() {
            @Override
            protected boolean onSetRequest(ChatReq.Builder req) {
                req.setOppsiteId(Integer.parseInt(remote_id));
                req.setTableId(business_type);
                // req.setBizId(Integer.parseInt(business_id));
                if (business_id != null) {
                    req.setBizIdStr(business_id);
                }
                List<Integer> lostList = new ArrayList<Integer>();
                for (long l : serialList) {
                    lostList.add((int) l);
                }
                req.addAllIndex(lostList);
                return true;
            }
        };
        ChatResp.Builder resp = net.request();
        // 根据对已删除数据的处理策略不同，解析时需要修改处理
        List<ChatMsg> result = ChatMsgParser.parse(remote_id, resp);
        if (result != null && !result.isEmpty()) {
            String tableName = ChatUtils.getChatMsgTableName(remote_id, business_type, business_id);
            ChatMsgDao.getInstance().insertAll(tableName, result);
        }
        return result;
    }
}
