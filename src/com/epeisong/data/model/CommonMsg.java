package com.epeisong.data.model;

import com.epeisong.model.BusinessChatModel;

/**
 * 通用消息类model
 * 
 * @author poet
 * 
 */
public class CommonMsg extends BaseListModel {

    public static final int STATUS_UNREAD = 0;
    public static final int STATUS_READED = 1;

    private String sender_id;
    private String sender_name;
    private int sender_logistic_type;
    private long send_time;
    private String content;
    private String extra_01; // 扩展字段，对于车源货源，用来区分是车源还是货源
    private String extra_02; // 对于车源货源，用来表示起始地点
    private String extra_03; // 对应车源货源，表示目标地点
    private String extra_04;
    private String extra_05;

    private BusinessChatModel businessChatModel;

    /**
     * 车源货源咨询： 
     *  extra_01：业务类型， 
     *  extra_02：业务id 
     *  extra_03：业务desc 
     *  extra_04：业务extra
     *  extra_05：业务所属id
     */

    /**
     * extra_01：(1)车源货源：表示车源货源的type (2)聊天：表示聊天的业务类型
     */

    /**
     * extra_02：(1) 车源货源：起始地点 （2） 聊天：表示聊天对应的业务id
     */

    /**
     * extra_03： （1）车源货源：目标地点 （2）聊天：暂无
     */

    /**
     * extra_04: （1）车源货源：联系人id
     * 
     * @return
     */

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public int getSender_logistic_type() {
        return sender_logistic_type;
    }

    public void setSender_logistic_type(int sender_logistic_type) {
        this.sender_logistic_type = sender_logistic_type;
    }

    public long getSend_time() {
        return send_time;
    }

    public void setSend_time(long send_time) {
        this.send_time = send_time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtra_01() {
        return extra_01;
    }

    public void setExtra_01(String extra_01) {
        this.extra_01 = extra_01;
    }

    public String getExtra_02() {
        return extra_02;
    }

    public void setExtra_02(String extra_02) {
        this.extra_02 = extra_02;
    }

    public String getExtra_03() {
        return extra_03;
    }

    public void setExtra_03(String extra_03) {
        this.extra_03 = extra_03;
    }

    public String getExtra_04() {
        return extra_04;
    }

    public void setExtra_04(String extra_04) {
        this.extra_04 = extra_04;
    }

    public String getExtra_05() {
        return extra_05;
    }

    public void setExtra_05(String extra_05) {
        this.extra_05 = extra_05;
    }

    public BusinessChatModel getBusinessChatModel() {
        return businessChatModel;
    }

    public void setBusinessChatModel(BusinessChatModel businessChatModel) {
        this.businessChatModel = businessChatModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof CommonMsg)) {
            return false;
        }
        CommonMsg msg = (CommonMsg) o;
        return this.getDataType() == msg.getDataType() && this.getId().equals(msg.getId());
    }
}
