package com.epeisong.net.request;

import java.net.ConnectException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Constants;
import com.epeisong.logistics.net.NetService;
import com.epeisong.logistics.net.NetServiceFactory;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.GeneratedMessage.Builder;
import com.google.protobuf.TextFormat;

/**
 * 网络请求的基类
 * 
 * @author poet
 * @param <RESP>
 * 
 */
@Deprecated
public abstract class NetRequestorAsync<REQ extends GeneratedMessage.Builder<?>, RESP extends GeneratedMessage.Builder<?>>
        implements OnCancelListener {

    public interface OnNetCancelListener {
        void onNetCancel();
    }

    private class MyThread extends Thread {

        private OnNetRequestListener<RESP> listener;

        public MyThread(OnNetRequestListener<RESP> listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                int code = getCommandCode();
                RESP bResp = null;
                switch (code) {
                case CommandConstants.SEND_VERIFICATION_CODE_REQ: // 请求验证码
                case CommandConstants.CREATE_ACCOUNT_REQ: // 创建账号
                case CommandConstants.USER_LOGIN_REQ: // 登录
                    // case CommandConstants.CREATE_LOGISTICS_REQ:
                    bResp = requestOneself();
                    if (bResp == null) {
                        return;
                    }
                    break;
                default:
                    Builder<REQ> builder = getRequestBuilder();
                    if (builder != null) {
                        if (isPrintReq()) {
                            LogUtils.d("NetRequest", TextFormat.printToString(builder));
                        }
                        if (frontendService == null || !frontendService.isAvailable()) {
                            if (mThread != null && !mThread.isInterrupted() && listener != null) {
                                HandlerUtils.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onFail("EPS网络不可用");
                                    }
                                });
                            }
                            return;
                        }
                        bResp = (RESP) frontendService.syncSend(builder, code, getTimeout());
                    } else {
                        ToastUtils.showToastInThread("请求参数错误");
                        return;
                    }
                    break;
                }
                final RESP resp = bResp;
                if (mThread != null && !mThread.isInterrupted() && listener != null) {
                    if (resp == null) {
                        HandlerUtils.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onFail("网络超时");
                            }
                        });
                        return;
                    }
                    HandlerUtils.post(new Runnable() {
                        @Override
                        public void run() {
                            boolean succ = Constants.SUCC.equals(getResult(resp));
                            if (succ) {
                                listener.onSuccess(resp);
                            } else {
                                listener.onFail(getDesc(resp));
                            }
                        }
                    });
                }
            } catch (Throwable e) {
                LogUtils.e("NetRequestor", e);
                if (e instanceof ConnectException) {
                    ToastUtils.showToast("服务器异常");
                }
                if (e instanceof TimeoutException) {
                    ToastUtils.showToastInThread("请求超时");
                }
                if (e instanceof ExecutionException) {
                    if (e.getCause() instanceof ConnectException) {
                        ToastUtils.showToast("连接超时");
                    }
                }
                if (e instanceof ClassCastException) {
                    ToastUtils.showToast("result class cast exception");
                    e.printStackTrace();
                }
                if (!(e instanceof TimeoutException) && e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                // TODO 其他异常处理：网络超时等
                e.printStackTrace();
                if (mThread != null && !mThread.isInterrupted() && listener != null) {
                    HandlerUtils.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError();
                        }
                    });
                }
            } finally {
                if (mShowDialog && mActivity != null) {
                    HandlerUtils.post(new Runnable() {
                        @Override
                        public void run() {
                            mActivity.dismissPendingDialog();
                            mActivity = null;
                        }
                    });
                }
            }

        }
    }

    private NetService frontendService = NetServiceFactory.getInstance();
    private XBaseActivity mActivity;

    private OnNetCancelListener mOnNetCancelListener;

    private boolean mShowDialog = true;

    private Thread mThread;

    public NetRequestorAsync() {
    }

    public NetRequestorAsync(XBaseActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        stopRequest();
        ToastUtils.showToast("请求取消");
        // TODO 关闭网络请求：socket？？
        if (mOnNetCancelListener != null) {
            mOnNetCancelListener.onNetCancel();
        }
    }

    public void request(final OnNetRequestListener<RESP> listener) {
        if (mThread != null) {
            stopRequest();
        }
        if (!SystemUtils.isNetworkAvaliable()) {
            ToastUtils.showToast("网络未连接！");
            HandlerUtils.post(new Runnable() {
                @Override
                public void run() {
                    listener.onError();
                }
            });
            return;
        }
        // check Service init
        mThread = new MyThread(listener);
        if (mShowDialog && mActivity != null) {
            mActivity.showPendingDialog(getPendingMsg(), this);
        }
        mThread.start();
    }

    public void setOnNetCancelListener(OnNetCancelListener listener) {
        mOnNetCancelListener = listener;
    }

    public void setShowDialog(boolean show) {
        mShowDialog = show;
    }

    public void stopRequest() {
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }

    protected abstract int getCommandCode();

    protected abstract String getDesc(RESP resp);

    protected abstract String getPendingMsg();

    protected GeneratedMessage.Builder<REQ> getRequestBuilder() {
        return null;
    }

    protected abstract String getResult(RESP resp);

    protected boolean isPrintReq() {
        return true;
    }

    protected long getTimeout() {
        return 9000;
    }

    protected RESP requestOneself() throws Exception {
        return null;
    }
}
