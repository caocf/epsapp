package com.epeisong.ui.fragment;

import com.epeisong.data.net.NetRequestor;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.GetMediaBytesReq;
import com.epeisong.logistics.proto.Eps.GetMediaBytesResp;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 获取多媒体二进制文件
 * @author poet
 *
 */
public abstract class NetMediaGet extends NetRequestor<GetMediaBytesReq.Builder, GetMediaBytesResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.GET_MEDIA_BYTES_REQ;
    }

    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.GetMediaBytesResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.GetMediaBytesResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected Builder<GetMediaBytesReq.Builder> getRequest() {
        GetMediaBytesReq.Builder req = GetMediaBytesReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return req;
    }

    protected abstract boolean onSetRequest(GetMediaBytesReq.Builder req);
}
