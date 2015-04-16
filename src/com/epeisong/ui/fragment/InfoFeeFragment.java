package com.epeisong.ui.fragment;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import lib.universal_image_loader.ImageLoaderUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.epeisong.R;
import com.epeisong.base.activity.XBaseActivity;
import com.epeisong.base.view.PwdInputView;
import com.epeisong.base.view.PwdInputView.SimpleTextWatcher;
import com.epeisong.base.view.statusview.StatusLayout;
import com.epeisong.data.dao.UserDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.layer02.InfoFeeManager;
import com.epeisong.data.model.PayResult;
import com.epeisong.data.net.parser.ComplainTaskParser;
import com.epeisong.data.net.parser.InfoFeeParser;
import com.epeisong.data.utils.infofee.InfoFeeStatusUtils;
import com.epeisong.data.utils.infofee.InfoFeeStatusUtils.InfoFeeStatusParams;
import com.epeisong.data.utils.infofee.InfoFeeStatusUtils.InfoFeeStatusResult;
import com.epeisong.data.utils.infofee.InfoFeeStatusUtils.Operation;
import com.epeisong.data.utils.PromptUtils;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Constants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.proto.ComplainTask.ComplainTaskReq;
import com.epeisong.logistics.proto.ComplainTask.ComplainTaskResp;
import com.epeisong.logistics.proto.InfoFee.InfoFeeReq.Builder;
import com.epeisong.logistics.proto.InfoFee.InfoFeeResp;
import com.epeisong.logistics.proto.Transaction.ProtoInfoFee;
import com.epeisong.model.ComplainTask;
import com.epeisong.model.InfoFee;
import com.epeisong.model.User;
import com.epeisong.model.Wallet;
import com.epeisong.net.request.NetComplainTask;
import com.epeisong.net.request.NetInfoFee;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.infofee.ApiInfoFee;
import com.epeisong.net.ws.infofee.RetInfoFeeResp;
import com.epeisong.ui.activity.InfoFeeDetailActivity;
import com.epeisong.utils.AliPayUtils;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.ShapeUtils;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;
import com.epeisong.utils.java.JavaUtils;
import com.epeisong.utils.java.Tool;
import com.nostra13.universalimageloader.core.ImageLoader;

public class InfoFeeFragment extends Fragment implements OnClickListener {

    public static final String EXTRA_INFO_FEE_ID = "info_fee_id";
    public static final String EXTRA_INFO_FEE = "info_fee";

    public static InfoFeeFragment instace;

    private ImageView mMineGuaranteeIv;
    private TextView mUpdateTimeTv;
    private TextView mStatusTv;
    private Button mBtn1, mBtn2, mBtn3;

    View mComtainView;
    Button mGetComtainBtn;

    StatusLayout mStatusLayout;

    private TextView mSourceTv, mSourceNameTv;
    private ImageView mRemoteGuaranteeIv;
    private TextView mInfoFeeTv;
    private Button mChangeInfoFeeBtn;
    private View mPercentageView;
    private TextView mPercentageTv;

    private ImageView mIconIv;
    private TextView mRegionTv;
    private TextView mTimeTv;
    private TextView mContentTv;
    private TextView mPublisherTv;

    private User mUserSelf;
    private String mInfoFeeId;
    private InfoFee mInfoFee;

    private XBaseActivity mXBaseActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mXBaseActivity = (XBaseActivity) activity;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = SystemUtils.inflate(R.layout.fragment_info_fee);
        mMineGuaranteeIv = (ImageView) root.findViewById(R.id.iv_mine_guarantee);
        mUpdateTimeTv = (TextView) root.findViewById(R.id.tv_update_time);
        mUpdateTimeTv.setText("");
        mStatusTv = (TextView) root.findViewById(R.id.tv_status);
        mStatusTv.setText("");
        mBtn1 = (Button) root.findViewById(R.id.btn_01);
        mBtn1.setVisibility(View.GONE);
        mBtn1.setOnClickListener(this);
        mBtn2 = (Button) root.findViewById(R.id.btn_02);
        mBtn2.setVisibility(View.GONE);
        mBtn2.setOnClickListener(this);
        mBtn3 = (Button) root.findViewById(R.id.btn_03);
        mBtn3.setVisibility(View.GONE);
        mBtn3.setOnClickListener(this);
        mBtn1.setBackgroundDrawable(ShapeUtils.getWhiteGrayBg(5, Color.BLUE));
        mBtn2.setBackgroundDrawable(ShapeUtils.getWhiteGrayBg(5, Color.BLUE));
        mBtn3.setBackgroundDrawable(ShapeUtils.getWhiteGrayBg(5, Color.BLUE));

        mComtainView = root.findViewById(R.id.rl_complain);
        mGetComtainBtn = (Button) root.findViewById(R.id.btn_get_complain_result);
        mGetComtainBtn.setOnClickListener(this);

        mStatusLayout = (StatusLayout) root.findViewById(R.id.statusLayout);

        mSourceTv = (TextView) root.findViewById(R.id.tv_source);
        mSourceTv.setText("");
        mSourceNameTv = (TextView) root.findViewById(R.id.tv_source_name);
        mSourceNameTv.setText("");
        mRemoteGuaranteeIv = (ImageView) root.findViewById(R.id.iv_remote_guarantee);
        mInfoFeeTv = (TextView) root.findViewById(R.id.tv_info_fee);
        mChangeInfoFeeBtn = (Button) root.findViewById(R.id.btn_change_info_fee);
        mChangeInfoFeeBtn.setBackgroundDrawable(ShapeUtils.getMainBtnBg(5));
        mChangeInfoFeeBtn.setVisibility(View.GONE);
        mChangeInfoFeeBtn.setOnClickListener(this);
        mPercentageView = root.findViewById(R.id.ll_percentage);
        mPercentageTv = (TextView) root.findViewById(R.id.tv_percentage);

        mIconIv = (ImageView) root.findViewById(R.id.iv_icon);
        mRegionTv = (TextView) root.findViewById(R.id.tv_region);
        mRegionTv.setText("");
        mContentTv = (TextView) root.findViewById(R.id.tv_content);
        mContentTv.setText("");
        mTimeTv = (TextView) root.findViewById(R.id.tv_time);
        mTimeTv.setText("");
        mPublisherTv = (TextView) root.findViewById(R.id.tv_publisher);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        instace = this;
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mInfoFeeId = args.getString(InfoFeeFragment.EXTRA_INFO_FEE_ID);
            mInfoFee = (InfoFee) args.getSerializable(EXTRA_INFO_FEE);
        }
        if (TextUtils.isEmpty(mInfoFeeId)) {
            ToastUtils.showToast("InfoFeeFragment 参数错误");
            getActivity().finish();
            return;
        }

        new InfoFeeManager().readInfoFee(mInfoFeeId);

        mUserSelf = UserDao.getInstance().getUser();

        requestData();
        getWallet();
    }

    @Override
    public void onDestroyView() {
        instace = null;
        super.onDestroyView();
    }

    private void requestData() {
        AsyncTask<Void, Void, InfoFee> task = new AsyncTask<Void, Void, InfoFee>() {
            @Override
            protected InfoFee doInBackground(Void... params) {
                return new InfoFeeManager().getInfoFeeFromNet(mInfoFeeId);
            }

            @Override
            protected void onPostExecute(InfoFee result) {
                if (result != null) {
                    mInfoFee = result;
                }
                refreshView();
            }
        };
        task.execute();
    }

    void getComplainResult() {
        mXBaseActivity.showPendingDialog(null);
        AsyncTask<Void, Void, ComplainTask> task = new AsyncTask<Void, Void, ComplainTask>() {
            @Override
            protected ComplainTask doInBackground(Void... params) {
                NetComplainTask net = new NetComplainTask() {

                    @Override
                    protected int getCommandCode() {
                        return CommandConstants.GET_COMPLAIN_TASK_REQ;
                    }

                    @Override
                    protected void setRequest(ComplainTaskReq.Builder req) {
                        req.setDealBillId(mInfoFee.getId());
                        req.setDealBillType(Properties.DEAL_BILL_TYPE_INFO_FEE);
                    }
                };
                try {
                    ComplainTaskResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        return ComplainTaskParser.parse(resp.getComplainTask());
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ComplainTask result) {
                mXBaseActivity.dismissPendingDialog();
                if (result != null) {
                    showComplainDialog(result);
                }
            }
        };
        task.execute();
    }

    void showComplainDialog(ComplainTask task) {
        View view = SystemUtils.inflate(R.layout.dialog_infofee_complain_result);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll_result);
        if (mInfoFee.getInfoAmount() > 0) {
            String infoFeeType = "";
            switch (task.getInfoFeeResultType()) {
            case Properties.COMPLAIN_TASK_PAYER_INFO_FEE_RESULT_TYPE_COMPENSATE:
                infoFeeType = "赔付";
                break;
            case Properties.COMPLAIN_TASK_PAYER_INFO_FEE_RESULT_TYPE_EXPROPRIATE:
                infoFeeType = "没收";
                break;
            case Properties.COMPLAIN_TASK_PAYER_INFO_FEE_RESULT_TYPE_REFUND:
                infoFeeType = "退还";
                break;
            }
            String infoFeeResult = task.getPayerName() + "信息费" + mInfoFee.getInfoAmount() / 100 + "元" + " "
                    + infoFeeType;
            ll.addView(createComplainResult(infoFeeResult));
        }
        if (mInfoFee.getPayerGuaranteeAmount() > 0) {
            String payerGuaType = "";
            switch (task.getPayerGuaranteeAmountResultType()) {
            case Properties.COMPLAIN_TASK_PAYER_GUARANTEE_MOUNT_RESULT_TYPE_COMPENSATE:
                payerGuaType = "赔付";
                break;
            case Properties.COMPLAIN_TASK_PAYER_GUARANTEE_MOUNT_RESULT_TYPE_EXPROPRIATE:
                payerGuaType = "没收";
                break;
            case Properties.COMPLAIN_TASK_PAYER_GUARANTEE_MOUNT_RESULT_TYPE_REFUND:
                payerGuaType = "退还";
                break;
            }
            String payerGuaResult = task.getPayerName() + "保证金" + mInfoFee.getPayerGuaranteeAmount() / 100 + "元" + " "
                    + payerGuaType;
            ll.addView(createComplainResult(payerGuaResult));
        }
        if (mInfoFee.getPayeeGuaranteeAmount() > 0) {
            String payeeGuaType = "";
            switch (task.getPayerGuaranteeAmountResultType()) {
            case Properties.COMPLAIN_TASK_PAYEE_GUARANTEE_MOUNT_RESULT_TYPE_COMPENSATE:
                payeeGuaType = "赔付";
                break;
            case Properties.COMPLAIN_TASK_PAYEE_GUARANTEE_MOUNT_RESULT_TYPE_EXPROPRIATE:
                payeeGuaType = "没收";
                break;
            case Properties.COMPLAIN_TASK_PAYEE_GUARANTEE_MOUNT_RESULT_TYPE_REFUND:
                payeeGuaType = "退还";
                break;
            }
            String payeeGuaResult = task.getPayeeName() + "保证金" + mInfoFee.getPayeeGuaranteeAmount() / 100 + "元" + " "
                    + payeeGuaType;
            ll.addView(createComplainResult(payeeGuaResult));
        }
        TextView tv_desc = (TextView) view.findViewById(R.id.tv_complain_desc);
        tv_desc.setText(task.getNote());
        AlertDialog.Builder b = new AlertDialog.Builder(mXBaseActivity);
        b.setView(view).setPositiveButton("确定", null);
        b.create().show();
    }

    TextView createComplainResult(String result) {
        TextView tv = new TextView(mXBaseActivity);
        tv.setText(result);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        return tv;
    }

    private void refreshView() {
        if (getActivity() == null || mInfoFee == null) {
            return;
        }
        setGuaranteeProductLogo();
        if (mInfoFee.getType() == Properties.INFO_FEE_TYPE_GOODS) {
            mIconIv.setImageResource(R.drawable.black_board_goods);
        } else if (mInfoFee.getType() == Properties.INFO_FEE_TYPE_VEHICLE) {
            mIconIv.setImageResource(R.drawable.black_board_truck);
        }
        mRegionTv.setText(mInfoFee.getFreightAddr());
        mTimeTv.setText(DateUtil.long2YMDHMSS(mInfoFee.getCreateDate()));
        mUpdateTimeTv.setText(DateUtil.long2YMDHMSS(mInfoFee.getUpdateDate()));
        mContentTv.setText(mInfoFee.getFreightInfo());
        mInfoFeeTv.setText(mInfoFee.getInfoAmount() / 100 + "元");

        InfoFeeStatusParams params = new InfoFeeStatusParams(mUserSelf.getId(), mInfoFee);
        params.needSmallStatus().needOperation().needStatusModel();
        InfoFeeStatusResult result = InfoFeeStatusUtils.getResult(params);
        mStatusTv.setText(result.getStatus());
        if (result.smallStatusId > 0) {
            Drawable left = getResources().getDrawable(result.smallStatusId);
            int size = DimensionUtls.getPixelFromDpInt(30);
            left.setBounds(0, 0, size, size);
            mStatusTv.setCompoundDrawables(left, null, null, null);
            mStatusTv.setCompoundDrawablePadding(size / 3);
        }

        mBtn1.setVisibility(View.GONE);
        mBtn2.setVisibility(View.GONE);
        mBtn3.setVisibility(View.GONE);
        if (result.operations != null && result.operations.size() > 0) {
            int size = result.operations.size();
            int i = 0;
            for (Operation op : result.operations) {
                if (op.getName().equals("催促结单")) {
                    if (System.currentTimeMillis() - mInfoFee.getUpdateDate() < 1000 * 60 * 60 * 24) {
                        continue;
                    }
                }
                if (i == 0) {
                    if (size == 1) {
                        mBtn3.setVisibility(View.VISIBLE);
                        mBtn3.setText(op.getName());
                        mBtn3.setTag(op.getAction());
                    } else {
                        mBtn1.setVisibility(View.VISIBLE);
                        mBtn1.setText(op.getName());
                        mBtn1.setTag(op.getAction());
                    }
                } else if (i == 1) {
                    mBtn2.setVisibility(View.VISIBLE);
                    mBtn2.setText(op.getName());
                    mBtn2.setTag(op.getAction());
                } else {
                    mBtn3.setVisibility(View.VISIBLE);
                    mBtn3.setText(op.getName());
                    mBtn3.setTag(op.getAction());
                }
                i++;
            }
        }

        if (result.hasComplainResult) {
            mBtn3.setVisibility(View.VISIBLE);
            mBtn3.setText("查看处理结果");
            mBtn3.setTag(InfoFeeStatusUtils.OPERATION_COMPLAIN_RESULT);
        }

        mStatusLayout.showStatus(result.statusModels);
        mSourceTv.setText(result.source + "：");
        mSourceNameTv.setText(result.sourceName);
        if (result.isCanChangeFee) {
            mChangeInfoFeeBtn.setVisibility(View.VISIBLE);
        } else {
            mChangeInfoFeeBtn.setVisibility(View.GONE);
        }

        if (mInfoFee.getType() == Properties.INFO_FEE_TYPE_GOODS) {
            if (result.isPayer) {
                mRemoteGuaranteeIv.setVisibility(View.GONE);
                mSourceTv.setText("操作：");
                mSourceNameTv.setText("订货");
                mPublisherTv.setVisibility(View.VISIBLE);
                mPublisherTv.setText("货源方：" + mInfoFee.getPayeeName());
            }
        } else if (mInfoFee.getType() == Properties.INFO_FEE_TYPE_VEHICLE) {
            if (!result.isPayer) {
                mRemoteGuaranteeIv.setVisibility(View.GONE);
                mSourceTv.setText("操     作：");
                mSourceNameTv.setText("配货");
                mPublisherTv.setVisibility(View.VISIBLE);
                mPublisherTv.setText("车源方：" + mInfoFee.getPayerName());
            }
        }
        if (mUserSelf.getId().equals(mInfoFee.getPayeeId()) && mInfoFee.getInfoAmount() > 0
                && mInfoFee.getPercentageValue() > 0) {
            mPercentageView.setVisibility(View.VISIBLE);
            if (mInfoFee.getPercentageType() == 1) {
                mPercentageTv.setText(mInfoFee.getPercentageValue() / 100f + "元");
            } else if (mInfoFee.getPercentageType() == 2) {
                mPercentageTv.setText(mInfoFee.getPercentageValue() + "%");
            }
        } else {
            mPercentageView.setVisibility(View.GONE);
        }
    }

    void setGuaranteeProductLogo() {
        if (mUserSelf.getId().equals(String.valueOf(mInfoFee.getPayerId()))) {
            String payerOwnerLogo = mInfoFee.getPayerGuaranteeProductOwnerLogo();
            String payeeOtherLogo = mInfoFee.getPayeeGuaranteeProductOtherLogo();
            if (!TextUtils.isEmpty(payerOwnerLogo)) {
                ImageLoader.getInstance().displayImage(payerOwnerLogo, mMineGuaranteeIv,
                        ImageLoaderUtils.getListOptions());
            }
            if (!TextUtils.isEmpty(payeeOtherLogo)) {
                ImageLoader.getInstance().displayImage(payeeOtherLogo, mRemoteGuaranteeIv,
                        ImageLoaderUtils.getListOptions());
            }
        } else {
            String payeeOwnerLogo = mInfoFee.getPayeeGuaranteeProductOwnerLogo();
            String payerOtherLogo = mInfoFee.getPayerGuaranteeProductOtherLogo();
            if (!TextUtils.isEmpty(payeeOwnerLogo)) {
                ImageLoader.getInstance().displayImage(payeeOwnerLogo, mMineGuaranteeIv,
                        ImageLoaderUtils.getListOptions());
            }
            if (!TextUtils.isEmpty(payerOtherLogo)) {
                ImageLoader.getInstance().displayImage(payerOtherLogo, mRemoteGuaranteeIv,
                        ImageLoaderUtils.getListOptions());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_get_complain_result:
            getComplainResult();
            break;
        case R.id.btn_change_info_fee:
            final EditText et = new EditText(getActivity());
            et.setText(String.valueOf(mInfoFee.getInfoAmount() / 100));
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
            et.setSelectAllOnFocus(true);
            int p = DimensionUtls.getPixelFromDpInt(10);
            LinearLayout ll = new LinearLayout(getActivity());
            ll.setPadding(p, p, p, p);
            ll.addView(et, new LinearLayout.LayoutParams(-1, -2));
            ((XBaseActivity) getActivity()).showYesNoDialog("修改信息费:元", ll, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        String text = et.getText().toString();
                        int fee = Integer.parseInt(text) * 100;
                        if (fee != mInfoFee.getInfoAmount()) {
                            changeInfoFee(fee);
                        }
                    }
                    SystemUtils.hideInputMethod(et);
                }
            });
            HandlerUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SystemUtils.showInputMethod(et);
                }
            }, 200);
            break;
        case R.id.btn_01:
        case R.id.btn_02:
        case R.id.btn_03:
            Object tag = v.getTag();
            if (tag != null && tag instanceof Integer) {
                int nextStatus = (Integer) tag;
                if (nextStatus == InfoFeeStatusUtils.OPERATION_CALL) {
                    // 电话催单
                    User remote = ((InfoFeeDetailActivity) getActivity()).getRemote();
                    if (remote != null) {
                        String call = null;
                        if (!TextUtils.isEmpty(remote.getContacts_phone())) {
                            call = remote.getContacts_phone();
                        } else if (!TextUtils.isEmpty(remote.getContacts_telephone())) {
                            call = remote.getContacts_telephone();
                        }
                        if (call != null) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + call));
                            startActivity(intent);
                        } else {
                            ToastUtils.showToast("对方无联系电话");
                        }
                    }
                    return;
                } else if (nextStatus == InfoFeeStatusUtils.OPERATION_COMPLAIN_RESULT) {
                    getComplainResult();
                    return;
                }
                String title = null;
                boolean isAliPay = false;// 是否调用支付宝

                switch (nextStatus) {
                case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_PAY_TO_PLATFORM: // 车源发布方付款到平台
                    title = "付款";

                    if (Tool.isEmpty(wallet) || wallet.getAmount() < mInfoFee.getInfoAmount()) {
                        isAliPay = true;
                    }

                    break;
                case Properties.INFO_FEE_PAYER_ORDER_STATUS_PAY_TO_PLATFORM: // 车源接单者付款到平台
                    title = "付款";

                    if (Tool.isEmpty(wallet) || wallet.getAmount() < mInfoFee.getInfoAmount()) {
                        isAliPay = true;
                    }
                    break;
                case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_CONFIRM_PULL_GOODS:
                case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_COMPLETE_FOR_DUNNING_THE_PARTY:
                    // case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_COMPLETE:
                    // // 车源发布方确认拉货
                    if (mInfoFee.getInfoAmount() > 0) {
                        title = "确认拉货";
                    }
                    break;
                case Properties.INFO_FEE_PAYER_ORDER_STATUS_CONFIRM_PULL_GOODS:
                case Properties.INFO_FEE_PAYER_ORDER_STATUS_COMPLETE_FOR_DUNNING_THE_PARTY:
                    // case Properties.INFO_FEE_PAYER_ORDER_STATUS_COMPLETE: //
                    // 车源接单者确认拉货
                    if (mInfoFee.getInfoAmount() > 0) {
                        title = "确认拉货";
                    }
                    break;
                case Properties.INFO_FEE_PAYER_ORDER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
                case Properties.INFO_FEE_PAYER_ORDER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION: // 同意赔付(申请取消订单,对方拒绝后)
                case Properties.INFO_FEE_PAYER_ORDER_STATUS_AGREE_COMPENSATION_OF_NO_GOODS:
                case Properties.INFO_FEE_PAYER_ORDER_STATUS_AGREE_COMPENSATION_OF_NOT_COME_PULL_GOODS: // 同意赔付(没有去拉货，已被投诉)
                    title = "同意赔付";
                    break;
                case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION: // 同意赔付(申请取消订单,对方拒绝后)
                case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
                case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_NO_GOODS:
                case Properties.INFO_FEE_PAYER_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_NOT_COME_PULL_GOODS:
                    title = "同意赔付";
                    break;
                case Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION:
                case Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
                case Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_COMPENSATION_OF_NO_GOODS:
                case Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_COMPENSATION_OF_NOT_COME_PULL_GOODS:
                    title = "同意赔付";
                    break;
                case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION:
                case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_APPLY_ORDERCANCELLATION_OF_THE_PARTY:
                case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_NO_GOODS:
                case Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_COMPENSATION_OF_NOT_COME_PULL_GOODS:
                    title = "同意赔付";
                    break;
                // case
                // Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_AGREE_ORDERCANCELLATION_WITHY_THE_PARTY:
                // // 同意对方取消订单
                // title = "同意对方取消订单";
                // break;
                // case
                // Properties.INFO_FEE_PAYEE_ORDER_STATUS_AGREE_ORDERCANCELLATION_WITHY_THE_PARTY:
                // // 同意对方取消订单
                // title = "同意对方取消订单";
                // break;
                // case
                // Properties.INFO_FEE_PAYEE_ORDER_STATUS_APPLY_ORDERCANCELLATION:
                // // 申请取消订单中
                // title = "申请取消订单";
                // break;
                // case
                // Properties.INFO_FEE_PAYEE_PUBLISHER_STATUS_APPLY_ORDERCANCELLATION:
                // // 申请取消订单中
                // title = "申请取消订单";
                // break;
                }
                if (isAliPay && "付款".equals(title)) { // 付款调用第三方
                    aliPay(nextStatus);
                    // otherPayDialog();
                } else if (title != null) {
                    // aliPay(nextStatus) ;
                    payDialog(title, nextStatus);
                } else {
                    changeStatus(nextStatus);
                }
            }
            break;
        default:
            break;
        }
    }

    private Wallet wallet;
    private int operationStatus = -1;

    private void getWallet() {

        final AsyncTask<Void, Void, com.epeisong.net.ws.utils.WalletResp> task = new AsyncTask<Void, Void, com.epeisong.net.ws.utils.WalletResp>() {
            @Override
            protected com.epeisong.net.ws.utils.WalletResp doInBackground(Void... params) {
                User user = UserDao.getInstance().getUser();
                String pwd = SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null);
                try {
                    return new ApiExecutor().getWallet(user.getPhone(), pwd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(com.epeisong.net.ws.utils.WalletResp resp) {
                if (isAdded()) {
                    mXBaseActivity.dismissPendingDialog();
                }

                if (resp != null) {
                    wallet = resp.getWallet();
                    operationStatus = resp.getResult();
                    switch (operationStatus) {
                    case Properties.GET_WALLET_STATUS_NOMRAL:
                        if (wallet.getStatus() != 2) { // 正常
                            wallet = null;
                        }
                        break;
                    case Properties.GET_WALLET_STATUS_NO_WALLET:
                        wallet = null;
                        break;
                    default:
                        wallet = null;
                        break;
                    }

                } else {
                    wallet = null;

                }
            }

        };
        if (isAdded()) {
            mXBaseActivity.showPendingDialog(null, new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    task.cancel(true);
                }
            });
        }

        task.execute();

    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     * 
     */
    public String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        LogUtils.et("key" + key);
        return key;
    }

    // 阿里支付
    private void aliPay(int nextStatus) {

        StringBuilder bodySb = new StringBuilder();
        bodySb.append("logisticsId=").append(UserDao.getInstance().getUser().getId()).append("#");
        // bodySb.append("type=").append(String.valueOf(mInfoFee.getType())).append(";");
        // bodySb.append("amount=").append(String.valueOf(mInfoFee.getInfoAmount())).append(";")
        // ;
        bodySb.append("status=").append(String.valueOf(nextStatus)).append("#");

        double realAmt = mInfoFee.getInfoAmount() / (double) 100;
        // 订单
        String orderInfo = AliPayUtils.getOrderInfo(String.valueOf(mInfoFee.getId()), "订单支付宝付款", bodySb.toString(),
                String.valueOf(realAmt));

        String sign = "";
        try {
            // 对订单做RSA 签名
            sign = AliPayUtils.sign(orderInfo);
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showToast("支付宝暂时未开通");
            return;
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + AliPayUtils.getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(getActivity());
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                aliHandler.sendMessage(msg);

            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private static final String[] payType = { "支付宝支付", "微信支付", "农行卡支付" };
    private static final int SDK_PAY_FLAG = 1; // 支付

    private static final int SDK_CHECK_FLAG = 2;// 检测
    private Handler aliHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SDK_PAY_FLAG: {
                PayResult payResult = new PayResult((String) msg.obj);

                // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
                String resultInfo = payResult.getResult();

                String resultStatus = payResult.getResultStatus();

                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {

                    Toast.makeText(getActivity(), "支付成功", Toast.LENGTH_SHORT).show();
                } else {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    if (TextUtils.equals(resultStatus, "8000")) {
                        Toast.makeText(getActivity(), "支付结果确认中", Toast.LENGTH_SHORT).show();

                    } else {
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        Toast.makeText(getActivity(), "支付失败", Toast.LENGTH_SHORT).show();

                    }
                }
                break;
            }
            case SDK_CHECK_FLAG: {
                Toast.makeText(getActivity(), "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
                break;
            }
            default:
                break;
            }
        };
    };

    private void payDialog(String title, final int nextStatus) {
        final PwdInputView inputView = new PwdInputView(getActivity());
        inputView.setPwdHint("请输入钱包支付密码");
        AlertDialog dialog = mXBaseActivity.showYesNoDialog(title, inputView, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SystemUtils.hideInputMethod(inputView);
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    String pwd = inputView.getPwd();
                    if (!TextUtils.isEmpty(pwd)) {
                        changeStatus(nextStatus, pwd);
                    }
                }
            }
        });
        final Button btn_ok = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn_ok.setEnabled(false);
        inputView.setPwdChangeListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                btn_ok.setEnabled(!TextUtils.isEmpty(s.toString()));
            }
        });
    }

    private void changeInfoFee(final int fee) {
        mXBaseActivity.showPendingDialog(null);
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                NetInfoFee net = new NetInfoFee() {
                    @Override
                    protected int getCommandCode() {
                        return CommandConstants.UPDATE_INFO_FEE_AMOUNT_REQ;
                    }

                    @Override
                    protected void setRequest(Builder req) {
                        req.setInfoAmount(fee);
                        req.setInfoFeeId(mInfoFeeId);
                    }
                };
                try {
                    InfoFeeResp.Builder resp = net.request();
                    if (resp != null) {
                        ProtoInfoFee protoInfoFee = resp.getInfoFee();
                        if (protoInfoFee != null) {
                            InfoFee infoFee = InfoFeeParser.parser(protoInfoFee);
                            if (infoFee != null && infoFee.getUpdateDate() > mInfoFee.getUpdateDate()) {
                                infoFee.setLocalStatus(mInfoFee.getLocalStatus());
                                if (new InfoFeeManager().changeDbAndList(infoFee, null)) {
                                    mInfoFee = infoFee;
                                }
                            }
                        }
                        return Constants.SUCC.equals(resp.getResult());
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                mXBaseActivity.dismissPendingDialog();
                if (result) {
                    mInfoFeeTv.setText(fee + "元");
                    refreshView();
                }
            }
        };
        task.execute();
    }

    private void changeStatus(final int nextStatus, final String... args) {
        mXBaseActivity.showPendingDialog(null);
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                String paymentPwd = "null";
                if (args != null && args.length == 1) {
                    paymentPwd = args[0];
                }
                try {
                    ApiInfoFee api = new ApiInfoFee();
                    RetInfoFeeResp resp = api
                            .UpdateInfoFeeFlowStatus(api.getUname(), api.getUpwd(), "" + mInfoFee.getId(),
                                    mInfoFee.getInfoAmount(), nextStatus, mInfoFee.getType(), paymentPwd);
                    InfoFee infoFee = resp.getInfoFee();
                    if (infoFee != null && infoFee.getUpdateDate() >= mInfoFee.getUpdateDate()) {
                        LogUtils.d("InfoFeeFragment.changeStatus", JavaUtils.getString(infoFee));
                        if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE
                                || infoFee.getStatus() == Properties.INFO_FEE_STATUS_CANCEL) {
                            infoFee.setLocalStatus(InfoFee.UNREAD);// 设为未读
                        } else {
                            infoFee.setLocalStatus(InfoFee.READ);
                        }
                        if (new InfoFeeManager().changeDbAndList(infoFee, null)) {
                            mInfoFee = infoFee;
                        }
                    }
                    if (resp.getResult() == RetInfoFeeResp.SUCC) {
                        return true;
                    }
                    boolean bPayer = Integer.parseInt(mUserSelf.getId()) == mInfoFee.getPayerId();
                    String msg = PromptUtils.getPrompt(resp.getResult(), bPayer);
                    ToastUtils.showToastInThread(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e("InfoFeeFragment.changeStatus", e);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                mXBaseActivity.dismissPendingDialog();
                if (result) {
                    refreshView();
                    ToastUtils.showToast("操作成功");
                }
            }
        };
        task.execute();
    }

    public void onInfoFeeChange(InfoFee infoFee) {
        mInfoFee = infoFee;
        refreshView();
    }
}
