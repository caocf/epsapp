package com.epeisong.service.receiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;

import com.epeisong.EpsApplication;
import com.epeisong.EpsNetConfig;
import com.epeisong.data.dao.ContactsDao;
import com.epeisong.data.dao.DictionaryDao;
import com.epeisong.data.exception.NetGetException;
import com.epeisong.data.net.NetContactsSync;
import com.epeisong.data.net.parser.ContactsParser;
import com.epeisong.logistics.common.Constants;
import com.epeisong.logistics.common.Properties;
import com.epeisong.logistics.net.NetServiceFactory;
import com.epeisong.logistics.proto.Base.ProtoSysDictionary;
import com.epeisong.logistics.proto.Eps.AppNewVersionResp;
import com.epeisong.logistics.proto.Eps.CommonLogisticsResp;
import com.epeisong.logistics.proto.Eps.ContactReq.Builder;
import com.epeisong.logistics.proto.Eps.SysDictionaryResp;
import com.epeisong.model.Contacts;
import com.epeisong.model.Dictionary;
import com.epeisong.utils.DateUtil;
import com.epeisong.utils.LogUtils;
import com.epeisong.utils.ReleaseLog;
import com.epeisong.utils.SpUtils;
import com.epeisong.utils.SpUtilsCur;
import com.epeisong.utils.android.AsyncTask;
import com.epeisong.utils.android.BroadcastUtils;
import com.test.log.CrashLogDao;
import com.test.log.CrashLogListActivity.CrashLog;

/**
 * 闹钟广播接收者
 * @author poet
 *
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public static final String EXTRA_ALARM_TASK_CODE = "alarm_task_code";

    private boolean save_log_to_db = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        syncContacts();
    }

    private void syncContacts() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final long newestTime = SpUtilsCur.getLong(SpUtilsCur.KEYS_SERVICE.LONG_CONTACTS_NEWEST_TIME, 0);
                final String newestId = SpUtilsCur.getString(SpUtilsCur.KEYS_SERVICE.STRING_CONTACTS_NEWEST_ID, null);

                if (EpsApplication.DEBUGGING && save_log_to_db) {
                    CrashLog log = new CrashLog();
                    log.setTime(System.currentTimeMillis());
                    log.setContent("发出请求\ntime:" + DateUtil.long2MDHMSS(newestTime) + "\nid" + newestId);
                    CrashLogDao.getInstance().insert(log);
                }

                NetContactsSync net = new NetContactsSync() {
                    @Override
                    protected boolean onSetRequest(Builder req) {
                        if (newestTime > 0 && newestId != null) {
                            req.setLatestUpdatedTimeOnClient(newestTime);
                            req.setId(Integer.parseInt(newestId));
                        }
                        req.setLimitCount(1001);
                        return true;
                    }
                };
                try {
                    CommonLogisticsResp.Builder resp = net.request();
                    if (net.isSuccess(resp)) {
                        List<Contacts> list = ContactsParser.parse(resp);
                        if (list.size() > 0) {
                            ContactsDao.getInstance().replaceAll(list);

                            Collections.sort(list, new Comparator<Contacts>() {
                                @Override
                                public int compare(Contacts lhs, Contacts rhs) {
                                    long d = lhs.getUpdate_time() - rhs.getUpdate_time();
                                    if (d > 0) {
                                        return 1;
                                    } else if (d < 0) {
                                        return -1;
                                    }
                                    return Integer.parseInt(lhs.getRelation_id())
                                            - Integer.parseInt(rhs.getRelation_id());
                                }
                            });

                            Contacts newest = list.get(list.size() - 1);
                            long mNewestTime = newest.getUpdate_time();
                            String mNewestId = newest.getRelation_id();
                            SpUtilsCur.put(SpUtilsCur.KEYS_SERVICE.LONG_CONTACTS_NEWEST_TIME, mNewestTime);
                            SpUtilsCur.put(SpUtilsCur.KEYS_SERVICE.STRING_CONTACTS_NEWEST_ID, mNewestId);

                            if (EpsApplication.DEBUGGING && save_log_to_db) {
                                StringBuilder sb = new StringBuilder();
                                for (Contacts c : list) {
                                    sb.append("time:" + DateUtil.long2YMDHMSS(c.getUpdate_time()) + "\nid:"
                                            + c.getRelation_id() + "\n状态:" + c.getStatus() + "\n\n");

                                }
                                CrashLog log2 = new CrashLog();
                                log2.setTime(System.currentTimeMillis());
                                log2.setContent("同步到数据\ntime:" + DateUtil.long2MDHMSS(mNewestTime) + "\nid:"
                                        + mNewestId + "\nncount:" + list.size() + "\n\ncontactss: (1、正常，2、删除，3、黑名单)\n"
                                        + sb.toString());
                                CrashLogDao.getInstance().insert(log2);
                            }
                        } else {
                            if (EpsApplication.DEBUGGING && save_log_to_db) {
                                CrashLog log3 = new CrashLog();
                                log3.setTime(System.currentTimeMillis());
                                log3.setContent("没有同步到数据");
                                CrashLogDao.getInstance().insert(log3);
                            }
                        }
                    }
                } catch (NetGetException e) {
                    e.printStackTrace();
                }
                checkDictionary();
                return null;
            }
        };
        task.execute();
    }

    private void checkDictionary() {
        long lastCheckTime = SpUtils.getLong(SpUtils.KEYS_SYS.LONG_LAST_CHECK_DICT_TIME, 0);
        if (System.currentTimeMillis() - lastCheckTime >= 1000 * 3600 * 12) {
            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        int version = SpUtils.getInt(SpUtils.KEYS_SYS.INT_DICTIONARY_VERSION, 0);
                        LogUtils.d("checkDictionary", "version:" + version);
                        SysDictionaryResp.Builder resp = NetServiceFactory.getInstance().checkSysDictionary(
                                EpsNetConfig.getHost(), EpsNetConfig.PORT, version, 9000);
                        if (resp == null || !Constants.SUCC.equals(resp.getResult())) {
                            return false;
                        }
                        SpUtils.put(SpUtils.KEYS_SYS.LONG_LAST_CHECK_DICT_TIME, System.currentTimeMillis());
                        int curVersion = resp.getCurrentVersionCodeOfSysDictionary();
                        List<ProtoSysDictionary.Builder> pDicts = resp.getSysDictionaryBuilderList();
                        LogUtils.d("checkDictionary", "curVersion:" + curVersion + "\ndicts.size:" + pDicts.size());
                        if (pDicts == null || pDicts.isEmpty()) {
                            return false;
                        }
                        List<Dictionary> dicts = new ArrayList<Dictionary>();
                        for (ProtoSysDictionary.Builder item : pDicts) {
                            Dictionary dict = new Dictionary();
                            dict.setId(item.getIndexId());
                            dict.setName(item.getName());
                            dict.setSort_order(item.getSortOrder());
                            dict.setType(item.getDictionaryTypeId());
                            dicts.add(dict);
                        }
                        if (DictionaryDao.getInstance().insertAll(dicts)) {
                            SpUtils.put(SpUtils.KEYS_SYS.INT_DICTIONARY_VERSION, curVersion);
                            return true;
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        String eStr = LogUtils.e("test", e);
                        LogUtils.saveLog("init", eStr);
                    }
                    return false;
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    checkNewVersion();
                }
            };
            task.execute();
        }
    }

    private void checkNewVersion() {
        long lastCheckTime = SpUtils.getLong(SpUtils.KEYS_SYS.LONG_LAST_CHECK_NEW_VERSION_TIME, 0);
        if (System.currentTimeMillis() - lastCheckTime >= 1000 * 3600 * 12) {
            AsyncTask<Void, Void, AppNewVersionResp.Builder> task = new AsyncTask<Void, Void, AppNewVersionResp.Builder>() {
                @Override
                protected AppNewVersionResp.Builder doInBackground(Void... arg0) {
                    int versionCode = 1;
                    try {
                        versionCode = EpsApplication.getInstance().getPackageManager()
                                .getPackageInfo(EpsApplication.getInstance().getPackageName(), 0).versionCode;
                    } catch (NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    try {
                        AppNewVersionResp.Builder resp = NetServiceFactory.getInstance().checkNewVersion(
                                EpsNetConfig.getHost(), EpsNetConfig.PORT, versionCode,
                                Properties.APP_CLIENT_TYPE_ANDROID_PHONE, 9000);
                        return resp;
                    } catch (Throwable e) {
                        e.printStackTrace();
                        ReleaseLog.log("checkNewVersion", e);
                        SpUtils.remove(SpUtils.KEYS_SYS.BOOL_APP_HAS_NEW_VERSION);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(AppNewVersionResp.Builder result) {
                    if (result == null || !result.getResult().equals(Constants.SUCC)) {
                        return;
                    }
                    boolean need = result.getIsNeedToUpdate();
                    if (need) {
                        String url = result.getDownLoadUrl();
                        SpUtils.put(SpUtils.KEYS_SYS.BOOL_APP_HAS_NEW_VERSION, true);
                        SpUtils.put(SpUtils.KEYS_SYS.BOOL_APP_IS_MUST_UPDATE, result.getIsMustUpdate());
                        SpUtils.put(SpUtils.KEYS_SYS.STRING_APP_NEW_VERSION_URL, url);
                        BroadcastUtils.send(BroadcastUtils.ACTION_FIND_NEW_VERSION, null);
                    } else {
                        SpUtils.remove(SpUtils.KEYS_SYS.BOOL_APP_HAS_NEW_VERSION);
                        SpUtils.remove(SpUtils.KEYS_SYS.BOOL_APP_IS_MUST_UPDATE);
                        SpUtils.remove(SpUtils.KEYS_SYS.STRING_APP_NEW_VERSION_URL);
                    }

                }
            };
            task.execute();
        }
    }
}
