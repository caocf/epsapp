package com.epeisong.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.epeisong.R;
import com.epeisong.base.activity.TabPages2Activity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.base.view.CustomTitle.Action;
import com.epeisong.base.view.CustomTitle.ActionImpl;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.User;
import com.epeisong.net.ws.ApiExecutor;
import com.epeisong.net.ws.utils.TaskResp;
import com.epeisong.net.ws.utils.Resp;
import com.epeisong.ui.fragment.TransferWithPayListFragment;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 转账支付
 * @author Jack
 *
 */
public class PaymentActivity extends TabPages2Activity {
	public static final int PAYMENT_TYPE_GUARANTEE = 1;
	public static final int PAYMENT_TYPE_FEEINFO = 2;
	public static final String PAYMENT_TYPE_STRING = "payment_type";
	
	private List<Fragment> mFragments;
	private TextView mTitleRightTv;

	private int mPayment_Guarantee_Status;
	private TransferWithPayListFragment oneFragment, twoFragment, threeFragment;

	private int mPayment_Gua_Fee;
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		RefreshFragmentList();
	}

	protected void RefreshFragmentList() {

		oneFragment.fragmentRefreshList(mViewPager.getCurrentItem(), mPayment_Guarantee_Status);
		twoFragment.fragmentRefreshList(mViewPager.getCurrentItem(), mPayment_Guarantee_Status);
		threeFragment.fragmentRefreshList(mViewPager.getCurrentItem(), mPayment_Guarantee_Status);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mPayment_Guarantee_Status = getPaymentStatus();
		mPayment_Gua_Fee = getIntent().getIntExtra(PAYMENT_TYPE_STRING, PAYMENT_TYPE_GUARANTEE);
		super.onCreate(savedInstanceState);
		mFragments = new ArrayList<Fragment>();

		oneFragment = new TransferWithPayListFragment();
		Bundle args = new Bundle();
		args.putInt(PAYMENT_TYPE_STRING, mPayment_Gua_Fee);
		args.putInt(TransferWithPayListFragment.PAYMENT_ORDER_TYPE, Properties.AUTO_TASK_STATUS_NOT_PROCESSED);
		oneFragment.setArguments(args);
		mFragments.add(oneFragment);
		twoFragment = new TransferWithPayListFragment();
		Bundle args2 = new Bundle();
		args2.putInt(PAYMENT_TYPE_STRING, mPayment_Gua_Fee);
		args2.putInt(TransferWithPayListFragment.PAYMENT_ORDER_TYPE, Properties.AUTO_TASK_STATUS_PROCESSED);
		twoFragment.setArguments(args2);
		mFragments.add(twoFragment);
		threeFragment = new TransferWithPayListFragment();
		Bundle args3 = new Bundle();
		args3.putInt(PAYMENT_TYPE_STRING, mPayment_Gua_Fee);
		args3.putInt(TransferWithPayListFragment.PAYMENT_ORDER_TYPE, Properties.AUTO_TASK_STATUS_WARNING);
		threeFragment.setArguments(args3);
		mFragments.add(threeFragment);

		mTabRoot.setBackgroundColor(Color.argb(0xff, 0x00, 0x9c, 0xff));
		int top = DimensionUtls.getPixelFromDpInt(10);
		int bottom = DimensionUtls.getPixelFromDpInt(15);
		mTabRoot.setPadding(bottom, top, bottom, bottom);

		mViewPager.setOffscreenPageLimit(3);
		HandlerUtils.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mFragments.get(0).isAdded()) {
					mFragments.get(0).setUserVisibleHint(true);
				} else {
					HandlerUtils.postDelayed(this, 100);
				}
			}
		}, 200);
	}

	void InputPayPassword(final int index) {

		final AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);   
		LayoutInflater factory = LayoutInflater.from(this);  
		View view = factory.inflate(R.layout.dialog_paypassword, null);  
		// builder.setIcon(R.drawable.icon);  
		//builder.setTitle("自定义输入框");  
		final EditText et_password = (EditText) view.findViewById(R.id.et_password);

		builder.setView(view);  
		//开启或关闭自动服务，请输入支付密码
		((TextView) view.findViewById(R.id.tv_passwordhint)).setText("开启或关闭自动服务，请输入支付密码");

		dialog = builder.show();
		dialog.setCanceledOnTouchOutside(true);
		view.findViewById(R.id.bt_cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//ToastUtils.showToast("cancel");
				dialog.dismiss();

			}
		});
		view.findViewById(R.id.bt_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String passwords = et_password.getText().toString();
				if (TextUtils.isEmpty(passwords)) {
					ToastUtils.showToast("请输入支付密码");
					return;
				} else {

					dialog.dismiss();
					if(mPayment_Guarantee_Status==Properties.AUTO_TASK_START)
						changeReceive(Properties.AUTO_TASK_STOPT, passwords);
					else
						changeReceive(Properties.AUTO_TASK_START, passwords);
				}
			}
		});

	}

	@Override
	protected TitleParams getTitleParams() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new ActionImpl() {

			@Override
			public void doAction(View v) {
				InputPayPassword(0);
			}

			@Override
			public View getView() {
				mTitleRightTv = new TextView(getApplicationContext());
				mTitleRightTv.setBackgroundResource(R.drawable.selector_common_bg_blue_red);
				mTitleRightTv.setTextColor(Color.WHITE);
				//mTitleRightTv.setTextSize(15);
				mTitleRightTv.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
				mTitleRightTv.setText("开启中");
				mTitleRightTv.setSelected(true);
				return mTitleRightTv;
			}

		});
		return new TitleParams(getDefaultHomeAction(), "转账支付", actions).setShowLogo(false);
	}

	void SetPaymentStatus(int status) {
		mPayment_Guarantee_Status = status;
	}

	int getPaymentStatus() {
		AsyncTask<Void, Void, TaskResp> task = new AsyncTask<Void, Void, TaskResp>() {
			@Override
			protected TaskResp doInBackground(Void... params) {
				ApiExecutor api = new ApiExecutor();
				try {
					User user=UserDao.getInstance().getUser();
					if(mPayment_Gua_Fee==PAYMENT_TYPE_GUARANTEE)
						return api.checkIsAuto(user.getAccount_name(), 
							SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), mPayment_Gua_Fee);
					else {
						return null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}
			@Override
			protected void onPostExecute(TaskResp result) {
				dismissPendingDialog();
				if (result==null) {
					ToastUtils.showToast("操作失败");
				}else {
					if (result.getResult() == Resp.SUCC) {
						if(mPayment_Gua_Fee==PAYMENT_TYPE_GUARANTEE)
							mPayment_Guarantee_Status = result.getIsAuto();
						else {
							mPayment_Guarantee_Status = result.getIsAuto();
						}
						if (mPayment_Guarantee_Status==Properties.AUTO_TASK_START) {
							mTitleRightTv.setText("自动执行");
						} else {
							mTitleRightTv.setText("手动执行");
						}
						mTitleRightTv.setSelected(mPayment_Guarantee_Status==Properties.AUTO_TASK_START);
					} else {
						ToastUtils.showToast(result.getDesc());
					}
				}
			}
		};
		task.execute();
		return mPayment_Guarantee_Status;
	}

	private void changeReceive(final int newStatus, final String passwords) {
		AsyncTask<Void, Void, TaskResp> task = new AsyncTask<Void, Void, TaskResp>() {
			@Override
			protected TaskResp doInBackground(Void... params) {
				ApiExecutor api = new ApiExecutor();
				try {
					User user=UserDao.getInstance().getUser();

					if (newStatus==Properties.AUTO_TASK_START) {
						return api.startGuaranteeTask(user.getAccount_name(), 
								SpUtils.getString(SpUtils.KEYS_SYS.STRING_CURR_USER_PWD_ENCODED, null), passwords, mPayment_Gua_Fee);
					}else {
						return null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}
			@Override
			protected void onPostExecute(TaskResp result) {
				dismissPendingDialog();
				if (result==null) {
					ToastUtils.showToast("操作失败");
				}else {

					if (result.getResult() == Resp.SUCC) {
						//ToastUtils.showToast("操作成功");

						mTitleRightTv.setSelected(newStatus==Properties.AUTO_TASK_START);
						if (newStatus==Properties.AUTO_TASK_START) {
							mTitleRightTv.setText("自动执行");
						} else {
							mTitleRightTv.setText("手动执行");
						}

						mPayment_Guarantee_Status=newStatus;
						RefreshFragmentList();
					} else {
						ToastUtils.showToast(result.getDesc());
					}
				}
			}
		};
		task.execute();
	}

	@Override
	protected void onSetTabTitle(List<String> titles) {
		titles.add("未处理");
		titles.add("已处理");
		titles.add("警报");
	}

	@Override
	protected TabStyle getTabStyle() {
		return new TabStyle() {
			@Override
			public int getTabBg() {
				return R.drawable.shape_content_trans_frame_white;
			}

			public int[] getTabItemBgs() {
				return new int[] { R.drawable.shape_tab2_bg_white_left, R.drawable.shape_tab2_bg_white_middle,
						R.drawable.shape_tab2_bg_white_right };
			}

			public int getTextColorSelectorId() {
				return R.color.selector_tab_text_3;
			}

			@Override
			public int getDividerColor() {
				return Color.WHITE;
			}
		};
	}

	@Override
	protected PagerAdapter onGetAdapter() {
		return new FragmentStatePagerAdapter(getSupportFragmentManager()) {
			@Override
			public int getCount() {
				return 3;
			}

			@Override
			public Fragment getItem(int position) {
				return mFragments.get(position);
			}
		};
	}

}
