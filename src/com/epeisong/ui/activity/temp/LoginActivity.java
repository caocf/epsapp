package com.epeisong.ui.activity.temp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.EpsNetConfig;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.common.Constants;
import com.epeisong.logistics.net.NetService;
import com.epeisong.logistics.net.NetServiceFactory;
import com.epeisong.logistics.proto.Base.ProtoSysDictionary;
import com.epeisong.logistics.proto.Eps.SysDictionaryResp;
import com.epeisong.logistics.proto.Eps.UserLoginResp;
import com.epeisong.logistics.proto.Eps.UserLoginResp.Builder;
import com.epeisong.model.Dictionary;
import com.epeisong.model.User;
import com.epeisong.ui.activity.ForgetLoginPwdActivity;
import com.epeisong.ui.activity.LoginFreezeAccountActivity;
import com.epeisong.ui.activity.user.TempActivity;
import com.epeisong.ui.activity.user.UserLoginedInitActivity;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.ReleaseLog;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 登录
 * 
 * @author poet
 * 
 */
public class LoginActivity extends TempActivity implements OnClickListener, OnTouchListener, OnItemClickListener,
        OnDismissListener {

    public static final String EXTRA_PHONE = "phone";

    private PopupWindow mPopupWindow;
    private MyAdapter mAdapter;

    private EditText mPhoneEt;
    private EditText mPwdEt;
    private ImageView mDropDownIv;
    private ImageView mClearIv;

    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPhone = getIntent().getStringExtra(EXTRA_PHONE);
        if (mPhone == null) {
            mPhone = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PHONE, null);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPhoneEt = (EditText) findViewById(R.id.et_phone);
        mPwdEt = (EditText) findViewById(R.id.et_pwd);
        mDropDownIv = (ImageView) findViewById(R.id.iv_drop_down);
        mDropDownIv.setOnClickListener(this);
        mClearIv = (ImageView) findViewById(R.id.iv_clear);
        mClearIv.setOnClickListener(this);
        findViewById(R.id.iv_eye).setOnTouchListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.tv_other_question).setOnClickListener(this);
        findViewById(R.id.tv_get_back_pwd).setOnClickListener(this);
        findViewById(R.id.tv_freeze_account).setOnClickListener(this);

        SpUtils.remove(SpUtils.KEYS_SYS.LONG_LAST_UNLOGIN);

        mPwdEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mClearIv.setVisibility(View.VISIBLE);
                } else {
                    mClearIv.setVisibility(View.GONE);
                }
            }
        });

        if (mPhone != null) {
            mPhoneEt.setText(mPhone);
        }
        Serializable extra = getIntent().getSerializableExtra("0");
        if (extra != null && extra instanceof String) {
            String msg = (String) extra;
            if (!TextUtils.isEmpty(msg)) {
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("提示").setMessage(msg).setPositiveButton("确定", null);
                b.create().show();
            }
        }

        if (EpsApplication.DEBUGGING) {
            View v = findViewById(R.id.btn_change_ip);
            v.setVisibility(View.VISIBLE);
            v.setOnClickListener(this);
            CheckBox cb = (CheckBox) findViewById(R.id.cb_debug);
            cb.setVisibility(View.VISIBLE);
            cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    EpsApplication.DEBUGGING = isChecked;
                }
            });
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new ActionImpl() {
            @Override
            public View getView() {
                TextView tv = getRightTextView("注册", R.drawable.shape_frame_black_content_trans);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                tv.setTextColor(getResources().getColor(R.color.white));
                return tv;
            }

            @Override
            public void doAction(View v) {
                if (checkDictionary()) {
                    Intent intent = new Intent(getApplicationContext(), RegisterStartActivity.class);
                    startActivity(intent);
                }
            }
        });
        return new TitleParams(getDefaultHomeAction(), "登录易配送", actions).setShowLogo(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.iv_drop_down:
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            } else {
                showPopupWindow();
            }
            break;
        case R.id.iv_clear:
            mPwdEt.setText("");
            break;
        case R.id.btn_login:
            if (!checkDictionary()) {
                return;
            }
            final String phone = mPhoneEt.getText().toString();
            if (TextUtils.isEmpty(phone)) {
                ToastUtils.showToast("请输入手机号码!");
                return;
            }
            final String pwd = mPwdEt.getText().toString();
            if (TextUtils.isEmpty(pwd)) {
                ToastUtils.showToast("请输入密码！");
                return;
            }

            showPendingDialog(null);
            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    return login(phone, pwd, new OnLoginListener() {
                        @Override
                        public void onNeedCreateLogi() {
                            Intent intent = new Intent(getApplicationContext(), ChooseRoleActivityNew.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    dismissPendingDialog();
                    if (result != null) {
                        if (result) {
                            Intent intent = new Intent(getApplicationContext(), UserLoginedInitActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            };
            task.execute();
            break;
        case R.id.tv_other_question:
//            Intent other = new Intent(this, LoginOtherProblemActivity.class);
        	Intent other = new Intent(this, LoginFreezeAccountActivity.class);
            startActivity(other);
            break;
        case R.id.tv_get_back_pwd:
            Intent intent = new Intent(this, ForgetLoginPwdActivity.class);
            startActivity(intent);
            break;
        case R.id.tv_freeze_account:
            Intent freeze = new Intent(this, LoginFreezeAccountActivity.class);
            startActivity(freeze);
            break;
        case R.id.btn_change_ip:
            changeIp(this);
            break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mPwdEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            mPwdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            break;
        }
        mPwdEt.setSelection(mPwdEt.getText().length());
        return true;
    }

    @Override
    public void onDismiss() {
        mDropDownIv.setSelected(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Loginer loginer = mAdapter.getItem(position);
        mPhoneEt.setText(loginer.getPhone());
        mPwdEt.setText(loginer.getPwd());
        mPopupWindow.dismiss();
    }

    private boolean checkDictionary() {
        int version = SpUtils.getInt(SpUtils.KEYS_SYS.INT_DICTIONARY_VERSION, 0);
        if (version > 0) {
            return true;
        }
        // TODO 下载字典
        showPendingDialog("同步数据中...");
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    int version = SpUtils.getInt(SpUtils.KEYS_SYS.INT_DICTIONARY_VERSION, 0);
                    LogUtils.d("checkDictionary", "version:" + version);
                    SysDictionaryResp.Builder resp = NetServiceFactory.getInstance().checkSysDictionary(
                            EpsNetConfig.getHost(), EpsNetConfig.PORT, version, 9000);
                    if (resp == null || !Constants.SUCC.equals(resp.getResult())) {
                        return false;
                    }
                    int curVersion = resp.getCurrentVersionCodeOfSysDictionary();
                    List<ProtoSysDictionary.Builder> pDicts = resp.getSysDictionaryBuilderList();
                    LogUtils.d("checkDictionary", "curVersion:" + curVersion + "\ndicts.size:" + pDicts.size());
                    if (pDicts == null || pDicts.isEmpty()) {
                        return false;
                    }
                    List<Dictionary> dicts = new ArrayList<Dictionary>();
                    for (ProtoSysDictionary.Builder item : pDicts) {
                        Dictionary dict = new Dictionary();
                        dict.setId(item.getIndexId());
                        dict.setName(item.getName());
                        dict.setSort_order(item.getSortOrder());
                        dict.setType(item.getDictionaryTypeId());
                        dicts.add(dict);
                    }
                    if (DictionaryDao.getInstance().insertAll(dicts)) {
                        SpUtils.put(SpUtils.KEYS_SYS.INT_DICTIONARY_VERSION, curVersion);
                        SpUtils.put(SpUtils.KEYS_SYS.LONG_LAST_CHECK_DICT_TIME, System.currentTimeMillis());
                        return true;
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    String eStr = LogUtils.e("test", e);
                    LogUtils.saveLog("init", eStr);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissPendingDialog();
                if (result) {
                    ToastUtils.showToast("数据同步成功");
                } else {
                    ToastUtils.showToast("数据同步失败");
                }
            }
        };
        task.execute();
        return false;
    }

    private void showPopupWindow() {
        if (mPopupWindow == null) {
            TextView tv = new TextView(getApplicationContext());
            tv.setVisibility(View.GONE);
            int p = DimensionUtls.getPixelFromDpInt(10);
            tv.setPadding(p, p, p, p);
            tv.setText("没有记录");
            FrameLayout layout = new FrameLayout(getApplicationContext());
            layout.setBackgroundResource(R.drawable.common_bg_rect_gray);
            layout.addView(tv);
            ListView lv = new ListView(getApplicationContext());
            lv.setCacheColorHint(Color.TRANSPARENT);
            lv.setOnItemClickListener(this);
            lv.setAdapter(mAdapter = new MyAdapter());
            List<Loginer> data = Loginer.getLoginers();
            mAdapter.replaceAll(data);
            lv.setEmptyView(tv);
            layout.addView(lv);
            mPopupWindow = new PopupWindow(layout, mPhoneEt.getWidth(), -2);
            mPopupWindow.setOnDismissListener(this);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        }
        int offset = DimensionUtls.getPixelFromDpInt(10);
        mPopupWindow.showAsDropDown(mPhoneEt, -offset, offset);
        mDropDownIv.setSelected(true);
    }

    private class MyAdapter extends HoldDataBaseAdapter<Loginer> implements OnClickListener {

        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof Integer) {
                int i = (Integer) tag;
                removeItem(getItem(i));
                Loginer.saveLoginer(getAllItem());
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getApplicationContext());
            int p = DimensionUtls.getPixelFromDpInt(10);
            tv.setPadding(p, p, p, p);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            tv.setTextColor(Color.BLACK);
            tv.setText(getItem(position).getPhone());
            ImageView iv = new ImageView(getApplicationContext());
            iv.setImageResource(R.drawable.common_icon_clear_gray);
            iv.setOnClickListener(this);
            iv.setTag(position);
            RelativeLayout rl = new RelativeLayout(getApplicationContext());
            rl.addView(tv);
            int w = DimensionUtls.getPixelFromDpInt(20);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, w);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.rightMargin = DimensionUtls.getPixelFromDpInt(10);
            rl.addView(iv, params);
            return rl;
        }
    }

    public static class Loginer {
        private String phone;
        private String pwd;

        public Loginer(String phone, String pwd) {
            super();
            this.phone = phone;
            this.pwd = pwd;
        }

        public String getPhone() {
            return phone;
        }

        public String getPwd() {
            return pwd;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Loginer)) {
                return false;
            }
            Loginer loginer = (Loginer) o;
            if (loginer.getPhone() == null || this.getPhone() == null) {
                return false;
            }
            return loginer.getPhone().equals(this.getPhone());
        }

        public static List<Loginer> getLoginers() {
            String str = SpUtils.getString(SpUtils.KEYS_SYS.STRING_LOGINERS, null);
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            List<Loginer> list = new ArrayList<LoginActivity.Loginer>();
            String[] ss = str.split(";");
            for (String s : ss) {
                String[] pair = s.split(":");
                String pwd = "";
                if (pair.length == 2) {
                    pwd = new String(Base64.decode(pair[1].getBytes(), Base64.DEFAULT));
                }
                Loginer loginer = new Loginer(pair[0], pwd);
                list.add(loginer);
            }
            return list;
        }

        public static void saveLoginer(String phone) {
            List<Loginer> list = Loginer.getLoginers();
            if (list == null) {
                list = new ArrayList<LoginActivity.Loginer>();
            }
            Loginer loginer = new Loginer(phone, "");
            if (list.contains(loginer)) {
                list.remove(loginer);
            }
            list.add(0, loginer);
            if (list.size() > 10) {
                list = list.subList(0, 10);
            }
            Loginer.saveLoginer(list);
        }

        public static void saveLoginer(List<Loginer> list) {
            if (list == null || list.isEmpty()) {
                SpUtils.remove(SpUtils.KEYS_SYS.STRING_LOGINERS);
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (Loginer loginer : list) {
                String pwdDes = "";
                if (!TextUtils.isEmpty(loginer.getPwd())) {
                    pwdDes = Base64.encodeToString(loginer.getPwd().getBytes(), Base64.DEFAULT);
                }
                sb.append(loginer.getPhone() + ":" + pwdDes + ";");
            }
            String result = sb.subSequence(0, sb.length() - 2).toString();
            SpUtils.put(SpUtils.KEYS_SYS.STRING_LOGINERS, result);
        }

    }

    public static Boolean login(String phone, String pwd, final OnLoginListener listener) {
        NetService frontendService = NetServiceFactory.getInstance();
        String ip = EpsNetConfig.getHost();
        int port = EpsNetConfig.PORT;
        frontendService.setDstName(ip);
        frontendService.setDstPort(port);
        LogUtils.d("Login1", "++++++++++++ " + ip + ":" + port);
        ReleaseLog.log("LoginActivity.login1", "++++++++++++ " + ip + ":" + port);
        try {
            frontendService.setDstName(ip);
            frontendService.setDstPort(port);
            frontendService.setMobile(phone);
            frontendService.setPassword(pwd);
            UserLoginResp.Builder resp = frontendService.login(15 * 1000);
            if (resp != null && Constants.SUCC.equals(resp.getResult())) {
                Loginer.saveLoginer(phone);
                UserDao.saveAccount(phone, pwd);
                if (resp.getIsNeedToCreateLogistic()) {
                    if (listener == null) {
                        ToastUtils.showToast("参数错误");
                        return false;
                    } else {
                        HandlerUtils.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onNeedCreateLogi();
                            }
                        });
                        return null;
                    }
                }
                String newIP = resp.getIpToLogin();
                int newPort = resp.getPortToLogin();
                if (TextUtils.isEmpty(newIP) || newPort <= 0) {
                    ToastUtils.showToastInThread("信息获取失败");
                    return false;
                } else {
                    return login2(phone, pwd, newIP, newPort);
                }
            } else {
                if (resp == null) {
                    ToastUtils.showToastInThread("连接超时");
                } else {
                    ToastUtils.showToastInThread(resp.getDesc());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("Login", e);
            ReleaseLog.log("LoginActivity.login", e);
            if (e instanceof TimeoutException) {
                ToastUtils.showToastInThread("连接超时");
            } else {
                ToastUtils.showToastInThread("连接失败");
            }
        }
        return false;
    }

    private static boolean login2(String phone, String pwd, String ip, int port) throws Exception {
        NetService frontendService = NetServiceFactory.getInstance();
        frontendService.setDstName(ip);
        frontendService.setDstPort(port);
        LogUtils.d("Login2", "++++++++ " + ip + ":" + port);
        ReleaseLog.log("LoginActivity.login2", "++++++++++++ " + ip + ":" + port);

        frontendService.setDstName(ip);
        frontendService.setDstPort(port);
        Builder resp = frontendService.login(15 * 1000);
        if (resp != null && Constants.SUCC.equals(resp.getResult())) {
            User user = UserParser.parse(resp);
            user.setPhone(phone);
            UserDao.saveLogin(user);
            UserDao.saveHost(ip, port);
            return true;
        } else {
            if (resp == null) {
                ToastUtils.showToastInThread("连接超时");
            } else {
                ToastUtils.showToastInThread(resp.getDesc());
            }
        }
        return false;
    }

    public static interface OnLoginListener {
        void onNeedCreateLogi();
    }

    public static void changeIp(final BaseActivity a) {
        if (a == null) {
            return;
        }
        String host = EpsNetConfig.getHost();
        View view = SystemUtils.inflate(R.layout.dialog_change_ip);
        final TextView tv = (TextView) view.findViewById(R.id.tv_ip);
        tv.setText(EpsNetConfig.HOST);
        final CheckBox cb_1 = (CheckBox) view.findViewById(R.id.cb_1);
        final EditText et = (EditText) view.findViewById(R.id.et_ip);
        final CheckBox cb_2 = (CheckBox) view.findViewById(R.id.cb_2);
        if (host.equals(EpsNetConfig.HOST)) {
            cb_1.setChecked(true);
            cb_2.setChecked(false);
        } else if (host.startsWith("192.168.1.")) {
            cb_1.setChecked(false);
            cb_2.setChecked(true);
            et.setText(host);
        }
        cb_1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_1.setChecked(true);
                cb_2.setChecked(false);
            }
        });
        cb_2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cb_1.setChecked(false);
                cb_2.setChecked(true);
            }
        });
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_1.setChecked(false);
                cb_2.setChecked(true);
            }
        });
        a.showYesNoDialog("更改ip:" + host, view, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    String host;
                    if (cb_1.isChecked()) {
                        host = tv.getText().toString();
                    } else {
                        host = et.getText().toString();
                    }
                    if (TextUtils.isEmpty(host)) {
                        host = EpsNetConfig.HOST;
                    }
                    EpsNetConfig.setHost(host);
                    EpsApplication.exit(a);
                }
            }
        });
    }
}
