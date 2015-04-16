package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.adapter.EndlessAdapter;
import com.epeisong.base.adapter.EndlessAdapter.OnLoadMoreListener;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.net.NetListMineFreight;
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
import com.epeisong.ui.view.ChooseFreightTypeLayout;
import com.epeisong.ui.view.ChooseFreightTypeLayout.OnChooseFreightTypeListener;
import com.epeisong.ui.view.ChooseLineLayout;
import com.epeisong.ui.view.ChooseLineLayout.OnChooseLineListener;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.SystemUtils;

/**
 * 别人的小黑板
 * 
 * @author Jack
 * 
 */
public class BlackBoardOtherActivity extends HorizontalFilterActivity implements OnClickListener, OnChooseLineListener,
        OnChooseFreightTypeListener, OnItemLongClickListener, OnLoadMoreListener {
    private class MyAdapter extends HoldDataBaseAdapter<Freight> {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.activity_freight_of_contacts_item);// item_search_the_source_supply_of_cars);
                // convertView =
                // SystemUtils.inflate(R.layout.item_search_the_source_supply_of_cars);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Freight f = getItem(position);
            holder.fillData(f);
            return convertView;
        }
    }

    private class ViewHolder {

        ImageView iv_type;
        TextView tv_start;
        TextView tv_end;
        TextView tv_gongsi;
        TextView tv_time;
        TextView tv_xiangqing;

        public void fillData(Freight f) {

            // int type = f.getType();
            // int status = f.getOrder_status();
            // iv_type.setImageResource(R.drawable.contacts_logo_default);
            // if (f.getType() == Freight.TYPE_GOODS && f.getOrder_status() ==
            // Freight.ORDER_STATUS_UN_ORDER) {
            // // 货源未被订
            // iv_type.setImageResource(R.drawable.selector_borad_goods);
            // } else if (f.getType() == Freight.TYPE_TRUCK &&
            // f.getOrder_status() == Freight.ORDER_STATUS_UN_ORDER) {
            // // 车源未被订
            // iv_type.setImageResource(R.drawable.selector_board_truck);
            // } else if (f.getType() == Freight.TYPE_GOODS &&
            // f.getOrder_status() == Freight.ORDER_STATUS_ORDERED) {
            // // 货源已被订
            // iv_type.setImageResource(R.drawable.selector_booked_goods);
            // } else if (f.getType() == Freight.TYPE_TRUCK &&
            // f.getOrder_status() == Freight.ORDER_STATUS_ORDERED) {
            // // 车源已被订
            // iv_type.setImageResource(R.drawable.selector_booked_truck);
            // } else {
            // //iv_type.setImageResource(R.drawable.ic_launcher);
            // }
            if (f.getStatus() != Properties.FREIGHT_STATUS_NO_PROCESSED && f.getStatus() != Properties.FREIGHT_STATUS_BOOK) {
                if (f.getType() == Freight.TYPE_GOODS) {
                    // 货源已过期
                    iv_type.setImageResource(R.drawable.black_board_goods2);
                } else if (f.getType() == Freight.TYPE_TRUCK) {
                    // 车源已过期
                    iv_type.setImageResource(R.drawable.black_board_truck2);
                }
            } else {
                if (f.getType() == Freight.TYPE_GOODS) {
                    iv_type.setImageResource(R.drawable.black_board_goods);
                } else if (f.getType() == Freight.TYPE_TRUCK) {
                    iv_type.setImageResource(R.drawable.black_board_truck);
                }
            }

            tv_start.setText(f.getStart_region());
            tv_end.setText(f.getEnd_region());

            // 不显示公司，时间加上时分 2014、12、02 Zhu
            // tv_gongsi.setText(f.getOwner_name());
            // tv_time.setText((DateUtil.long2vague(f.getCreate_time())));
            tv_gongsi.setText((DateUtil.long2vaguehour(f.getCreate_time())));
            tv_time.setVisibility(View.GONE);

            tv_xiangqing.setText(f.getDesc());
            // 删除或耳机根据此方法判断
            /*
             * if (f.getUser_id() == UserDao.getInstance().getUser().getId()) {
             * }
             */
        }

        public void findView(View v) {

            // iv_type = (ImageView) v.findViewById(R.id.iv_type);
            // tv_start = (TextView) v.findViewById(R.id.tv_start);
            // tv_end = (TextView) v.findViewById(R.id.tv_end);
            // tv_gongsi = (TextView) v.findViewById(R.id.tv_gongsi);
            // tv_time = (TextView) v.findViewById(R.id.tv_time);
            // tv_xiangqing = (TextView) v.findViewById(R.id.tv_xiangqing);

            iv_type = (ImageView) v.findViewById(R.id.iv_freight_type);// iv_type);
            tv_start = (TextView) v.findViewById(R.id.tv_start_region);// tv_start);
            tv_end = (TextView) v.findViewById(R.id.tv_end_region);// tv_end);
            tv_gongsi = (TextView) v.findViewById(R.id.tv_name);// tv_gongsi);
            tv_time = (TextView) v.findViewById(R.id.tv_time);
            tv_xiangqing = (TextView) v.findViewById(R.id.tv_freight_desc);// tv_xiangqing);

        }
    }

    private static final int SIZE_LOAD_MORE = 10;
    // private static final int LOAD_SIZE_FIRST = 10;
    // private static final int LAOD_SIZE_MORE = 10;
    private static final int SIZE_LOAD_FIRST = 10;

    private int mStartRegionCode;
    // private ChooseServeRegionLayout mChooseServeRegionLayout;
    // private int Logistic_type =
    // Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT;
    protected EndlessAdapter mEndlessAdapter;

    private User mUser;
    // private String mPlatformName;
    private String mUserId;
    // private String flag;

    private int mFreightType = Properties.FREIGHT_TYPE_ALL;
    // private RegionResult mStartRegion;
    // private RegionResult mEndRegion;
    private int mEndRegionCode;
    MyAdapter mAdapter = new MyAdapter();
    // private View mUnderLine01;
    // private View mUnderLine02;
    private static ChooseFreightTypeLayout mChooseFreightTypeLayout;
    private static ChooseLineLayout mChooseLineLayout;

    private RegionResult mStartRegion;
    private RegionResult mEndRegion;

    @Override
    protected View onCreateContentView() {
        ListView lv = new ListView(getApplicationContext());
        lv.setCacheColorHint(Color.TRANSPARENT);
        lv.setBackgroundResource(R.color.white);
        // lv.setDivider(new ColorDrawable(0x000000));
        lv.setDividerHeight(0);
        lv.setAdapter(mAdapter = new MyAdapter());
        lv.setOnItemLongClickListener(this);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                Freight f = mAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), FreightDetailActivity.class);
                intent.putExtra(FreightDetailActivity.EXTRA_FREIGHT, f);
                BusinessChatModel model = new BusinessChatModel();
                model.setBusiness_type(ChatMsg.business_type_freight);
                model.setBusiness_id(f.getId());
                model.setBusiness_desc(f.getStart_region() + "-" + f.getEnd_region());
                model.setBusiness_extra(String.valueOf(f.getType()));
                model.setBusiness_owner_id(f.getUser_id());
                intent.putExtra(FreightDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
                intent.putExtra(FreightDetailActivity.EXTRA_CAN_DELETE, false);
                intent.putExtra("flag", "FreightOfContacts");
                startActivity(intent);
                return;
            }
        });

        if (mAdapter != null) {
            mEndlessAdapter = new EndlessAdapter(this, mAdapter);
            mEndlessAdapter.setIsAutoLoad(true);
            mEndlessAdapter.setOnLoadMoreListener(this);
            lv.setAdapter(mEndlessAdapter);
        }
        lv.setEmptyView(getEmptyText());

        loadData(SIZE_LOAD_FIRST, "0", 0, true);
        return lv;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Bundle args = getArguments();
        mUserId = getIntent().getStringExtra(ContactsDetailActivity.EXTRA_USER_ID);
        mUser = (User) getIntent().getSerializableExtra(ContactsDetailActivity.EXTRA_USER);

        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_cargood_source);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mChooseLineLayout.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    @Override
    public void onChoosedFreightType(String name, int type, boolean change) {
        if (change) {
            setFilterTitile(0, name);

            if (type == OnChooseFreightTypeListener.FREIGHT_TYPE_GOODS) {
                mFreightType = Freight.TYPE_GOODS;
            } else if (type == OnChooseFreightTypeListener.FREIGHT_TYPE_TRUCK) {
                mFreightType = Freight.TYPE_TRUCK;
            } else {
                mFreightType = Properties.FREIGHT_TYPE_ALL;
            }

            // mFreightType = type;
            // refreshData();
            loadData(SIZE_LOAD_FIRST, "0", 0, true);
        }
        hideFilter(0);
    }

    @Override
    public void onChoosedLine(RegionResult start, RegionResult end) {
        if (start != null && end != null) {
            setFilterTitile(1, start.getShortNameFromDistrict() + "-" + end.getShortNameFromDistrict());
            mStartRegion = start;
            mEndRegion = end;
            mStartRegionCode = start.getCode();
            mEndRegionCode = end.getCode();
        } else {
            mStartRegion = null;
            mEndRegion = null;
            mStartRegionCode = 0;
            mEndRegionCode = 0;
            setFilterTitile(1, "线路不限");
        }

        // refreshData();
        loadData(SIZE_LOAD_FIRST, "0", 0, true);
        hideFilter(1);
    }

    private void loadData(final int size, final String edge_id, final double weight, final boolean bFirst) {
        final int marketId = Integer.parseInt(mUserId);
        NetListMineFreight net = new NetListMineFreight() {

            @Override
            protected boolean onSetRequest(Builder req) {
            	//req.setDate(Long.MAX_VALUE);
            	req.setStartPointCode(mStartRegionCode);
                req.setDestinationCode(mEndRegionCode);
                req.setLimitCount(size);
                req.setId(Integer.parseInt(edge_id));
                req.setFreightType(mFreightType);
                req.setLogisticId(marketId);
                return true;
            }
        };
//        NetSearchFreight net = new NetSearchFreight() {
//            @Override
//            protected boolean onSetRequest(com.epeisong.logistics.proto.Eps.FreightReq.Builder req) {
//                req.setStartPointCode(mStartRegionCode);
//                req.setDestinationCode(mEndRegionCode);
//                req.setLimitCount(size);
//                req.setId(Integer.parseInt(edge_id));
//                req.setFreightType(mFreightType);
//                req.setLogisticId(marketId);
//                return true;
//            }
//        };
        try {
            FreightResp.Builder response = net.request();
            if (net.isSuccess(response)) {
                List<ProtoEFreight> freightList = response.getFreightList();
                if (freightList == null || freightList.isEmpty()) {
                    // showMessageDialog(null, "车源货源不存在");
                    mAdapter.clear();
                    getEmptyText().setText("没有数据");
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

        // NetSearchFreight net = new NetSearchFreight((XBaseActivity) this,
        // mStartRegionCode, mEndRegionCode,
        // mFreightType, size, Integer.parseInt(edge_id), marketId);
        //
        // net.request(new OnNetRequestListenerImpl<Eps.FreightResp.Builder>() {
        // @Override
        // public void onSuccess(FreightResp.Builder response) {
        // List<ProtoEFreight> freightList = response.getFreightList();
        // if (freightList == null || freightList.isEmpty()) {
        // // showMessageDialog(null, "车源货源不存在");
        // mAdapter.clear();
        // getEmptyText().setText("没有数据");
        // return;
        // }
        // List<Freight> freight = new ArrayList<Freight>();
        // for (ProtoEFreight freights : freightList) {
        // freight.add(FreightParser.parse(freights));
        // }
        // mEndlessAdapter.setHasMore(freight.size() >= SIZE_LOAD_FIRST);
        // mAdapter.replaceAll(freight);
        // }
        //
        // });

    }

    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
        if (mUserId == null || "-1".equals(mUserId)) {

        } else {

        }
        Freight f = mAdapter.getItem(position);
        if (true) {
            Intent intent = new Intent(this.getApplicationContext(), FreightDetailActivity.class);
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

    @Override
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        return false;
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "小黑板(" + mUser.getShow_name() + ")", null).setShowLogo(false);
    }

    @Override
    protected Map<String, ? extends View> onCreateFilterTitle() {
        Map<String, View> map = new LinkedHashMap<String, View>();
        map.put("全部车源货源", mChooseFreightTypeLayout = new ChooseFreightTypeLayout(this));
        map.put("路线不限", mChooseLineLayout = new ChooseLineLayout(this));
        mChooseFreightTypeLayout.setOnChooseFreightTypeListener(this);
        mChooseLineLayout.setActivity(this);
        // mChooseLineLayout.setFilter(ChooseRegionActivity.FILTER_0_2);
        mChooseLineLayout.setOnChooseLineListener(this);
        return map;
    }

    @Override
    public void onStartLoadMore(EndlessAdapter adapter) {
        final int marketId = Integer.parseInt(mUserId);
        final String id = mAdapter.getItem(mAdapter.getCount() - 1).getId();
        //final long time = mAdapter.getItem(mAdapter.getCount() - 1).getCreate_time();
//        NetSearchFreight net = new NetSearchFreight() {
//            @Override
//            protected boolean onSetRequest(com.epeisong.logistics.proto.Eps.FreightReq.Builder req) {
//                req.setStartPointCode(mStartRegionCode);
//                req.setDestinationCode(mEndRegionCode);
//                req.setLimitCount(SIZE_LOAD_MORE);
//                req.setId(Integer.parseInt(id));
//                req.setFreightType(mFreightType);
//                req.setLogisticId(marketId);
//                return true;
//            }
//        };
        NetListMineFreight net = new NetListMineFreight() {

            @Override
            protected boolean onSetRequest(Builder req) {
            	//req.setDate(time);
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
            if (net.isSuccess(response)) {
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

        // NetSearchFreight net = new NetSearchFreight((XBaseActivity) this,
        // mStartRegionCode, mEndRegionCode,
        // mFreightType, SIZE_LOAD_MORE, Integer.parseInt(id), marketId);
        // net.request(new OnNetRequestListener<Eps.FreightResp.Builder>() {
        //
        // @Override
        // public void onError() {
        // mEndlessAdapter.endLoad(false);
        // }
        //
        // @Override
        // public void onFail(String msg) {
        // mEndlessAdapter.endLoad(false);
        // }
        //
        // @Override
        // public void onSuccess(Builder response) {
        // mEndlessAdapter.endLoad(true);
        // List<ProtoEFreight> freightList = response.getFreightList();
        // if (freightList == null || freightList.isEmpty()) {
        // mEndlessAdapter.setHasMore(false);
        // return;
        // }
        // List<Freight> freight = new ArrayList<Freight>();
        // for (ProtoEFreight freights : freightList) {
        // freight.add(FreightParser.parse(freights));
        // }
        // mAdapter.addAll(freight);
        // }
        // });

    }
}
