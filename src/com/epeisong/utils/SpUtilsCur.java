package com.epeisong.utils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;

import com.epeisong.EpsApplication;
import com.epeisong.MainActivity;
import com.epeisong.ui.activity.temp.LoginActivity;

/**
 * 当前用户的配置文件
 * @author poet
 *
 */
public class SpUtilsCur {

    private static SharedPreferences sp;

    private static SharedPreferences getSp() {
        if (sp == null) {
            synchronized (SpUtilsCur.class) {
                if (sp == null) {
                    String curMd5 = EpsApplication.getUserIdMd5();
                    if (curMd5 == null) {
                        EpsApplication.exit(MainActivity.sMainActivity, LoginActivity.class);
                        return null;
                    }
                    sp = EpsApplication.getInstance().getSharedPreferences(curMd5 + "_eps_config.sp",
                            Context.MODE_APPEND);
                }
            }
        }
        return sp;
    }

    public static void put(String key, Object value) {
        if (value == null) {
            getSp().edit().putString(key, null).commit();
        } else {
            if (value instanceof Boolean) {
                getSp().edit().putBoolean(key, (Boolean) value).commit();
            } else if (value instanceof Integer) {
                getSp().edit().putInt(key, (Integer) value).commit();
            } else if (value instanceof Float) {
                getSp().edit().putFloat(key, (Float) value).commit();
            } else if (value instanceof Long || value.getClass() == long.class) {
                getSp().edit().putLong(key, (Long) value).commit();
            } else if (value instanceof String) {
                getSp().edit().putString(key, (String) value).commit();
            }
        }
        notify(key);
    }

    public static void remove(String key) {
        getSp().edit().remove(key).commit();
        notify(key);
    }

    public static boolean getBoolean(String key, boolean def) {
        return getSp().getBoolean(key, def);
    }

    public static int getInt(String key, int def) {
        return getSp().getInt(key, def);
    }

    public static float getFloat(String key, float def) {
        return getSp().getFloat(key, def);
    }

    public static long getLong(String key, long def) {
        return getSp().getLong(key, def);
    }

    public static String getString(String key, String def) {
        return getSp().getString(key, def);
    }

    public static void clear() {
        getSp().edit().clear().commit();
    }

    public static interface KEYS_NORMAL {
        // 常用联系人id
        String STRING_COMMON_USE_CONTACTS_IDS = "common_use_contacts_ids";
    }

    public static interface KEYS_SERVICE {
        // 本地最新的联系人的时间
        String LONG_CONTACTS_NEWEST_TIME = "contacts_newest_time";
        // 本地最新的联系人关系id
        String STRING_CONTACTS_NEWEST_ID = "contacts_newest_id";

        // 手机通讯录已加载的联系人最大id
        String INT_PHONE_CONTACTS_LOADED_MAX_ID = "phone_contacts_loaded_max_id";

        // 是否已同意使用通讯录
        String BOOL_PHONE_CONTACTS_CAN_USE = "phone_contacts_can_use";
        // 通讯录使用完毕
        String BOOL_PHONE_CONTACTS_USE_COMPLETED = "phone_contacts_use_completed";
        // 通讯录当前加载（到自己的数据库）的个数
        String INT_PHONE_CONTACTS_COMPLETED_COUNT = "phone_contacts_use_count";

        // 是否需要刷新定位地址
        String INT_IS_REFRESH_LOC = "is_refresh_loc"; // 1打开，2关闭
    }

    public static interface KEYS_NOTIFY {
        String BOOL_NOTIFY_NEWS_SOUND = "notify_news_sound";
        String BOOL_NOTIFY_NEWS_SHAKE = "notify_news_shake";
        String BOOL_NOTIFY_TASK_SOUND = "notify_task_sound";
        String BOOL_NOTIFY_TASK_SHAKE = "notify_task_shake";

        String BOOL_OPEN_TTS = "open_tts";

        String BOOL_NO_DISTURB = "no_disturb";
        String LONG_START_TIME = "start_time";
        String LONG_END_TIME = "end_time";
    }

    public static Map<String, WeakReference<SpListener>> sListenerRefs;

    private static void notify(String key) {
        if (sListenerRefs != null && !sListenerRefs.isEmpty()) {
            Iterator<Entry<String, WeakReference<SpListener>>> it = sListenerRefs.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, WeakReference<SpListener>> next = it.next();
                if (next.getValue().get() == null) {
                    it.remove();
                } else {
                    if (next.getKey().equals(key)) {
                        next.getValue().get().onSpChange(key);
                    }
                }
            }
        }
    }

    public static void registerListener(String key, SpListener l) {
        if (l == null) {
            return;
        }
        if (sListenerRefs == null) {
            sListenerRefs = new HashMap<String, WeakReference<SpListener>>();
        }
        sListenerRefs.put(key, new WeakReference<SpUtilsCur.SpListener>(l));
    }

    public static interface SpListener {
        void onSpChange(String key);
    }
}
