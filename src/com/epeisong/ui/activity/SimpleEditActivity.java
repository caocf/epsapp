package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetLogisticsUpdate;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.model.User;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

public class SimpleEditActivity extends BaseActivity implements OnClickListener {

    public static final int WHAT_SHOW_NAME = 1;
    public static final int WHAT_CONTACTS_NAME = 2;
    public static final int WHAT_CONTACTS_PHONE = 3;
    public static final int WHAT_CONTACTS_TELEPHONE = 4;
    public static final int WHAT_INTRODUCTION = 5; // 简介

    public static final String EXTRA_WHAT = "what";
    public static final String EXTRA_WHAT_DATA = "what_data";

    private int mWhat;
    private String mWhatData;
    private EditText mEditText;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWhat = getIntent().getIntExtra(EXTRA_WHAT, -1);
        mWhatData = getIntent().getStringExtra(EXTRA_WHAT_DATA);
        mUser = (User) getIntent().getSerializableExtra("user");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_edit);
        mEditText = (EditText) findViewById(R.id.et);
        findViewById(R.id.btn).setOnClickListener(this);
        initEditText();
    }

    @Override
    protected TitleParams getTitleParams() {
        String title = "更改" + getTitle(mWhat);
        return new TitleParams(getDefaultHomeAction(), title, null).setShowLogo(false);
    }

    private String getTitle(int what) {
        switch (what) {
        case WHAT_SHOW_NAME:
            return "名称";
        case WHAT_CONTACTS_NAME:
            return "联系人";
        case WHAT_CONTACTS_PHONE:
            return "手机";
        case WHAT_CONTACTS_TELEPHONE:
            return "电话";
        case WHAT_INTRODUCTION:
            return "简介";
        default:
            return "";
        }
    }

    private void initEditText() {
        String hint = "";
        switch (mWhat) {
        case WHAT_SHOW_NAME:
            hint = "请输入名称";
            break;
        case WHAT_CONTACTS_NAME:
            hint = "请输入联系人姓名";
            break;
        case WHAT_CONTACTS_PHONE:
            hint = "请输入手机号码";
            mEditText.setInputType(InputType.TYPE_CLASS_PHONE);
            break;
        case WHAT_CONTACTS_TELEPHONE:
            hint = "请输入电话号码";
            mEditText.setInputType(InputType.TYPE_CLASS_PHONE);
            break;
        case WHAT_INTRODUCTION:
            hint = "请输入简介(不能超过250个字)";
            InputFilter[] filters = mEditText.getFilters();
            List<InputFilter> list = new ArrayList<InputFilter>();
            for (InputFilter filter : filters) {
                list.add(filter);
            }
            list.add(new InputFilter.LengthFilter(250));
            mEditText.setFilters(list.toArray(filters));
            mEditText.setMinLines(3);
            mEditText.setGravity(Gravity.LEFT | Gravity.TOP);
            mEditText.setBackgroundResource(R.drawable.shape_content_transparent);
            int p = DimensionUtls.getPixelFromDpInt(5);
            mEditText.setPadding(p, p, p, p);
            break;
        }
        mEditText.setHint(hint);
        if (mWhatData != null) {
            mEditText.setText(mWhatData);
        }
        mEditText.requestFocus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn:
            String content = mEditText.getText().toString();
            if (TextUtils.isEmpty(content)) {
            } else if (content.equals(mWhatData)) {
                finish();
                return;
            }
            if (content == null) {
                content = "";
            }
            update(content);
            break;
        }
    }

    private void update(final String content) {
        showPendingDialog(null);
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                NetLogisticsUpdate net = new NetLogisticsUpdate() {
                    @Override
                    protected boolean onSetRequestParams(ProtoEBizLogistics.Builder logi) {
                        switch (mWhat) {
                        case WHAT_SHOW_NAME:
                            logi.setName(content);
                            break;
                        case WHAT_CONTACTS_NAME:
                            logi.setContact(content);
                            break;
                        case WHAT_CONTACTS_PHONE:
                            logi.setMobile1(content);
                            break;
                        case WHAT_CONTACTS_TELEPHONE:
                            logi.setTelephone1(content);
                            break;
                        case WHAT_INTRODUCTION:
                            logi.setSelfIntroduction(content);
                            break;
                        default:
                            return false;
                        }
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        switch (mWhat) {
                        case WHAT_SHOW_NAME:
                            mUser.setShow_name(content);
                            User u = UserParser.parseSingleUser(resp);
                            if (u != null) {
                                mUser.setPinyin(u.getPinyin());
                            }
                            break;
                        case WHAT_CONTACTS_NAME:
                            mUser.setContacts_name(content);
                            break;
                        case WHAT_CONTACTS_PHONE:
                            mUser.setContacts_phone(content);
                            break;
                        case WHAT_CONTACTS_TELEPHONE:
                            mUser.setContacts_telephone(content);
                            break;
                        case WHAT_INTRODUCTION:
                            mUser.setSelf_intro(content);
                            break;
                        }
                        UserDao.getInstance().replace(mUser);
                        return true;
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissPendingDialog();
                if (result) {
                    ToastUtils.showToast("修改成功");
                    finish();
                }
            }
        };
        task.execute();
    }
}
