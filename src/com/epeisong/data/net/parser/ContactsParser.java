package com.epeisong.data.net.parser;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.data.dao.ContactsDao;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
import com.epeisong.logistics.proto.Base.ProtoEMarketScreenBannedMember;
import com.epeisong.logistics.proto.Base.ProtoRContact;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.LogisticAndContact;
import com.epeisong.model.Contacts;
import com.epeisong.utils.LogUtils;

/**
 * 联系人解析
 * @author poet
 *
 */
public class ContactsParser {

    public static Contacts parse(ProtoEBizLogistics logi) {
        Contacts c = new Contacts();
        c.setId(String.valueOf(logi.getId()));
        c.setPhone(logi.getAccountName());
        c.setAddress(logi.getAddress());
        c.setContacts_name(logi.getContact());
        c.setContacts_phone(logi.getMobile1());
        c.setContacts_telephone(logi.getTelephone1());
        c.setEmail(logi.getEmail());
        c.setId(String.valueOf(logi.getId()));
        c.setLogo_url(logi.getLogo());
        c.setPinyin(logi.getPinyin());
        c.setQq(logi.getQq());
        c.setShow_name(logi.getName());
        c.setWechat(logi.getWeixin());
        c.setStar_level(logi.getStarLevel());
        c.setLogistic_type_code(logi.getLogisticsType());
        c.setLogistic_type_name(logi.getLogisticsTypeName());
        c.setUserRole(UserParser.parseUserRole(logi));
        return c;
    }

    public static Contacts parse(LogisticAndContact lc) {
        if (lc != null) {
            if (lc.hasLogistic() && lc.hasContact()) {
                ProtoEBizLogistics logi = lc.getLogistic();
                ProtoRContact relation = lc.getContact();
                Contacts c = parse(logi);
                c.setRelation_id(String.valueOf(relation.getId()));
                c.setRelation_time(relation.getCreateDate());
                c.setUpdate_time(relation.getUpdateDate());
                c.setStatus(relation.getStatus());
                return c;
            } else {
                LogUtils.d("ContactsParse", "!lc.hasLogistic() || !lc.hasContact()");
            }
        }
        return null;
    }

    public static List<Contacts> parse(CommonLogisticsResp.Builder resp) {
        List<Contacts> list = new ArrayList<Contacts>();
        List<LogisticAndContact> contactss = resp.getLogisticAndContactList();
        if (contactss != null && !contactss.isEmpty()) {
            for (LogisticAndContact lc : contactss) {
                Contacts c = ContactsParser.parse(lc);
                if (c != null) {
                    list.add(c);
                }
            }
        }
        return list;
    }

    public static Contacts parse(ProtoEMarketScreenBannedMember logi) {
        Contacts c = ContactsDao.getInstance().queryById(String.valueOf(logi.getBannedLogisticId()));
        if (c != null) {
            c.setMarket_banned_id(logi.getId());
        }
        return c;

    }
}
