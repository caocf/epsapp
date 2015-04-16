package com.epeisong.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.epeisong.EpsApplication;
import com.epeisong.utils.java.ResersibleCipher;

/**
 * 用户配置的SharedPreference
 * 
 * @author poet
 * 
 */
public class SpUtils {

    private static String privateKey = "1234567890";

    private static SharedPreferences sp = EpsApplication.getInstance().getSharedPreferences("eps_config.sp",
            Context.MODE_PRIVATE);

    public static void put(String key, Object value) {
        if (value == null) {
            sp.edit().putString(key, null).commit();
        } else {
            if (value instanceof Boolean) {
                sp.edit().putBoolean(key, (Boolean) value).commit();
            } else if (value instanceof Integer) {
                sp.edit().putInt(key, (Integer) value).commit();
            } else if (value instanceof Float) {
                sp.edit().putFloat(key, (Float) value).commit();
            } else if (value instanceof Long) {
                sp.edit().putLong(key, (Long) value).commit();
            } else if (value instanceof String) {
                if (KEYS_SYS.STRING_CURR_USER_PWD_ENCODED.equals(key)) {
                    value = ResersibleCipher.encode(privateKey, (String) value);
                }
                sp.edit().putString(key, (String) value).commit();
            }
        }
    }

    public static void remove(String key) {
        sp.edit().remove(key).commit();
    }

    public static boolean getBoolean(String key, boolean def) {
        return sp.getBoolean(key, def);
    }

    public static int getInt(String key, int def) {
        return sp.getInt(key, def);
    }

    public static float getFloat(String key, float def) {
        return sp.getFloat(key, def);
    }

    public static long getLong(String key, long def) {
        return sp.getLong(key, def);
    }

    public static String getString(String key, String def) {
        String result = sp.getString(key, def);
        if (KEYS_SYS.STRING_CURR_USER_PWD_ENCODED.equals(key)) {
            if (TextUtils.isEmpty(result)) {
                result = sp.getString(KEYS_SYS.STRING_CURR_USER_PWD, null);
                if (result != null) {
                    remove(KEYS_SYS.STRING_CURR_USER_PWD);
                    put(key, ResersibleCipher.encode(privateKey, result));
                    return result;
                }
            }
            return ResersibleCipher.decode(privateKey, result);
        }
        return result;
    }

    public static void clear() {
        sp.edit().clear().commit();
    }

    public static interface KEYS_SYS {
        String BOOL_HINT_INSTALL_SHORTCUT = "hint_install_shortcut";
        // 系统字典版本
        String INT_DICTIONARY_VERSION = "dictionary_version";
        String LONG_LAST_CHECK_DICT_TIME = "last_check_dict_time";
        // 检查到的新版本，新版本版本号
        String LONG_LAST_CHECK_NEW_VERSION_TIME = "last_check_new_version_time";
        String BOOL_APP_HAS_NEW_VERSION = "app_has_new_version";
        String STRING_APP_NEW_VERSION_URL = "app_new_version_url";
        String BOOL_APP_IS_MUST_UPDATE = "app_is_must_update";
        // 当前登录者的id（md5后）
        String STRING_CURR_USER_ID_MD5 = "curr_user_id_md5";
        String STRING_CURR_USER_PHONE = "curr_user_phone";
        String STRING_CURR_USER_PWD = "curr_user_pwd";
        String STRING_CURR_USER_PWD_ENCODED = "curr_user_pwd_encoded";
        String BOOL_CURR_USER_LOGINED = "curr_user_logined";
        // 当前连接的ip（业务）
        String STRING_CURR_CONN_IP = "curr_conn_ip";
        String INT_CURR_CONN_PORT = "curr_conn_port";
        // 登录记录
        String STRING_LOGINERS = "loginers";
        // 上次断开连接（登录）的时间
        String LONG_LAST_UNLOGIN = "last_unlogin";
    }

}
