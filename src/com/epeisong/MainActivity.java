package com.epeisong;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.dialog.ChooseLineActivity;
import com.epeisong.base.fragment.EmptyFragment;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.PointDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.Point;
import com.epeisong.model.Point.PointCode;
import com.epeisong.model.RegionResult;
import com.epeisong.model.User;
import com.epeisong.service.CoreService;
import com.epeisong.service.notify.MenuBean;
import com.epeisong.service.notify.MenuEnum;
import com.epeisong.service.notify.NotifyService;
import com.epeisong.ui.activity.ContactsActivity;
import com.epeisong.ui.fragment.CommonMsgFragment;
import com.epeisong.ui.fragment.HomeFragment;
import com.epeisong.ui.fragment.HomeFragment4Courier;
import com.epeisong.ui.fragment.MineFragment;
import com.epeisong.ui.fragment.OrderFragment;
import com.epeisong.ui.fragment.TaskGuaranteeFragment;
import com.epeisong.ui.fragment.TaskManageFragment;
import com.epeisong.ui.fragment.TaskWalletCashFragment;
import com.epeisong.utils.HandlerUtils;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SysUtils;
import com.epeisong.utils.SystemUtils;
import com.epeisong.utils.ToastUtils;
import com.epeisong.utils.android.BroadcastUtils;
import com.test.log.CrashLogListActivity;
import com.test.log.LogcatService;
import com.zxing.CaptureActivity;

/**
 * 新的主界面
 * 
 * @author poet
 * 
 */
public class MainActivity extends BaseActivity implements OnClickListener, OnPageChangeListener,
        com.epeisong.data.dao.PointDao.PointObserver {

    public static final String EXTRA_DEFAULT_POS = "default_pos";

    public static final int HOME_POS = 0;
    public static final int MESSAGE_POS = 1;
    public static final int TASK_POS = 2;
    public static final int MINE_POS = 3;

    private long mStartTime;
    private int mCount;
    public static MainActivity sMainActivity;
    public static int sCurPagePos;

    private TextView mTitleTv;

    private boolean mUseAnimation = false;
    private RotateAnimation mAnimation;
    private View mHomeLl, mTaskLl, mMessageLl, mMineLl;
    private TextView mHomeTv;
    private TextView mTaskTv;
    private TextView mMessageTv;
    private TextView mMineTv;
    private ImageView mHomeIv;
    private ImageView mTaskIv;
    private ImageView mMessageIv;
    private ImageView mMineIv;
    private View mContactsPoint;
    private View mHomePoint;
    private View mTaskPoint;
    private View mMessagePoint;
    private View mMinePoint;

    private ViewPager mViewPager;
    private List<Fragment> mFragments;

    private long mListBackTime;

    static int mDefaultPos = 0;
    static boolean needChange = false;

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            ToastUtils.showToast("onReceive:" + action);
            if (BroadcastUtils.ACTION_FIND_NEW_VERSION.equals(action)) {
                boolean hasNewVersion = SpUtils.getBoolean(SpUtils.KEYS_SYS.BOOL_APP_HAS_NEW_VERSION, false);
                if (hasNewVersion == true) {
                    boolean mustUpdate = SpUtils.getBoolean(SpUtils.KEYS_SYS.BOOL_APP_IS_MUST_UPDATE, false);
                    String url = SpUtils.getString(SpUtils.KEYS_SYS.STRING_APP_NEW_VERSION_URL, null);
                    if (url != null) {
                        showUpdateDialog(url, mustUpdate);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDefaultPos = getIntent().getIntExtra(EXTRA_DEFAULT_POS, 0);
        sMainActivity = this;
        User user = UserDao.getInstance().getUser();
        setContentView(R.layout.activity_main);
        mFragments = new ArrayList<Fragment>();
        switch (user.getUser_type_code()) {
        case Properties.LOGISTIC_TYPE_COURIER:
            mFragments.add(new HomeFragment4Courier());
            break;
        default:
            mFragments.add(new HomeFragment());
            break;
        }
        mFragments.add(new CommonMsgFragment());
        switch (user.getUser_type_code()) {
        case Properties.LOGISTIC_TYPE_LOGISTICS_PARK: // 物流园
        case Properties.LOGISTIC_TYPE_MARKET: // 配货市场
        case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE: // 整车运输
        case Properties.LOGISTIC_TYPE_LESS_THAN_TRUCKLOAD_AND_LINE:// 零担专线
        case Properties.LOGISTIC_TYPE_STOWAGE_INFORMATION_DEPARTMENT: // 配载信息部
        case Properties.LOGISTIC_TYPE_THIRD_PART_LOGISTICS: // 第三方物流
        case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS: // 驳货
        case Properties.LOGISTIC_TYPE_PLATFORM_CUSTOMER_SERVICE: // 平台客服
        case Properties.LOGISTIC_TYPE_EQUIPMENT_LEASING:
            // mFragments.add(new TaskCustomerServiceFragment());
            mFragments.add(new OrderFragment());
            break;
        case Properties.LOGISTIC_TYPE_GUARANTEE:// 担保方FI
            mFragments.add(new TaskGuaranteeFragment());
            break;
        case Properties.LOGISTIC_TYPE_PLATFORM_ADMINISTRATOR:
            mFragments.add(new TaskManageFragment());
            break;
        case Properties.LOGISTIC_TYPE_WALLET_CASH:
            mFragments.add(new TaskWalletCashFragment());
            // mFragments.add(new TaskNewFragment());
            break;

        default:
            mFragments.add(new EmptyFragment() {
                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                    TextView tv = new TextView(MainActivity.this);
                    tv.setText("该角色暂无界面");
                    tv.setGravity(Gravity.CENTER);
                    return tv;
                }
            });
            break;
        }

        mFragments.add(new MineFragment());
        if (user.getUser_type_code() == Properties.LOGISTIC_TYPE_GUARANTEE && EpsApplication.DEBUGGING == true) {
            mFragments.add(new TaskManageFragment());
            mFragments.add(new OrderFragment());// TaskNewFragment());
            mFragments.add(new TaskWalletCashFragment());
        } else if (user.getUser_type_code() == Properties.LOGISTIC_TYPE_PLATFORM_ADMINISTRATOR
                && EpsApplication.DEBUGGING == true) {
            mFragments.add(new TaskGuaranteeFragment());
            mFragments.add(new OrderFragment());// TaskNewFragment());
            mFragments.add(new TaskWalletCashFragment());
        }

        initView();

        if (!SystemUtils.isServiceRunning(CoreService.class.getName())) {
            CoreService.startService();
        }

        BroadcastUtils.register(BroadcastUtils.ACTION_FIND_NEW_VERSION, mBroadcastReceiver);
        HandlerUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean hasNewVersion = SpUtils.getBoolean(SpUtils.KEYS_SYS.BOOL_APP_HAS_NEW_VERSION, false);
                if (hasNewVersion == true) {
                    boolean mustUpdate = SpUtils.getBoolean(SpUtils.KEYS_SYS.BOOL_APP_IS_MUST_UPDATE, false);
                    String url = SpUtils.getString(SpUtils.KEYS_SYS.STRING_APP_NEW_VERSION_URL, null);
                    if (url != null) {
                        showUpdateDialog(url, mustUpdate);
                    }
                } else {
                    boolean hasHint = SpUtils.getBoolean(SpUtils.KEYS_SYS.BOOL_HINT_INSTALL_SHORTCUT, false);
                    if (!hasHint && !SysUtils.hasShortcut()) {
                        showYesNoDialog("提示", "是否创建快捷方式", "否", "是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    SysUtils.installShortcut();
                                }
                            }
                        });
                        SpUtils.put(SpUtils.KEYS_SYS.BOOL_HINT_INSTALL_SHORTCUT, true);
                    }
                }

            }
        }, 1000 * 2);

        if (EpsApplication.DEBUGGING) {
            HandlerUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showToast("DEGUG模式");
                }
            }, 1000 * 2);
        }
        HandlerUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    if (versionName != null && versionName.contains("developing")) {
                        ToastUtils.showToast("开发模式，发布前需要修改versionCode！");
                    }
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }, 1000 * 3);

        PointDao.getInstance().addObserver(PointCode.Code_Home, this);
        // PointDao.getInstance().addObserver(PointCode.Code_Task, this);
        PointDao.getInstance().addObserver(PointCode.Code_Message, this);
        PointDao.getInstance().addObserver(PointCode.Code_Mine, this);
        // PointDao.getInstance().addObserver(PointCode.Code_Contacts, this);
        LogUtils.e(null, "oncreate mainactivity");
        change(mDefaultPos);
    }

    private void initView() {
        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mTitleTv.setOnClickListener(this);
        findViewById(R.id.rl_left).setOnClickListener(this);
        findViewById(R.id.rl_right).setOnClickListener(this);

        mHomeLl = findViewById(R.id.ll_home);
        mMessageLl = findViewById(R.id.ll_message);
        mTaskLl = findViewById(R.id.ll_task);
        mMineLl = findViewById(R.id.ll_mine);
        mHomeTv = (TextView) mHomeLl.findViewById(R.id.tv);
        mHomeIv = (ImageView) mHomeLl.findViewById(R.id.iv);
        mHomePoint = mHomeLl.findViewById(R.id.iv_point);
        mHomeIv.setImageResource(R.drawable.selector_main_bottom_home);
        mHomeTv.setText("首页");
        mMessageTv = (TextView) mMessageLl.findViewById(R.id.tv);
        mMessageIv = (ImageView) mMessageLl.findViewById(R.id.iv);
        mMessagePoint = mMessageLl.findViewById(R.id.iv_point);
        mMessageIv.setImageResource(R.drawable.selector_main_bottom_message);
        mMessageTv.setText("消息");
        mTaskTv = (TextView) mTaskLl.findViewById(R.id.tv);
        mTaskIv = (ImageView) mTaskLl.findViewById(R.id.iv);
        mTaskPoint = mTaskLl.findViewById(R.id.iv_point);
        mTaskIv.setImageResource(R.drawable.selector_main_bottom_order);
        mTaskTv.setText("订单");
        mMineTv = (TextView) mMineLl.findViewById(R.id.tv);
        mMineIv = (ImageView) mMineLl.findViewById(R.id.iv);
        mMinePoint = mMineLl.findViewById(R.id.iv_point);
        mMineIv.setImageResource(R.drawable.selector_main_bottom_mine);
        mMineTv.setText("我的");

        mContactsPoint = findViewById(R.id.iv_contacts_point);
        // if (PointDao.getInstance().isShow(PointCode.Code_Contacts)) {
        // mContactsPoint.setVisibility(View.VISIBLE);
        // }
        if (PointDao.getInstance().isShow(PointCode.Code_Home)) {
            mHomePoint.setVisibility(View.VISIBLE);
        }
        // if (PointDao.getInstance().isShow(PointCode.Code_Task)) {
        // mTaskPoint.setVisibility(View.VISIBLE);
        // }
        if (PointDao.getInstance().isShow(PointCode.Code_Message)) {
            mMessagePoint.setVisibility(View.VISIBLE);
        }
        if (PointDao.getInstance().isShow(PointCode.Code_Mine)) {
            mMinePoint.setVisibility(View.VISIBLE);
        }

        mHomeLl.setOnClickListener(this);
        mTaskLl.setOnClickListener(this);
        mMessageLl.setOnClickListener(this);
        mMineLl.setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.vp);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }
        });
        mViewPager.setOnPageChangeListener(this);

    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mListBackTime < 2000) {
            moveTaskToBack(true);
        } else {
            ToastUtils.showToast("再按一次退出界面");
        }
        mListBackTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        BroadcastUtils.unRegister(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_title:
            if (EpsApplication.DEBUGGING) {
                Intent i = new Intent(this, LogcatService.class);
                startService(i);
            }
            break;
        case R.id.rl_left:
            CaptureActivity.launchDefaultResult(this);
            break;
        case R.id.rl_right:
            showPendingDialog(null);
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
            HandlerUtils.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismissPendingDialog();
                }
            }, 100);

            break;
        case R.id.ll_home:
            if (mStartTime == 0 || System.currentTimeMillis() - mStartTime > 1000) {
                mStartTime = System.currentTimeMillis();
                mCount = 1;
            } else {
                mCount++;
                if (EpsApplication.DEBUGGING && mCount >= 3) {
                    Intent log = new Intent(this, CrashLogListActivity.class);
                    startActivity(log);
                }
            }
            if (!mHomeTv.isSelected()) {
                change(0);
            }
            break;
        case R.id.ll_message:
            if (!mMessageTv.isSelected()) {
                change(1);
            }
            break;
        case R.id.ll_task:
            if (!mTaskTv.isSelected()) {
                change(2);
            }
            break;
        case R.id.ll_mine:
            if (!mMineTv.isSelected()) {
                change(3);
            }
            break;
        default:
            break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageSelected(int position) {
        change(position);
    }

    @Override
    public void onPointChange(Point p) {
        boolean show = p.isShow();
        PointCode pointCode = PointCode.convertFromValue(p.getCode());
        switch (pointCode) {
        case Code_Home:
            showOrHide(mHomePoint, show);
            break;
        // case Code_Contacts:
        // showOrHide(mContactsPoint, show);
        // break;
        // case Code_Task:
        // showOrHide(mTaskPoint, show);
        // break;
        case Code_Message:
            showOrHide(mMessagePoint, show);
            break;
        case Code_Mine:
            showOrHide(mMinePoint, show);
            break;
        }
    }

    private void change(int pos) {
        switch (pos) {
        case 0:
            selected(mHomeIv, mHomeTv);
            mTitleTv.setText("易配送");
            break;
        case 1:
            selected(mMessageIv, mMessageTv);
            mTitleTv.setText("消息");
            break;
        case 2:
            selected(mTaskIv, mTaskTv);
            mTitleTv.setText("订单");
            break;
        case 3:
            selected(mMineIv, mMineTv);
            mTitleTv.setText("我的");
            break;
        }
        if (mUseAnimation) {
            selectedAnimation(pos);
        }
        mViewPager.setCurrentItem(pos);
        sCurPagePos = pos;
        switch (sCurPagePos) {
        case HOME_POS:
            PointDao.getInstance().hide(PointCode.Code_Home);
            break;
        case MESSAGE_POS:
            PointDao.getInstance().hide(PointCode.Code_Message);
            break;
        case TASK_POS:
            NotifyService.showNoUiByOrder(CommandConstants.INFO_FEE_SERVER_GET_INFO_FEE_PUSH_REQ, MenuEnum.OrderMenu);
            // PointDao.getInstance().hide(PointCode.Code_Task);
            break;
        case MINE_POS:
            PointDao.getInstance().hide(PointCode.Code_Mine);
            break;
        }
    }

    private void selectedAnimation(int pos) {
        if (mAnimation == null) {
            mAnimation = new RotateAnimation(0f, 720f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                    0.5f);
            mAnimation.setDuration(400);
        }
        mHomeLl.clearAnimation();
        mTaskLl.clearAnimation();
        mMessageLl.clearAnimation();
        mMineLl.clearAnimation();
        switch (pos) {
        case 0:
            mHomeLl.startAnimation(mAnimation);
            break;
        case 1:
            mTaskLl.startAnimation(mAnimation);
            break;
        case 2:
            mMessageLl.startAnimation(mAnimation);
            break;
        case 3:
            mMineLl.startAnimation(mAnimation);
            break;
        }
    }

    private void selected(ImageView iv, TextView tv) {
        mHomeIv.setSelected(false);
        mHomeTv.setSelected(false);
        mTaskIv.setSelected(false);
        mTaskTv.setSelected(false);
        mMessageIv.setSelected(false);
        mMessageTv.setSelected(false);
        mMineIv.setSelected(false);
        mMineTv.setSelected(false);
        iv.setSelected(true);
        tv.setSelected(true);
    }

    private void showOrHide(View v, boolean show) {
        if (show) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
        }
    }

    @Override
    protected TitleParams getTitleParams() {
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 200) {
                RegionResult start = (RegionResult) data.getSerializableExtra(ChooseLineActivity.EXTRA_START_REGION);
                RegionResult end = (RegionResult) data.getSerializableExtra(ChooseLineActivity.EXTRA_END_REGION);
                if (start != null && end != null) {
                    ToastUtils.showToast(start.getCityName() + "-" + end.getCityName());
                } else {
                    ToastUtils.showToast("线路不限");
                }
            }
        }
    }

    private void showUpdateDialog(final String url, boolean mustUpdate) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setCancelable(false);
        alert.setTitle("软件升级");
        alert.setMessage("发现有新版本，建议立即更新使用");
        alert.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                EpsApplication.exit(MainActivity.this);
            }
        });
        if (!mustUpdate) {
            alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            alert.setMessage("新版本有重大更新，必须更新才能使用。");
        }
        alert.create().show();

    }

    public static void launchAndClear(Activity a) {
        launchAndClear(a, 0);
    }

    public static void launchAndClear(Activity a, int pos) {
        if (a != null) {
            Intent intent = new Intent(a, MainActivity.class);
            // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
            // Intent.FLAG_ACTIVITY_CLEAR_TOP);
            a.startActivity(intent);
        } else {
            Intent intent = new Intent(EpsApplication.getInstance(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(EXTRA_DEFAULT_POS, pos);
            EpsApplication.getInstance().startActivity(intent);
        }
    }

    @Override
    public void refresh(Object... param) {
        int type = (Integer) param[0];
        switch (type) {
        case CommandConstants.ADD_CONTACT_SERVER_PUSH_REQ: // 有人关注
            int show = (Integer) param[1];
            if (NotifyService.isShow == show) {
                mContactsPoint.setVisibility(View.VISIBLE);
            } else {
                mContactsPoint.setVisibility(View.GONE);
            }
            break;
        case CommandConstants.INFO_FEE_SERVER_GET_INFO_FEE_PUSH_REQ: // 订单变化
            int ordershow = (Integer) param[1];
            boolean iscurOrder = (Boolean) param[2];// 当前是否是订单页面
            if (!iscurOrder) {
                if (NotifyService.isShow == ordershow) {
                    mTaskPoint.setVisibility(View.VISIBLE);
                    OrderFragment org = (OrderFragment) getFragment(2, "OrderFragment");
                    org.refreshPoint(NotifyService.isShow);
                } else {
                    mTaskPoint.setVisibility(View.GONE);
                }
            } else {// 当前在订单页面上
                if (NotifyService.isShow == ordershow) {

                    OrderFragment org = (OrderFragment) getFragment(2, "OrderFragment");
                    org.refreshPoint(NotifyService.isShow);
                }
            }
            break;
        }
    }

    // 获取特定fragment
    private Fragment getFragment(int defNum, String fragname) {
        int temp = defNum;
        for (int i = 0; i < mFragments.size(); i++) {
            if (mFragments.get(i).getClass().getName().indexOf(fragname) > 0) {
                temp = i;
                break;
            }
        }
        return mFragments.get(temp);
    }

    // 初始化时判断是否需要更新UI
    public void initDbByUi() {
        String filter = "Where actName='" + MenuEnum.ContactsMenu.getActName() + "'";
        List<MenuBean> listBean = NotifyService.ListMenuBean(filter);
        for (MenuBean bean : listBean) {
            if (bean.getIsShow() == NotifyService.isShow) {
                if (bean.getMenuCode().equals(MenuEnum.ContactsMenu.getMenuCode())) {// 联系人
                    mContactsPoint.setVisibility(View.VISIBLE);
                } else if (bean.getMenuCode().equals(MenuEnum.OrderMenu.getMenuCode())) {
                    mTaskPoint.setVisibility(View.VISIBLE);
                } else if (bean.getMenuCode().equals(MenuEnum.OrderPeihuo.getMenuCode())) {
                    OrderFragment org = (OrderFragment) getFragment(2, "OrderFragment");
                    org.refreshPoint(NotifyService.isShow);
                }
            }

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        initDbByUi();
    }

    public static void setTabPos(int index, boolean isNeedChange) {
        mDefaultPos = index;
        if (index != sCurPagePos) {
            needChange = isNeedChange;
        }

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // mDefaultPos = getIntent().getIntExtra(EXTRA_DEFAULT_POS, 0);
        if (needChange) {
            change(mDefaultPos);
            needChange = false;
        }

    }

}
