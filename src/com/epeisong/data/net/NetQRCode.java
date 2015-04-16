package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.QRCodeReq;
import com.epeisong.logistics.proto.Eps.QRCodeResp;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 二维码
 * @author poet
 *
 */
public abstract class NetQRCode extends NetRequestor<QRCodeReq.Builder, QRCodeResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.GENERATE_QR_CODE_TO_ADD_CONTACT_REQ;
    }

    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.QRCodeResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.QRCodeResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected Builder<QRCodeReq.Builder> getRequest() {
        QRCodeReq.Builder req = QRCodeReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(QRCodeReq.Builder req);
}
