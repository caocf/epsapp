package com.epeisong.net.request;

import com.epeisong.EpsNetConfig;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.net.NetService;
import com.epeisong.logistics.net.NetServiceFactory;
import com.epeisong.logistics.proto.Eps.SendVerificationCodeReq;
import com.epeisong.logistics.proto.Eps.SendVerificationCodeResp;

/**
 * 请求验证码
 * 
 * @author poet
 * 
 */
public class NetGetCode extends NetRequestorAsync<SendVerificationCodeReq.Builder, SendVerificationCodeResp.Builder> {

    private String mPhone;
    private int mPurpose;

    public NetGetCode(XBaseActivity activity, String phone, int purpose) {
        super(activity);
        this.mPhone = phone;
        this.mPurpose = purpose;
    }

    @Override
    protected int getCommandCode() {
        return CommandConstants.SEND_VERIFICATION_CODE_REQ;
    }

    @Override
    protected String getPendingMsg() {
        return "请求验证码...";
    }

    @Override
    protected SendVerificationCodeResp.Builder requestOneself() throws Exception {
        NetService frontendService = NetServiceFactory.getInstance();
        return frontendService.sendVerificationCode(EpsNetConfig.getHost(), EpsNetConfig.PORT, mPhone, mPurpose, 9000);
    }

    @Override
    protected String getResult(SendVerificationCodeResp.Builder resp) {
        return resp.getResult();
    }

    @Override
    protected String getDesc(SendVerificationCodeResp.Builder resp) {
        return resp.getDesc();
    }
}
