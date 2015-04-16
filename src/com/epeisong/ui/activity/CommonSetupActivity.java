package com.epeisong.ui.activity;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.epeisong.EpsApplication;
import com.epeisong.EpsNetConfig;
import com.epeisong.R;
import com.epeisong.base.dialog.YesNoDialog;
import com.epeisong.base.dialog.YesNoDialog.OnYesNoDialogClickListener;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.PointDao;
import com.epeisong.logistics.common.Constants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.net.NetService;
import com.epeisong.logistics.net.NetServiceFactory;
import com.epeisong.logistics.proto.Eps.AppNewVersionResp;
import com.epeisong.model.Point.PointCode;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SysUtils;
import com.epeisong.utils.android.AsyncTask;

/**
 * 通用设置
 * 
 * @author poet
 * 
 */
public class CommonSetupActivity extends MenuListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PointDao.getInstance().hide(PointCode.Code_Mine_Common_Setup);
    }

    @Override
    protected TitleParams getTitleParams() {
        return new TitleParams(getDefaultHomeAction(), "通用设置", null).setShowLogo(false);
    }

    @Override
    protected void onAddHeaderOrFooter(ListView listView) {
        LinearLayout footWrapper = new LinearLayout(this);
        int p = (int) DimensionUtls.getPixelFromDp(10);
        footWrapper.setPadding(p, p, p, p);
        Button exitBtn = new Button(this);
        int btnHeight = (int) DimensionUtls.getPixelFromDp(45);
        exitBtn.setLayoutParams(new ViewGroup.LayoutParams(-1, btnHeight));
        exitBtn.setText("退出登录");
        exitBtn.setTextColor(Color.WHITE);
        exitBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        exitBtn.setBackgroundResource(R.drawable.selector_common_btn_bg_red);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showYesNoDialog("提示", "退出后将不再收到易配送消息！", "否", "是", new OnYesNoDialogClickListener() {
                    @Override
                    public void onYesNoDialogClick(int yesOrNo) {
                        if (yesOrNo == YesNoDialog.BTN_YES) {
                            EpsApplication.exit(CommonSetupActivity.this);
                        }
                    }
                });
            }
        });
        footWrapper.setLayoutParams(new AbsListView.LayoutParams(-1, -2));
        footWrapper.addView(exitBtn);
        listView.addFooterView(footWrapper);
    }

    @Override
    protected void onSetData(List<Menu> data) {
        data.add(new Menu(Menu.TYPE_NORMAL, R.drawable.mine_secure_center_normal, true, 0, "安全中心", new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), SecurityCenterActivity.class);
                startActivity(intent);
            }
        }));
        data.add(new Menu(Menu.TYPE_NORMAL, R.drawable.mine_privacy_normal, true, 0, "我的隐私", new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MinePrivacyActivity.class);
                startActivity(intent);
            }
        }));
        data.add(new Menu(Menu.TYPE_NORMAL, R.drawable.mine_notify_setup_normal, true, 0, "消息提醒", new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), NotifySetupActivity.class);
                startActivity(intent);
            }
        }));

        boolean hasNewVersion = SpUtils.getBoolean(SpUtils.KEYS_SYS.BOOL_APP_HAS_NEW_VERSION, false);
        if (hasNewVersion) {
            data.add(new Menu(Menu.TYPE_NORMAL, R.drawable.new_version_pic, true, 0, "检查新版本", new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), TheNewVersionActivity.class);
                    startActivity(intent);
                }
            }).setShowPoint(true));
        } else {
            data.add(new Menu(Menu.TYPE_NORMAL, R.drawable.new_version_pic, true, 0, "检查新版本", new Runnable() {
                @Override
                public void run() {
                    checkNewVersion();
                }
            }).setShowPoint(false));
        }
    }

    private void checkNewVersion() {
        showPendingDialog(null);
        AsyncTask<Void, Void, Boolean> task1 = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... arg0) {
                int versionCode = 1;
                try {
                    versionCode = EpsApplication.getInstance().getPackageManager()
                            .getPackageInfo(EpsApplication.getInstance().getPackageName(), 0).versionCode;
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    NetService frontendService = NetServiceFactory.getInstance();
                    AppNewVersionResp.Builder resp = frontendService.checkNewVersion(EpsNetConfig.getHost(),
                            EpsNetConfig.PORT, versionCode, Properties.APP_CLIENT_TYPE_ANDROID_PHONE, 9000);
                    if (resp != null && Constants.SUCC.equals(resp.getResult())) {
                        boolean need = resp.getIsNeedToUpdate();
                        if (need) {
                            String url = resp.getDownLoadUrl();
                            SpUtils.put(SpUtils.KEYS_SYS.BOOL_APP_HAS_NEW_VERSION, true);
                            SpUtils.put(SpUtils.KEYS_SYS.STRING_APP_NEW_VERSION_URL, url);
                            return true;
                        } else {
                            SpUtils.remove(SpUtils.KEYS_SYS.BOOL_APP_HAS_NEW_VERSION);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    SpUtils.remove(SpUtils.KEYS_SYS.BOOL_APP_HAS_NEW_VERSION);
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissPendingDialog();
                if (result) {
                    Intent intent = new Intent(getApplicationContext(), TheNewVersionActivity.class);
                    startActivity(intent);
                } else {
                    AlertDialog.Builder b = new Builder(CommonSetupActivity.this);
                    b.setMessage("当前已是最新版本\n" + SysUtils.getPackageInfo().versionName).setPositiveButton("确认", null);
                    b.create().show();
                }
            }
        };
        task1.execute();
    }
}
