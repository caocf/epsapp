package com.epeisong.ui.fragment;

import lib.universal_image_loader.ImageLoaderUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.data.dao.InfoFeeDao;
import com.epeisong.data.dao.InfoFeeDao.InfoFeeObserver;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.dao.util.CRUD;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.ComplainTask;
import com.epeisong.model.GuaComplainTask;
import com.epeisong.model.InfoFee;
import com.epeisong.model.User;
import com.epeisong.ui.activity.GuaCompDetailActivity;
import com.epeisong.ui.activity.GuaCompResultActivity;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 担保申诉详情
 * @author Jack
 *
 */
public class GuaCompDetailFragment extends Fragment implements OnClickListener, InfoFeeObserver {

    // public static final String EXTRA_INFO_FEE_ID = "info_fee_id";
    // public static final String EXTRA_INFO_FEE = "info_fee";

    private ImageView iv_guarantee, iv_dguarantee;
    private ImageView mIconIv;
    private TextView mRegionTv;
    private TextView mTimeTv;
    private TextView mContentTv;
    private TextView mSourceTv, mSourceNameTv, mdSourceTv, mdSourceNameTv;
    private TextView et_infoname;
    private TextView et_payergua;
    private TextView et_payeegua, et_resultnote;
    private TextView et_infonum, et_payeeguanum, et_payerguanum;
    // private LinearLayout ll_resultnote;
    private RelativeLayout ll_info, ll_infopayee, ll_infopayer;
    private LinearLayout ll_done;
    private TextView mInfoFeeTv;
    // private Button mChangeInfoFeeBtn;
    private TextView mStatusTv, mdStatusTv;
    // private TextView mUpdateTimeTv;
    // private Button mLeftBtn, mRightBtn,
    private Button mBottomBtn;

    private User mUserSelf;
    private String mInfoFeeId;
    private GuaComplainTask guaComplainTask;
    private InfoFee mInfoFee;
    // private XBaseActivity mXBaseActivity;
    private int mComplaintState = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = SystemUtils.inflate(R.layout.fragment_guacomp_detail);
        mIconIv = (ImageView) root.findViewById(R.id.iv_icon);
        mRegionTv = (TextView) root.findViewById(R.id.tv_region);
        mRegionTv.setText("");
        mTimeTv = (TextView) root.findViewById(R.id.tv_time);
        mTimeTv.setText("");
        mContentTv = (TextView) root.findViewById(R.id.tv_content);
        mContentTv.setText("");
        mSourceTv = (TextView) root.findViewById(R.id.tv_source);
        mSourceTv.setText("");
        mSourceNameTv = (TextView) root.findViewById(R.id.tv_source_name);
        mSourceNameTv.setText("");
        iv_guarantee = (ImageView) root.findViewById(R.id.iv_guarantee);
        mStatusTv = (TextView) root.findViewById(R.id.tv_status);
        mStatusTv.setText("");
        mdSourceTv = (TextView) root.findViewById(R.id.tv_dsource);
        mdSourceTv.setText("");
        mdSourceNameTv = (TextView) root.findViewById(R.id.tv_dsource_name);
        mdSourceNameTv.setText("");
        iv_dguarantee = (ImageView) root.findViewById(R.id.iv_dguarantee);
        mdStatusTv = (TextView) root.findViewById(R.id.tv_dstatus);
        mdStatusTv.setText("");
        mInfoFeeTv = (TextView) root.findViewById(R.id.tv_info_fee);
        // mChangeInfoFeeBtn = (Button)
        // root.findViewById(R.id.btn_change_info_fee);
        mStatusTv = (TextView) root.findViewById(R.id.tv_status);
        mStatusTv.setText("");
        // mUpdateTimeTv = (TextView) root.findViewById(R.id.tv_update_time);
        // mUpdateTimeTv.setText("");
        mBottomBtn = (Button) root.findViewById(R.id.btn_bottom);

        ll_done = (LinearLayout) root.findViewById(R.id.ll_done);
        ll_info = (RelativeLayout) root.findViewById(R.id.ll_info);
        ll_infopayee = (RelativeLayout) root.findViewById(R.id.ll_infopayee);
        ll_infopayer = (RelativeLayout) root.findViewById(R.id.ll_infopayer);
        // ll_resultnote = (LinearLayout) root.findViewById(R.id.ll_resultnote);
        et_infoname = (TextView) root.findViewById(R.id.et_infoname);
        et_infonum = (TextView) root.findViewById(R.id.et_infonum);
        et_payeegua = (TextView) root.findViewById(R.id.et_payeegua);
        et_payeeguanum = (TextView) root.findViewById(R.id.et_payeeguanum);
        et_payergua = (TextView) root.findViewById(R.id.et_payergua);
        et_payerguanum = (TextView) root.findViewById(R.id.et_payerguanum);
        et_resultnote = (TextView) root.findViewById(R.id.et_resultnote);
        // mInfoFee.setViews(String.valueOf(mInfoFee.getPayeeId()), null,
        // mStatusTv);
        mBottomBtn.setOnClickListener(this);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mInfoFeeId = args.getString(GuaCompDetailActivity.EXTRA_INFO_FEE_ID);
            guaComplainTask = (GuaComplainTask) args.getSerializable(GuaCompDetailActivity.EXTRA_GUA_COMPLAIN_TASK);
            mComplaintState = args.getInt(ComplainFragment.ARGS_COMPLAINT_TYPE);
            mInfoFee = guaComplainTask.getInfoFee();
        }
        if (TextUtils.isEmpty(mInfoFeeId)) {
            ToastUtils.showToast("InfoFeeFragment 参数错误");
            getActivity().finish();
            return;
        }
        mUserSelf = UserDao.getInstance().getUser();

        // requestData();
        String payeeOwnerLogo = mInfoFee.getPayeeGuaranteeProductOtherLogo();
        if (!TextUtils.isEmpty(payeeOwnerLogo)) {
            ImageLoader.getInstance().displayImage(payeeOwnerLogo, iv_guarantee, ImageLoaderUtils.getListOptions());
        }

        String payerOwnerLogo = mInfoFee.getPayerGuaranteeProductOtherLogo();
        if (!TextUtils.isEmpty(payerOwnerLogo)) {
            ImageLoader.getInstance().displayImage(payerOwnerLogo, iv_dguarantee, ImageLoaderUtils.getListOptions());
        }

        if (mComplaintState != Properties.COMPLAIN_TASK_STATUS_NOT_PROCESSED) {
            mBottomBtn.setVisibility(View.GONE);
            ComplainTask complainTask = guaComplainTask.getComplainTask();
            int selfid = Integer.valueOf(mUserSelf.getId());
            if (mInfoFee.getInfoAmount() > 0.0) {
                et_infoname.setText(mInfoFee.getPayerName() + "信息费" + mInfoFee.getInfoAmount() / 100.0 + "元");
                int feeid = complainTask.getInfoFeeResultId();
                if (feeid == mInfoFee.getPayerId())
                    et_infonum.setText("退款");
                else if (feeid == mInfoFee.getPayeeId())
                    et_infonum.setText("赔付");
                else if (feeid == selfid)
                    et_infonum.setText("没收");
            } else {
                ll_info.setVisibility(View.GONE);
            }
            if (mInfoFee.getPayeeGuaranteeAmount() > 0.0) {
                et_payeegua.setText(mInfoFee.getPayeeName() + "保证金" + mInfoFee.getPayeeGuaranteeAmount() / 100.0 + "元");
                int payeeid = complainTask.getPayeeGuaranteeAmountResultId();
                if (payeeid == mInfoFee.getPayeeId())
                    et_payeeguanum.setText("退款");
                else if (payeeid == mInfoFee.getPayerId())
                    et_payeeguanum.setText("赔付");
                else if (payeeid == selfid)
                    et_payeeguanum.setText("没收");
            } else {
                ll_infopayee.setVisibility(View.GONE);
            }
            if (mInfoFee.getPayerGuaranteeAmount() > 0.0) {
                et_payergua.setText(mInfoFee.getPayerName() + "保证金" + mInfoFee.getPayerGuaranteeAmount() / 100.0 + "元");
                int payerid = complainTask.getPayerGuaranteeAmountResultId();
                if (payerid == mInfoFee.getPayerId())
                    et_payerguanum.setText("退款");
                else if (payerid == mInfoFee.getPayeeId())
                    et_payerguanum.setText("赔付");
                else if (payerid == selfid)
                    et_payerguanum.setText("没收");
            } else {
                ll_infopayer.setVisibility(View.GONE);
            }
            et_resultnote.setText(guaComplainTask.getComplainTask().getNote());
        } else {
            ll_done.setVisibility(View.GONE);
            // ll_info.setVisibility(View.GONE);
            // ll_infopayee.setVisibility(View.GONE);
            // ll_infopayer.setVisibility(View.GONE);
            // ll_resultnote.setVisibility(view.GONE);
        }

        InfoFeeDao.getInstance().addObserver(this);
    }

    @Override
    public void onDestroyView() {
        InfoFeeDao.getInstance().removeObserver(this);
        super.onDestroyView();
    }

    @Override
    public void onInfoFeeChange(InfoFee infoFee, CRUD crud) {
        if (mInfoFeeId.equals(infoFee.getId())) {
            if (crud == CRUD.REPLACE) {
                InfoFeeDao.getInstance().read(infoFee);
            } else if (crud == CRUD.READ) {
                mInfoFee = infoFee;
                refreshView();
            }
        }
    }

    private void refreshView() {
        if (mInfoFee == null) {
            return;
        }
        if (mInfoFee.getType() == Properties.INFO_FEE_TYPE_GOODS) {
            mIconIv.setImageResource(R.drawable.black_board_goods);
        } else if (mInfoFee.getType() == Properties.INFO_FEE_TYPE_VEHICLE) {
            mIconIv.setImageResource(R.drawable.black_board_truck);
        }
        mRegionTv.setText(mInfoFee.getFreightAddr());
        mTimeTv.setText(DateUtil.long2YMDHM(mInfoFee.getUpdateDate()));
        // mUpdateTimeTv.setText(DateUtil.long2YMDHMSS(mInfoFee.getUpdateDate()));

        if (Properties.INFO_FEE_TYPE_GOODS == mInfoFee.getType()) {
            mSourceTv.setText("货源方：");
            mdSourceTv.setText("订货方：");
            mSourceNameTv.setText(mInfoFee.getPayeeName());
            mStatusTv.setText(String.valueOf(mInfoFee.getPayeeFlowStatus()));
            mdSourceNameTv.setText(mInfoFee.getPayerName());
            mdStatusTv.setText(String.valueOf(mInfoFee.getPayerFlowStatus()));
        } else if (Properties.INFO_FEE_TYPE_VEHICLE == mInfoFee.getType()) {
            mSourceTv.setText("车源方：");
            mdSourceTv.setText("配货方：");
            mSourceNameTv.setText(mInfoFee.getPayerName());
            mStatusTv.setText(String.valueOf(mInfoFee.getPayerFlowStatus()));
            mdSourceNameTv.setText(mInfoFee.getPayeeName());
            mdStatusTv.setText(String.valueOf(mInfoFee.getPayeeFlowStatus()));
        }

        mContentTv.setText(mInfoFee.getFreightInfo());

        mInfoFeeTv.setText(mInfoFee.getInfoAmount() / 100 + "元");
        // BillViewHolder holder = new BillViewHolder(mChangeInfoFeeBtn,
        // mLeftBtn, mRightBtn, mBottomBtn, mSourceTv,
        // mSourceNameTv, mStatusTv);
        // mInfoFee.setViews(mUserSelf.getId(), holder, null);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_bottom:
            Intent i = new Intent(getActivity(), GuaCompResultActivity.class);
            i.putExtra(GuaCompDetailActivity.EXTRA_GUA_COMPLAIN_TASK, guaComplainTask);
            i.putExtra(GuaCompDetailActivity.EXTRA_INFO_FEE_ID, mInfoFeeId);
            startActivityForResult(i, 101);
            // startActivity(i);
            break;
        default:
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                getActivity().finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
