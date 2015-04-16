package com.zxing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.PhoneContactsDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetAddContacts;
import com.epeisong.data.net.parser.ContactsParser;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq.Builder;
import com.epeisong.model.Contacts;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.google.protobuf.TextFormat;

/**
 * 扫一扫结果
 * @author poet
 *
 */
public class CaptureResultActivity extends BaseActivity {

    public static final String EXTRA_RESULT = "result";

    private String mResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResult = getIntent().getStringExtra(EXTRA_RESULT);
        TextView tv = new TextView(this);
        setContentView(tv);
        if (TextUtils.isEmpty(mResult)) {
            tv.setText("无结果");
        } else if (mResult.startsWith("http://www.epeisong.com/addcontact")) {
            addContacts(mResult);
        } else {
            tv.setText(mResult);
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "扫一扫结果").setShowLogo(false);
    }

    private void addContacts(final String qrUrl) {
        showPendingDialog("添加联系人...");
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                NetAddContacts net = new NetAddContacts() {
                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setQrCodeAddContactURL(qrUrl);
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        Contacts c = ContactsParser.parse(resp.getBizLogistics(0));
                        c.setStatus(Contacts.STATUS_NORNAL);
                        c.setUpdate_time(resp.getUpdateTime());

                        LogUtils.d("", TextFormat.printToString(resp));

                        if (ContactsDao.getInstance().replace(c)) {
                            PhoneContactsDao.getInstance().updateAdded(c.getPhone());
                            return true;
                        }
                    } else {
                        LogUtils.e("", resp.getDesc());
                    }
                    ToastUtils.showToastInThread("添加失败");
                } catch (NetGetException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtils.showToastInThread("解析失败");
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissPendingDialog();
                if (result) {
                    ToastUtils.showToast("添加联系人成功");
                } else {
                    ToastUtils.showToast("添加联系人失败");
                }
                finish();
            }
        };
        task.execute();
    }

    public static void launch(Context context, String result) {
        Intent i = new Intent(context, CaptureResultActivity.class);
        i.putExtra(EXTRA_RESULT, result);
        context.startActivity(i);
    }
}
