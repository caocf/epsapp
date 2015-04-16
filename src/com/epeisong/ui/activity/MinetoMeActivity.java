package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import android.app.Activity;
import android.content.Intent;
import com.epeisong.utils.android.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.model.InfoFee;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

/**
 * 别人给我的报价
 * 
 * @author Jack
 * 
 */
public class MinetoMeActivity extends BaseActivity implements OnItemClickListener {

	public static final int REQUEST_CODE_DETAIL = 100;
	private static final int listViewContentCount = 10;
	private lib.pulltorefresh.PullToRefreshListView lv;
	private MyAdapter myAdapter;

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
                //InfoFeeProvider infoFeeProvider = new InfoFeeProvider();
                //User user = UserDao.getInstance().getUser();
                if (lastSyncIndex > 0) {
                    return null;//infoFeeProvider.getInfoFeeList(Integer.parseInt(user.getId()), lastSyncIndex, size,
                    		//Properties.INFO_FEE_STATUS_EXECUTE);
                }
                return null;//infoFeeProvider.getNewestInfoFeeList(Integer.parseInt(user.getId()), 0, size, Properties.INFO_FEE_STATUS_EXECUTE);
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
		//InfoFee infoFee = myAdapter.getItem(position);

		Intent i = new Intent(this, MineBillDetailActivity.class);
		i.putExtra(MineBillDetailActivity.EXTRA_INFO_MBD, "1");
		startActivityForResult(i, REQUEST_CODE_DETAIL);
	}

	@Override
	protected TitleParams getTitleParams() {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new ActionImpl() {
            @Override
            public View getView() {
                return getRightTextView("添加加盟商", R.drawable.shape_frame_black_content_trans);
            }

            @Override
            public void doAction(View v) {
            	ToastUtils.showToast("添加加盟商");
            }
        });
        
		return new TitleParams(getDefaultHomeAction(), "别人给我的报价", actions).setShowLogo(false);
	}

	private class ViewHolder {
		//public ImageView iv_icon;
		public TextView tv_nameTextView;


		public void fillData(InfoFee infoFee) {


			//User user = UserDao.getInstance().getUser();

			//iv_icon.setImageResource(resId);

		}

		public void findView(View v) {
			//iv_icon = (ImageView) v.findViewById(R.id.iv_icon);
			tv_nameTextView = (TextView) v.findViewById(R.id.tv_addr);
			tv_nameTextView.setVisibility(View.GONE);
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
