package com.epeisong.ui.activity;

import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import android.app.Activity;
import android.content.Intent;
import com.epeisong.utils.android.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.layer02.InfoFeeProvider_old;
import com.epeisong.model.InfoFee;
import com.epeisong.model.User;
import com.epeisong.utils.SystemUtils;

/**
 * 我给别人的报价
 * 
 * @author Jack
 * 
 */
public class MinetoOtherActivity extends BaseActivity implements OnItemClickListener {

    public static final int REQUEST_CODE_DETAIL = 100;
    private static final int listViewContentCount = 10;
    private lib.pulltorefresh.PullToRefreshListView lv;
    private MyAdapter myAdapter;
    private TextView view_empty;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

        }
    }

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minetoother_bill);

        lv = (lib.pulltorefresh.PullToRefreshListView) findViewById(R.id.lv_toother_bill);
        lv.setAdapter(myAdapter = new MyAdapter());
        lv.setOnItemClickListener(this);

        view_empty = (TextView) findViewById(R.id.view_empty);
        view_empty.setText(null);
        lv.setEmptyView(view_empty);
        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                requestData(0, listViewContentCount);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (myAdapter.isEmpty()) {
                    return;
                }
                int lastSyncIndex = myAdapter.getItem(myAdapter.getCount() - 1).getSyncIndex();
                requestData(lastSyncIndex, listViewContentCount);
            }

        });

        requestData(0, listViewContentCount);
    }

    private void requestData(final int lastSyncIndex, final int size) {
        AsyncTask<Void, Void, List<InfoFee>> task = new AsyncTask<Void, Void, List<InfoFee>>() {
            @Override
            protected List<InfoFee> doInBackground(Void... params) {
                User user = UserDao.getInstance().getUser();
                if (lastSyncIndex > 0) {
                    return null;// infoFeeProvider.getInfoFeeList(Integer.parseInt(user.getId()),
                                // lastSyncIndex, size,
                    // Properties.INFO_FEE_STATUS_EXECUTE);
                }
                return null;// infoFeeProvider.getNewestInfoFeeList(Integer.parseInt(user.getId()),
                            // 0, size, Properties.INFO_FEE_STATUS_EXECUTE);
            }

            @Override
            protected void onPostExecute(List<InfoFee> result) {
                lv.onRefreshComplete();
                if (lastSyncIndex > 0) {
                    myAdapter.addAll(result);
                } else {
                    myAdapter.replaceAll(result);
                }
            }
        };
        task.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        position -= lv.getRefreshableView().getHeaderViewsCount();
        InfoFee infoFee = myAdapter.getItem(position);

        Intent i = new Intent(this, MineBillDetailActivity.class);
        i.putExtra(MineBillDetailActivity.EXTRA_INFO_MBD, "2");
        startActivityForResult(i, REQUEST_CODE_DETAIL);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "我给别人的报价", null).setShowLogo(false);
    }

    private class ViewHolder {
        public ImageView iv_icon;
        public TextView tv_addr, tv_name, tv_line, tv_amount;

        public void fillData(InfoFee infoFee) {

            User muser = UserDao.getInstance().getUser();

            // tv_addr.setText(infoFee.getDistributionName());
            // tv_line.setText(infoFee.getFreightAddr());
            // tv_name.setText(infoFee.getPublisherName());
            // tv_amount.setText(String.valueOf(infoFee.getInfoAmount()));

        }

        public void findView(View v) {

            iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
            tv_addr = (TextView) v.findViewById(R.id.tv_addr);
            tv_name = (TextView) v.findViewById(R.id.tv_name);
            tv_line = (TextView) v.findViewById(R.id.tv_line);
            tv_amount = (TextView) v.findViewById(R.id.tv_amount);

        }
    }

    private class MyAdapter extends HoldDataBaseAdapter<InfoFee> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.item_toother_bill);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            InfoFee infoFee = getItem(position);
            holder.fillData(infoFee);

            return convertView;
        }
    }
}
