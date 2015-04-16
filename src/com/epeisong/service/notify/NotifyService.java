package com.epeisong.service.notify;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.epeisong.EpsApplication;
import com.epeisong.MainActivity;
import com.epeisong.base.activity.BaseActivity;
import com.epeisong.data.dao.UserDao;
import com.epeisong.logistics.common.CommandConstants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.model.ChatMsg;
import com.epeisong.model.Fans;
import com.epeisong.model.InfoFee;
import com.epeisong.model.SystemNotice;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.java.Tool;

/**
 * 管理接收消息并处理
 * @author chenchong
 */
public class NotifyService extends Service implements Runnable {
    public static boolean isrun = false;
    private static ArrayList<Task> allTask = new ArrayList<Task>();
    public static ComActDb db;
    private static final String MenuTabName = MenuBean.class.getSimpleName();// 通用菜单表名
    public static final int isShow = 1;// 要展示
    public static final int noShow = 0;// 不展示

    public static void startService() {

        Context context = EpsApplication.getInstance();
        Intent intent = new Intent(context, NotifyService.class);
        context.startService(intent);
    }

    public static void stopService() {
        isrun = false;
        Context context = EpsApplication.getInstance();
        Intent intent = new Intent(context, NotifyService.class);
        context.stopService(intent);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    // 添加窗口到集合中
    public static void addActivity(BaseActivity ia) {
        AppManager.getAppManager().addActivity(ia);
    }

    public static void removeActivity(BaseActivity ia) {
        AppManager.getAppManager().finishActivity(ia);
    }

    // 添加任务
    public static void newTask(Task ts) {
        allTask.add(ts);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isrun = false;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);
        isrun = true;
        db = new ComActDb(EpsApplication.getInstance());
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (isrun) {
            if (allTask.size() > 0) {
                doTask(allTask.get(0));
            } else {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {

                }
            }
        }
    }

    private void doTask(Task ts) {
        Message message = hand.obtainMessage();
        message.what = ts.getTaskID();
        switch (ts.getTaskID()) {
        case CommandConstants.SYSTEM_NOTICE_SERVER_PUSH_REQ:// 系统通知
            SystemNotice noti = (SystemNotice) ts.getTaskParam().get("notice");
            message.obj = noti;
            break;
        case CommandConstants.ADD_CONTACT_SERVER_PUSH_REQ:// 有人关注
            Fans bean = (Fans) ts.getTaskParam().get("bean");
            message.obj = bean;
            break;
        case CommandConstants.CHAT_SEND_MULTI_TYPE_SERVER_PUSH_REQ:// 收到聊天消息
            ChatMsg beanc = (ChatMsg) ts.getTaskParam().get("bean");
            message.obj = beanc;
            break;
        // 订单创建推送 、 状态变化
        case CommandConstants.INFO_FEE_SERVER_GET_INFO_FEE_PUSH_REQ:
            InfoFee infoFee = (InfoFee) ts.getTaskParam().get("bean");
            message.obj = infoFee;
            break;
        }

        allTask.remove(ts);
        hand.sendMessage(message);
    }

    private final Handler hand = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case CommandConstants.SYSTEM_NOTICE_SERVER_PUSH_REQ: // 系统通知

                break;
            case CommandConstants.ADD_CONTACT_SERVER_PUSH_REQ:// 有人关注
                mngFans(msg);
                break;
            case CommandConstants.INFO_FEE_SERVER_GET_INFO_FEE_PUSH_REQ: // 订单创建推送
                                                                         // 、
                                                                         // 状态变化
                mngOrder(msg);
                break;

            }
        };
    };

    // ----------------------------处理有人关注
    private void mngFans(Message msg) {
        showUiByFans(msg.what);
    }

    // 有人关注时更新UI展示
    public static void showUiByFans(int comtype) {
        BaseActivity contMain = AppManager.getAppManager().getActivityByName(MenuEnum.ContactsMenu.getActName());
        BaseActivity contAct = AppManager.getAppManager().getActivityByName(MenuEnum.ContList.getActName());
        if (AppManager.getAppManager().currentActivity().getClass().getName().indexOf(MenuEnum.ContFans.getActName()) <= 0) {// 只要不在关注我的人页面
            contMain.refresh(comtype, isShow);// 更新主页
            if (!Tool.isEmpty(contAct)) {
                contAct.refresh(comtype, isShow);
            }
            updateTabByCode(MenuEnum.ContactsMenu.getMenuCode(), isShow);
            updateTabByCode(MenuEnum.ContList.getMenuCode(), isShow);
        }
    }

    // 更新UI消失红点
    public static void showNoUiByFans(int what) {
        BaseActivity contMain = AppManager.getAppManager().getActivityByName(MenuEnum.ContactsMenu.getActName());
        BaseActivity contAct = AppManager.getAppManager().getActivityByName(MenuEnum.ContList.getActName());
        contMain.refresh(what, noShow);// 更新主页
        if (!Tool.isEmpty(contAct)) {
            contAct.refresh(what, noShow);
        }
        updateTabByCode(MenuEnum.ContactsMenu.getMenuCode(), noShow);
        updateTabByCode(MenuEnum.ContList.getMenuCode(), noShow);
    }

    // ------------------------分割线

    // ------------------处理订单
    private void mngOrder(Message msg) {
        InfoFee infoFee = (InfoFee) msg.obj;
        BaseActivity orderMain = AppManager.getAppManager().getActivityByName(MenuEnum.OrderMenu.getActName());
        BaseActivity infoAct = AppManager.getAppManager().getActivityByName(MenuEnum.OrderList.getActName());
        if (AppManager.getAppManager().currentActivity().getClass().getName().indexOf(MenuEnum.OrderMenu.getActName()) > 0) {// 在主页面
            if (MainActivity.sCurPagePos != MainActivity.TASK_POS) { // 不在订单页面
                orderMain.refresh(msg.what, isShow, false);
                updateTabByCode(MenuEnum.OrderMenu.getMenuCode(), isShow);
            } else {
                orderMain.refresh(msg.what, isShow, true);
            }
            updateTabByCode(MenuEnum.OrderPeihuo.getMenuCode(), isShow);
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_EXECUTE) {
                updateTabByCode(MenuEnum.OrderListExe.getMenuCode(), isShow);
            } else if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                updateTabByCode(MenuEnum.OrderListDone.getMenuCode(), isShow);
            } else if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_CANCEL) {
                updateTabByCode(MenuEnum.OrderListCancel.getMenuCode(), isShow);
            } else {
                updateTabByCode(MenuEnum.OrderListExe.getMenuCode(), isShow);
            }
        } else if (AppManager.getAppManager().getActivityByName(MenuEnum.OrderList.getActName()) != null) {// 订单列表ACT存在栈中
            int destatus = 0;
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_EXECUTE) {
                destatus = 0;
                updateTabByCode(MenuEnum.OrderListExe.getMenuCode(), isShow);
            } else if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                destatus = 1;
                updateTabByCode(MenuEnum.OrderListDone.getMenuCode(), isShow);
            } else if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_CANCEL) {
                destatus = 2;
                updateTabByCode(MenuEnum.OrderListCancel.getMenuCode(), isShow);
            }
            infoAct.refresh(msg.what, isShow, destatus);
        } else { // 在其他act
        	if(!Tool.isEmpty(orderMain)) {
        		  orderMain.refresh(msg.what, isShow, false);
        	}
            updateTabByCode(MenuEnum.OrderMenu.getMenuCode(), isShow);
            updateTabByCode(MenuEnum.OrderPeihuo.getMenuCode(), isShow);
            if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_EXECUTE) {
                updateTabByCode(MenuEnum.OrderListExe.getMenuCode(), isShow);
            } else if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_COMPLETE) {
                updateTabByCode(MenuEnum.OrderListDone.getMenuCode(), isShow);
            } else if (infoFee.getStatus() == Properties.INFO_FEE_STATUS_CANCEL) {
                updateTabByCode(MenuEnum.OrderListCancel.getMenuCode(), isShow);
            } else {
                updateTabByCode(MenuEnum.OrderListExe.getMenuCode(), isShow);
            }
        }
    }

    // 更新UI订单消失红点
    public static void showNoUiByOrder(int what, MenuEnum mem) {
        BaseActivity bAct = AppManager.getAppManager().getActivityByName(mem.getActName());
        bAct.refresh(what, noShow, false);// 更新主页
        updateTabByCode(mem.getMenuCode(), noShow);
    }

 
    // ------------------------分割线

    // 根据code修改是否展示状态
    public static void updateTabByCode(String menuCode, int isshow) {
        try {
            db.executeSql(" Update  " + MenuTabName + " Set isshow='" + String.valueOf(isshow) + "' Where menuCode='"
                    + menuCode + "' And curLoginPhone='" + UserDao.getInstance().getUser().getPhone() + "' ");
        } catch (Exception e) {
            LogUtils.e(null, e);
        }
 
    }

    // 根据条件获取列表
    @SuppressWarnings("unchecked")
    public static List<MenuBean> ListMenuBean(String filter) {
        List<MenuBean> listBean = new ArrayList<MenuBean>();
        try {
            listBean = (List<MenuBean>) db.listBeanByFilter(MenuBean.class, filter + " And curLoginPhone='"
                    + UserDao.getInstance().getUser().getPhone() + "' ");
        } catch (Exception e) {

            return listBean;
        }
        return listBean;

    }

    // 根据条件获取一个
    @SuppressWarnings("unchecked")
    public static MenuBean getMenuBean(String filter) {
        MenuBean bean = new MenuBean();
        try {
            bean = (MenuBean) db.getBeanByFilter(MenuBean.class, filter + " And curLoginPhone='"
                    + UserDao.getInstance().getUser().getPhone() + "' ");
        } catch (Exception e) {

            return bean;
        }
        return bean;

    }

}
