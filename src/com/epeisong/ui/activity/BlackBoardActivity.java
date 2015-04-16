package com.epeisong.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshListView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.view.SlipButton;
import com.epeisong.base.view.SlipButton.SlipButtonChangeListener;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetFreight;
import com.epeisong.data.net.NetFreightUpdateStatus;
import com.epeisong.data.net.NetListMineFreight;
import com.epeisong.data.net.NetUpdateFreightStatus;
import com.epeisong.data.net.parser.FreightParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEFreight;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightReq.Builder;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.model.BusinessChatModel;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.Freight;
import com.epeisong.model.User;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 小黑板
 * 
 * @author poet
 * 
 */
public class BlackBoardActivity extends BaseActivity implements OnClickListener, OnItemClickListener,
        OnItemLongClickListener, SlipButtonChangeListener {
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
        public SlipButton cb_switch;
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
            // add hour&minute 2014/12/2 zhu
            tv_black_time.setText(DateUtil.long2vaguehourMinute(f.getCreate_time()));
            tv_black_content.setText(f.getDesc());
            iv_state.setVisibility(View.GONE);
            iv_truck_goods.setImageResource(0);
            int status = f.getStatus();
            // TODO
            if (status == Properties.FREIGHT_STATUS_NO_PROCESSED) {
                // cb_switch.setChecked(true);
            	
                cb_switch.setDefaultOpen(true);
                if (f.getType() == Freight.TYPE_GOODS) {
                    // 货源未被订
                    iv_truck_goods.setImageResource(R.drawable.selector_borad_goods);
                } else if (f.getType() == Freight.TYPE_TRUCK) {
                    // 车源未被订
                    iv_truck_goods.setImageResource(R.drawable.selector_board_truck);
                }
                if (!mIsSelf) {
                    iv_state.setVisibility(View.GONE);
                }
            } else if (status == Properties.FREIGHT_STATUS_BOOK) {
            	cb_switch.setDefaultOpen(true);
            	iv_state.setVisibility(View.VISIBLE);
            	iv_state.setImageResource(R.drawable.yibeiding_icon);
            	if (f.getType() == Freight.TYPE_GOODS) {
                    // 货源已被订
                    iv_truck_goods.setImageResource(R.drawable.selector_borad_goods);
                    
                } else if (f.getType() == Freight.TYPE_TRUCK) {
                    // 车源已被订
                    iv_truck_goods.setImageResource(R.drawable.selector_board_truck);
                    
                }
                if (!mIsSelf) {
                    iv_state.setVisibility(View.GONE);
                }
            } else if (status == Properties.FREIGHT_STATUS_COMPLETED) {
                // 已完成
                cb_switch.setDefaultOpen(false);
                iv_state.setVisibility(View.VISIBLE);
            	iv_state.setImageResource(R.drawable.yiguoqi_icon);
                if (f.getType() == Freight.TYPE_GOODS) {
                    // 货源已过期
                    iv_truck_goods.setImageResource(R.drawable.selector_booked_goods);
                } else if (f.getType() == Freight.TYPE_TRUCK) {
                    // 车源已过期
                    iv_truck_goods.setImageResource(R.drawable.selector_booked_truck);
                }
            }

            // if (f.getStatus() == Freight.STATUS_VALID) {
            // cb_switch.setChecked(true);
            // } else {
            // cb_switch.setChecked(false);
            // }
            cb_switch.setTag(f);
        }

        public void findView(View v) {
            tv_black_start = (TextView) v.findViewById(R.id.tv_black_start);
            iv_truck_goods = (ImageView) v.findViewById(R.id.iv_truck_goods);
            tv_black_end = (TextView) v.findViewById(R.id.tv_black_end);
            tv_black_time = (TextView) v.findViewById(R.id.tv_black_time);
            cb_switch = (SlipButton) v.findViewById(R.id.iv_switch);
            if (checkgone)
                cb_switch.setVisibility(View.GONE);
            tv_black_content = (TextView) v.findViewById(R.id.tv_black_content);
            iv_black_arrow = (TextView) v.findViewById(R.id.iv_black_arrow);
            iv_state = (ImageView) v.findViewById(R.id.iv_state);
            // cb_switch.setOnClickListener(BlackBoardActivity.this);
            cb_switch.SetOnChangedListener(BlackBoardActivity.this);
        }
    }

    public static final String EXTRA_FREIGHT = "freight";
    public static final String EXTRA_MARKET = "market";
    public static final String EXTRA_PUBLISH_DISPATCH = "publish_dispatch";
    public static final String EXTRA_IS_SELF = "is_self";

    public static final int REQUEST_CODE_PUBLISH = 100;
    public static final int REQUEST_CODE_DETAIL = 101;
    public static final int RESULT_CODE_DELETE_DETAIL = 1000;
    public static final int REQUEST_CODE_BACKTASK = 200;// 2014/12/1 add by Zhu

    // add zhu 2014/12/4
    public static final int MAX_COUNT_ONBOARD = 30;
    public static final int MAX_PUBLISH_COUNT_EVERYDAY = 100;

    private boolean mIsSelf;

    private User mUser;
    private MyAdapter mAdapter;
    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    // private boolean isChgLsnOn;

    private String mUserId;
    private Boolean checkgone;

    private TextView tvPublishCount;
    private int mTodayCount;
    private int mTotalCount;

    @Override
    public void OnChanged(boolean CheckState, SlipButton btn) {
        Object tag = btn.getTag();
        if (tag != null && tag instanceof Freight) {
            changeState((Freight) tag, btn);
        }
    }

    private void toggleSlipButton(final SlipButton btn) {
        HandlerUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                btn.toggle();
            }
        }, 200);
    }

    private void changeState(final Freight f, final SlipButton btn) {
        final int curStatus = f.getStatus();
        showPendingDialog(null);
        if (curStatus == Properties.FREIGHT_STATUS_COMPLETED) {
            if (mTodayCount > MAX_PUBLISH_COUNT_EVERYDAY) {
            	dismissPendingDialog();
                ToastUtils.showToast("今天发布的车源货源已经达到上限！");
                toggleSlipButton(btn);
                return;
            }
            if (mTotalCount > MAX_COUNT_ONBOARD) {
            	dismissPendingDialog();
                ToastUtils.showToast("黑板上的车源货源信息太多，请删除一些无效信息！");
                toggleSlipButton(btn);
                return;
            }
        }
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                NetFreightUpdateStatus net = new NetFreightUpdateStatus() {
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
                try {
                    FreightResp.Builder resp = net.request();
                    if (resp != null && "SUCC".equals(resp.getResult())) {
                        if (curStatus == Properties.FREIGHT_STATUS_COMPLETED) {
                            f.setStatus(Properties.FREIGHT_STATUS_NO_PROCESSED);
                            f.setId(String.valueOf(resp.getFreightId()));
                            f.setCreate_time(resp.getUpdateDate());
                            Collections.sort(mAdapter.getAllItem());
                        } else {
                            f.setStatus(Properties.FREIGHT_STATUS_COMPLETED);
                        }
                        mTodayCount = resp.getFreightCountOfTodatyOnBlackBoard();
                        mTotalCount = resp.getFreightCountOnBlackBoard();
                        return true;
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissPendingDialog();
                if (result) {
                    tvPublishCount.setText("今天已发布" + mTodayCount + "条，还可以发布"
                            + Math.max((MAX_PUBLISH_COUNT_EVERYDAY - mTodayCount), 0) + "条");
                    mAdapter.notifyDataSetChanged();
                    String text = "";
                    if(curStatus == Properties.FREIGHT_STATUS_COMPLETED) {
                        text = "信息已发布";
                    } else {
                        text = "信息已关闭";
                    }
                    ToastUtils.showToast(text);
                }
            }

        };
        task.execute();
    }

    @Override
    public void onClick(final View v) {
        int id = v.getId();
        switch (id) {

        case R.id.btn_publish_truck:
            if (mTodayCount > MAX_PUBLISH_COUNT_EVERYDAY) {
                ToastUtils.showToast("今天发布的车源货源已经达到上限！");
                break;
            }
            if (mTotalCount > MAX_COUNT_ONBOARD) {
                ToastUtils.showToast("黑板上的车源货源信息太多，请删除一些无效信息！");
                break;
            }
            Intent intent = new Intent(this, PublishTruckActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PUBLISH);
            break;
        case R.id.btn_publish_goods:
            if (mTodayCount > MAX_PUBLISH_COUNT_EVERYDAY) {
                ToastUtils.showToast("今天发布的车源货源已经达到上限！");
                break;
            }
            if (mTotalCount > MAX_COUNT_ONBOARD) {
                ToastUtils.showToast("黑板上的车源货源信息太多，请删除一些无效信息！");
                break;
            }
            Intent intent2 = new Intent(this, PublishGoodsActivity.class);
            startActivityForResult(intent2, REQUEST_CODE_PUBLISH);
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
        position -= mPullToRefreshListView.getRefreshableView().getHeaderViewsCount();
        Freight f = mAdapter.getItem(position);
        if (true) {
            Intent intent = new Intent(this, FreightDetailActivity.class);
            intent.putExtra(FreightDetailActivity.EXTRA_FREIGHT, f);
            BusinessChatModel model = new BusinessChatModel();
            model.setBusiness_type(ChatMsg.business_type_freight);
            model.setBusiness_id(f.getId());
            model.setBusiness_owner_id(f.getUser_id());
            model.setBusiness_desc(f.getStart_region() + "-" + f.getEnd_region());
            model.setBusiness_extra(String.valueOf(f.getType()));
            intent.putExtra(FreightDetailActivity.EXTRA_BUSINESS_CHAT_MODEL, model);
            intent.putExtra(FreightDetailActivity.EXTRA_CAN_DELETE, true);
            intent.putExtra(ContactsDetailActivity.EXTRA_USER_ID, mUserId);
            startActivityForResult(intent, REQUEST_CODE_DETAIL);
            return;
        }
        // if (f.getType() == Freight.TYPE_GOODS) {
        // Intent i = new Intent(this, MySupplyDetailsActivity.class);
        // i.putExtra(MySupplyDetailsActivity.EXTRA_FREIGHT, f);
        // i.putExtra(MySupplyDetailsActivity.EXTRA_FREIGHT_ID, f.getId());
        // i.putExtra(MySupplyDetailsActivity.EXTRA_USER_ID, f.getUser_id());
        // startActivityForResult(i, REQUEST_CODE_DETAIL);
        // } else {
        // Intent i = new Intent(this, MyCarSourceActivity.class);
        // i.putExtra(MyCarSourceActivity.EXTRA_FREIGHT, f);
        // i.putExtra(MyCarSourceActivity.EXTRA_FREIGHT_ID, f.getId());
        // i.putExtra(MyCarSourceActivity.EXTRA_USER_ID, f.getUser_id());
        // startActivityForResult(i, REQUEST_CODE_DETAIL);
        // }
    }

    @Override
    // 2014/12/04 Jack Zhu add
    public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        if (!TextUtils.isEmpty(mUserId))
            return false;
        final int position = arg2 - mPullToRefreshListView.getRefreshableView().getHeaderViewsCount();
        final Freight f = mAdapter.getItem(position);
        if (f != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // builder.setTitle();
            final String[] items = { "删除", "删除所有无效" };
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

                            @Override
                            protected Boolean doInBackground(Void... params) {
                                NetUpdateFreightStatus net = new NetUpdateFreightStatus() {

                                    @Override
                                    protected boolean onSetRequest(Builder req) {
                                        req.setFreightId(Integer.parseInt(f.getId()));
                                        req.setNewStatus(Properties.FREIGHT_STATUS_DELETED);
                                        return true;
                                    }
                                };
                                try {
                                    FreightResp.Builder resp = net.request();
                                    if (resp != null && "SUCC".equals(resp.getResult())) {
                                        mTodayCount = resp.getFreightCountOfTodatyOnBlackBoard();
                                        mTotalCount = resp.getFreightCountOnBlackBoard();
                                        return true;
                                    }
                                } catch (NetGetException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }

                            @Override
                            protected void onPostExecute(Boolean result) {
                                if (result) {
                                    mAdapter.removeItem(f);
                                    tvPublishCount.setText("今天已发布" + mTodayCount + "条，还可以发布"
                                            + Math.max((MAX_PUBLISH_COUNT_EVERYDAY - mTodayCount), 0) + "条");
                                    mPullToRefreshListView.onRefreshComplete();
                                    ToastUtils.showToast("删除成功！");
                                }
                            };

                        };
                        task.execute();
                    } else if (which == 1) {
                        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(Void... params) {
                                NetFreight net = new NetFreight() {
                                    @Override
                                    protected int getCommandCode() {
                                        return CommandConstants.DELETE_ALL_INVALID_FREIGHTS_ON_BLACK_BOARD_REQ;
                                    }

                                    @Override
                                    protected boolean onSetRequest(FreightReq.Builder req) {
                                        int id = Integer.parseInt(UserDao.getInstance().getUser().getId());
                                        req.setLogisticId(id);
                                        return true;
                                    }
                                };

                                try {
                                    FreightResp.Builder resp = net.request();
                                    if (resp == null) {
                                        return null;
                                    }
                                    mTodayCount = resp.getFreightCountOfTodatyOnBlackBoard();
                                    mTotalCount = resp.getFreightCountOnBlackBoard();
                                    return true;
                                } catch (NetGetException e) {
                                    e.printStackTrace();
                                }
                                return null;

                            }

                            protected void onPostExecute(Boolean result) {
                                if (result) {
                                    ToastUtils.showToast("删除无效数据成功！");
                                    List<Freight> list = mAdapter.getAllItem();
                                    List<Freight> newList = new ArrayList<Freight>();
                                    for (Freight f : list) {
                                        if (f.getStatus() == Properties.FREIGHT_STATUS_COMPLETED) {
                                            newList.add(f);
                                        }
                                    }
                                    mAdapter.removeAllItem(newList);

                                    tvPublishCount.setText("今天已发布" + mTodayCount + "条，还可以发布"
                                            + Math.max((MAX_PUBLISH_COUNT_EVERYDAY - mTodayCount), 0) + "条");
                                    mPullToRefreshListView.onRefreshComplete();
                                }
                            }
                        };
                        task.execute();

                    }
                }
            });
            AlertDialog d = builder.create();
            d.setCanceledOnTouchOutside(true);
            d.show();
        }
        return true;
    }

    @Override
    protected TitleParams getTitleParams() {
        if (!TextUtils.isEmpty(mUserId) && !mUserId.equals(UserDao.getInstance().getUser().getId())) {
            if (mUser != null)// Zhu 2014/12/1 update
                return new TitleParams(getDefaultHomeAction(), "小黑板(" + mUser.getShow_name() + ")", null)
                        .setShowLogo(false);
        }
        return new TitleParams(getDefaultHomeAction(), "我的小黑板", null).setShowLogo(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PUBLISH) {
            if (data != null) {
                Serializable serial = data.getSerializableExtra(EXTRA_PUBLISH_DISPATCH);
                if (serial != null && serial instanceof Freight) {
                    mAdapter.addItem(0, (Freight) serial);
                    mTodayCount = data.getIntExtra("todayCount", 0);
                    mTotalCount = data.getIntExtra("totalCount", 0);
                    tvPublishCount.setText("今天已发布" + mTodayCount + "条，还可以发布"
                            + Math.max((MAX_PUBLISH_COUNT_EVERYDAY - mTodayCount), 0) + "条");
                }
            }
        } else if (resultCode == RESULT_CODE_DELETE_DETAIL && requestCode == REQUEST_CODE_DETAIL) {
            Freight f = (Freight) data.getSerializableExtra(EXTRA_FREIGHT);
            mAdapter.removeItem(f);
            mTodayCount = data.getIntExtra("todayCount", 0);
            mTotalCount = data.getIntExtra("totalCount", 0);
            tvPublishCount.setText("今天已发布" + mTodayCount + "条，还可以发布"
                    + Math.max((MAX_PUBLISH_COUNT_EVERYDAY - mTodayCount), 0) + "条");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mUserId = getIntent().getStringExtra(ContactsDetailActivity.EXTRA_USER_ID);
        mUser = (User) getIntent().getSerializableExtra(ContactsDetailActivity.EXTRA_USER);
        mIsSelf = getIntent().getBooleanExtra(EXTRA_IS_SELF, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_borad);

        findViewById(R.id.btn_publish_truck).setOnClickListener(this);
        findViewById(R.id.btn_publish_goods).setOnClickListener(this);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_borad_list);
        tvPublishCount = (TextView) findViewById(R.id.tv_black_count);

        if (!TextUtils.isEmpty(mUserId)) {
            findViewById(R.id.btn_publish_truck).setVisibility(View.GONE);
            findViewById(R.id.btn_publish_goods).setVisibility(View.GONE);
            checkgone = true;
        } else {

            checkgone = false;
        }
        mListView = mPullToRefreshListView.getRefreshableView();
        mPullToRefreshListView.setAdapter(mAdapter = new MyAdapter());
        mPullToRefreshListView.setMode(Mode.BOTH);
        mPullToRefreshListView.setOnItemClickListener(this);
        if (checkgone == false)
            mListView.setOnItemLongClickListener(this);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                slideDownEvent();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                slideUpEvent();
            }
        });

        final int userid;
        if (TextUtils.isEmpty(mUserId))
            userid = Integer.parseInt(UserDao.getInstance().getUser().getId());
        else
            userid = Integer.parseInt(mUserId);

        AsyncTask<Void, Void, List<ProtoEFreight>> task = new AsyncTask<Void, Void, List<ProtoEFreight>>() {

            @Override
            protected List<ProtoEFreight> doInBackground(Void... params) {
                NetListMineFreight net = new NetListMineFreight() {

                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setDate(Long.MAX_VALUE);
                        req.setLimitCount(10);
                        req.setLogisticId(userid);
                        req.setId(0);
                        req.setLogisticName(UserDao.getInstance().getUser().getShow_name());
                        return true;
                    }
                };
                try {
                    FreightResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        mTotalCount = resp.getFreightCountOnBlackBoard();
                        mTodayCount = resp.getFreightCountOfTodatyOnBlackBoard();
                        List<ProtoEFreight> freightList = resp.getFreightList();
                        return freightList;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<ProtoEFreight> freightList) {
                if (freightList == null || freightList.isEmpty()) {
                    // return;
                } else {
                    List<Freight> result = new ArrayList<Freight>();
                    for (ProtoEFreight freight : freightList) {
                        Freight d = FreightParser.parse(freight);
                        checkGone(result, d);
                    }
                    mAdapter.replaceAll(result);
                }
                tvPublishCount.setText("今天已发布" + mTodayCount + "条，还可以发布"
                        + Math.max((MAX_PUBLISH_COUNT_EVERYDAY - mTodayCount), 0) + "条");
            }

        };
        task.execute();
    }

    // 2014/12/01 Jack Zhu add
    void checkGone(List<Freight> result, Freight d) {
        if (d.getStatus() != Properties.FREIGHT_STATUS_NO_PROCESSED && checkgone == true) {

        } else {
            result.add(d);
        }
    }

    // 往下滑动加载
    private void slideDownEvent() {
        final int userid;
        if (TextUtils.isEmpty(mUserId))
            userid = Integer.parseInt(UserDao.getInstance().getUser().getId());
        else
            userid = Integer.parseInt(mUserId);

        AsyncTask<Void, Void, List<ProtoEFreight>> task = new AsyncTask<Void, Void, List<ProtoEFreight>>() {

            @Override
            protected List<ProtoEFreight> doInBackground(Void... params) {
                NetListMineFreight net = new NetListMineFreight() {

                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setDate(Long.MAX_VALUE);
                        req.setLimitCount(10);
                        req.setLogisticId(userid);
                        req.setId(0);
                        req.setLogisticName(UserDao.getInstance().getUser().getShow_name());
                        return true;
                    }
                };
                try {
                    FreightResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        mTotalCount = resp.getFreightCountOnBlackBoard();
                        mTodayCount = resp.getFreightCountOfTodatyOnBlackBoard();
                        List<ProtoEFreight> freightList = resp.getFreightList();
                        return freightList;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<ProtoEFreight> freightList) {
                mPullToRefreshListView.onRefreshComplete();
                if (freightList == null || freightList.isEmpty()) {
                    ToastUtils.showToast("您还没有发布更新的信息");
                    return;
                } else {
                    List<Freight> result = new ArrayList<Freight>();
                    for (ProtoEFreight freight : freightList) {
                        Freight d = FreightParser.parse(freight);
                        checkGone(result, d);
                    }
                    mAdapter.replaceAll(result);
                }
            }
        };
        task.execute();

        // NetListMineFreight net = new NetListMineFreight(null, Long.MAX_VALUE,
        // 10, userid);
        // net.request(new OnNetRequestListenerImpl<FreightResp.Builder>() {
        //
        // @Override
        // public void onError() {
        // super.onError();
        // lv_black.onRefreshComplete();
        // }
        //
        // @Override
        // public void onFail(String msg) {
        // super.onFail(msg);
        // lv_black.onRefreshComplete();
        // }
        //
        // @Override
        // public void onSuccess(FreightResp.Builder response) {
        // mTotalCount = response.getFreightCountOnBlackBoard();
        // mTodayCount = response.getFreightCountOfTodatyOnBlackBoard();
        // List<ProtoEFreight> freightList = response.getFreightList();
        // if (freightList == null || freightList.isEmpty()) {
        // ToastUtils.showToast("您还没有发布更新的信息");
        // lv_black.onRefreshComplete();
        // return;
        // } else {
        // List<Freight> result = new ArrayList<Freight>();
        // for (ProtoEFreight freight : freightList) {
        // Freight d = FreightParser.parse(freight);
        // checkGone(result, d);
        // }
        // mAdapter.replaceAll(result);
        // lv_black.onRefreshComplete();
        // }
        // }
        // });
    }

    // 往上滑动加载
    private void slideUpEvent() {
        if (mAdapter.isEmpty()) {
        	HandlerUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPullToRefreshListView.onRefreshComplete();
                }
            }, 100);
            return;
        }

        final long time = mAdapter.getItem(mAdapter.getCount() - 1).getCreate_time();
        final String edgeId = mAdapter.getItem(mAdapter.getCount() - 1).getId();
        final int userid;
        if (TextUtils.isEmpty(mUserId))
            userid = Integer.parseInt(UserDao.getInstance().getUser().getId());
        else
            userid = Integer.parseInt(mUserId);

        AsyncTask<Void, Void, List<ProtoEFreight>> task = new AsyncTask<Void, Void, List<ProtoEFreight>>() {

            @Override
            protected List<ProtoEFreight> doInBackground(Void... params) {
                NetListMineFreight net = new NetListMineFreight() {
                    @Override
                    protected boolean onSetRequest(Builder req) {
                        req.setDate(time);
                        req.setLimitCount(10);
                        req.setId(Integer.parseInt(edgeId));
                        req.setLogisticId(userid);
                        req.setLogisticName(UserDao.getInstance().getUser().getShow_name());
                        return true;
                    }
                };
                try {
                    FreightResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        mTotalCount = resp.getFreightCountOnBlackBoard();
                        mTodayCount = resp.getFreightCountOfTodatyOnBlackBoard();
                        List<ProtoEFreight> freightList = resp.getFreightList();
                        return freightList;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<ProtoEFreight> freightList) {
                mPullToRefreshListView.onRefreshComplete();
                if (freightList == null || freightList.isEmpty()) {
                    ToastUtils.showToast("小黑板没有更多信息");
                    return;
                } else {
                    List<Freight> result = new ArrayList<Freight>();
                    for (ProtoEFreight freight : freightList) {
                        Freight d = FreightParser.parse(freight);
                        checkGone(result, d);
                    }
                    mAdapter.addAll(result);
                    HandlerUtils.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int y = mListView.getScrollY() - mPullToRefreshListView.getFootViewSize();
                                mListView.scrollTo(0, y);
                            } catch (Exception e) {

                            }
                        }
                    }, 100);
                }
            }
        };
        task.execute();

        // NetListMineFreight net = new NetListMineFreight(null, time, 10,
        // userid);
        // net.request(new OnNetRequestListenerImpl<FreightResp.Builder>() {
        //
        // @Override
        // public void onError() {
        // super.onError();
        // lv_black.onRefreshComplete();
        // }
        //
        // @Override
        // public void onFail(String msg) {
        // super.onFail(msg);
        // lv_black.onRefreshComplete();
        // }
        //
        // @Override
        // public void onSuccess(FreightResp.Builder response) {
        // lv_black.onRefreshComplete();
        // mTotalCount = response.getFreightCountOnBlackBoard();
        // mTodayCount = response.getFreightCountOfTodatyOnBlackBoard();
        // List<ProtoEFreight> freightList = response.getFreightList();
        // if (freightList == null || freightList.isEmpty()) {
        // ToastUtils.showToast("小黑板没有更多信息");
        // return;
        // } else {
        // List<Freight> result = new ArrayList<Freight>();
        // for (ProtoEFreight freight : freightList) {
        // Freight d = FreightParser.parse(freight);
        // checkGone(result, d);
        // }
        // mAdapter.addAll(result);
        // HandlerUtils.postDelayed(new Runnable() {
        // @Override
        // public void run() {
        // try {
        // int y = mListView.getScrollY()
        // - lv_black.getFootViewSize();
        // mListView.scrollTo(0, y);
        // } catch (Exception e) {
        //
        // }
        // }
        // }, 100);
        // }
        // }
        // });
    }

    // private void autoLoading() {}
}
