package com.epeisong.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshListView;
import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.net.parser.FreightParser;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEFreight;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.Freight;
import com.epeisong.model.User;
import com.epeisong.net.request.NetFreightUpdateStatus;
import com.epeisong.net.request.NetListMineFreight;
import com.epeisong.net.request.NetRequestorAsync.OnNetCancelListener;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.ui.activity.ContactsDetailActivity;
import com.epeisong.ui.activity.FreightDetailActivity;
import com.epeisong.ui.activity.PublishGoodsActivity;
import com.epeisong.ui.activity.PublishTruckActivity;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;

public class BlackBoardFragment extends Fragment implements OnClickListener, OnItemClickListener {

    private class MyAdapter extends HoldDataBaseAdapter<Freight> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.item_black_board);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Freight f = getItem(position);
            holder.fillData(f);
            if (f.getStatus() != Properties.FREIGHT_STATUS_COMPLETED) {
                convertView.setEnabled(true);
            } else {
                convertView.setEnabled(false);
            }
            holder.enable(f.getStatus() != Properties.FREIGHT_STATUS_COMPLETED);
            return convertView;
        }
    }

    private class ViewHolder {
        public ImageView iv_truck_goods;
        public TextView tv_black_start;
        public TextView tv_black_end;
        public TextView tv_black_time;
        public CheckBox cb_switch;
        public TextView tv_black_content;
        public TextView iv_black_arrow;
        public ImageView iv_state;

        public void enable(boolean enabled) {
            iv_truck_goods.setEnabled(enabled);
            iv_black_arrow.setEnabled(enabled);
            tv_black_start.setEnabled(enabled);
            tv_black_end.setEnabled(enabled);
            tv_black_time.setEnabled(enabled);
            tv_black_content.setEnabled(enabled);
        }

        public void fillData(Freight f) {
            tv_black_start.setText(f.getStart_region());
            tv_black_end.setText(f.getEnd_region());
            tv_black_time.setText(DateUtil.long2vague(f.getCreate_time()));
            tv_black_content.setText(f.getDesc());
            int status = f.getStatus();
            
            if (status == Properties.FREIGHT_STATUS_NO_PROCESSED) {
                if (f.getType() == Freight.TYPE_GOODS) {
                	iv_truck_goods.setImageResource(R.drawable.black_board_goods);
                } else if (f.getType() == Freight.TYPE_TRUCK) {
                	iv_truck_goods.setImageResource(R.drawable.black_board_truck);
                }
            } else if (status == Properties.FREIGHT_STATUS_BOOK) {
                if (f.getType() == Freight.TYPE_GOODS) {
                	iv_truck_goods.setImageResource(R.drawable.bload_booked_goods);
                	iv_state.setBackgroundResource(R.drawable.yibeiding_icon);
                } else if (f.getType() == Freight.TYPE_TRUCK) {
                	iv_truck_goods.setImageResource(R.drawable.bload_booked_truck);
                	iv_state.setBackgroundResource(R.drawable.yibeiding_icon);
                }
            } else if (status == Properties.FREIGHT_STATUS_COMPLETED) {
            	cb_switch.setChecked(false);
				if (f.getType() == Freight.TYPE_GOODS) {
					// 货源已过期
					iv_truck_goods.setImageResource(R.drawable.selector_borad_goods);
					iv_state.setBackgroundResource(R.drawable.yiguoqi_icon);
				} else if (f.getType() == Freight.TYPE_TRUCK) {
					// 车源已过期
					iv_truck_goods.setImageResource(R.drawable.selector_board_truck);
					iv_state.setBackgroundResource(R.drawable.yiguoqi_icon);
				}
            }

//			if (f.getStatus() == Freight.STATUS_VALID) {
//				cb_switch.setChecked(true);
//			} else {
//				cb_switch.setChecked(false);
//			}
            cb_switch.setTag(f);
        }

        public void findView(View v) {
            tv_black_start = (TextView) v.findViewById(R.id.tv_black_start);
            iv_truck_goods = (ImageView) v.findViewById(R.id.iv_truck_goods);
            tv_black_end = (TextView) v.findViewById(R.id.tv_black_end);
            tv_black_time = (TextView) v.findViewById(R.id.tv_black_time);
            cb_switch = (CheckBox) v.findViewById(R.id.iv_switch);
            tv_black_content = (TextView) v.findViewById(R.id.tv_black_content);
            iv_black_arrow = (TextView) v.findViewById(R.id.iv_black_arrow);
            iv_state = (ImageView) v.findViewById(R.id.iv_state);
            cb_switch.setOnClickListener(BlackBoardFragment.this);
        }
    }

    public static final String EXTRA_FREIGHT = "freight";
    public static final String EXTRA_MARKET = "market";
    public static final String EXTRA_PUBLISH_DISPATCH = "publish_dispatch";

    public static final int REQUEST_CODE_PUBLISH = 100;
    public static final int REQUEST_CODE_DETAIL = 101;
    public static final int RESULT_CODE_DELETE_DETAIL = 1000;

    private User mMarket;
    private String mUserId;
    private MyAdapter mAdapter;
    private PullToRefreshListView lv_black;
	private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = SystemUtils.inflate(R.layout.activity_black_borad);
        mMarket = (User) getActivity().getIntent().getSerializableExtra(EXTRA_MARKET);
        mUserId = getActivity().getIntent().getStringExtra(ContactsDetailActivity.EXTRA_USER_ID);

        view.findViewById(R.id.btn_publish_truck).setOnClickListener(this);
        view.findViewById(R.id.btn_publish_goods).setOnClickListener(this);
		if(!TextUtils.isEmpty(mUserId))
		{
			view.findViewById(R.id.btn_publish_truck).setVisibility(View.GONE);
			view.findViewById(R.id.btn_publish_goods).setVisibility(View.GONE);
		}
		else
		{
			mUserId = UserDao.getInstance().getUser().getId();
		}
        lv_black = (PullToRefreshListView) view.findViewById(R.id.lv_borad_list);
        mListView = lv_black.getRefreshableView();
		lv_black.setAdapter(mAdapter = new MyAdapter());
        mAdapter.setMaxSize(10);
        lv_black.setMode(Mode.BOTH);
        lv_black.setOnItemClickListener(this);
        
        lv_black.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                slideDownEvent();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                slideUpEvent();
            }
        });
        int userid;
        userid = Integer.parseInt(mUserId);

		NetListMineFreight net = new NetListMineFreight(null, Long.MAX_VALUE,
				10, userid);
		net.request(new OnNetRequestListenerImpl<FreightResp.Builder>() {
			@Override
			public void onSuccess(FreightResp.Builder response) {
				List<ProtoEFreight> freightList = response.getFreightList();
				if (freightList == null || freightList.isEmpty()) {
					return;
				} else {
					List<Freight> result = new ArrayList<Freight>();
					for (ProtoEFreight freight : freightList) {
						Freight d = FreightParser.parse(freight);
						result.add(d);
					}
					mAdapter.replaceAll(result);
				}
			}
		});
        return view;
    }
    
 // 往下滑动加载
    private void slideDownEvent() {
        int userid;
        userid = Integer.parseInt(mUserId);

    	NetListMineFreight net = new NetListMineFreight(null, Long.MAX_VALUE,
				10, userid);
		net.request(new OnNetRequestListenerImpl<FreightResp.Builder>() {
			
			@Override
            public void onError() {
                super.onError();
                lv_black.onRefreshComplete();
            }

            @Override
            public void onFail(String msg) {
                super.onFail(msg);
                lv_black.onRefreshComplete();
            }
			
			@Override
			public void onSuccess(FreightResp.Builder response) {
				List<ProtoEFreight> freightList = response.getFreightList();
				if (freightList == null || freightList.isEmpty()) {
					ToastUtils.showToast("您还没有发布更新的信息");
					lv_black.onRefreshComplete();
                    return;
				} else {
					List<Freight> result = new ArrayList<Freight>();
					for (ProtoEFreight freight : freightList) {
						Freight d = FreightParser.parse(freight);
						result.add(d);
					}
					mAdapter.replaceAll(result);
					lv_black.onRefreshComplete();
				}
			}
		});
    }

    // 往上滑动加载
    private void slideUpEvent() {
        if (mAdapter.isEmpty()) {
            return;
        }

        int userid;
        userid = Integer.parseInt(mUserId);

        long time = mAdapter.getItem(mAdapter.getCount() - 1).getCreate_time();
//        long time = mAdapter.getItem(mAdapter.getCount()).getCreate_time();
        NetListMineFreight net = new NetListMineFreight(null, time,
				10, userid);
		net.request(new OnNetRequestListenerImpl<FreightResp.Builder>() {
			
			@Override
            public void onError() {
                super.onError();
                lv_black.onRefreshComplete();
            }

            @Override
            public void onFail(String msg) {
                super.onFail(msg);
                lv_black.onRefreshComplete();
            }
			
			@Override
			public void onSuccess(FreightResp.Builder response) {
				List<ProtoEFreight> freightList = response.getFreightList();
				if (freightList == null || freightList.isEmpty()) {
					ToastUtils.showToast("小黑板没有更多信息");
					lv_black.onRefreshComplete();
                    return;
				} else {
					List<Freight> result = new ArrayList<Freight>();
					for (ProtoEFreight freight : freightList) {
						Freight d = FreightParser.parse(freight);
						result.add(d);
					}
//					mAdapter.replaceAll(result);
					mAdapter.addAll(result);
					lv_black.onRefreshComplete();
				}
			}
		});
    }

    @Override
    public void onClick(final View v) {
        // TODO
        XBaseActivity a = null;
        if (getActivity() instanceof XBaseActivity) {
            a = (XBaseActivity) getActivity();
        }
        int id = v.getId();
        switch (id) {
        case R.id.iv_switch:
            Object tag = v.getTag();
            if (tag != null && tag instanceof Freight) {
                final Freight f = (Freight) tag;
                final CheckBox cb = (CheckBox) v;
                final int curStatus = f.getStatus();
                cb.setChecked(curStatus != Properties.FREIGHT_STATUS_NO_PROCESSED);
                NetFreightUpdateStatus net = new NetFreightUpdateStatus(a) {
                    @Override
                    protected boolean onSetRequest(FreightReq.Builder req) {
                        req.setFreightId(Integer.parseInt(f.getId()));
                        if (curStatus == Properties.FREIGHT_STATUS_COMPLETED) {
                            req.setNewStatus(Properties.FREIGHT_STATUS_NO_PROCESSED);
                        } else {
                            req.setNewStatus(Properties.FREIGHT_STATUS_COMPLETED);
                        }
                        return true;
                    }
                };
                net.setOnNetCancelListener(new OnNetCancelListener() {
                    @Override
                    public void onNetCancel() {
                        if (curStatus != Properties.FREIGHT_STATUS_COMPLETED) {
                            cb.setChecked(true);
                        } else {
                            cb.setChecked(false);
                        }
                    }
                });
                net.request(new OnNetRequestListenerImpl<FreightResp.Builder>() {

                    @Override
                    public void onError() {
                        super.onError();
                        if (curStatus != Properties.FREIGHT_STATUS_COMPLETED) {
                            cb.setChecked(true);
                        } else {
                            cb.setChecked(false);
                        }
                    }

                    @Override
                    public void onFail(String msg) {
                        super.onFail(msg);
                        if (curStatus != Properties.FREIGHT_STATUS_COMPLETED) {
                            cb.setChecked(true);
                        } else {
                            cb.setChecked(false);
                        }
                    }

                    @Override
                    public void onSuccess(FreightResp.Builder response) {
                        if (curStatus != Properties.FREIGHT_STATUS_COMPLETED) {
                            f.setStatus(Properties.FREIGHT_STATUS_COMPLETED);
                        } else {
                            f.setStatus(Properties.FREIGHT_STATUS_NO_PROCESSED);
                            f.setId(String.valueOf(response.getFreightId()));
                            f.setCreate_time(response.getUpdateDate());
                            Collections.sort(mAdapter.getAllItem());
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });

            }
            break;
        case R.id.btn_publish_truck:
            Intent intent = new Intent(getActivity(), PublishTruckActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PUBLISH);
            break;
        case R.id.btn_publish_goods:
            Intent intent2 = new Intent(getActivity(), PublishGoodsActivity.class);
            startActivityForResult(intent2, REQUEST_CODE_PUBLISH);
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
    	position -= lv_black.getRefreshableView().getHeaderViewsCount();
        Freight f = mAdapter.getItem(position);
        Intent intent = new Intent(getActivity(), FreightDetailActivity.class);
        intent.putExtra(FreightDetailActivity.EXTRA_FREIGHT, f);
        intent.putExtra(FreightDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, BusinessChatModel.getFromFreight(f));
        intent.putExtra(FreightDetailActivity.EXTRA_CAN_DELETE, true);
        startActivityForResult(intent, REQUEST_CODE_DETAIL);
        return;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PUBLISH) {
            if (data != null) {
                Serializable serial = data.getSerializableExtra(EXTRA_PUBLISH_DISPATCH);
                if (serial != null && serial instanceof Freight) {
                    mAdapter.addItem(0, (Freight) serial);
                }
            }
        } else if (resultCode == RESULT_CODE_DELETE_DETAIL && requestCode == REQUEST_CODE_DETAIL) {
            Freight f = (Freight) data.getSerializableExtra(EXTRA_FREIGHT);
            mAdapter.removeItem(f);
        }
    }

}
