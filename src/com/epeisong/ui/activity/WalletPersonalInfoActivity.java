package com.epeisong.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetCustomService;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.proto.Eps.QuestionResp;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.User;
import com.epeisong.model.Wallet;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

public class WalletPersonalInfoActivity extends BaseActivity {
    private Wallet wallet;
    private TextView tv_time;
    private TextView tv_card;
    private TextView tv_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        wallet = (Wallet) getIntent().getSerializableExtra("wallet");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_personal_info);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_card = (TextView) findViewById(R.id.tv_card);
        tv_name = (TextView) findViewById(R.id.tv_name);

        tv_time.setText(DateUtil.long2YMDC(wallet.getUpdateDate()));
        tv_card.setText(wallet.getIdentityNumber() + "");
        tv_name.setText(wallet.getRealName());

        handleContactCustomService();
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "个人信息", null).setShowLogo(false);
    }

    private void getCustomService() {
        showPendingDialog(null);
        AsyncTask<Void, Void, User> task = new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... params) {
                NetCustomService net = new NetCustomService();
                try {
                    QuestionResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        return UserParser.parse(resp.getCustomerService());
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(User result) {
                dismissPendingDialog();
                if (result != null) {
                    Intent intent = new Intent(WalletPersonalInfoActivity.this, ChatRoomActivity.class);
                    intent.putExtra(ChatRoomActivity.EXTRA_REMOTE_ID, result.getId());
                    intent.putExtra(ChatRoomActivity.EXTRA_BUSINESS_TYPE, ChatMsg.business_type_normal);
                    startActivity(intent);
                } else {
                    ToastUtils.showToast("系统繁忙");
                }
            }
        };
        task.execute();
    }

    private void handleContactCustomService() {
        TextView tv_contact_custom_service = (TextView) findViewById(R.id.tv_contact_custom_service);
        tv_contact_custom_service.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence charSequence = tv_contact_custom_service.getText();
        if (charSequence != null && charSequence instanceof Spannable) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(charSequence);
            ssb.clearSpans();

            int end = charSequence.length();
            Spannable spannable = (Spannable) charSequence;
            URLSpan[] spans = spannable.getSpans(0, end, URLSpan.class);
            for (URLSpan span : spans) {
                int start = spannable.getSpanStart(span);
                end = spannable.getSpanEnd(span);
                spannable.removeSpan(span);
                span = new URLSpanNoUnderline(span.getURL());
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        getCustomService();
                    }
                }, start, end, 0);
            }
            tv_contact_custom_service.setText(ssb);
        }
    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}
