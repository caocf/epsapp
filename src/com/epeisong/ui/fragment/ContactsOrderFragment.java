package com.epeisong.ui.fragment;

import java.util.List;

import android.content.Intent;
import com.epeisong.utils.android.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.EndlessAdapter.OnLoadMoreListener;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.EndlessEmptyListView;
import com.epeisong.data.net.NetBulletinList;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.BulletinReq;
import com.epeisong.model.Bulletin;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.ui.activity.ContactsDetailActivity;
import com.epeisong.ui.activity.EntireVehicleActivity;
import com.epeisong.ui.view.ChooseLineLayout;
import com.epeisong.ui.view.ChooseLineLayout.OnChooseLineListener;
import com.epeisong.utils.SystemUtils;

/**
 * 联系人 - 下单
 * 
 * @author Jack
 * 
 */
public class ContactsOrderFragment extends Fragment implements
		OnChooseLineListener, OnClickListener, OnItemClickListener, OnLoadMoreListener {

	private static View mChooseContainer;
	private static TextView mChoosableTv02;
	private RegionResult mStartRegion;
	private RegionResult mEndRegion;
	private int mStartRegionCode;
	private int mEndRegionCode;
	private TextView view_empty;
    MyAdapter mAdapter = new MyAdapter();
	protected EndlessAdapter mEndlessAdapter;
	private EndlessEmptyListView lv;
	private int Logistic_type;
	
	private User mUser;
	private static ChooseLineLayout mChooseLineLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
    	Bundle args = getArguments();
        mUser = (User) args.getSerializable(ContactsDetailActivity.EXTRA_USER);
//        if(muser!=null)
//        	Logistic_type = muser.getUser_type_code();
//        else
        Logistic_type = args.getInt(ContactsDetailActivity.EXTRA_USER_TYPEID);
        
		View root = inflater.inflate(R.layout.activity_order_info, null);
		View view02 = root.findViewById(R.id.fl_choosable_002);
		mChoosableTv02 = (TextView) view02.findViewById(R.id.tv_choosable0);
		mChoosableTv02.setText("线路不限");
		view02.setOnClickListener(this);

		switch(Logistic_type)
		{
		case Properties.LOGISTIC_TYPE_EXPRESS:
			break;
		default:
			root.findViewById(R.id.btn_quick_order).setVisibility(View.GONE);
			break;
		}
		
		root.findViewById(R.id.btn_quick_web).setVisibility(View.GONE);
		mChooseContainer = root.findViewById(R.id.fl_choose_container0);
		mChooseContainer.setOnClickListener(this);

		mChooseLineLayout = (ChooseLineLayout) root
				.findViewById(R.id.choose_line_layout0);
		mChooseLineLayout.setFragment(this);
		mChooseLineLayout.setOnChooseLineListener(this);

		lv = (EndlessEmptyListView) root.findViewById(R.id.lv);
		// mEndlessAdapter = new EndlessAdapter(getApplicationContext(),
		// mAdapter);
		// mEndlessAdapter.setOnLoadMoreListener(this);
		// mEndlessAdapter.setIsAutoLoad(true);
		// mEndlessAdapter.setHasMore(true);
		// lv.setAdapter(mEndlessAdapter);
		lv.setOnItemClickListener(this);
        lv.setAdapter(mAdapter = new MyAdapter());
        //lv.setOnItemClickListener(this);
        
		if (mAdapter != null) {
			mEndlessAdapter = new EndlessAdapter(getActivity(), mAdapter);
			mEndlessAdapter.setIsAutoLoad(false);
			mEndlessAdapter.setOnLoadMoreListener(this);
			lv.setAdapter(mEndlessAdapter);
		}
		view_empty = (TextView) root.findViewById(R.id.view_empty);
		view_empty.setText(null);
		lv.setEmptyView(view_empty);
		loadData(10, "0", 0, true);
		return root;
	}
	private void loadData(final int size, final String edge_id,
			final double weight, final boolean bFirst) {
        AsyncTask<Void, Void, List<Bulletin>> task = new AsyncTask<Void, Void, List<Bulletin>>() {
            @Override
            protected List<Bulletin> doInBackground(Void... params) {
                NetBulletinList net = new NetBulletinList() {
                    @Override
                    protected boolean onSetRequest(BulletinReq.Builder req) {
                        req.setLimitCount(size);
                        //req.setLogisticId(Integer.parseInt(mUser.getId()));

                        return true;
                    }
                };
//                try {
//                	
//                    BulletinResp.Builder resp = net.request();
//                    
////                    List<Bulletin> result = new ArrayList<Bulletin>();
////                    Bulletin tempblt = null;
////                    tempblt = new Bulletin();
////                    
////                    result.add(tempblt);
////                    return result;
//                    
//                    return BulletinParser.parse(resp);
//                } catch (NetGetException e) {
//                    e.printStackTrace();
//                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Bulletin> result) {

            	if (result==null || result.isEmpty()) {
            		view_empty.setText("暂无报价");//"没有数据");
            		//ToastUtils.showToast("没有数据");
            	} else {
            		mAdapter.replaceAll(result);
            		//mAdapter.clear();
            	}
            }
        };
        task.execute();

	}

	@Override
	public void onChoosedLine(RegionResult start, RegionResult end) {
		if (start != null && end != null) {
			mChoosableTv02.setText(start.getShortNameFromDistrict() + "-"
					+ end.getShortNameFromDistrict());
			mStartRegion = start;
			mEndRegion = end;
			mStartRegionCode = start.getCode();
			mEndRegionCode = end.getCode();

		} else {
			mStartRegion = null;
			mEndRegion = null;
			mStartRegionCode = 0;
			mEndRegionCode = 0;
			mChoosableTv02.setText("线路不限");
		}
		hideChooseLine();
	}

	private static void hideChooseLine() {
		mChooseContainer.setVisibility(View.GONE);
		mChooseLineLayout.setVisibility(View.GONE);
		mChoosableTv02.setSelected(false);
	}

	private void showChooseLine() {
		mChooseContainer.setVisibility(View.VISIBLE);
		mChooseLineLayout.setVisibility(View.VISIBLE);
		mChoosableTv02.setSelected(true);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.fl_choose_container0:
			mChooseContainer.setVisibility(View.GONE);
			hideChooseLine();
			break;

		case R.id.fl_choosable_002:
			if (mChooseLineLayout.getVisibility() == View.GONE) {
				showChooseLine();
			} else {
				hideChooseLine();
			}
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mChooseLineLayout.onActivityResult(requestCode, resultCode, data)) {
			return;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		//Toast.makeText(getActivity(), "南京-上海 整车 1000元", Toast.LENGTH_SHORT).show();
	}

	private class MyAdapter extends HoldDataBaseAdapter<Bulletin> {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = SystemUtils.inflate(R.layout.activity_order_list);
				holder = new ViewHolder();
				holder.findView(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			Bulletin f = getItem(position);
			holder.fillData(f);
			return convertView;
		}
	}

	private class ViewHolder {

		TextView tv_start;
		TextView tv_end;
		TextView tv_type;
		TextView tv_price;

		public void fillData(Bulletin f) {
			tv_start.setText("南京");//f.getStart_region());
			tv_end.setText("上海");//f.getEnd_region());
			tv_type.setText("整车");
			tv_price.setText(tv_price.getText().toString()+"元");
		}

		public void findView(View v) {
			tv_start = (TextView) v.findViewById(R.id.tv_start);
			tv_end = (TextView) v.findViewById(R.id.tv_end);
			tv_type = (TextView) v.findViewById(R.id.tv_type);
			tv_price = (TextView) v.findViewById(R.id.tv_price);

		}
	}

    public static boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	
        	if(mChooseLineLayout.getVisibility() == View.VISIBLE)
        	{
        		hideChooseLine();
        		return false;
        	}
        }
        return true;
    }

	@Override
	public void onStartLoadMore(EndlessAdapter adapter) {
		// TODO Auto-generated method stub
        String edge_id = mAdapter.getItem(mAdapter.getCount() - 1).getId();
        loadData(EntireVehicleActivity.LAOD_SIZE_MORE, edge_id, 0, false);// remember to add weight;
	}

}
