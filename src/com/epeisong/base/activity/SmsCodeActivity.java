package com.epeisong.base.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetGetCode;
import com.epeisong.logistics.common.Constants;
import com.epeisong.logistics.proto.Eps.SendVerificationCodeResp;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 获取短信验证码的基础类
 * @author poet
 *
 */
public abstract class SmsCodeActivity extends BaseActivity {

    private BroadcastReceiver mSmsReceiver;

    private Button mGetCodeBtn;

    protected abstract void onSendRequest(SendVerificationCodeResp.Builder resp);

    protected abstract void onReceiveSmsCode(String code);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerSmsReceiver();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mSmsReceiver);
        super.onDestroy();
    }

    private void registerSmsReceiver() {
        mSmsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtils.et("onReceive");
                Object[] objs = (Object[]) intent.getExtras().get("pdus");
                for (Object obj : objs) {
                    byte[] pdu = (byte[]) obj;
                    SmsMessage sms = SmsMessage.createFromPdu(pdu);
                    String message = sms.getMessageBody();
                    LogUtils.et("message:" + message);
                    // String from = sms.getOriginatingAddress();
                    if (message != null && message.contains("南京易配送信息")) {
                        Pattern pattern = Pattern.compile("[^0-9]");
                        Matcher matcher = pattern.matcher(message);
                        String result = matcher.replaceAll("").trim();
                        onReceiveSmsCode(result);
                        break;
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(mSmsReceiver, filter);
    }

    protected void addTextWatcher(EditText source, TextView target) {
        source.addTextChangedListener(new TextChangeListener(target));
    }

    protected void addTextWatcher(EditText source, View target, int visibility) {
        source.addTextChangedListener(new TextChangeListener(target).setEffectViewVisibility(visibility));
    }

    protected void getSmsCode(final Button getCodeBtn, final String phone, final int purpose) {
        if (TextUtils.isEmpty(phone)) {
            return;
        }
        mGetCodeBtn = getCodeBtn;
        showPendingDialog(null);
        AsyncTask<Void, Void, SendVerificationCodeResp.Builder> task = new AsyncTask<Void, Void, SendVerificationCodeResp.Builder>() {
            @Override
            protected SendVerificationCodeResp.Builder doInBackground(Void... params) {
                NetGetCode net = new NetGetCode() {
                    @Override
                    protected int getPurpose() {
                        return purpose;
                    }

                    @Override
                    protected String getPhone() {
                        return phone;
                    }
                };
                try {
                    return net.request();
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(SendVerificationCodeResp.Builder resp) {
                dismissPendingDialog();
                onSendRequest(resp);
                if (resp != null && Constants.SUCC.equals(resp.getResult())) {
                    long remain = resp.getRemainingTime();
                    if (remain > 0 && mGetCodeBtn != null) {
                        new MyCountDownTimer(remain * 1000, 1000).start();
                    }
                }
            }
        };
        task.execute();
    }

    class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            mGetCodeBtn.setText("获取验证码(" + millisUntilFinished / 1000 + ")");
        }

        @Override
        public void onFinish() {
            mGetCodeBtn.setText("获取验证码");
        }
    }

    class TextChangeListener implements TextWatcher {

        View effectView;

        int visibility = View.GONE;

        public TextChangeListener(View effectView) {
            this.effectView = effectView;
        }

        TextChangeListener setEffectViewVisibility(int visibility) {
            this.visibility = visibility;
            return this;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (effectView != null) {
                effectView.setVisibility(visibility);
            }
        }

    }
}
