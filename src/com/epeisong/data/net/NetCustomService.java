package com.epeisong.data.net;

import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.proto.Eps.QuestionReq;
import com.epeisong.logistics.proto.Eps.QuestionResp;
import com.google.protobuf.GeneratedMessage.Builder;

/**
 * 客服
 * @author poet
 *
 */
public class NetCustomService extends NetRequestor<QuestionReq.Builder, QuestionResp.Builder> {

    @Override
    protected int getCommandCode() {
        return CommandConstants.GET_CUSTOMER_SERVICE_REQ;
    }

    @Override
    protected String getResult(com.epeisong.logistics.proto.Eps.QuestionResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(com.epeisong.logistics.proto.Eps.QuestionResp.Builder resp) {
        return resp.getDesc();
    }

    @Override
    protected Builder<QuestionReq.Builder> getRequest() {
        return QuestionReq.newBuilder();
    }
}
