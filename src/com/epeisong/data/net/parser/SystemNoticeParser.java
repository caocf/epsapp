package com.epeisong.data.net.parser;

import java.util.List;

import com.epeisong.logistics.proto.Base.ProtoESystemNotice;
import com.epeisong.logistics.proto.Base.ProtoESystemNotice.Builder;
import com.epeisong.logistics.proto.Eps.SystemNoticeReq;
import com.epeisong.model.SystemNotice;

public class SystemNoticeParser {

    public static SystemNotice parseSingle(SystemNoticeReq.Builder req) {
        List<Builder> list = req.getSystemNoticeBuilderList();
        if (list != null && !list.isEmpty()) {
            return parse(list.get(0));
        }
        return null;
    }

    public static SystemNotice parse(ProtoESystemNotice.Builder b) {
        SystemNotice notice = new SystemNotice();
        notice.setId(b.getId());
        notice.setType(b.getType());
        notice.setContent(notice.getContent());
        notice.setCreateDate(b.getCreateDate());
        if (b.hasThumbnail()) { }
        notice.setThumbnailType(b.getThumbnailType());
        notice.setThumbnailUrl(b.getThumbnailUrl());
        notice.setTitle(b.getTitle());
        notice.setWebUrl(b.getWebUrl());
        return notice;
    }
}
