package com.epeisong.data.net.parser;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEChat;
import com.epeisong.logistics.proto.Eps.ChatResp;
import com.epeisong.model.ChatMsg;

/**
 * 聊天消息解析
 * 
 * @author poet
 * 
 */
public class ChatMsgParser {

    public static ChatMsg parse(ProtoEChat chat) {
        if (chat != null) {
            String id = String.valueOf(chat.getId());
            int business_type = chat.getBizTableId();
            String business_id = chat.getBizIdStr();
            if (TextUtils.isEmpty(business_id)) {
                business_id = String.valueOf(chat.getBizId());
            }
            String business_owner_id = String.valueOf(chat.getBizPublisherId());
            String business_desc = chat.getBizDescription();
            String business_extra = chat.getBizDescriptionStandby();
            long serial = chat.getSyncIndex();
            String sender_id = String.valueOf(chat.getSenderId());
            String sender_name = chat.getSenderName();
            int sender_logistic_type_code = chat.getSenderLogisticTypeCode();
            String sender_logistic_type_name = chat.getSenderLogisticTypeName();
            String receiver_id = String.valueOf(chat.getReceiverId());
            long time = chat.getCreateDate();
            int type = chat.getChatType();
            int conversationA = chat.getConversationA();
            int conversationB = chat.getConversationB();

            ChatMsg chatMsg = new ChatMsg();
            chatMsg.setId(id);
            chatMsg.setBusiness_type(business_type);
            chatMsg.setBusiness_id(business_id);
            chatMsg.setBusiness_owner_id(business_owner_id);
            chatMsg.setBusiness_desc(business_desc);
            chatMsg.setBusiness_extra(business_extra);
            chatMsg.setSerial(serial);
            chatMsg.setSender_id(sender_id);
            chatMsg.setSender_name(sender_name);
            chatMsg.setSender_logistic_type_code(sender_logistic_type_code);
            chatMsg.setSender_logistic_type_name(sender_logistic_type_name);
            chatMsg.setReceiver_id(receiver_id);
            chatMsg.setSend_time(time);
            chatMsg.setType(type);

            chatMsg.setConversationA(conversationA);
            chatMsg.setConversationB(conversationB);

            if (type == ChatMsg.type_text || type == ChatMsg.type_location) {
                chatMsg.setType_data(chat.getContent());
            } else if (type == ChatMsg.type_voice) {
                chatMsg.setType_data(chat.getVoiceUrl());
            } else if (type == ChatMsg.type_pic) {
                chatMsg.setType_data(chat.getPictureUrl());
            }

            if (chatMsg.isSelf()) {
                chatMsg.setRemote_status(chat.getSenderStatus());
            } else {
                chatMsg.setRemote_status(chat.getReceiverStatus());
            }
            return chatMsg;
        }
        return null;
    }

    private static List<ChatMsg> parse(String remote_id, List<ProtoEChat> list, List<Integer> missList) {
        List<ChatMsg> getMsgs = new ArrayList<ChatMsg>();
        if (list != null && !list.isEmpty()) {
            for (ProtoEChat chat : list) {
                ChatMsg msg = parse(chat);
                if (msg != null) {
                    getMsgs.add(msg);
                }
            }
        }
        if (missList != null && !missList.isEmpty()) {
            for (int i : missList) {
                ChatMsg msg = new ChatMsg();
                msg.setSender_id(UserDao.getInstance().getUser().getId());
                msg.setReceiver_id(remote_id);
                msg.setSerial(i);
                msg.setRemote_status(Properties.CHAT_STATUS_DELETED);
                getMsgs.add(msg);
            }
        }
        return getMsgs;
    }

    public static List<ChatMsg> parse(String remote_id, ChatResp.Builder resp) {
        List<ProtoEChat> list = resp.getChatList();
        if (resp.getMissedSyncIndexCount() > 0) {
            return parse(remote_id, list, resp.getMissedSyncIndexList());
        }
        return parse(remote_id, list, null);
    }
}
