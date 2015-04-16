package com.epeisong.data.utils;

import com.epeisong.data.dao.UserDao;
import com.epeisong.model.ChatMsg;

/**
 * 聊天相关的工具类
 * 
 * @author poet
 * 
 */
public class ChatUtils {

    public static String getChatMsgTableName(ChatMsg msg) {
        return getChatMsgTableName(msg.getRemoteId(), msg.getBusiness_type(), msg.getBusiness_id());
    }

    public static String getChatMsgTableName(String remote_id, int business_type, String business_id) {
        switch (business_type) {
        case ChatMsg.business_type_normal:
            return ChatUtils.getChatMsgTableName(UserDao.getInstance().getUser().getId(), remote_id);
        case ChatMsg.business_type_freight:
            return ChatUtils.getChatMsgTableNameForFreight(UserDao.getInstance().getUser().getId(), remote_id,
                    business_id);
        case ChatMsg.business_type_info_fee:
            return ChatUtils.getChatMsgTableNameForInfoFee(UserDao.getInstance().getUser().getId(), remote_id,
                    business_id);
        case ChatMsg.business_type_complaint:
        	return ChatUtils.getChatMsgTableNameForComplaint(UserDao.getInstance().getUser().getId(), remote_id,
                    business_id);
        case ChatMsg.business_type_withdraw:
        	return ChatUtils.getChatMsgTableNameForWithdrawal(UserDao.getInstance().getUser().getId(), remote_id,
                    business_id);
        default:
            return null;
        }
    }

    public static String getChatMsgTableName(String mine_id, String remote_id) {
        return "chatmsg_" + mine_id + "_" + remote_id;
    }

    public static String getChatMsgTableNameForFreight(String mine_id, String remote_id, String freight_id) {
        return getChatMsgTableName(mine_id, remote_id) + "_freight_" + freight_id;
    }
    
    public static String getChatMsgTableNameForComplaint(String mine_id, String remote_id, String complaint_id) {
        return getChatMsgTableName(mine_id, remote_id) + "_complaint_" + complaint_id;
    }
    
    public static String getChatMsgTableNameForWithdrawal(String mine_id, String remote_id, String withdrawal_id) {
        return getChatMsgTableName(mine_id, remote_id) + "_withdrawal_" + withdrawal_id;
    }

    public static String getChatMsgTableNameForInfoFee(String mine_id, String remote_id, String infoFee_id) {
        return getChatMsgTableName(mine_id, remote_id) + "_info_fee_" + infoFee_id;
    }

    public static boolean isFreightAdvisoryChatRoom(String chatRoomId, String freightId) {
        return chatRoomId.endsWith("_freight_" + freightId);
    }

    public static boolean isInfoFeeAdvisoryChatRoom(String chatRoomId, String infoFeeId) {
        return chatRoomId.endsWith("_info_fee_" + infoFeeId);
    }
    
    public static boolean isComplaintAdvisoryChatRoom(String chatRoomId, String complaintId) {
        return chatRoomId.endsWith("_complaint_" + complaintId);
    }
}
