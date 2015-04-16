package com.epeisong.data.net;

import com.epeisong.logistics.proto.CustomServiceTask.CustomServiceTaskReq;
import com.epeisong.logistics.proto.CustomServiceTask.CustomServiceTaskResp;
import com.google.protobuf.GeneratedMessage.Builder;

public abstract class NetCustomServiceTask extends NetRequestor<CustomServiceTaskReq.Builder, CustomServiceTaskResp.Builder> {

	@Override
	protected String getResult(CustomServiceTaskResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getResult();
	}

	@Override
	protected String getDesc(CustomServiceTaskResp.Builder resp) {
		// TODO Auto-generated method stub
		return resp.getDesc();
	}
	
	@Override
    protected Builder<CustomServiceTaskReq.Builder> getRequest() {
		CustomServiceTaskReq.Builder req = CustomServiceTaskReq.newBuilder();
		if (onSetRequest(req)) {
            return req;
        }
        return null;
    }
	
	protected abstract boolean onSetRequest(CustomServiceTaskReq.Builder req);
	protected abstract int getCommandCode();

}
