package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.EndlessAdapter.OnLoadMoreListener;
import com.epeisong.data.net.NetListMineFreight;
import com.epeisong.data.net.NetSearchFreight;
import com.epeisong.data.net.parser.FreightParser;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEFreight;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.proto.Eps.FreightReq.Builder;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.Freight;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.ui.activity.ChatRoomActivity;
import com.epeisong.ui.activity.ContactsDetailActivity;
import com.epeisong.ui.activity.FreightDetailActivity;
import com.epeisong.ui.adapter.FreightListAdapter;
import com.epeisong.ui.view.ChooseFreightTypeLayout;
import com.epeisong.ui.view.ChooseFreightTypeLayout.OnChooseFreightTypeListener;
import com.epeisong.ui.view.ChooseLineLayout;
import com.epeisong.ui.view.ChooseLineLayout.OnChooseLineListener;
//import com.epeisong.ui.view.ChooseServeRegionLayout;

/**
 * 车源货源 - 联系人
 * @author Jack
 *
 */
public class CarGoodsSourceFragment extends Fragment implements OnClickListener, OnItemClickListener,
        OnChooseLineListener, OnChooseFreightTypeListener, OnLoadMoreListener {

//    private class MyAdapter extends HoldDataBaseAdapter<Freight> {
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            ViewHolder holder = null;
//            if (convertView == null) {
//            	convertView = SystemUtils.inflate(R.layout.activity_freight_of_contacts_item);//item_search_the_source_supply_of_cars);
//                //convertView = SystemUtils.inflate(R.layout.item_search_the_source_supply_of_cars);
//                holder = new ViewHolder();
//                holder.findView(convertView);
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//            Freight f = getItem(position);
//            holder.fillData(f);
//            return convertView;
//        }
//    }
//
//    private class ViewHolder {
//
//        ImageView iv_type;
//        TextView tv_start;
//        TextView tv_end;
//        TextView tv_gongsi;
//        TextView tv_time;
//        TextView tv_xiangqing;
//
//        public void fillData(Freight f) {
//
//            // int type = f.getType();
//            // int status = f.getOrder_status();
//            // iv_type.setImageResource(R.drawable.contacts_logo_default);
//        	if (f.getStatus() != Freight.STATUS_VALID) {
//        		if (f.getType() == Freight.TYPE_GOODS) {
//        			// 货源已过期
//        			iv_type.setImageResource(R.drawable.black_board_goods2);
//        		} else if (f.getType() == Freight.TYPE_TRUCK) {
//        			// 车源已过期
//        			iv_type.setImageResource(R.drawable.black_board_truck2);
//        		}
//        	} else {
//        		if (f.getType() == Freight.TYPE_GOODS) {
//        			iv_type.setImageResource(R.drawable.black_board_goods);
//        		} else if (f.getType() == Freight.TYPE_TRUCK) {
//        			iv_type.setImageResource(R.drawable.black_board_truck);
//        		}
//        	}
//
//            tv_start.setText(f.getStart_region());
//            tv_end.setText(f.getEnd_region());
//
//            // 不显示公司，时间加上时分 2014、12、02 Zhu
//            // tv_gongsi.setText(f.getOwner_name());
//            // tv_time.setText((DateUtil.long2vague(f.getCreate_time())));
//            tv_gongsi.setText((DateUtil.long2vaguehour(f.getCreate_time())));
//            tv_time.setVisibility(View.GONE);
//
//            tv_xiangqing.setText(f.getDesc());
//            // 删除或耳机根据此方法判断
//            /*
//             * if (f.getUser_id() == UserDao.getInstance().getUser().getId()) {
//             * }
//             */
//        }
//
//        public void findView(View v) {
//
////            iv_type = (ImageView) v.findViewById(R.id.iv_type);
////            tv_start = (TextView) v.findViewById(R.id.tv_start);
////            tv_end = (TextView) v.findViewById(R.id.tv_end);
////            tv_gongsi = (TextView) v.findViewById(R.id.tv_gongsi);
////            tv_time = (TextView) v.findViewById(R.id.tv_time);
////            tv_xiangqing = (TextView) v.findViewById(R.id.tv_xiangqing);
//            
//            iv_type = (ImageView) v.findViewById(R.id.iv_freight_type);//iv_type);
//            tv_start = (TextView) v.findViewById(R.id.tv_start_region);//tv_start);
//            tv_end = (TextView) v.findViewById(R.id.tv_end_region);//tv_end);
//            tv_gongsi = (TextView) v.findViewById(R.id.tv_name);//tv_gongsi);
//            tv_time = (TextView) v.findViewById(R.id.tv_time);
//            tv_xiangqing = (TextView) v.findViewById(R.id.tv_freight_desc);//tv_xiangqing);
//
//        }
//    }

    private static final int SIZE_LOAD_MORE = 10;
    // private static final int LOAD_SIZE_FIRST = 10;
    // private static final int LAOD_SIZE_MORE = 10;
    private static final int SIZE_LOAD_FIRST = 10;

    private int mStartRegionCode;
    // private ChooseServeRegionLayout mChooseServeRegionLayout;
    // private int Logistic_type =
    // Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT;
    private static View mChooseContainer;
    protected EndlessAdapter mEndlessAdapter;

    private User mUser;
    // private String mPlatformName;
    private String mUserId;
    private TextView view_empty;
    // private String flag;

    private int mFreightType = Properties.FREIGHT_TYPE_ALL;
    // private RegionResult mStartRegion;
    // private RegionResult mEndRegion;
    private int mEndRegionCode;
    FreightListAdapter mAdapter = new FreightListAdapter();
    private ListView lv;
    private static TextView mChoosableTv01;
    // private View mUnderLine01;
    private static TextView mChoosableTv02;
    // private View mUnderLine02;
    private static ChooseFreightTypeLayout mChooseFreightTypeLayout;
    private static ChooseLineLayout mChooseLineLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        mUser = (User) args.getSerializable(ContactsDetailActivity.EXTRA_USER);

        mUserId = args.getString(ContactsDetailActivity.EXTRA_USER_ID);

        // flag = getActivity().getIntent().getStringExtra(EXTRA_FLAG);
        // if (mMarketId == null || "-1".equals(mMarketId)) {
        // mPlatformName = "易配送";
        // } else {
        // mPlatformName = mMarket.getShow_name();
        // }

        View root = inflater.inflate(R.layout.activity_cargood_source, null);

        View view01 = root.findViewById(R.id.fl_choosable_001);
        mChoosableTv01 = (TextView) view01.findViewById(R.id.tv_choosable0);
        mChoosableTv01.setText("全部车源货源");
        // mUnderLine01 = view01.findViewById(R.id.view_under_line0);
        view01.setOnClickListener(this);
        mChooseContainer = root.findViewById(R.id.fl_choose_container0);
        mChooseContainer.setOnClickListener(this);
        mChooseFreightTypeLayout = (ChooseFreightTypeLayout) root.findViewById(R.id.choose_freight_type_layout0);
        mChooseFreightTypeLayout.setOnChooseFreightTypeListener(this);

        View view02 = root.findViewById(R.id.fl_choosable_002);
        mChoosableTv02 = (TextView) view02.findViewById(R.id.tv_choosable0);
        mChoosableTv02.setText("线路不限");
        // mUnderLine02 = view02.findViewById(R.id.view_under_line0);
        view02.setOnClickListener(this);

        mChooseLineLayout = (ChooseLineLayout) root.findViewById(R.id.choose_line_layout0);
        mChooseLineLayout.setFragment(this);
        mChooseLineLayout.setOnChooseLineListener(this);
//        mChooseLineLayout.setFilter(ChooseRegionActivity.FILTER_0_2);

        lv = (ListView) root.findViewById(R.id.lv);
        lv.setAdapter(mAdapter = new FreightListAdapter());
        lv.setOnItemClickListener(this);

        if (mAdapter != null) {
            mEndlessAdapter = new EndlessAdapter(getActivity(), mAdapter);
            mEndlessAdapter.setIsAutoLoad(true);
            mEndlessAdapter.setOnLoadMoreListener(this);
            lv.setAdapter(mEndlessAdapter);
        }

        // mEndlessAdapter = new
        // EndlessAdapter(getActivity().getApplicationContext(), mAdapter);
        // mEndlessAdapter.setOnLoadMoreListener(this);
        // mEndlessAdapter.setIsAutoLoad(true);
        // mEndlessAdapter.setHasMore(true);
        // lv.setAdapter(mEndlessAdapter);
        // lv.setOnItemClickListener(this);

        view_empty = (TextView) root.findViewById(R.id.view_empty);
        view_empty.setText(null);
        lv.setEmptyView(view_empty);
        // bt_search = (Button) root.findViewById(R.id.bt_search);

        // bt_search.setOnClickListener(this);
        loadData(SIZE_LOAD_FIRST, "0", 0, true);
        // btn_search();
        return root;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mChooseLineLayout.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof Freight) {
            Freight f = (Freight) tag;
            String user_id = f.getUser_id();
            String freight_id = f.getId();
            Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
            intent.putExtra(ChatRoomActivity.EXTRA_REMOTE_ID, user_id);
            intent.putExtra(ChatRoomActivity.EXTRA_BUSINESS_TYPE, ChatMsg.business_type_freight);
            intent.putExtra(ChatRoomActivity.EXTRA_BUSINESS_ID, freight_id);
            startActivity(intent);
            return;
        }

        switch (v.getId()) {
        case R.id.fl_choose_container0:
            mChooseContainer.setVisibility(View.GONE);
            hideChooseFreightType();
            hideChooseLine();
            break;
        case R.id.fl_choosable_001:
            // TODO Auto-generated method stub
            // List<Dictionary> data = (List<Dictionary>) new
            // ArrayList<Dictionary>();
            // //List<Dictionary> data =
            // DictionaryDao.getInstance().queryByType(Dictionary.TYPE_TRUCK_LENGTH);
            // String stringl[] = {"全部车源货源", "车源", "货源"};
            // for(int i=1;i<=3;i++)
            // {
            // Dictionary dictionary = new Dictionary();
            // dictionary.setId((i-1)<<1);
            // dictionary.setName(stringl[i-1]);
            // dictionary.setSort_order(i);
            // dictionary.setType(2);
            // data.add(dictionary);
            // }
            // ((XBaseActivity)
            // getActivity()).showDictionaryListDialog("全部车源货源", data, new
            // OnChooseDictionaryListener() {
            // @Override
            // public void onChoosedDictionary(Dictionary item) {
            // mChoosableTv01.setText(item.getName());
            // if (item.getSort_order() ==
            // OnChooseFreightTypeListener.FREIGHT_TYPE_GOODS) {
            // mFreightType = Freight.TYPE_GOODS;
            // } else if (item.getSort_order() ==
            // OnChooseFreightTypeListener.FREIGHT_TYPE_TRUCK) {
            // mFreightType = Freight.TYPE_TRUCK;
            // } else {
            // mFreightType = Properties.FREIGHT_TYPE_ALL;
            // }
            // loadData(SIZE_LOAD_FIRST, "0", 0, true);
            // }
            // });

            // //framgment inner
            if (mChooseFreightTypeLayout.getVisibility() == View.GONE) {
                showChooseFreightType();
            } else {
                hideChooseFreightType();
            }
            break;
        case R.id.fl_choosable_002:
            // ChooseLineActivity.launch(this, 200);

            // //framgment inner
            if (mChooseLineLayout.getVisibility() == View.GONE) {
                showChooseLine();
            } else {
                hideChooseLine();
            }
            break;
        case R.id.bt_search:
            loadData(SIZE_LOAD_FIRST, "0", 0, true);
            break;
        default:
            break;
        }
    }

    public void onChoosedFreightType(String name, int type, boolean change) {
        if (type != -1) {
            mChoosableTv01.setText(name);
            if (type == OnChooseFreightTypeListener.FREIGHT_TYPE_GOODS) {
                mFreightType = Freight.TYPE_GOODS;
            } else if (type == OnChooseFreightTypeListener.FREIGHT_TYPE_TRUCK) {
                mFreightType = Freight.TYPE_TRUCK;
            } else {
                mFreightType = Properties.FREIGHT_TYPE_ALL;
            }
            loadData(SIZE_LOAD_FIRST, "0", 0, true);
        }
        hideChooseFreightType();
    }

    @Override
    public void onChoosedLine(RegionResult start, RegionResult end) {
        if (start != null && end != null) {
            mChoosableTv02.setText(start.getShortNameFromDistrict() + "-" + end.getShortNameFromDistrict());
            // mStartRegion = start;
            // mEndRegion = end;
            mStartRegionCode = start.getCode();
            mEndRegionCode = end.getCode();

        } else {
            // mStartRegion = null;
            // mEndRegion = null;
            mStartRegionCode = 0;
            mEndRegionCode = 0;
            mChoosableTv02.setText("线路不限");

        }
        loadData(SIZE_LOAD_FIRST, "0", 0, true);
        hideChooseLine();
    }

    private void loadData(final int size, final String edge_id, final double weight, final boolean bFirst) {
        final int marketId = Integer.parseInt(mUserId);
        NetListMineFreight net = new NetListMineFreight() {

            @Override
            protected boolean onSetRequest(Builder req) {
            	req.setDate(Long.MAX_VALUE);
            	req.setStartPointCode(mStartRegionCode);
                req.setDestinationCode(mEndRegionCode);
                req.setLimitCount(size);
                req.setId(Integer.parseInt(edge_id));
                req.setFreightType(mFreightType);
                req.setLogisticId(marketId);
                return true;
            }
        };
		try {
			FreightResp.Builder response = net.request();
			if(net.isSuccess(response)){
				List<ProtoEFreight> freightList = response.getFreightList();
                if (freightList == null || freightList.isEmpty()) {
                    // showMessageDialog(null, "车源货源不存在");
                    mAdapter.clear();
                    view_empty.setText("没有数据");
                    return;
                }
                List<Freight> freight = new ArrayList<Freight>();
                for (ProtoEFreight freights : freightList) {
                    freight.add(FreightParser.parse(freights));
                }
                mEndlessAdapter.setHasMore(freight.size() >= SIZE_LOAD_FIRST);
                mAdapter.replaceAll(freight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//        NetSearchFreight net = new NetSearchFreight((XBaseActivity) getActivity(), mStartRegionCode, mEndRegionCode,
//                mFreightType, size, Integer.parseInt(edge_id), marketId);
//
//        net.request(new OnNetRequestListenerImpl<Eps.FreightResp.Builder>() {
//            @Override
//            public void onSuccess(FreightResp.Builder response) {
//                List<ProtoEFreight> freightList = response.getFreightList();
//                if (freightList == null || freightList.isEmpty()) {
//                    // showMessageDialog(null, "车源货源不存在");
//                    mAdapter.clear();
//                    view_empty.setText("没有数据");
//                    return;
//                }
//                List<Freight> freight = new ArrayList<Freight>();
//                for (ProtoEFreight freights : freightList) {
//                    freight.add(FreightParser.parse(freights));
//                }
//                mEndlessAdapter.setHasMore(freight.size() >= SIZE_LOAD_FIRST);
//                mAdapter.replaceAll(freight);
//            }
//
//        });

    }

    @Override
    public void onStartLoadMore(EndlessAdapter adapter) {
        final int marketId = Integer.parseInt(mUserId);
        final String id = mAdapter.getItem(mAdapter.getCount() - 1).getId();
        final long time = mAdapter.getItem(mAdapter.getCount() - 1).getCreate_time();
        NetListMineFreight net = new NetListMineFreight() {

            @Override
            protected boolean onSetRequest(Builder req) {
            	req.setDate(time);
            	req.setStartPointCode(mStartRegionCode);
                req.setDestinationCode(mEndRegionCode);
                req.setLimitCount(SIZE_LOAD_MORE);
                req.setId(Integer.parseInt(id));
                req.setFreightType(mFreightType);
                req.setLogisticId(marketId);
                return true;
            }
        };
		try {
			FreightResp.Builder response = net.request();
			if(net.isSuccess(response)){
				mEndlessAdapter.endLoad(true);
                List<ProtoEFreight> freightList = response.getFreightList();
                if (freightList == null || freightList.isEmpty()) {
                    mEndlessAdapter.setHasMore(false);
                    return;
                }
                List<Freight> freight = new ArrayList<Freight>();
                for (ProtoEFreight freights : freightList) {
                    freight.add(FreightParser.parse(freights));
                }
                mAdapter.addAll(freight);
			}
		} catch (Exception e) {
			mEndlessAdapter.endLoad(false);
			e.printStackTrace();
		}

//        NetSearchFreight net = new NetSearchFreight((XBaseActivity) getActivity(), mStartRegionCode, mEndRegionCode,
//                mFreightType, SIZE_LOAD_MORE, Integer.parseInt(id), marketId);
//        net.request(new OnNetRequestListener<Eps.FreightResp.Builder>() {
//
//            @Override
//            public void onError() {
//                mEndlessAdapter.endLoad(false);
//            }
//
//            @Override
//            public void onFail(String msg) {
//                mEndlessAdapter.endLoad(false);
//            }
//
//            @Override
//            public void onSuccess(Builder response) {
//                mEndlessAdapter.endLoad(true);
//                List<ProtoEFreight> freightList = response.getFreightList();
//                if (freightList == null || freightList.isEmpty()) {
//                    mEndlessAdapter.setHasMore(false);
//                    return;
//                }
//                List<Freight> freight = new ArrayList<Freight>();
//                for (ProtoEFreight freights : freightList) {
//                    freight.add(FreightParser.parse(freights));
//                }
//                mAdapter.addAll(freight);
//            }
//        });

    }

    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
        if (mUserId == null || "-1".equals(mUserId)) {

        } else {

        }
        Freight f = mAdapter.getItem(position);
        if (true) {
            Intent intent = new Intent(getActivity().getApplicationContext(), FreightDetailActivity.class);
            intent.putExtra(FreightDetailActivity.EXTRA_FREIGHT, f);
            BusinessChatModel model = new BusinessChatModel();
            model.setBusiness_type(ChatMsg.business_type_freight);
            model.setBusiness_id(f.getId());
            model.setBusiness_desc(f.getStart_region() + "-" + f.getEnd_region());
            model.setBusiness_extra(String.valueOf(f.getType()));
            model.setBusiness_owner_id(f.getUser_id());
            intent.putExtra(FreightDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
            intent.putExtra(FreightDetailActivity.EXTRA_CAN_DELETE, false);
            startActivity(intent);
            return;
        }
    }

    private static void hideChooseFreightType() {
        mChooseContainer.setVisibility(View.GONE);
        mChooseFreightTypeLayout.setVisibility(View.GONE);
        mChoosableTv01.setSelected(false);
        // mUnderLine01.setSelected(false);
    }

    private static void hideChooseLine() {
        mChooseContainer.setVisibility(View.GONE);
        mChooseLineLayout.setVisibility(View.GONE);
        mChoosableTv02.setSelected(false);
        // mUnderLine02.setSelected(false);
    }

    private void showChooseFreightType() {
        hideChooseLine();
        mChooseContainer.setVisibility(View.VISIBLE);
        mChooseFreightTypeLayout.setVisibility(View.VISIBLE);
        mChoosableTv01.setSelected(true);
        // mUnderLine01.setSelected(true);
    }

    private void showChooseLine() {
        hideChooseFreightType();
        mChooseContainer.setVisibility(View.VISIBLE);
        mChooseLineLayout.setVisibility(View.VISIBLE);
        mChoosableTv02.setSelected(true);
        // mUnderLine02.setSelected(true);
    }

    public static boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (mChooseLineLayout.getVisibility() == View.VISIBLE) {
                hideChooseLine();
                return false;
            } else if (mChooseFreightTypeLayout.getVisibility() == View.VISIBLE) {
                hideChooseFreightType();
                return false;
            }
            // Log.d("GameFragmet事件", "OK");
        }
        return true;
    }

}
