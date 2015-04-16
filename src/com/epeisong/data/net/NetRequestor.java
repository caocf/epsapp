package com.epeisong.data.net;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import android.text.TextUtils;

import com.epeisong.data.exception.NetGetException;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Constants;
import com.epeisong.logistics.net.NetService;
import com.epeisong.logistics.net.NetServiceFactory;
import com.epeisong.logistics.proto.InfoFee.InfoFeeResp;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.TextFormat;

/**
 * 同步获取网络数据的基类
 * 
 * @author poet
 * 
 * @param <REQ>
 * @param <RESP>
 */
public abstract class NetRequestor<REQ extends GeneratedMessage.Builder<?>, RESP extends GeneratedMessage.Builder<?>> {

    protected abstract int getCommandCode();

    private NetService frontendService = NetServiceFactory.getInstance();

    protected abstract GeneratedMessage.Builder<REQ> getRequest();

    protected long getTimeout() {
        return 9000;
    }

    protected boolean isPrintReq() {
        return true;
    }

    protected boolean isShowToast() {
        return true;
    }

    protected boolean isRequestSelf() {
        return false;
    }

    protected RESP requestSelf() throws Exception {
        return null;
    }

    protected abstract String getResult(RESP resp);

    protected abstract String getDesc(RESP resp);

    public final boolean isSuccess(RESP resp) {
        if (resp == null) {
            return false;
        }
        return Constants.SUCC.equals(getResult(resp));
    }

    /**
     * 成功时，返回response，失败弹出toast并返回null，异常时抛出NetGetException
     * @return
     * @throws NetGetException
     */
    @SuppressWarnings("unchecked")
    public RESP request() throws NetGetException {
        if (!SystemUtils.isNetworkAvaliable()) {
            if (isShowToast()) {
                ToastUtils.showToastInThread("网络未连接！");
            }
            throw new NetGetException();
        }
        if (!isRequestSelf() && !frontendService.isAvailable()) {
            if (isShowToast()) {
                ToastUtils.showToastInThread("EPS网络不可用");
            }
            throw new NetGetException();
        }
        int commandCode = getCommandCode();
        if (commandCode <= 0) {
            ToastUtils.showToastInThread("command error!");
            throw new NetGetException();
        }

        try {
            RESP resp = null;
            if (isRequestSelf()) {
                resp = requestSelf();
            } else {
                GeneratedMessage.Builder<REQ> req = getRequest();
                if (req != null) {
                    if (isPrintReq()) {
                        LogUtils.d("NetRequest", TextFormat.printToString(req));
                    }
                    resp = (RESP) frontendService.syncSend(req, commandCode, getTimeout());
                }
            }
            if (resp == null) {
                throw new NetGetException();
            }

            if (resp instanceof InfoFeeResp.Builder) {
                return resp;
            }

            if (Constants.SUCC.equals(getResult(resp))) {
                return resp;
            } else {
                String desc = getDesc(resp);
                if (!TextUtils.isEmpty(desc)) {
                    ToastUtils.showToastInThread(desc);
                }
                return resp;
            }
        } catch (Exception e) {
            LogUtils.e("NetRequest", e);
            if (e instanceof TimeoutException) {
                if (commandCode != CommandConstants.SYNC_CONTACT_REQ) {
                    ToastUtils.showToastInThread("请求超时");
                }
            }
            if (e instanceof ConnectException || e.getCause() instanceof ConnectException) {
                if (commandCode != CommandConstants.SYNC_CONTACT_REQ) {
                    ToastUtils.showToastInThread("服务器异常");
                }
            }
            if (!(e instanceof SocketTimeoutException) && e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            e.printStackTrace();
            throw new NetGetException();
        }
    }
}
