package com.epeisong.ui.fragment;

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
import android.widget.TextView;

import com.epeisong.EpsApplication;
import com.epeisong.R;
import com.epeisong.data.dao.PointDao;
import com.epeisong.data.dao.PointDao.PointObserver;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.Point;
import com.epeisong.model.Point.PointCode;
import com.epeisong.model.Region;
import com.epeisong.model.User;
import com.epeisong.service.notify.MenuEnum;
import com.epeisong.service.notify.NotifyService;
import com.epeisong.ui.activity.BlackBoardActivity;
import com.epeisong.ui.activity.ComplaintHandlingActivity;
import com.epeisong.ui.activity.CustomerAddUserActivity;
import com.epeisong.ui.activity.CustomerProblemActivity;
import com.epeisong.ui.activity.FreightOfContactsActivity;
import com.epeisong.ui.activity.InfoFeeListActivity;
import com.epeisong.ui.activity.MarketManageActivity;
import com.epeisong.ui.activity.MemberManagerActivity;
import com.epeisong.ui.activity.NearbyMarketActivity;
import com.epeisong.ui.activity.PublishBulletinActivity;
import com.epeisong.ui.activity.SearchFollowActivity;
import com.epeisong.ui.activity.TransferWithdrawalActivity;
import com.epeisong.ui.activity.WithdrawalActivity;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.java.Tool;

public class OrderFragment extends Fragment implements OnItemClickListener, OnClickListener, PointObserver {

    private EditText mOrderEt;
    public View root;

    private ImageView iv_redmanage, iv_redfriend,iv_redpoint;

    public TextView tv_namef, tv_phname;
    private PullToRefreshScrollView pullscr;

    private View mMarketContainer;
    private View mMemberManagerContainer;
    private LinearLayout mTixian;
    private LinearLayout mTixianManager;
    private LinearLayout mInfoFee;
    private LinearLayout mComplaints;
    private LinearLayout mProblem;
    private LinearLayout mAddUser;

    private Region mRegion;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = SystemUtils.inflate(R.layout.activity_tasknew1);

        root.findViewById(R.id.ll_nearby).setVisibility(View.GONE);
        root.findViewById(R.id.ll_pub_pub).setVisibility(View.GONE);

        root.findViewById(R.id.ll_publish).setOnClickListener(this);

        iv_redmanage = (ImageView) root.findViewById(R.id.iv_redmanage);
        iv_redfriend = (ImageView) root.findViewById(R.id.iv_redfriend);
        iv_redpoint = (ImageView) root.findViewById(R.id.iv_redpoint);

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
        mMemberManagerContainer = root.findViewById(R.id.ll_member_manager);
        mMemberManagerContainer.setOnClickListener(this);
        mComplaints = (LinearLayout) root.findViewById(R.id.ll_complaints);
        mComplaints.setOnClickListener(this);
        mProblem = (LinearLayout) root.findViewById(R.id.ll_problem);
        mProblem.setOnClickListener(this);
        mAddUser = (LinearLayout) root.findViewById(R.id.ll_add_user);
        mAddUser.setOnClickListener(this);

        User user = UserDao.getInstance().getUser();
        switch (user.getUser_type_code()) {
        case Properties.LOGISTIC_TYPE_MARKET:
            mMarketContainer.setVisibility(View.VISIBLE);
            break;
        case Properties.LOGISTIC_TYPE_LOGISTICS_PARK:
            mMemberManagerContainer.setVisibility(View.VISIBLE);
            break;
        // case Properties.LOGISTIC_TYPE_WALLET_CASH:
        // mInfoFee.setVisibility(View.GONE);
        // mTixian.setVisibility(View.VISIBLE);
        // mTixianManager.setVisibility(View.VISIBLE);
        // break;
        case Properties.LOGISTIC_TYPE_PLATFORM_CUSTOMER_SERVICE:
            mComplaints.setVisibility(View.VISIBLE);
            mProblem.setVisibility(View.VISIBLE);
            mAddUser.setVisibility(View.VISIBLE);
            break;
        case Properties.LOGISTIC_TYPE_GUARANTEE:
        case Properties.LOGISTIC_TYPE_PLATFORM_ADMINISTRATOR:
            if (EpsApplication.DEBUGGING) {
                mMarketContainer.setVisibility(View.VISIBLE);
                mComplaints.setVisibility(View.VISIBLE);
                mProblem.setVisibility(View.VISIBLE);
                mAddUser.setVisibility(View.VISIBLE);
            }
            break;
        default:
            break;
        }
        tv_phname = (TextView) root.findViewById(R.id.tv_phname);

        tv_namef = (TextView) root.findViewById(R.id.tv_namef);

        pullscr = (PullToRefreshScrollView) root.findViewById(R.id.scr_task);
        pullscr.setMode(lib.pulltorefresh.PullToRefreshBase.Mode.DISABLED);
        PointDao.getInstance().addObserver(PointCode.Code_Task_InfoFee, this);
        return root;
    }
    
    public void refreshPoint(int  show) {
    	if(!Tool.isEmpty(iv_redpoint)) {
    	if(NotifyService.isShow == show) {
    		
    		iv_redpoint.setVisibility(View.VISIBLE);
    	} else {
    		iv_redpoint.setVisibility(View.GONE);
    	}
     }
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
        	refreshPoint(NotifyService.noShow);
        	NotifyService.updateTabByCode( MenuEnum.OrderPeihuo.getMenuCode(),
        			NotifyService.noShow);
            Intent infofee = new Intent(getActivity(), InfoFeeListActivity.class);
            startActivity(infofee);
            break;
        case R.id.ll_market:
            Intent market = new Intent(getActivity(), MarketManageActivity.class);
            startActivity(market);
            break;
        case R.id.ll_member_manager:
            Intent memberManager = new Intent(getActivity(), MemberManagerActivity.class);
            startActivity(memberManager);
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
            Intent problem = new Intent(getActivity(), CustomerProblemActivity.class);
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
