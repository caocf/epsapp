package com.epeisong.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.base.adapter.impl.IconTextAdapter;
import com.epeisong.base.adapter.item.IconTextItem;
import com.epeisong.base.view.AdjustHeightGridView;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.PhoneContactsDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.layer02.SupplyDetailsProvider;
import com.epeisong.data.layer02.SupplyDetailsProvider.ProvideResult;
import com.epeisong.data.net.NetCreateInfoFee;
import com.epeisong.data.net.NetGetPublishBlack;
import com.epeisong.data.net.NetGuarantee;
import com.epeisong.data.net.NetRepasteFreight;
import com.epeisong.data.net.parser.ContactsParser;
import com.epeisong.data.net.parser.FreightParser;
import com.epeisong.data.net.parser.GuaranteeParser;
import com.epeisong.data.utils.PromptUtils;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Base.ProtoEBizLogistics;
import com.epeisong.logistics.proto.Eps;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.FreightReq;
import com.epeisong.logistics.proto.Eps.FreightResp;
import com.epeisong.logistics.proto.InfoFee.InfoFeeReq;
import com.epeisong.logistics.proto.Transaction.ProtoInfoFee;
import com.epeisong.model.Contacts;
import com.epeisong.model.Freight;
import com.epeisong.model.Guarantee;
import com.epeisong.model.User;
import com.epeisong.net.request.NetAddBanned;
import com.epeisong.net.request.NetAddContacts;
import com.epeisong.net.request.NetUpdateFreightDeliveryReceiverStatus;
import com.epeisong.net.request.NetUpdateIsAllowToShow;
import com.epeisong.net.request.OnNetRequestListener;
import com.epeisong.net.request.OnNetRequestListenerImpl;
import com.epeisong.service.receiver.FreightReceiver;
import com.epeisong.ui.activity.BlackBoardActivity;
import com.epeisong.ui.activity.ChooseNewContactsActivity;
import com.epeisong.ui.activity.NoticeOrRelayActivity;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressWarnings("deprecation")
public class OtherCarSourceFragment extends Fragment implements OnClickListener {
    public static final String EXTRA_FREIGHT = "mFreight";

    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_FREIGHT_ID = "freight_id";
    private static final int REQUEST_CHOOSE_CONTACTS_NOTICE = 100;
    public static final String EXTRA_IS_HIDE_DELETE_BTN = "is_hide_delete_btn";
    public static final String SEND_BLACKBOARD_BTN = "is_send_btn";

    // private ImageView iv_zt;
    private TextView tv_state;
    private ImageView iv_goods;
    private TextView tv_time;
    private TextView tv_region;
    private TextView tv_describe;
    // private TextView tv_money;
    private LinearLayout bt_forwarding;
    private LinearLayout bt_chalkboard;
    // private LinearLayout bt_immediately_consult;
    private TextView tv_chalkboard;
    private TextView tv_user_show_name;
    private RatingBar ratingBar;
    private TextView tv_contact_name;
    private ImageView iv_add_contacts;
    private TextView tv_phone_number;
    private ImageView iv_phone;
    private TextView tv_telephone_number;
    private ImageView iv_telephone;
    private Button bt_delete;
    private LinearLayout bt_distribution;
    private RelativeLayout rllt3;
    private RelativeLayout rllt4;
    private RelativeLayout rllt5;
    private TextView tv_distribution;

    private int mTodayCount;
    private int mTotalCount;

    private User user;
    private Freight mFreight;
    private String user_id;
    private String mFreight_id;
    private PopupWindow mPopupWindowMenu;

    private AdjustHeightGridView mGridView;
    private MyAdapter mAdapter;
    private Guarantee mGuarantee;

    private FreightReceiver mFreightReceiver = new FreightReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Freight f = (Freight) intent.getSerializableExtra(FreightReceiver.EXTRA_FREIGHT);
            if (f != null && mFreight != null && mFreight.getId().equals(f.getId())) {
                mFreight.setStatus(f.getStatus());
                updateOrderStatus();
            }
        }
    };

    private void initPopupWindowMenu() {
        final List<IconTextItem> items = new ArrayList<IconTextItem>();
        items.add(new IconTextItem(R.drawable.selector_common_checkable, "屏蔽该用户", null));
        items.add(new IconTextItem(R.drawable.selector_common_checkable, "屏蔽该信息", null));
        mPopupWindowMenu = new PopupWindow(getActivity());
        IconTextAdapter adapter = new IconTextAdapter(getActivity(), 40);
        adapter.replaceAll(items);
        ListView lv = new ListView(getActivity());
        lv.setAdapter(adapter);
        lv.setBackgroundColor(Color.argb(0xFF, 0x2D, 0x30, 0x37));
        mPopupWindowMenu.setContentView(lv);
        mPopupWindowMenu.setWidth(EpsApplication.getScreenWidth() / 2);
        mPopupWindowMenu.setHeight(LayoutParams.WRAP_CONTENT);
        mPopupWindowMenu.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        mPopupWindowMenu.setFocusable(true);
        mPopupWindowMenu.setOutsideTouchable(true);
        mPopupWindowMenu.setAnimationStyle(R.style.popup_window_menu);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPopupWindowMenu.dismiss();

                if (position == 0) {
                    NetAddBanned net = new NetAddBanned((XBaseActivity) getActivity(), Integer.parseInt(mFreight
                            .getUser_id()));
                    net.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {

                        @Override
                        public void onSuccess(CommonLogisticsResp.Builder response) {
                            items.add(new IconTextItem(R.drawable.selector_common_checkable, "该用户已屏蔽", null));
                            items.add(new IconTextItem(R.drawable.selector_common_checkable, "屏蔽该信息", null));
                        }

                    });
                } else if (position == 1) {
                    NetUpdateIsAllowToShow netShow = new NetUpdateIsAllowToShow((XBaseActivity) getActivity(),
                            Properties.MARKET_SCREEN_NOT_ALLOW_TO_SHOW, Integer.parseInt(mFreight.getId()));
                    netShow.request(new OnNetRequestListenerImpl<Eps.CommonLogisticsResp.Builder>() {
                        @Override
                        public void onSuccess(com.epeisong.logistics.proto.Eps.CommonLogisticsResp.Builder response) {
                            items.add(new IconTextItem(R.drawable.selector_common_checkable, "屏蔽该用户", null));
                            items.add(new IconTextItem(R.drawable.selector_common_checkable, "该信息已被屏蔽", null));
                        }
                    });
                }

                Object obj = parent.getItemAtPosition(position);
                if (obj != null && obj instanceof IconTextItem) {
                    IconTextItem item = (IconTextItem) obj;
                    ToastUtils.showToast(item.getName());
                }
            }
        });
        lv.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP
                        && mPopupWindowMenu.isShowing()) {
                    mPopupWindowMenu.dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    private void updateOrderStatus() {
        if (mFreight == null) {
            return;
        }
        switch (mFreight.getStatus()) {
        case Properties.FREIGHT_STATUS_NO_PROCESSED:
            iv_goods.setImageResource(R.drawable.selector_board_truck);
            tv_state.setText("该车待配货");
            break;
        case Properties.FREIGHT_STATUS_BOOK:
            // 已被订
            bt_forwarding.setEnabled(true);
            bt_chalkboard.setEnabled(true);
            bt_distribution.setBackgroundColor(Color.argb(255, 192, 192, 192));
            iv_goods.setImageResource(R.drawable.selector_board_truck);
            tv_state.setText("该车源被预订，还未成交，请速与车源方联系");
            break;

        default:
            bt_distribution.setBackgroundColor(Color.argb(255, 192, 192, 192));
            iv_goods.setImageResource(R.drawable.selector_board_truck);
            bt_forwarding.setBackgroundColor(Color.argb(255, 192, 192, 192));
            bt_chalkboard.setVisibility(View.GONE);
            tv_state.setText("该信息已成交");
            break;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        mFreight = (Freight) args.getSerializable(EXTRA_FREIGHT);
        user_id = args.getString(EXTRA_USER_ID);
        mFreight_id = args.getString(EXTRA_FREIGHT_ID);

        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.activity_other_car_source, null);

        tv_time = (TextView) root.findViewById(R.id.tv_time);
        tv_region = (TextView) root.findViewById(R.id.tv_region);
        tv_describe = (TextView) root.findViewById(R.id.tv_describe);
        // tv_money = (TextView) root.findViewById(R.id.tv_money);
        bt_forwarding = (LinearLayout) root.findViewById(R.id.bt_forwarding);
        bt_chalkboard = (LinearLayout) root.findViewById(R.id.bt_chalkboard);
        // bt_immediately_consult = (LinearLayout) root
        // .findViewById(R.id.bt_immediately_consult);
        tv_user_show_name = (TextView) root.findViewById(R.id.tv_user_show_name);
        ratingBar = (RatingBar) root.findViewById(R.id.ratingBar);
        tv_contact_name = (TextView) root.findViewById(R.id.tv_contact_name);
        iv_add_contacts = (ImageView) root.findViewById(R.id.iv_add_contacts);
        tv_phone_number = (TextView) root.findViewById(R.id.tv_phone_number);
        iv_phone = (ImageView) root.findViewById(R.id.iv_phone);
        tv_telephone_number = (TextView) root.findViewById(R.id.tv_telephone_number);

        iv_telephone = (ImageView) root.findViewById(R.id.iv_telephone);
        bt_delete = (Button) root.findViewById(R.id.bt_delete);
        bt_distribution = (LinearLayout) root.findViewById(R.id.bt_distribution);
        tv_distribution = (TextView) root.findViewById(R.id.tv_distribution);
        rllt3 = (RelativeLayout) root.findViewById(R.id.rllt3);
        rllt4 = (RelativeLayout) root.findViewById(R.id.rllt4);
        rllt5 = (RelativeLayout) root.findViewById(R.id.rllt5);
        tv_chalkboard = (TextView) root.findViewById(R.id.tv_chalkboard);
        // iv_zt = (ImageView) root.findViewById(R.id.iv_zt);
        tv_state = (TextView) root.findViewById(R.id.tv_state_content);
        iv_goods = (ImageView) root.findViewById(R.id.iv_goods);

        iv_add_contacts.setOnClickListener(this);
        iv_telephone.setOnClickListener(this);
        iv_phone.setOnClickListener(this);
        bt_forwarding.setOnClickListener(this);
        // bt_immediately_consult.setOnClickListener(this);
        bt_chalkboard.setOnClickListener(this);
        bt_delete.setOnClickListener(this);
        bt_distribution.setOnClickListener(this);

        mGridView = (AdjustHeightGridView) root.findViewById(R.id.gv_img);
        mGridView.setNumColumns(4);
        int p = DimensionUtls.getPixelFromDpInt(10);
        mGridView.setPadding(0, p, 0, 0);
        mGridView.setSelector(R.color.transparent);
        mGridView.setBackgroundColor(Color.WHITE);
        mGridView.setAdapter(mAdapter = new MyAdapter());

        if (args.getBoolean(EXTRA_IS_HIDE_DELETE_BTN, false)) {
            bt_delete.setVisibility(View.GONE);

        }

        if (ContactsDao.getInstance().queryById(user_id) == null) {
            iv_add_contacts.setImageResource(R.drawable.loading_distribution_lianxiren);

        } else {
            iv_add_contacts.setImageResource(R.drawable.loading_distribution_lianxirenyitianjia1);
            iv_add_contacts.setEnabled(false);
        }

        iv_add_contacts.setVisibility(View.INVISIBLE);

        refreshData();

        FreightReceiver.register(getActivity(), mFreightReceiver);
        return root;
    }

    private void loadData(final int size, final String edge_id, final double weight, final boolean bFirst) {

        AsyncTask<Void, Void, List<Guarantee>> task = new AsyncTask<Void, Void, List<Guarantee>>() {

            @Override
            protected List<Guarantee> doInBackground(Void... params) {

                // TODO Auto-generated method stub
                NetGuarantee netGuarantee = new NetGuarantee() {

                    @Override
                    protected int getCommandCode() {
                        // TODO Auto-generated method stub
                        return CommandConstants.LIST_GUARANTEE_PRODUCT_CUSTOMER_CONTACT_REQ;// LIST_GUARANTEE_PRODUCT_REQ;
                    }

                    @Override
                    protected boolean onSetRequest(com.epeisong.logistics.proto.Eps.GuaranteeReq.Builder req) {
                        // TODO Auto-generated method stub
                        req.setLimitCount(size);
                        int id = 0;
                        try {
                            if (edge_id != null) {
                                id = Integer.parseInt(edge_id);
                            }
                        } catch (Exception e) {
                            id = 0;
                        }
                        req.setCustomerId(Integer.valueOf(user.getId()));
                        return true;
                    }

                };
                try {
                    com.epeisong.logistics.proto.Eps.GuaranteeResp.Builder resp = netGuarantee.request();
                    return GuaranteeParser.parse(resp);
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Guarantee> result) {
                // TODO Auto-generated method stub

                if (result != null && !result.isEmpty()) {
                    mAdapter.addAll(result);
                    mGuarantee = result.get(0);
                }
            }

        };
        task.execute();
    }

    @Override
    public void onDestroyView() {
        FreightReceiver.unRegister(getActivity(), mFreightReceiver);
        super.onDestroyView();
    }

    private void refreshData() {
        AsyncTask<Void, Void, ProvideResult> task = new AsyncTask<Void, Void, ProvideResult>() {
            @Override
            protected ProvideResult doInBackground(Void... params) {
                SupplyDetailsProvider provider = new SupplyDetailsProvider();
                return provider.provide(user_id, mFreight_id);
            }

            @Override
            protected void onPostExecute(ProvideResult result) {

                if (result != null) {

                    mFreight = result.getmFreight();

                    if (mFreight == null) {
                        ToastUtils.showToast("refreshView freight is null");
                        return;
                    }

                    updateOrderStatus();

                    user = result.getUser();

                    if (mFreight.getForward_to_blacklist() == Freight.FORWARD_TO_BLACKBOARD_NOT) {
                        tv_chalkboard.setText("转发到我的小黑板");
                    } else {
                        tv_chalkboard.setText("已转发到我的小黑板");
                        bt_chalkboard.setEnabled(false);
                    }
                    /******************* 朋友的车源详情信息费 ************************/
                    // tv_money.setText(String.valueOf(mFreight.getInfo_cost()));

                    tv_time.setText(DateUtil.long2YMDHM(mFreight.getCreate_time()));
                    // 起始地址，终止地址
                    tv_region.setText(mFreight.getStart_region() + " — " + mFreight.getEnd_region());
                    tv_describe.setText(mFreight.getDesc());
                    tv_user_show_name.setText(mFreight.getOwner_name());
                    // if (mFreight.getOrder_status() ==
                    // Freight.ORDER_STATUS_UN_ORDER) {
                    // iv_goods.setImageResource(R.drawable.selector_board_truck);
                    // } else if (mFreight.getOrder_status() ==
                    // Freight.ORDER_STATUS_ORDERED) {
                    // iv_goods.setImageResource(R.drawable.selector_booked_truck);
                    // }
                    iv_goods.setImageResource(R.drawable.selector_board_truck);
                    Contacts contacts = ContactsDao.getInstance().queryById(user_id);
                    if (user != null) {
                        tv_contact_name.setText(user.getContacts_name());
                        tv_phone_number.setText(user.getContacts_phone());
                        tv_telephone_number.setText(user.getContacts_telephone());
                        ratingBar.setProgress(user.getStar_level());
                        if (contacts != null) {
                            ratingBar.setProgress(contacts.getStar_level());
                        }
                    }

                    loadData(-1, "0", 0, true);
                }
            }
        };
        task.execute();
    }

    private void refreshView() {
        if (mFreight == null) {
            ToastUtils.showToast("refreshView freight is null");
            return;
        }
        updateOrderStatus();
    }

    private void checkTodayCount() {
        final User user = UserDao.getInstance().getUser();
        NetGetPublishBlack net = new NetGetPublishBlack() {
            @Override
            protected boolean onSetRequest(com.epeisong.logistics.proto.Eps.FreightReq.Builder req) {
                req.setLogisticId(Integer.parseInt(user.getId()));
                return true;
            }
        };
        try {
            FreightResp.Builder response = net.request();
            if (response != null && "SUCC".equals(response.getResult())) {
                mTodayCount = response.getFreightCountOfTodatyOnBlackBoard();
                mTotalCount = response.getFreightCountOnBlackBoard();
                if (mTodayCount >= BlackBoardActivity.MAX_PUBLISH_COUNT_EVERYDAY) {
                    ToastUtils.showToast("今天发布的车源货源已经达到上限！");
                    return;
                }
                if (mTotalCount >= BlackBoardActivity.MAX_COUNT_ONBOARD) {
                    ToastUtils.showToast("黑板上的车源货源信息太多，请删除一些无效信息！");
                    return;
                }
                if (mFreight.getForward_to_blacklist() == Freight.FORWARD_TO_BLACKBOARD_ALREADY) {
                    return;
                }
                ((XBaseActivity) getActivity()).showPendingDialog(null);
                AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

                    @Override
                    protected Boolean doInBackground(Void... params) {
                        NetRepasteFreight repaste = new NetRepasteFreight() {
                            @Override
                            protected boolean onSetRequest(FreightReq.Builder req) {
                                req.setFreightId(Integer.parseInt(mFreight.getId()));
                                return true;
                            }
                        };
                        try {
                            FreightResp.Builder resp = repaste.request();
                            if (resp != null && "SUCC".equals(resp.getResult())) {
                                return true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        ((XBaseActivity) getActivity()).dismissPendingDialog();
                        if (result) {
                            mFreight.setForward_to_blacklist(Freight.FORWARD_TO_BLACKBOARD_ALREADY);
                            tv_chalkboard.setText("转发到我的小黑板");
                            bt_chalkboard.setClickable(false);
                            bt_chalkboard.setOnClickListener(null);
                        }
                    }
                };
                task.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // NetGetPublishBlack net = new NetGetPublishBlack((XBaseActivity)
        // getActivity(), Integer.parseInt(user.getId()));
        // net.request(new OnNetRequestListenerImpl<Eps.FreightResp.Builder>() {
        //
        // @Override
        // public void onSuccess(Builder response) {
        // // TODO Auto-generated method stub
        // mTodayCount = response.getFreightCountOfTodatyOnBlackBoard();
        // mTotalCount = response.getFreightCountOnBlackBoard();
        // if (mTodayCount >= BlackBoardActivity.MAX_PUBLISH_COUNT_EVERYDAY) {
        // ToastUtils.showToast("今天发布的车源货源已经达到上限！");
        // return;
        // }
        // if (mTotalCount >= BlackBoardActivity.MAX_COUNT_ONBOARD) {
        // ToastUtils.showToast("黑板上的车源货源信息太多，请删除一些无效信息！");
        // return;
        // }
        // if (mFreight.getForward_to_blacklist() ==
        // Freight.FORWARD_TO_BLACKBOARD_ALREADY) {
        // return;
        // }
        //
        // NetRepasteFreight repaste = new NetRepasteFreight((XBaseActivity)
        // getActivity()) {
        // @Override
        // protected boolean onSetRequest(FreightReq.Builder req) {
        // req.setFreightId(Integer.parseInt(mFreight.getId()));
        // return true;
        // }
        // };
        // repaste.request(new OnNetRequestListener<Eps.FreightResp.Builder>() {
        // @Override
        // public void onError() {
        //
        // }
        //
        // @Override
        // public void onFail(String msg) {
        //
        // }
        //
        // @Override
        // public void
        // onSuccess(com.epeisong.logistics.proto.Eps.FreightResp.Builder
        // response) {
        // mFreight.setForward_to_blacklist(Freight.FORWARD_TO_BLACKBOARD_ALREADY);
        // FreightOfContactsDao.getInstance().update(mFreight);
        // tv_chalkboard.setText("转发到我的小黑板");
        // bt_chalkboard.setClickable(false);
        // bt_chalkboard.setOnClickListener(null);
        // }
        // });
        // }

        // });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.iv_add_contacts:
            NetAddContacts net = new NetAddContacts((XBaseActivity) getActivity(), user_id);
            net.request(new OnNetRequestListenerImpl<CommonLogisticsResp.Builder>() {
                @Override
                public void onSuccess(CommonLogisticsResp.Builder response) {
                    ProtoEBizLogistics logistics = response.getBizLogistics(0);

                    if (logistics != null) {
                        Contacts c = ContactsParser.parse(logistics);
                        c.setStatus(Contacts.STATUS_NORNAL);
                        ContactsDao.getInstance().insert(c);
                        PhoneContactsDao.getInstance().updateAdded(c.getPhone());
                        ToastUtils.showToast("添加成功");
                        iv_add_contacts.setImageResource(R.drawable.loading_distribution_lianxirenyitianjia1);
                        iv_add_contacts.setEnabled(false);
                    }
                }
            });
            break;
        case R.id.iv_telephone:

            if (user != null) {
                if (TextUtils.isEmpty(user.getContacts_telephone())) {
                    iv_telephone.setEnabled(false);
                } else {

                    Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.getContacts_telephone()));
                    startActivity(intent1);
                }
            }

            break;

        case R.id.iv_phone:
            if (user != null) {
                if (TextUtils.isEmpty(user.getContacts_phone())) {
                    iv_phone.setEnabled(false);
                } else {

                    Intent intent2 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.getContacts_phone()));
                    startActivity(intent2);
                }

            }
            break;

        case R.id.bt_forwarding:
            // Intent intent3 = new Intent(getActivity(),
            // ChooseContactsActivity.class);
            Intent intent3 = new Intent(getActivity(), ChooseNewContactsActivity.class);
            intent3.putExtra("originalUserId", mFreight.getUser_id());
            startActivityForResult(intent3, REQUEST_CHOOSE_CONTACTS_NOTICE);
            break;
        /********** 咨询 **************/
        /*
         * case R.id.bt_immediately_consult: Intent intent4 = new
         * Intent(getActivity(), ChatRoomActivity.class);
         * intent4.putExtra(ChatRoomActivity.EXTRA_REMOTE_ID, user_id);
         * intent4.putExtra(ChatRoomActivity.EXTRA_BUSINESS_TYPE,
         * ChatMsg.business_type_freight);
         * intent4.putExtra(ChatRoomActivity.EXTRA_BUSINESS_ID, mFreight_id);
         * startActivity(intent4); break;
         */

        case R.id.bt_chalkboard:
            checkTodayCount();

            break;

        case R.id.bt_delete:
            NetUpdateFreightDeliveryReceiverStatus update = new NetUpdateFreightDeliveryReceiverStatus(
                    (XBaseActivity) getActivity()) {
                @Override
                protected boolean onSetRequest(FreightReq.Builder req) {
                    req.setFreightDeliveryId(Integer.parseInt(mFreight.getId()));
                    req.setNewReceiverStatus(Properties.FREIGHT_STATUS_DELETED);
                    return true;
                }
            };
            update.request(new OnNetRequestListener<Eps.FreightResp.Builder>() {

                @Override
                public void onError() {

                }

                @Override
                public void onFail(String msg) {

                }

                @Override
                public void onSuccess(com.epeisong.logistics.proto.Eps.FreightResp.Builder response) {
                    // iv_zt.setBackgroundColor(Color.argb(255, 192, 192, 192));
                    bt_distribution.setBackgroundColor(Color.argb(255, 192, 192, 192));
                    bt_forwarding.setBackgroundColor(Color.argb(255, 192, 192, 192));
                    bt_chalkboard.setBackgroundColor(Color.argb(255, 192, 192, 192));
                }
            });
            break;
        case R.id.bt_distribution:
            if (mFreight.getStatus() != Properties.FREIGHT_STATUS_NO_PROCESSED) {
                return;
            } else {
                orderCar();
            }

            break;
        }
    }

    private void orderCar() {
        LinearLayout ll_goods;
        LinearLayout ll_truck;

        TextView tv_type_name;
        TextView tv_user_name;
        TextView tv_info;
        TextView tv_info_money;
        TextView tv_title;
        ImageView iv_pic;
        Button bt_sure;
        Button bt_false;
        final Dialog dialog = new Dialog(getActivity());
        View view = SystemUtils.inflate(R.layout.truck_goods_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        tv_type_name = (TextView) view.findViewById(R.id.tv_type_name);
        tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);

        iv_pic = (ImageView) view.findViewById(R.id.iv_pic);

        if (!UserDao.getInstance().getUser().getId().equals(user_id)) {
            if (mGuarantee != null && !TextUtils.isEmpty(mGuarantee.getMark_url2()))
                ImageLoader.getInstance().displayImage(mGuarantee.getMark_url2(), iv_pic);
        } else {
            if (mGuarantee != null && !TextUtils.isEmpty(mGuarantee.getMark_url1()))
                ImageLoader.getInstance().displayImage(mGuarantee.getMark_url1(), iv_pic);
        }

        bt_sure = (Button) view.findViewById(R.id.bt_sure);
        bt_false = (Button) view.findViewById(R.id.bt_false);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        ll_truck = (LinearLayout) view.findViewById(R.id.ll_truck);
        ll_goods = (LinearLayout) view.findViewById(R.id.ll_goods);
        ll_goods.setVisibility(View.GONE);
        ll_truck.setVisibility(View.GONE);
        tv_user_name.setText(mFreight.getOwner_name());

        ll_goods.setVisibility(View.GONE);
        ll_truck.setVisibility(View.VISIBLE);
        tv_info = (TextView) view.findViewById(R.id.tv_truck_info);
        final EditText et_info = (EditText) view.findViewById(R.id.et_info);
        tv_info_money = (TextView) view.findViewById(R.id.tv_info_money);
        tv_info_money.setText(mFreight.getInfo_cost() + "");
        tv_title.setText("给对方配货！");
        tv_type_name.setText("车源方：");
        tv_info.setText("我要信息费：");
        bt_sure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final String money = et_info.getText().toString();
                if (TextUtils.isEmpty(money)) {
                    ToastUtils.showToast("请填写信息费");
                } else {
                    mFreight.setInfo_cost((int) Float.parseFloat(money));
                    if (mFreight.getStatus() == Properties.FREIGHT_STATUS_NO_PROCESSED) {
                        ((XBaseActivity) getActivity()).showPendingDialog(null);
                        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

                            @Override
                            protected Boolean doInBackground(Void... params) {
                                NetCreateInfoFee net = new NetCreateInfoFee() {

                                    @Override
                                    protected boolean onSetRequest(InfoFeeReq.Builder req) {
                                        ProtoInfoFee.Builder infofeebuBuilder = ProtoInfoFee.newBuilder();
                                        infofeebuBuilder.setType(Properties.INFO_FEE_TYPE_VEHICLE);
                                        infofeebuBuilder.setFreightId(Integer.parseInt(mFreight.getId()));
                                        infofeebuBuilder.setFreightAddr(mFreight.getStart_region() + " - "
                                                + mFreight.getEnd_region());
                                        infofeebuBuilder.setFreightInfo(mFreight.getDesc());
                                        infofeebuBuilder.setInfoAmount(mFreight.getInfo_cost());
                                        // 担保方目前测试写死的 start
                                        // infofeebuBuilder.setGuaranteeId(6);
                                        // infofeebuBuilder.setGuaranteeName("刘林");
                                        // 担保方目前测试写死的 end
                                        User payee = UserDao.getInstance().getUser();
                                        infofeebuBuilder.setPayeeId(Integer.parseInt(payee.getId()));
                                        infofeebuBuilder.setPayeeName(payee.getShow_name());

                                        infofeebuBuilder.setPayerId(Integer.parseInt(mFreight.getUser_id()));
                                        infofeebuBuilder.setPayerName(mFreight.getOwner_name());

                                        infofeebuBuilder
                                                .setPayeeFlowStatus(Properties.INFO_FEE_PAYEE_ORDER_STATUS_WAITING_FOR_CONFIRM);
                                        infofeebuBuilder
                                                .setPayerFlowStatus(Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_REQUEST_ORDERS);

                                        req.setInfoFee(infofeebuBuilder);
                                        req.setLogisticsId(Integer.parseInt(payee.getId()));
                                        return true;
                                    }
                                };

                                FreightResp.Builder resp = null;
                                try {
                                    resp = net.request();
                                    Freight f = FreightParser.parseSingle(resp);
                                    if (f != null) {
                                        mFreight = f;
                                        HandlerUtils.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                updateOrderStatus();
                                            }
                                        });
                                    }
                                    if (net.isSuccess(resp)) {
                                        return true;
                                    }
                                    boolean bPayer = false;
                                    String msg = PromptUtils.getPrompt(resp.getResultStatus(), bPayer);
                                    ToastUtils.showToastInThread(msg);
                                } catch (NetGetException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }

                            @Override
                            protected void onPostExecute(Boolean bool) {
                                ((XBaseActivity) getActivity()).dismissPendingDialog();
                                if (bool) {
                                    ToastUtils.showToast("配货成功");
                                    // mFreight.setOrder_status(Properties.FREIGHT_DEAL_STATUS_ORDERED);
                                    refreshView();

                                    Intent intent = new Intent("com.epeisong.ui.activity.refreshLish");
                                    intent.putExtra("freshList", mFreight);
                                    getActivity().sendBroadcast(intent); // 发送广播

                                    bt_distribution.setEnabled(false);
                                } else {
                                    // ToastUtils.showToast("配货失败");
                                }
                                dialog.dismiss();
                            }
                        };

                        task.execute();
                    }
                }
            }
        });

        bt_false.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        dialog.show();
        HandlerUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                SystemUtils.showInputMethod(et_info);
            }
        }, 200);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CHOOSE_CONTACTS_NOTICE) {
            Intent intent = new Intent(getActivity(), NoticeOrRelayActivity.class);
            intent.putExtra(NoticeOrRelayActivity.EXTRA_ACTION_TYPE, NoticeOrRelayActivity.ACTION_RELAY);
            intent.putExtra(NoticeOrRelayActivity.EXTRA_DISPATCH, mFreight);
            intent.putExtra(NoticeOrRelayActivity.EXTRA_CONTACTS_LIST,
                    data.getSerializableExtra(ChooseNewContactsActivity.EXTRA_SELECTED_CONTACTS_LIST));
            intent.putExtra("originalUserId", mFreight.getUser_id());
            startActivity(intent);
        }
    }

    private class MyAdapter extends HoldDataBaseAdapter<Guarantee> {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = SystemUtils.inflate(R.layout.fragment_guarantee_gridview_item);
                holder = new ViewHolder();
                holder.findView(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.fillData(getItem(position));
            return convertView;
        }
    }

    private class ViewHolder {
        ImageView iv;

        public void findView(View v) {
            iv = (ImageView) v.findViewById(R.id.iv);
        }

        public void fillData(final Guarantee item) {
            if (!UserDao.getInstance().getUser().getId().equals(user_id)) {
                if (!TextUtils.isEmpty(item.getMark_url2())) {
                    ImageLoader.getInstance().displayImage(item.getMark_url2(), iv);
                } else {
                    return;
                }
            } else {
                if (!TextUtils.isEmpty(item.getMark_url1())) {
                    ImageLoader.getInstance().displayImage(item.getMark_url1(), iv);
                } else {
                    return;
                }
            }
            iv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if (item != null) {
                        // ToastUtils.showToast("对方已开启"+item.getName()+"服务，若对方在交易中违约，对方将赔付"+String.valueOf(item.getAccount())+"元");
                        if (UserDao.getInstance().getUser().getId().equals(user_id))
                            Toast.makeText(
                                    getActivity(),
                                    "您已开启" + item.getName() + "服务，若您在交易中违约，您将赔付"
                                            + String.valueOf(item.getAccount() / 100) + "元", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(
                                    getActivity(),
                                    "对方已开启" + item.getName() + "服务，若对方在交易中违约，对方将赔付"
                                            + String.valueOf(item.getAccount() / 100) + "元", Toast.LENGTH_LONG).show();
                        // Intent intentcerIntent = new Intent(getActivity(),
                        // ProductDetailActivity.class);
                        // intentcerIntent.putExtra(ProductDetailActivity.EXTRA_GUARANTEE,
                        // item);
                        // intentcerIntent.putExtra(ProductDetailActivity.EXTRA_GUARDIS,
                        // "disable");
                        // startActivity(intentcerIntent);
                    }
                }
            });

        }
    }
}
