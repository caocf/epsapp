package com.epeisong.data.net;

import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.parser.FreightParser;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.proto.Eps.FreightResp.Builder;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.model.Freight;
import com.google.protobuf.GeneratedMessage;

public abstract class NetFreightDetail extends NetRequestor<FreightReq.Builder, FreightResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.GET_FREIGHT_REQ;
    }

    @Override
    protected String getResult(Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected GeneratedMessage.Builder<FreightReq.Builder> getRequest() {
        FreightReq.Builder req = FreightReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(FreightReq.Builder req);

    public Freight requestFreight() {
        try {
            Builder resp = request();
            if (resp != null) {
                return FreightParser.parseSingle(resp);
            }
        } catch (NetGetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
