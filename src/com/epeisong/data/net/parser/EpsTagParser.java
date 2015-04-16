package com.epeisong.data.net.parser;

import java.util.ArrayList;
import java.util.List;

import com.epeisong.logistics.proto.Base.ProtoETag;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.model.EpsTag;

/**
 * 标签相关解析
 * @author poet
 *
 */
public class EpsTagParser {

    private static EpsTag parse(ProtoETag eTag) {
        return new EpsTag(eTag.getId(), eTag.getName());
    }

    public static List<EpsTag> parse(CommonLogisticsResp.Builder resp) {
        List<EpsTag> result = new ArrayList<EpsTag>();
        List<ProtoETag> list = resp.getEtagList();
        for (ProtoETag eTag : list) {
            result.add(parse(eTag));
        }
        return result;
    }
}
