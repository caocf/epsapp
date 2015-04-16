package com.epeisong.ui.fragment;

import java.util.List;

import lib.pulltorefresh.PullToRefreshBase;
import lib.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import lib.pulltorefresh.PullToRefreshScrollView;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bdmap.LocManager;
import com.bdmap.LocManager.RegionObserver;
import com.epeisong.R;
import com.epeisong.data.dao.PointDao;
import com.epeisong.data.dao.PointDao.PointObserver;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetLogisticsSearch;
import com.epeisong.data.net.parser.UserParser;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.model.Point;
import com.epeisong.model.Point.PointCode;
import com.epeisong.model.Region;
import com.epeisong.model.User;
import com.epeisong.ui.activity.BlackBoardActivity;
import com.epeisong.ui.activity.ComplaintDealDetailActivity;
import com.epeisong.ui.activity.ComplaintHandlingActivity;
import com.epeisong.ui.activity.CustomerAddUserActivity;
import com.epeisong.ui.activity.InfoFeeListActivity;
import com.epeisong.ui.activity.MarketManageActivity;
import com.epeisong.ui.activity.NearbyMarketActivity;
import com.epeisong.ui.activity.PublishBulletinActivity;
import com.epeisong.ui.activity.SearchFollowActivity;
import com.epeisong.ui.activity.TransferWithdrawalActivity;
import com.epeisong.ui.activity.WithdrawalActivity;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 任务
 * 
 * @author Jack
 * 
 */

public class TaskNewFragment extends Fragment implements OnItemClickListener, OnClickListener, PointObserver {

    private EditText mOrderEt;
    public View root;
    private Boolean timeBoolean = true;
    private int mStartRegionCode;

    // private TextView mVechName, mVechCont;
    private TextView mFriendName, mFriendCont, tv_nocont;
    private TextView mNearbyCity;
    private ImageView /* ipublish, */ifriend;
    private ImageView tv_arrownearc;
    private ImageView tv_arrownearf, iv_noinfor;

    private ImageView iv_redmanage, iv_redfriend;

    public TextView tv_namef, tv_phname;
    private TextView tv_namec, tv_nearno;
    private PullToRefreshScrollView pullscr;

    private View mMarketContainer;
    private LinearLayout mTixian;
    private LinearLayout mTixianManager;
    private LinearLayout mInfoFee;
    private LinearLayout mComplaints;
    private LinearLayout mProblem;
    private LinearLayout mAddUser;

    private Region mRegion;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = SystemUtils.inflate(R.layout.activity_tasknew1);
        root.findViewById(R.id.ll_publish).setOnClickListener(this);

        iv_redmanage = (ImageView) root.findViewById(R.id.iv_redmanage);
        iv_redfriend = (ImageView) root.findViewById(R.id.iv_redfriend);

        root.findViewById(R.id.ll_friend).setOnClickListener(this);
        root.findViewById(R.id.ll_nearby).setOnClickListener(this);
        mInfoFee = (LinearLayout) root.findViewById(R.id.ll_infofee);
        mInfoFee.setOnClickListener(this);
        root.findViewById(R.id.ll_publishme).setOnClickListener(this);
        mTixian = (LinearLayout) root.findViewById(R.id.ll_tixian);
        mTixianManager = (LinearLayout) root.findViewById(R.id.ll_tixian_manager);
        mTixian.setOnClickListener(this);
        mTixianManager.setOnClickListener(this);
        root.findViewById(R.id.ll_complaints).setOnClickListener(this);
        root.findViewById(R.id.ll_problem).setOnClickListener(this);
        mMarketContainer = root.findViewById(R.id.ll_market);
        mMarketContainer.setOnClickListener(this);
        mComplaints = (LinearLayout) root.findViewById(R.id.ll_complaints);
        mComplaints.setOnClickListener(this);
        mProblem = (LinearLayout) root.findViewById(R.id.ll_problem);
        mProblem.setOnClickListener(this);
        mAddUser = (LinearLayout) root.findViewById(R.id.ll_add_user);
        mAddUser.setOnClickListener(this);

        User user = UserDao.getInstance().getUser();
        switch (user.getUser_type_code()) {
        case Properties.LOGISTIC_TYPE_MARKET:
        case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
            mMarketContainer.setVisibility(View.VISIBLE);
            break;
        // case Properties.LOGISTIC_TYPE_WALLET_CASH:
        // mInfoFee.setVisibility(View.GONE);
        // mTixian.setVisibility(View.VISIBLE);
        // mTixianManager.setVisibility(View.VISIBLE);
        // break;
        case Properties.LOGISTIC_TYPE_PLATFORM_CUSTOMER_SERVICE:
            mInfoFee.setVisibility(View.GONE);
            mComplaints.setVisibility(View.VISIBLE);
            mProblem.setVisibility(View.VISIBLE);
            mAddUser.setVisibility(View.VISIBLE);
            break;
        default:
            break;
        }

        // mVechName = (TextView) root.findViewById(R.id.tv_vechname);
        // mVechCont = (TextView) root.findViewById(R.id.tv_vechcont);

        mNearbyCity = (TextView) root.findViewById(R.id.tv_nearby_city);
        tv_namec = (TextView) root.findViewById(R.id.tv_namec);
        tv_nearno = (TextView) root.findViewById(R.id.tv_nearno);
        tv_arrownearc = (ImageView) root.findViewById(R.id.tv_arrownearc);
        tv_phname = (TextView) root.findViewById(R.id.tv_phname);

        mFriendName = (TextView) root.findViewById(R.id.tv_friendname);
        mFriendCont = (TextView) root.findViewById(R.id.tv_friendcont);
        tv_nocont = (TextView) root.findViewById(R.id.tv_nocont);
        // ipublish = (ImageView) root.findViewById(R.id.image_publish);
        ifriend = (ImageView) root.findViewById(R.id.image_friend);
        tv_namef = (TextView) root.findViewById(R.id.tv_namef);
        tv_arrownearf = (ImageView) root.findViewById(R.id.tv_arrownearf);
        iv_noinfor = (ImageView) root.findViewById(R.id.iv_noinfor);
        timeBoolean = true;
        // loadDataPublish();

        pullscr = (PullToRefreshScrollView) root.findViewById(R.id.scr_task);
        // pullscr.setMode(lib.pulltorefresh.PullToRefreshBase.Mode.DISABLED);
        pullscr.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                RefreshPubFriNear();
                pullscr.onRefreshComplete();
            }
        });

        PointDao.getInstance().addObserver(PointCode.Code_Task_FreightOfContacts, this);
        PointDao.getInstance().addObserver(PointCode.Code_Task_InfoFee, this);

        return root;
    }

    @Override
    public void onPointChange(Point p) {
        PointCode pointCode = PointCode.convertFromValue(p.getCode());
        switch (pointCode) {
        case Code_Task_FreightOfContacts:
            iv_redfriend.setVisibility(p.isShow() ? View.VISIBLE : View.GONE);
            break;
        case Code_Task_InfoFee:
            iv_redmanage.setVisibility(p.isShow() ? View.VISIBLE : View.GONE);
            break;
        default:
            break;
        }
    }

    void RefreshPubFriNear() {
        // loadDataPublish();
        // loadDataNearby(1, "0", 0, true);
        loadNearByMarket(mRegion);
    }

    private void loadNearByMarket(Region region) {
        if (region != null) {
            mStartRegionCode = region.getCode();
            mNearbyCity.setText(region.getName());
            // loadFromNet
            loadDataNearby(1, "0", 0, false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            if (timeBoolean == true) {
                RefreshPubFriNear();
                timeBoolean = false;
            }
        }
    }

    private void loadDataNearby(final int size, final String edge_id, final double weight, final boolean bFirst) {
        AsyncTask<Void, Void, List<User>> task = new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... params) {

                NetLogisticsSearch net = new NetLogisticsSearch() {
                    @Override
                    protected int getCommandCode() {
                        return CommandConstants.SEARCH_RecvGoods_ST_LU_LP_MARKET_REQ;
                    }

                    @Override
                    protected boolean onSetRequest(com.epeisong.logistics.proto.Eps.SearchCommonLogisticsReq.Builder req) {

                        req.setLimitCount(size);
                        int id = 0;
                        try {
                            if (edge_id != null) {
                                id = Integer.parseInt(edge_id);
                            }
                        } catch (Exception e) {
                            id = 0;
                        }
                        req.setId(id);
                        req.setLogisticTypeCode(Properties.LOGISTIC_TYPE_MARKET);
                        req.setWeightScore(weight);
                        if (mStartRegionCode > 0) {
                            req.setServeRegionCode(mStartRegionCode);
                        }

                        return true;

                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (resp == null) {
                        return null;
                    }
                    return UserParser.parse(resp);
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<User> result) {
                // dismissPendingDialog();
                if (result != null) {
                    if (result.isEmpty()) {
                        if (tv_phname != null)
                            tv_phname.setText("");
                    } else {
                        if (tv_phname != null)
                            tv_phname.setText(result.get(0).getShow_name());

                        if (tv_phname != null)
                            tv_phname.setVisibility(View.VISIBLE);
                        if (tv_nearno != null)
                            tv_nearno.setVisibility(View.GONE);
                        if (tv_arrownearc != null)
                            tv_arrownearc.setVisibility(View.VISIBLE);
                        if (tv_namec != null)
                            tv_namec.setVisibility(View.VISIBLE);

                    }
                } else {
                    // 网络异常，不刷新 2015、1、9 zhu
                    // if (tv_phname != null)
                    // tv_phname.setVisibility(View.GONE);
                    // if (tv_nearno != null)
                    // tv_nearno.setVisibility(View.VISIBLE);
                    // if (tv_arrownearc != null)
                    // tv_arrownearc.setVisibility(View.GONE);
                    // if (tv_namec != null)
                    // tv_namec.setVisibility(View.GONE);

                    // if(tv_phname!=null)
                    // tv_phname.setVisibility(View.VISIBLE);
                    // if(tv_nearno!=null)
                    // tv_nearno.setVisibility(View.GONE);
                    // if(tv_arrownearc!=null)
                    // tv_arrownearc.setVisibility(View.VISIBLE);
                    // if(tv_namec!=null)
                    // tv_namec.setVisibility(View.VISIBLE);

                }
            }
        };
        task.execute();
        if (bFirst) {
            // showPendingDialog(null);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        // //2014/12/1 add by Zhu
        case BlackBoardActivity.REQUEST_CODE_BACKTASK:// back from blackboard
            // if(resultCode==BlackBoardActivity.RESULT_OK)
            // do something
            RefreshPubFriNear();
            break;

        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // View root = SystemUtils.inflate(R.layout.activity_tasknew);
        // loadDataPublish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.ll_publishme:
            Intent intent = new Intent(getActivity(), PublishBulletinActivity.class);
            startActivity(intent);
            break;
        case R.id.ll_publish:
            Intent blackboard = new Intent(getActivity(), BlackBoardActivity.class);
            // //2014/12/1 add by Zhu
            startActivityForResult(blackboard, BlackBoardActivity.REQUEST_CODE_BACKTASK);
            // startActivity(blackboard);
            break;
        case R.id.ll_nearby:
            Intent nearby = new Intent(getActivity(), NearbyMarketActivity.class);
            nearby.putExtra(NearbyMarketActivity.EXTRA_DEFAULT_REGION, mRegion);
            startActivity(nearby);
            break;
        case R.id.ll_infofee:
            // Intent infofee = new Intent(getActivity(),
            // DealBillActivity.class);
            // startActivity(infofee);

            Intent infofee = new Intent(getActivity(), InfoFeeListActivity.class);
            startActivity(infofee);
            break;
        case R.id.ll_market:
            Intent market = new Intent(getActivity(), MarketManageActivity.class);
            startActivity(market);
            break;
        case R.id.ll_tixian:
            Intent tixian = new Intent(getActivity(), TransferWithdrawalActivity.class);
            startActivity(tixian);
            break;
        case R.id.ll_tixian_manager:
            Intent manager = new Intent(getActivity(), WithdrawalActivity.class);
            startActivity(manager);
            break;
        case R.id.ll_complaints:
            Intent complaint = new Intent(getActivity(), ComplaintHandlingActivity.class);
            startActivity(complaint);
            break;
        case R.id.ll_problem:
            // Intent problem = new Intent(getActivity(),
            // CustomerProblemActivity.class);
            Intent problem = new Intent(getActivity(), ComplaintDealDetailActivity.class);
            startActivity(problem);
            break;
        case R.id.ll_add_user:
            Intent add = new Intent(getActivity(), CustomerAddUserActivity.class);
            startActivity(add);
            break;
        case R.id.btn_follow_order:
            String orderNumber = mOrderEt.getText().toString();
            if (TextUtils.isEmpty(orderNumber)) {
                ToastUtils.showToast("请输入单号");
                return;
            }
            mOrderEt.setText("");
            mOrderEt.clearFocus();
            // ToastUtils.showToast(orderNumber);
            Intent searchfollow = new Intent(getActivity(), SearchFollowActivity.class);
            searchfollow.putExtra("oddnums", orderNumber);
            startActivity(searchfollow);
            break;

        default:
            break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

    }

}
