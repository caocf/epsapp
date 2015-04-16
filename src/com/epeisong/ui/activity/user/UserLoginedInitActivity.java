package com.epeisong.ui.activity.user;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.epeisong.EpsApplication;
import com.epeisong.MainActivity;
import com.epeisong.R;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.base.view.TitleParams;
import com.epeisong.data.dao.ChatMsgDao;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.FreightDao;
import com.epeisong.data.dao.PointDao;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.User;
import com.epeisong.service.CoreService;
import com.epeisong.service.notify.ComActDb;
import com.epeisong.service.notify.MenuBean;
import com.epeisong.service.notify.MenuEnum;
import com.epeisong.ui.activity.temp.LoginActivity;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SpUtilsCur;
import com.epeisong.utils.android.AsyncTask;
import com.epeisong.utils.java.Tool;

/**
 * 用户登录后，进入主界面前的各种数据初始化界面
 * 
 * @author poet
 * 
 */
public class UserLoginedInitActivity extends BaseActivity {
    private static final String SHAREDPREFERENCES_NAME = "first_pref";
    boolean isFirstIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TempActivityManager.getInstance().clear();
        super.onCreate(savedInstanceState);
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView iv = new ImageView(getApplicationContext());
        iv.setImageResource(R.drawable.eps_loading);
        iv.setScaleType(ScaleType.FIT_XY);
        setContentView(iv);

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    User user = UserDao.getInstance().getUser();
                    switch (user.getUser_type_code()) {
                    case Properties.LOGISTIC_TYPE_ENTIRE_VEHICLE:
                    case Properties.LOGISTIC_TYPE_TRANSHIP_GOODS:
                        if (SpUtilsCur.getInt(SpUtilsCur.KEYS_SERVICE.INT_IS_REFRESH_LOC, 0) == 0) {
                            SpUtilsCur.put(SpUtilsCur.KEYS_SERVICE.INT_IS_REFRESH_LOC, 1);
                        }
                        break;
                    }
                    ChatMsgDao.getInstance();
                    ContactsDao.getInstance();
                    FreightDao.getInstance();

                    PointDao.getInstance();

                    CoreService.startService();

                    SpUtils.put(SpUtils.KEYS_SYS.BOOL_CURR_USER_LOGINED, true);

                    EpsApplication.registAlarmTask();
                } catch (Exception e) {
                    EpsApplication.exit(UserLoginedInitActivity.this, LoginActivity.class);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                Intent intent = new Intent(UserLoginedInitActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        task.execute();

        // 判断当前用户是否第一次登录
        SharedPreferences preferences = getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
        isFirstIn = preferences.getBoolean(UserDao.getInstance().getUser().getPhone() + "isFirstIn", true);
        if (isFirstIn) {
            initDb();
        } else { //新增订车配货
        	 ComActDb db = new ComActDb(UserLoginedInitActivity.this);
        	 try {
				MenuBean bean = (MenuBean) db.getBeanByFilter(MenuBean.class, " Where menuCode='"
				         + MenuEnum.OrderPeihuo.getMenuCode() + "' And curLoginPhone='" + 
						 UserDao.getInstance().getUser().getPhone() + "' ");
				if(Tool.isEmpty(bean) || Tool.isEmpty(bean.getMenuCode())) { //数据库还没有
					  MenuEnum e = MenuEnum.OrderPeihuo;
					MenuBean tempbean = new MenuBean(e.getMenuName(), e.getMenuCode(),
							e.getIsShow(), e.getParentCode(),
	                        e.getActName(), UserDao.getInstance().getUser().getPhone());
					db.insertSql(tempbean );
				    db.close();
		            db = null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }

    }

    @Override
    protected TitleParams getTitleParams() {
        return null;
    }

    // 第一次进入初始化数据库
    private void initDb() {
        ComActDb db = new ComActDb(UserLoginedInitActivity.this);
        try {
            List<MenuBean> listBean = new ArrayList<MenuBean>();
            for (int i = 0; i < MenuEnum.getEnumNum(); i++) {
                MenuEnum e = MenuEnum.getMenuBean(i);
                MenuBean bean = new MenuBean(e.getMenuName(), e.getMenuCode(), e.getIsShow(), e.getParentCode(),
                        e.getActName(), UserDao.getInstance().getUser().getPhone());
                listBean.add(bean);
            }
            boolean insert = db.batchInsert(listBean, MenuBean.class);
            LogUtils.e(null, "ComActDb batchInsert success!!!" + insert);
//            db.close();
//            db = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        SharedPreferences preferences = UserLoginedInitActivity.this.getSharedPreferences(SHAREDPREFERENCES_NAME,
                Context.MODE_PRIVATE);
        Editor editor = preferences.edit();

        editor.putBoolean(UserDao.getInstance().getUser().getPhone() + "isFirstIn", false);

        editor.commit();
    }
}
