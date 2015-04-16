package com.epeisong.model;

import java.io.Serializable;

import com.epeisong.net.ws.utils.WithdrawTask;

/**
 * 针对某件事情聊天的数据对象
 * @author poet
 *
 */
public class BusinessChatModel implements Serializable {

    private static final long serialVersionUID = -4199592730560539300L;

    private int business_type;
    private String business_id;
    private String business_owner_id;
    private String business_desc;
    private String business_extra;

    public int getBusiness_type() {
        return business_type;
    }

    public void setBusiness_type(int business_type) {
        this.business_type = business_type;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getBusiness_owner_id() {
        return business_owner_id;
    }

    public void setBusiness_owner_id(String business_owner_id) {
        this.business_owner_id = business_owner_id;
    }

    public String getBusiness_desc() {
        return business_desc;
    }

    public void setBusiness_desc(String business_desc) {
        this.business_desc = business_desc;
    }

    public String getBusiness_extra() {
        return business_extra;
    }

    public int getBusiness_extra_int() {
        int extra = -1;
        try {
            extra = Integer.parseInt(business_extra);
        } catch (NumberFormatException e) {

        }
        return extra;
    }

    public void setBusiness_extra(String business_extra) {
        this.business_extra = business_extra;
    }

    @Override
    public String toString() {
        return "business_id:" + business_id + "\nbusiness_type:" + business_type + "\nbusiness_desc:" + business_desc;
    }

    public static BusinessChatModel getFromChatMsg(ChatMsg msg) {
        BusinessChatModel model = new BusinessChatModel();
        model.setBusiness_type(msg.getBusiness_type());
        model.setBusiness_id(msg.getBusiness_id());
        model.setBusiness_desc(msg.getBusiness_desc());
        model.setBusiness_extra(msg.getBusiness_extra());
        model.setBusiness_owner_id(msg.getBusiness_owner_id());
        return model;
    }

    public static BusinessChatModel getFromFreight(Freight f) {
        BusinessChatModel model = new BusinessChatModel();
        model.setBusiness_type(ChatMsg.business_type_freight);
        model.setBusiness_id(f.getId());
        model.setBusiness_desc(f.getStart_region() + "-" + f.getEnd_region());
        model.setBusiness_extra(String.valueOf(f.getType()));
        model.setBusiness_owner_id(f.getUser_id());
        return model;
    }

    public static BusinessChatModel getFromInfoFee(InfoFee infoFee) {
        BusinessChatModel model = new BusinessChatModel();
        model.setBusiness_type(ChatMsg.business_type_info_fee);
        model.setBusiness_id(infoFee.getId());
        model.setBusiness_desc(infoFee.getFreightAddr());
        return model;
    }
    
    public static BusinessChatModel getFromWithdraw(WithdrawTask withdraw) {
        BusinessChatModel model = new BusinessChatModel();
        model.setBusiness_type(ChatMsg.business_type_withdraw);
        model.setBusiness_id(withdraw.getId().toString());
        return model;
    }
    
    public static BusinessChatModel getFromComplaint(Complaint complaint) {
        BusinessChatModel model = new BusinessChatModel();
        model.setBusiness_type(ChatMsg.business_type_complaint);
        model.setBusiness_id(complaint.getId());
        model.setBusiness_owner_id(complaint.getOwner_id());
        return model;
    }
}
