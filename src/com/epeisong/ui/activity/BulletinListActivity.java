package com.epeisong.ui.activity;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.SimpleListActivity;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetBulletinList;
import com.epeisong.data.net.parser.BulletinParser;
import com.epeisong.logistics.proto.Eps.BulletinReq;
import com.epeisong.logistics.proto.Eps.BulletinResp;
import com.epeisong.model.Bulletin;
import com.epeisong.model.User;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 公告列表
 * 
 * @author poet
 * 
 */
public class BulletinListActivity extends SimpleListActivity<Bulletin> {

    public static final String EXTRA_USER = "user";

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mUser = (User) getIntent().getSerializableExtra(EXTRA_USER);
        super.onCreate(savedInstanceState);
        if (mUser == null) {
            ToastUtils.showToast("参数错误");
            finish();
            return;
        }

        TextView tv = new TextView(this);
        tv.setText("公告");
        tv.setBackgroundResource(R.drawable.public_truck_btn_bg);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tv.setTextColor(Color.WHITE);
//        tv.setGravity(Gravity.CENTER);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        int p = DimensionUtls.getPixelFromDpInt(10);
        int h = DimensionUtls.getPixelFromDpInt(70);
        tv.setPadding(p, p, p, p);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1, h, Gravity.TOP);
//        params.bottomMargin = params.topMargin = params.leftMargin = params.rightMargin = DimensionUtls
//                .getPixelFromDpInt(20);
        params.bottomMargin = params.topMargin = params.leftMargin = params.rightMargin = DimensionUtls
                .getPixelFromDpInt(20);
        mFrameLayoutTop.addView(tv, params);

        loadData(false, 10);
    }

    @Override
    protected TitleParams getTitleParams() {
        String title = "";
        if (mUser != null) {
            title = mUser.getShow_name();
        }
        return new TitleParams(getDefaultHomeAction(), title, null).setShowLogo(false);
    }

    @Override
    protected boolean isUseEndlessAdapter() {
        return true;
    }

    @Override
    public void onStartLoadMore(EndlessAdapter adapter) {
        loadData(true, 10);
    }

    private void loadData(final boolean loadMore, final int size) {
        AsyncTask<Void, Void, List<Bulletin>> task = new AsyncTask<Void, Void, List<Bulletin>>() {
            @Override
            protected List<Bulletin> doInBackground(Void... params) {
                NetBulletinList net = new NetBulletinList() {
                    @Override
                    protected boolean onSetRequest(BulletinReq.Builder req) {
                        req.setLimitCount(size);
                        //req.setBulletinId(Integer.parseInt(mUser.getId()));
                        req.setLogisticId(Integer.parseInt(mUser.getId()));
                        //req.setLogisticId(mUser.getUser_type_code());
                        if (loadMore) {
                            long last_time = mAdapter.getItem(mAdapter.getCount() - 1).getCreate_time();
                            req.setCreateDate(last_time);
                        }
                        return true;
                    }
                };
                try {
                    BulletinResp.Builder resp = net.request();
                    return BulletinParser.parse(resp);
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Bulletin> result) {
                if (!loadMore) {
                    dismissPendingDialog();
                }
                if (result == null) {
                    if (loadMore) {
                        mEndlessAdapter.endLoad(false);
                    }
                } else {
                    if (loadMore) {
                        mEndlessAdapter.endLoad(true);
                        mEndlessAdapter.setHasMore(result.size() >= size);
                        mAdapter.addAll(result);
                    } else {
                        if (result.isEmpty()) {
                            ToastUtils.showToast("没有公告");
                        } else {
                            mAdapter.replaceAll(result);
                        }
                    }
                }
            }
        };
        task.execute();
        if (!loadMore) {
            showPendingDialog(null);
        }
    }

    @Override
    protected View getItemView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = SystemUtils.inflate(R.layout.activity_bulletin_list_item);
            holder = new ViewHolder();
            holder.findView(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.fillData(mAdapter.getItem(position));
        return convertView;
    }

    private class ViewHolder {

        TextView tv_content;
        TextView tv_time;

        public void findView(View v) {
            tv_content = (TextView) v.findViewById(R.id.tv_content);
            tv_time = (TextView) v.findViewById(R.id.tv_time);
        }

        public void fillData(Bulletin b) {
            tv_content.setText(b.getContent());
            tv_time.setText(DateUtil.long2YMDHM(b.getCreate_time()));
        }
    }
}
