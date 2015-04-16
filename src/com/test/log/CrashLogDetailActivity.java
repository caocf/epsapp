package com.test.log;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ToastUtils;
import com.test.log.CrashLogListActivity.CrashLog;

public class CrashLogDetailActivity extends BaseActivity {

    public static final String EXTRA_LOG = "log";

    private CrashLog mCrashLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView sv = new ScrollView(this);
        TextView tv = new TextView(this);
        int p = (int) DimensionUtls.getPixelFromDp(5);
        tv.setPadding(p, p, p, p);
        sv.addView(tv);
        setContentView(sv);
        mCrashLog = (CrashLog) getIntent().getSerializableExtra(EXTRA_LOG);
        if (mCrashLog != null) {
            tv.setText(mCrashLog.getContent());
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new ActionImpl() {
            @Override
            public View getView() {
                return getRightTextView("复制", 0);
            }

            @SuppressLint("NewApi")
            @Override
            public void doAction(View v) {
                if (mCrashLog != null) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(mCrashLog.getContent());
                    ToastUtils.showToast("内容已复制粘贴板");
                }
            }
        });
        return new TitleParams(getDefaultHomeAction(), "详情", actions).setShowLogo(false);
    }

}
