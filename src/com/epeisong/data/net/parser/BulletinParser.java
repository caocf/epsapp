package com.epeisong.data.net.parser;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.data.model.CommonMsg;
import com.epeisong.logistics.proto.Base.ProtoEBulletin;
import com.epeisong.logistics.proto.Base.ProtoETempBulletin;
import com.epeisong.logistics.proto.Eps.BulletinResp;
import com.epeisong.logistics.proto.Eps.BulletinServerPushReq;
import com.epeisong.model.Bulletin;
import com.epeisong.utils.LogUtils;

/**
 * 公告信息解析
 * 
 * @author poet
 * 
 */
public class BulletinParser {

    public static Bulletin parse(BulletinServerPushReq.Builder req) {
        ProtoETempBulletin temp = req.getTempBulletin();
        LogUtils.et("temp test:" + temp.getContent());
        Bulletin b = new Bulletin();
        b.setId(String.valueOf(temp.getId()));
        b.setContent(temp.getContent());
        b.setCreate_time(temp.getCreateDate());
        b.setUpdate_time(temp.getCreateDate());
        b.setSender_id(String.valueOf(temp.getOwnerId()));
        b.setSender_name(temp.getOwnerName());
        b.setStatus(CommonMsg.STATUS_UNREAD);
        return b;
    }

    public static Bulletin parse(ProtoEBulletin.Builder builder) {
        Bulletin b = new Bulletin();
        b.setContent(builder.getContent());
        b.setCreate_time(builder.getCreateDate());
        b.setSender_id(String.valueOf(builder.getOwnerId()));
        b.setId(String.valueOf(builder.getId()));
        return b;
    }

    public static List<Bulletin> parse(BulletinResp.Builder resp) {
        if (resp == null) {
            return null;
        }
        List<Bulletin> result = new ArrayList<Bulletin>();
        List<ProtoEBulletin.Builder> list = resp.getProtoEBulletinBuilderList();
        for (ProtoEBulletin.Builder item : list) {
            result.add(parse(item));
        }
        return result;
    }
}
