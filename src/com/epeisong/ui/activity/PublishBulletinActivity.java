package com.epeisong.ui.activity;

import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import lib.pulltorefresh.PullToRefreshListView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.EndlessAdapter.OnLoadMoreListener;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.layer02.BulletinDetailProvider;
import com.epeisong.data.model.CommonMsg;
import com.epeisong.data.net.parser.BulletinParser;
import com.epeisong.logistics.proto.Eps.BulletinReq;
import com.epeisong.logistics.proto.Eps.BulletinResp;
import com.epeisong.model.Bulletin;
import com.epeisong.net.request.NetListMineBulletin;
import com.epeisong.net.request.NetPublishBulletin;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

//import org.apache.commons.lang3.time.DateUtils;

/**
 * 发布公告
 * 
 * @author poet
 * 
 */
public class PublishBulletinActivity extends BaseActivity implements OnClickListener, OnRefreshListener<ListView>,
        OnLoadMoreListener {

    private class MyAdapter extends HoldDataBaseAdapter<Bulletin> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = SystemUtils.inflate(R.layout.item_bulletin_history);
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fillData(getItem(position), position);
            return convertView;
        }
    }

    private class MyDataObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            if (mAdapter.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }

    private class MyOnNetRequestListener extends OnNetRequestListenerImpl<BulletinResp.Builder> {

        private boolean mIsLoadMore;

        public MyOnNetRequestListener(boolean bLoadMore) {
            mIsLoadMore = bLoadMore;
        }

        @Override
        public void onError() {
            super.onError();
            if (mIsLoadMore) {
                mEndlessAdapter.endLoad(false);
            } else {
                mPullToRefreshListView.onRefreshComplete();
            }

        }

        @Override
        public void onFail(String msg) {
            super.onFail(msg);
            if (mIsLoadMore) {
                mEndlessAdapter.endLoad(false);
            } else {
                mPullToRefreshListView.onRefreshComplete();
            }
        }

        @Override
        public void onSuccess(BulletinResp.Builder response) {
            List<Bulletin> result = BulletinParser.parse(response);
            if (result == null || result.size() < 10) {
                mEndlessAdapter.setHasMore(false);
            } else {
                mEndlessAdapter.setHasMore(true);
            }
            if (mIsLoadMore) {
                mAdapter.addAll(result);
                mEndlessAdapter.endLoad(true);
            } else {
                mAdapter.replaceAll(result);
            }
            mPullToRefreshListView.onRefreshComplete();
            if (!mIsLoadMore) {
                checkTodayCount(response.getBulletinCountOfTodaty());
            }
        }

    }

    private void checkTodayCount(int alreadyCount) {
        if (alreadyCount > 0) {
            mAlreadyCount = alreadyCount;
            int remainCount = MAX_PUBLISH_COUNT - alreadyCount;
            if (remainCount < 0) {
                remainCount = 0;
            }
            mPublicCount.setText("今天已发布" + alreadyCount + "条，还可以发布" + remainCount + "条");
            return;
        }
        // Date today = new Date(System.currentTimeMillis());
        int todayCount = 0;
        for (Bulletin b : mAdapter.getAllItem()) {
            // long todayn=today.getTime()/86400000;
            // todayn = new Date(b.getCreate_time()).getTime()/86400000;

            // if( today.getTime()/86400 == new
            // Date(b.getCreate_time()).getTime()/86400) {
            if (DateUtil.long2YMD(System.currentTimeMillis()).equals(DateUtil.long2YMD(b.getCreate_time()))) {
                // if (DateUtils.isSameDay(today, new Date(b.getCreate_time())))
                // {
                todayCount++;
            }
        }
        if (todayCount >= MAX_PUBLISH_COUNT) {
            mIsOver = true;
        }
        mPublicCount.setText("今天已发布" + todayCount + "条，还可以发布" + (MAX_PUBLISH_COUNT - todayCount) + "条");
    }

    class ViewHolder {
        TextView tv_content;
        ImageView iv_close;
        TextView tv_date;

        public void fillData(Bulletin notice, int pos) {
            tv_content.setText(notice.getContent());
            iv_close.setTag(pos);
            tv_date.setText(DateUtil.long2YMDHM(notice.getCreate_time()));
        }

        public void findView(View v) {
            tv_content = (TextView) v.findViewById(R.id.tv_content);
            iv_close = (ImageView) v.findViewById(R.id.iv_close);
            iv_close.setOnClickListener(PublishBulletinActivity.this);
            tv_date = (TextView) v.findViewById(R.id.tv_public_time);
        }
    }

    public static final int MAX_PUBLISH_COUNT = 5;

    private EditText mBulletinContentEt;
    private TextView mPublicBulletin;
    private TextView mPublicCount;

    private PullToRefreshListView mPullToRefreshListView;
    private EndlessAdapter mEndlessAdapter;
    private MyAdapter mAdapter;
    private MyDataObserver mDataObserver;
    private View mEmptyView;

    private int mAlreadyCount;

    private boolean mIsOver;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
        case R.id.iv_close:
            final Object tag = v.getTag();
            // if (tag != null && tag instanceof Integer) {
            // int pos = (Integer) tag;
            // final Bulletin bulletin = mAdapter.getItem(pos);
            // AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void,
            // Boolean>() {
            // @Override
            // protected Boolean doInBackground(Void... arg0) {
            // String bulletinId = bulletin.getId();
            // BulletinDetailProvider bdProvider = new BulletinDetailProvider();
            // return bdProvider.delOneSelfById(bulletinId);
            // }
            //
            // @Override
            // protected void onPostExecute(Boolean bool) {
            // if (bool) {
            // mAdapter.removeItem(bulletin);
            // checkTodayCount();
            // } else {
            // System.out.println("删除失败");
            // }
            //
            // };
            // };
            // task.execute();
            // }
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("删除公告").setMessage("您确定要删除该条公告？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (tag != null && tag instanceof Integer) {
                                int pos = (Integer) tag;
                                final Bulletin bulletin = mAdapter.getItem(pos);
                                AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                                    @Override
                                    protected Boolean doInBackground(Void... arg0) {
                                        String bulletinId = bulletin.getId();
                                        BulletinDetailProvider bdProvider = new BulletinDetailProvider();
                                        return bdProvider.delOneSelfById(bulletinId);
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean bool) {
                                        if (bool) {
                                            mAdapter.removeItem(bulletin);
                                        } else {
                                            System.out.println("删除失败");
                                        }

                                    };
                                };
                                task.execute();
                            }
                        }
                    }).setNegativeButton("否", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub

                        }
                    }).show();
            dialog.setCanceledOnTouchOutside(true);
            break;
        case R.id.tv_public_bulletin:
            String content = mBulletinContentEt.getText().toString();
            if (TextUtils.isEmpty(content)) {
                ToastUtils.showToast("请输入公告内容！");
                return;
            }

            if (mAlreadyCount >= MAX_PUBLISH_COUNT) {
                ToastUtils.showToast("今日发布条数已达最大限额");
                return;
            }

            final Bulletin b = new Bulletin();
            b.setSender_id(UserDao.getInstance().getUser().getId());
            b.setContent(content);
            b.setStatus(CommonMsg.STATUS_READED);
            NetPublishBulletin net = new NetPublishBulletin(PublishBulletinActivity.this, content);
            net.request(new OnNetRequestListenerImpl<BulletinResp.Builder>() {
                @Override
                public void onSuccess(BulletinResp.Builder response) {
                    mBulletinContentEt.setText("");
                    b.setId(String.valueOf(response.getBulletinId()));
                    b.setCreate_time(response.getCreateDate());
                    b.setUpdate_time(response.getCreateDate());
                    mAdapter.addItem(0, b);
                    checkTodayCount(response.getBulletinCountOfTodaty());
                    ToastUtils.showToast("发布成功");
                }
            });
            break;

        default:
            break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        NetListMineBulletin net = new NetListMineBulletin() {
            @Override
            protected boolean onSetRequest(BulletinReq.Builder req) {
                req.setCreateDate(Long.MAX_VALUE);
                req.setLimitCount(10);
                req.setStatus(Bulletin.status_web_normal);
                return true;
            }
        };
        net.request(new MyOnNetRequestListener(false));
    }

    @Override
    public void onStartLoadMore(EndlessAdapter adapter) {
        if (mAdapter.getCount() <= 0) {
            return;
        }
        final long create_time = mAdapter.getItem(mAdapter.getCount() - 1).getCreate_time();
        NetListMineBulletin net = new NetListMineBulletin() {
            @Override
            protected boolean onSetRequest(BulletinReq.Builder req) {
                req.setCreateDate(create_time);
                req.setLimitCount(10);
                req.setStatus(Bulletin.status_web_normal);
                return true;
            }
        };
        net.request(new MyOnNetRequestListener(true));
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "发布公告", null).setShowLogo(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_bulletin);
        mBulletinContentEt = (EditText) findViewById(R.id.et_bulletin_content);
        mPublicCount = (TextView) findViewById(R.id.tv_public_count);
        mPublicBulletin = (TextView) findViewById(R.id.tv_public_bulletin);
        mPublicBulletin.setOnClickListener(this);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_bulletin_history);
        mPullToRefreshListView.setMode(Mode.PULL_FROM_START);
        mPullToRefreshListView.setOnRefreshListener(this);

        mAdapter = new MyAdapter();
        mAdapter.registerDataSetObserver(mDataObserver = new MyDataObserver());
        mEndlessAdapter = new EndlessAdapter(this, mAdapter);
        mEndlessAdapter.setOnLoadMoreListener(this);
        mPullToRefreshListView.setAdapter(mEndlessAdapter);

        mEmptyView = findViewById(R.id.empty_view);

        mPullToRefreshListView.setRefreshing();
        // TODO

        // ToastUtils.showToast(mAdapter.getAllItem().size()+"");
    }

    @Override
    protected void onDestroy() {
        mAdapter.unregisterDataSetObserver(mDataObserver);
        super.onDestroy();
    }
}
