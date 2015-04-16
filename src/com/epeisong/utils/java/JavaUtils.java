package com.epeisong.utils.java;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Pattern;

import android.text.TextUtils;

/**
 * java通用工具类
 * @author poet
 *
 */
public class JavaUtils {

    public static String joinString(String separator, Object... items) {
        if (items == null || items.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            if (items[i] != null) {
                sb.append(items[i]);
            }
        }
        return sb.toString();
    }

    public static String joinString(String separator, List<String> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(separator);
            }
            if (list.get(i) != null) {
                sb.append(list.get(i));
            }
        }
        return sb.toString();
    }

    public static boolean isPhoneNumValid(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        return phone.startsWith("1") && phone.length() == 11;
    }

    public static boolean isPwdValid(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            return false;
        }
        String regex = "^[a-zA-Z0-9_]{6,20}$";
        return Pattern.matches(regex, pwd);
    }

    public static String getShadowPwd(String mobile, String pwd, long currentTimeMillis) {
        String timestamp = "" + currentTimeMillis;

        String shadowPasswordSource = mobile + pwd + timestamp;

        byte[] btInput = shadowPasswordSource.getBytes();
        MessageDigest mdInst;
        try {
            mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            return byte2hex(md);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String byte2hex(byte[] b) {
        StringBuffer sb = new StringBuffer();
        String tmp = "";
        for (int i = 0; i < b.length; i++) {
            tmp = Integer.toHexString(b[i] & 0XFF);
            if (tmp.length() == 1) {
                sb.append("0" + tmp);
            } else {
                sb.append(tmp);
            }

        }
        return sb.toString();
    }

    public static String getString(Object obj) {
        if (obj == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder(obj.getClass().getSimpleName() + "[\n");
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                sb.append(field.getName() + " ---- " + field.get(obj) + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
