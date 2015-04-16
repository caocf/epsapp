package com.test.log;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.EpsNetConfig;
import com.epeisong.R;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.LogUtils.LogListener;
import com.epeisong.utils.SystemUtils;
import com.test.log.LogcatActivity.Log;
import com.test.request.RequestTestActivity;

public class LogcatService extends Service implements OnTouchListener, OnClickListener, LogListener {

    private WindowManager mWindowManager;
    private LayoutParams mWindowParams;
    private View mView;
    private Button mUpBtn;
    private TextView mHostTv;
    private ListView mListView;
    private MyAdapter mAdapter;

    private int mStartX;
    private int mStartY;

    @Override
    public void onCreate() {
        super.onCreate();
        initWindowManager();
        initView();
        mWindowManager.addView(mView, mWindowParams);
        LogUtils.addLogListener(this);

        mHostTv.setText(EpsNetConfig.getHost() + "\n" + EpsNetConfig.getLogisticsServeUrl() + "\n"
                + EpsNetConfig.getTransactionUrl());
    }

    private void initWindowManager() {
        mWindowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        mWindowParams = new LayoutParams();
        mWindowParams.type = LayoutParams.TYPE_PHONE; // 电话窗口。它用于电话交互（特别是呼入）。它置于所有应用程序之上，状态栏之下。
        mWindowParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.gravity = Gravity.LEFT | Gravity.TOP; // 悬浮窗左上
        mWindowParams.x = 0;
        mWindowParams.y = 0;
        mWindowParams.width = LayoutParams.WRAP_CONTENT;
        mWindowParams.height = LayoutParams.WRAP_CONTENT;
        mWindowParams.format = LayoutParams.LAYOUT_CHANGED;
    }

    private void initView() {
        mView = LayoutInflater.from(this).inflate(R.layout.layout_logcat_service, null);
        mView.setOnTouchListener(this);
        mHostTv = (TextView) mView.findViewById(R.id.tv_host);
        mListView = (ListView) mView.findViewById(R.id.lv);
        mListView.setAdapter(mAdapter = new MyAdapter());
        mAdapter.setMaxSize(500);
        mView.findViewById(R.id.btn_close).setOnClickListener(this);
        mUpBtn = (Button) mView.findViewById(R.id.btn_up);
        mUpBtn.setOnClickListener(this);
        mView.findViewById(R.id.btn_clear).setOnClickListener(this);
        mView.findViewById(R.id.btn_go).setOnClickListener(this);
    }

    @Override
    public void onLog(Log log) {
        mAdapter.addItem(log);
        mListView.setSelection(mAdapter.getCount() - 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_close:
            stopSelf();
            break;
        case R.id.btn_up:
            if ("收起".equals(mUpBtn.getText())) {
                mListView.setVisibility(View.GONE);
                mUpBtn.setText("展开");
            } else {
                mListView.setVisibility(View.VISIBLE);
                mUpBtn.setText("收起");
            }
            break;
        case R.id.btn_clear:
            mAdapter.clear();
            break;
        case R.id.btn_go:
            // goLogcat();
            goRequestTest();
            break;
        }
    }

    private void goRequestTest() {
        Intent intent = new Intent(this, RequestTestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        stopSelf();
    }

    private void goLogcat() {
        Intent i = new Intent(this, LogcatActivity.class);
        i.putExtra(LogcatActivity.EXTRA_DATA, (ArrayList<Log>) mAdapter.getAllItem());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        stopSelf();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mStartX = (int) event.getRawX();
            mStartY = (int) event.getRawY();
            break;
        case MotionEvent.ACTION_MOVE:
            int dx = (int) (event.getRawX() - mStartX);
            int dy = (int) (event.getRawY() - mStartY);
            mWindowParams.x += dx;
            mWindowParams.y += dy;
            mWindowManager.updateViewLayout(mView, mWindowParams);
            mStartX = (int) event.getRawX();
            mStartY = (int) event.getRawY();
            break;
        case MotionEvent.ACTION_UP:
            // 可以记录view的坐标，下次启动时直接显示当前位置
            break;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeView(mView);
        LogUtils.removeLogListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MyAdapter extends HoldDataBaseAdapter<Log> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null || !(convertView instanceof RelativeLayout)) {
                convertView = SystemUtils.inflate(R.layout.activity_logcat_list_view_item);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fillData(getItem(position));
            return convertView;
        }
    }

    private class ViewHolder {
        TextView tv_time;
        TextView tv_log;

        public void findView(View v) {
            tv_time = (TextView) v.findViewById(R.id.tv_time);
            tv_log = (TextView) v.findViewById(R.id.tv_log);
        }

        public void fillData(Log log) {
            tv_time.setText(DateUtil.long2HMSS(log.getTime()));
            tv_log.setText(log.getTag() + "\n" + log.getContent());
            if (log.getLevel() == LogListener.error) {
                tv_log.setTextColor(Color.RED);
            } else if (log.getLevel() == LogListener.warn) {
                tv_log.setTextColor(Color.argb(0x88, 0xAA, 0x00, 0x00));
            } else if (log.getLevel() == LogListener.debug) {
                tv_log.setTextColor(Color.BLACK);
            } else {
                tv_log.setTextColor(Color.GREEN);
            }
        }
    }
}
