package com.epeisong.net.request;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.proto.Eps.GuaranteeReq;
import com.epeisong.logistics.proto.Eps.GuaranteeResp;
import com.epeisong.model.Guarantee;
import com.google.protobuf.GeneratedMessage;

public abstract class NetGuaranteeUpdateStatus extends NetRequestorAsync<GuaranteeReq.Builder, GuaranteeResp.Builder> {
    /*
     * private int freightId; private int newStatus;
     */
	private Guarantee mGuarantee;
    public NetGuaranteeUpdateStatus(XBaseActivity activity, Guarantee f) {
        super(activity);
        mGuarantee = f;
    }
    
    @Override
    protected String getPendingMsg() {
        // TODO Auto-generated method stub
    	return "更新中...";
    }

    @Override
    protected String getResult(GuaranteeResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getResult();
    }

    @Override
    protected String getDesc(GuaranteeResp.Builder resp) {
        // TODO Auto-generated method stub
        return resp.getDesc();
    }

    @Override
    protected GeneratedMessage.Builder<GuaranteeReq.Builder> getRequestBuilder() {
    	GuaranteeReq.Builder req = GuaranteeReq.newBuilder();
        if (onSetRequest(req)) {
            return req;
        }
        return null;
    }

    protected abstract boolean onSetRequest(GuaranteeReq.Builder req);
}
