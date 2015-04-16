package com.test.log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.SimpleListActivity;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.base.view.TitleParams;
import com.epeisong.ui.activity.temp.LoginActivity;
import com.epeisong.utils.CacheDataUtils;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.android.AsyncTask;
import com.test.log.CrashLogListActivity.CrashLog;
import com.test.other.MoreFunctionActivity;

public class CrashLogListActivity extends SimpleListActivity<CrashLog> implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AsyncTask<Void, Void, List<CrashLog>> task = new AsyncTask<Void, Void, List<CrashLog>>() {
            @Override
            protected List<CrashLog> doInBackground(Void... params) {
                return CrashLogDao.getInstance().queryAll();
            }

            @Override
            protected void onPostExecute(List<CrashLog> result) {
                mAdapter.replaceAll(result);
            }
        };
        task.execute();

        List<TextView> menus = new ArrayList<TextView>();
        initMenu(menus);

        int p = DimensionUtls.getPixelFromDpInt(10);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        for (TextView tv : menus) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
            params.topMargin = p;
            ll.addView(tv, params);
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2, Gravity.RIGHT | Gravity.BOTTOM);

        params.rightMargin = params.bottomMargin = p * 2;
        addContentView(ll, params);
    }

    void initMenu(List<TextView> menus) {
        menus.add(createMenuItem("更换IP", new Runnable() {
            @Override
            public void run() {
                LoginActivity.changeIp(CrashLogListActivity.this);
            }
        }));
        menus.add(createMenuItem("清除数据", new Runnable() {
            @Override
            public void run() {
                showYesNoDialog("提示", "清除数据会删除该应用在本地所有数据", "取消", "确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            CacheDataUtils.clearAppData();
                            EpsApplication.exit(CrashLogListActivity.this);
                        }
                    }
                });
            }
        }));
        menus.add(createMenuItem("更多功能", new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(CrashLogListActivity.this, MoreFunctionActivity.class);
                startActivity(intent);
            }
        }));
    }

    TextView createMenuItem(String name, Runnable runnable) {
        TextView tv = new TextView(this);
        tv.setBackgroundColor(Color.argb(0x99, 0x00, 0, 0));
        int p = DimensionUtls.getPixelFromDpInt(10);
        tv.setPadding(p, p, p, p);
        tv.setTextColor(Color.WHITE);
        tv.setGravity(Gravity.CENTER);
        tv.setText(name);
        tv.setTag(runnable);
        tv.setOnClickListener(this);
        return tv;
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof Runnable) {
            v.post((Runnable) tag);
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        List<Action> actions = new ArrayList<Action>();
        actions.add(createAction());
        actions.add(createAction2());
        return new TitleParams(getDefaultHomeAction(), "日志", actions).setShowLogo(false);
    }

    private Action createAction() {
        return new ActionImpl() {

            @Override
            public View getView() {
                return getRightTextView("logcat", R.drawable.selector_common_bg_red);
            }

            @Override
            public void doAction(View v) {
                Intent intent = new Intent(getApplicationContext(), LogcatActivity.class);
                startActivity(intent);
            }
        };
    }

    private Action createAction2() {
        return new ActionImpl() {

            @Override
            public int getDrawable() {
                return R.drawable.chat_send_fail;
            }

            @Override
            public void doAction(View v) {
                AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        CrashLogDao.getInstance().clear();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mAdapter.clear();
                    }
                };
                task.execute();
            }
        };
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CrashLog log = mAdapter.getItem(position);
        Intent intent = new Intent(this, CrashLogDetailActivity.class);
        intent.putExtra(CrashLogDetailActivity.EXTRA_LOG, log);
        startActivity(intent);
    }

    @Override
    protected View getItemView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            LinearLayout ll = new LinearLayout(getApplicationContext());
            int p = (int) DimensionUtls.getPixelFromDp(10);
            ll.setOrientation(LinearLayout.VERTICAL);
            TextView tv_time = new TextView(getApplicationContext());
            tv_time.setPadding(p, p, p, p);
            TextView tv_content = new TextView(getApplicationContext());
            tv_content.setMaxLines(4);
            tv_content.setPadding(p, 0, p, p);
            ll.addView(tv_time);
            ll.addView(tv_content);
            holder = new ViewHolder();
            holder.tv_time = tv_time;
            holder.tv_content = tv_content;
            convertView = ll;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CrashLog log = mAdapter.getItem(position);
        holder.tv_time.setText(DateUtil.long2vague(log.getTime()));
        holder.tv_content.setText(log.getContent());
        return convertView;
    }

    private class ViewHolder {
        TextView tv_time;
        TextView tv_content;
    }

    public static class CrashLog implements Serializable {

        private static final long serialVersionUID = -9143069015088773840L;

        private long _id;
        private long time;
        private String content;

        public long get_id() {
            return _id;
        }

        public void set_id(long _id) {
            this._id = _id;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public ContentValues getContentValues() {
            ContentValues values = new ContentValues();
            values.put("time", time);
            values.put("content", content);
            return values;
        }
    }
}
