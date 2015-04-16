package com.epeisong.net.ws.infofee;

import com.epeisong.EpsNetConfig;
import com.epeisong.net.ws.ApiBase;
import com.epeisong.utils.java.JavaUtils;

public class ApiInfoFee extends ApiBase {

    static String url_ws = EpsNetConfig.getTransactionUrl() + "InfoFeeWS/";

    public RetInfoFeeResp UpdateInfoFeeFlowStatus(String uname, String upwd, String infoFeeId, long amount,
            int nextStatus, int type, String paymentPwd) throws Exception {
        String url = url_ws + "UpdateInfoFeeFlowStatus/"
                + JavaUtils.joinString("/", uname, upwd, infoFeeId, amount, nextStatus, type, paymentPwd);
        return getResult(url, RetInfoFeeResp.class);
    }
}
