package com.epeisong.data.layer01;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.epeisong.data.dao.FreightDao;
import com.epeisong.data.layer02.BulletinProvider;
import com.epeisong.data.layer02.ChatRoomProvider;
import com.epeisong.data.model.BaseListModel.DataType;
import com.epeisong.data.model.CommonMsg;
import com.epeisong.model.Bulletin;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.ChatRoom;
import com.epeisong.model.Freight;
import com.epeisong.model.FreightForward;
import com.epeisong.utils.LogUtils;

public class CommonMsgProvider {

    public List<CommonMsg> provideFirst(int size) {

        LogUtils.saveLog("CommonMsgProvider", "provideFirst.entry");

        List<CommonMsg> data = new ArrayList<CommonMsg>();

        // 公告
        BulletinProvider bulletinProvider = new BulletinProvider();
        List<Bulletin> bulletins = bulletinProvider.provideFirst(size);
        if (bulletins != null && !bulletins.isEmpty()) {
            for (Bulletin b : bulletins) {
                data.add(convert(b));
            }
        }

        // 聊天消息
        ChatRoomProvider chatRoomProvider = new ChatRoomProvider();
        List<ChatRoom> rooms = chatRoomProvider.provideFirst(size);
        if (rooms != null && !rooms.isEmpty()) {
            for (ChatRoom room : rooms) {
                data.add(convert(room));
            }
        }

        // 车源货源
        List<Freight> fList = FreightDao.getInstance().queryFirst(size);
        if (fList != null) {
            for (Freight f : fList) {
                data.add(convert(f));
            }
        }

        LogUtils.saveLog("CommonMsgProvider", "provideFirst.return");
        List<CommonMsg> result = handleResult(data, size);
        return result;
    }

    public List<CommonMsg> provideNewer(long last_time, ProviderHelper helper, int size) {

        List<CommonMsg> data = new ArrayList<CommonMsg>();

        // 聊天消息
        ChatRoomProvider chatRoomProvider = new ChatRoomProvider();
        List<ChatRoom> rooms = chatRoomProvider.provideNewer(last_time, size);
        if (rooms != null && !rooms.isEmpty()) {
            for (ChatRoom room : rooms) {
                data.add(convert(room));
            }
        }

        // 车源货源
        String edge_id = helper.getFreight_id_newer();
        List<Freight> fList = FreightDao.getInstance().queryNewer(last_time, edge_id, size);
        if (fList != null) {
            for (Freight f : fList) {
                data.add(convert(f));
            }
        }

        return handleResult(data, size);
    }

    public List<CommonMsg> provideOlder(long last_time, ProviderHelper helper, int size) {
        List<CommonMsg> data = new ArrayList<CommonMsg>();

        // 聊天消息
        ChatRoomProvider chatRoomProvider = new ChatRoomProvider();
        List<ChatRoom> rooms = chatRoomProvider.provideOlder(last_time, size);
        if (rooms != null && !rooms.isEmpty()) {
            for (ChatRoom room : rooms) {
                data.add(convert(room));
            }
        }

        // 车源货源
        String edge_id = helper.getFreight_id_older();
        List<Freight> fList = FreightDao.getInstance().queryOlder(last_time, edge_id, size);
        if (fList != null) {
            for (Freight f : fList) {
                data.add(convert(f));
            }
        }

        return handleResult(data, size);
    }

    private List<CommonMsg> handleResult(List<CommonMsg> data, int size) {
        Collections.sort(data, new Comparator<CommonMsg>() {
            @Override
            public int compare(CommonMsg lhs, CommonMsg rhs) {
                if (rhs == null) {
                    return 1;
                }
                int dTime = (int) (lhs.getSend_time() - rhs.getSend_time());
                if (dTime == 0) {
                    return (int) (lhs.getIdOrSerial() - rhs.getIdOrSerial());
                }
                return dTime;
            }
        });
        Collections.reverse(data);
        if (data.size() > size) {
            data = data.subList(0, size);
        }

        return data;
    }

    public static CommonMsg convert(Bulletin b) {
        CommonMsg msg = new CommonMsg();
        msg.setId(b.getId());
        msg.setDataType(DataType.BULLETIN);
        msg.setSender_id(b.getSender_id());
        msg.setSender_name(b.getSender_name());
        msg.setSend_time(b.getUpdate_time());
        msg.setContent(b.getContent());
        msg.setExtra_01(String.valueOf(b.getStatus()));
        return msg;
    }

    public static CommonMsg convert(ChatRoom room) {
        CommonMsg msg = new CommonMsg();
        msg.setId(room.getId());
        msg.setDataType(DataType.CHAT);
        msg.setSender_id(room.getRemote_id());
        msg.setSender_name(room.getRemote_name());
        msg.setSender_logistic_type(room.getRemote_logistic_type_code());
        msg.setSend_time(room.getUpdate_time());
        msg.setContent(room.getLast_msg());
        String new_msg_count = null;
        int count = room.getNew_msg_count();
        if (count > 0) {
            if (count > 99) {
                new_msg_count = "99+";
            } else {
                new_msg_count = String.valueOf(count);
            }
        }
        msg.setExtra_01(new_msg_count);
        if (room.getBusiness_type() != ChatMsg.business_type_normal) {
            BusinessChatModel model = new BusinessChatModel();
            model.setBusiness_type(room.getBusiness_type());
            model.setBusiness_id(room.getBusiness_id());
            model.setBusiness_desc(room.getBusiness_desc());
            model.setBusiness_extra(room.getBusiness_extra());
            model.setBusiness_owner_id(room.getBusiness_owner_id());
            msg.setBusinessChatModel(model);
        }
        return msg;
    }
    
    public static CommonMsg convert(Freight f) {
        CommonMsg msg = new CommonMsg();
        msg.setId(f.getId());
        msg.setIdOrSerial(f.getSerial());
        msg.setDataType(DataType.FREIGHT);
        msg.setSender_id(f.getUser_id());
        msg.setSender_name(f.getOwner_name());
        msg.setSend_time(f.getCreate_time());
        msg.setContent(f.getDesc());
        msg.setExtra_01(String.valueOf(f.getType()));
        msg.setExtra_02(f.getStart_region());
        msg.setExtra_03(f.getEnd_region());
        msg.setExtra_04(f.getUser_id());
        BusinessChatModel model = new BusinessChatModel();
        model.setBusiness_type(ChatMsg.business_type_freight);
        model.setBusiness_id(f.getId());
        model.setBusiness_desc(f.getStart_region() + "-" + f.getEnd_region());
        model.setBusiness_extra(String.valueOf(f.getType()));
        model.setBusiness_owner_id(f.getUser_id());
        msg.setBusinessChatModel(model);
        return msg;
    }

    public static CommonMsg convert(FreightForward ff) {
        CommonMsg msg = new CommonMsg();
        msg.setId(ff.getFreight().getId());
        msg.setIdOrSerial(ff.getSerial());
        // msg.setDataType(DataType.FREIGHT_FORWARD);
        msg.setSender_id(ff.getUser_id());
        msg.setSender_name(ff.getUser_show_name());
        msg.setSend_time(ff.getForward_create_time());
        msg.setContent(ff.getFreight().getDesc());
        msg.setExtra_01(String.valueOf(ff.getFreight().getType()));
        msg.setExtra_02(ff.getFreight().getStart_region());
        msg.setExtra_03(ff.getFreight().getEnd_region());
        msg.setExtra_04(ff.getFreight().getUser_id());
        BusinessChatModel model = new BusinessChatModel();
        model.setBusiness_type(ChatMsg.business_type_freight);
        model.setBusiness_id(ff.getFreight().getId());
        model.setBusiness_desc(ff.getFreight().getStart_region() + "-" + ff.getFreight().getEnd_region());
        model.setBusiness_extra(String.valueOf(ff.getFreight().getType()));
        model.setBusiness_owner_id(ff.getFreight().getUser_id());
        msg.setBusinessChatModel(model);
        return msg;
    }

    public static class ProviderHelper {

        private String freight_id_older;
        private String freight_id_newer;

        public String getFreight_id_older() {
            return freight_id_older;
        }

        public void setFreight_id_older(String freight_id_older) {
            this.freight_id_older = freight_id_older;
        }

        public String getFreight_id_newer() {
            return freight_id_newer;
        }

        public void setFreight_id_newer(String freight_id_newer) {
            this.freight_id_newer = freight_id_newer;
        }

    }
}
