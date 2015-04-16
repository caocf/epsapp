package com.epeisong.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间、日期工具类
 * 
 * @author poet
 * 
 */
public class DateUtil {

    private static final SimpleDateFormat DATEFORMAT_YMD = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
    private static final SimpleDateFormat DATEFORMAT_YMDHM = new SimpleDateFormat("yyyy-M-d H:mm", Locale.getDefault());
    private static final SimpleDateFormat DATEFORMAT_YMDHMSS = new SimpleDateFormat("yyyy-M-d H:mm:ss",
            Locale.getDefault());
    private static final SimpleDateFormat DATEFORMAT_MDHMSS = new SimpleDateFormat("M-d H:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat DATEFORMAT_HMSS = new SimpleDateFormat("H:mm:ss", Locale.getDefault());
    private static final SimpleDateFormat DATEFORMAT_MD = new SimpleDateFormat("M-d", Locale.getDefault());
    private static final SimpleDateFormat DATEFORMAT_MD_HM = new SimpleDateFormat("M-d H:mm", Locale.getDefault());
    private static final SimpleDateFormat DATEFORMAT_HM = new SimpleDateFormat("H:mm", Locale.getDefault());
    private static final SimpleDateFormat DATEFORMAT_MS = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private static final SimpleDateFormat DATEFORMAT_MS_SSS = new SimpleDateFormat("mm:ss SSS", Locale.getDefault());
    private static final SimpleDateFormat DATEFORMAT_YMDW = new SimpleDateFormat("yyyy年M月d日 EEEE", Locale.getDefault());
    private static final SimpleDateFormat DATEFORMAT_YMDC = new SimpleDateFormat("yyyy年M月d日", Locale.getDefault());

    public static String date2YMDW(Date date) {
        return DATEFORMAT_YMDW.format(date);
    }
    
    public static String date2MDHM(Date date){
    	return DATEFORMAT_MD_HM.format(date);
    }
    
    public static String date2YMDHMSS(Date date) {
        return DATEFORMAT_YMDHMSS.format(date);
    }

    public static String long2YMD(long time) {
        Date date = new Date(time);
        return DATEFORMAT_YMD.format(date);
    }

    public static String long2YMDC(long time) {
        Date date = new Date(time);
        return DATEFORMAT_YMDC.format(date);
    }

    public static String long2YMDW(long time) {
        Date date = new Date(time);
        return date2YMDW(date);
    }

    public static String date2YMDHM(Date date) {
        return DATEFORMAT_YMDHM.format(date);
    }

    public static String long2YMDHM(long time) {
        Date date = new Date(time);
        return date2YMDHM(date);
    }

    public static String long2YMDHMSS(long time) {
        Date date = new Date(time);
        return DATEFORMAT_YMDHMSS.format(date);
    }

    public static String long2MDHMSS(long time) {
        Date date = new Date(time);
        return DATEFORMAT_MDHMSS.format(date);
    }

    public static String long2HMSS(long time) {
        Date date = new Date(time);
        return DATEFORMAT_HMSS.format(date);
    }

    public static String long2HM(long time) {
        Date date = new Date(time);
        return DATEFORMAT_HM.format(date);
    }

    public static String long2MD(long time) {
        Date date = new Date(time);
        return DATEFORMAT_MD.format(date);
    }

    public static String long2MDHM(long time) {
        Date date = new Date(time);
        return DATEFORMAT_MD_HM.format(date);
    }

    public static String int2MS(int time) {
        Date date = new Date(time);
        return DATEFORMAT_MS.format(date);
    }

    public static String long2MS_SSS(long time) {
        Date date = new Date(time);
        return DATEFORMAT_MS_SSS.format(date);
    }

    public static String long2vague(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        Calendar today = Calendar.getInstance();

        // int dayOffset = today.get(Calendar.DAY_OF_MONTH) -
        // cal.get(Calendar.DAY_OF_MONTH);
        if (today.get(Calendar.YEAR) > cal.get(Calendar.YEAR))// 14.11.25 zhu
            return DATEFORMAT_MD.format(cal.getTime());
        int dayOffset = today.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR);
        if (dayOffset > 1) {
            return DATEFORMAT_MD.format(cal.getTime());
        }
        if (dayOffset == 1) {
            StringBuilder sb = new StringBuilder("昨天");
            if (cal.get(Calendar.HOUR_OF_DAY) < 12) {
                sb.append(" 早上");
            } else {
                sb.append(" 下午");
            }
            return sb.toString();
        }
        StringBuilder sb = new StringBuilder();
        if (cal.get(Calendar.HOUR_OF_DAY) < 12) {
            sb.append("早上");
        } else {
            sb.append("下午");
        }
        return sb.append(DATEFORMAT_HM.format(cal.getTime())).toString();
    }

    public static String long2vaguehour(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        Calendar today = Calendar.getInstance();

        // int dayOffset = today.get(Calendar.DAY_OF_MONTH) -
        // cal.get(Calendar.DAY_OF_MONTH);
        if (today.get(Calendar.YEAR) > cal.get(Calendar.YEAR))// 14.11.25 zhu
            return DATEFORMAT_MD.format(cal.getTime());
        int dayOffset = today.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR);
        String Shourminute = " " + String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + ":"
                + String.valueOf(cal.get(Calendar.MINUTE));
        if (dayOffset > 1) {
            return DATEFORMAT_MD.format(cal.getTime()) + Shourminute;
        }
        if (dayOffset == 1) {
            StringBuilder sb = new StringBuilder("昨天");
            if (cal.get(Calendar.HOUR_OF_DAY) < 12) {
                sb.append(" 早上");
            } else {
                sb.append(" 下午");
            }
            return sb.toString() + Shourminute;
        }
        StringBuilder sb = new StringBuilder();
        if (cal.get(Calendar.HOUR_OF_DAY) < 12) {
            sb.append("早上");
        } else {
            sb.append("下午");
        }
        return sb.append(DATEFORMAT_HM.format(cal.getTime())).toString();
    }

    public static String long2vaguehourMinute(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        Calendar today = Calendar.getInstance();

        // int dayOffset = today.get(Calendar.DAY_OF_MONTH) -
        // cal.get(Calendar.DAY_OF_MONTH);
        if (today.get(Calendar.YEAR) > cal.get(Calendar.YEAR))
            return DATEFORMAT_MD.format(cal.getTime());
        int dayOffset = today.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR);
        // String Shourminute =
        // " "+String.valueOf(cal.get(Calendar.HOUR))+":"+String.valueOf(cal.get(Calendar.MINUTE));
        if (dayOffset > 1) {
            return DATEFORMAT_MD_HM.format(cal.getTime());
        }
        if (dayOffset == 1) {
            StringBuilder sb = new StringBuilder("昨天 ");
            // if (cal.get(Calendar.HOUR_OF_DAY) < 12) {
            // sb.append(" 早上");
            // } else {
            // sb.append(" 下午");
            // }
            return sb.append(DATEFORMAT_HM.format(cal.getTime())).toString();
        }
        StringBuilder sb = new StringBuilder();
        if (cal.get(Calendar.HOUR_OF_DAY) < 12) {
            sb.append("早上 ");
        } else {
            sb.append("下午 ");
        }
        return sb.append(DATEFORMAT_HM.format(cal.getTime())).toString();
    }

    public static String long2vague(long time, boolean lastDayShowHM) {
        if (time <= 0) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.YEAR) != cal.get(Calendar.YEAR)) {
            return DATEFORMAT_YMD.format(cal.getTime());
        }
        int dayOffset = today.get(Calendar.DAY_OF_YEAR) - cal.get(Calendar.DAY_OF_YEAR);
        if (dayOffset > 1 || dayOffset < 0) {
            return DATEFORMAT_MD_HM.format(cal.getTime());
        }
        if (dayOffset == 1) {
            StringBuilder sb = new StringBuilder("昨天");
            if (cal.get(Calendar.HOUR_OF_DAY) < 12) {
                sb.append(" 早上");
            } else {
                sb.append(" 下午");
            }
            if (lastDayShowHM) {
                return sb.append(DATEFORMAT_HM.format(cal.getTime())).toString();
            }
            return sb.toString();
        }
        StringBuilder sb = new StringBuilder();
        if (cal.get(Calendar.HOUR_OF_DAY) < 12) {
            sb.append("早上");
        } else {
            sb.append("下午");
        }
        return sb.append(DATEFORMAT_HM.format(cal.getTime())).toString();
    }

    public static String long2noon(long timeOfDay) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeOfDay);
        StringBuffer sb = new StringBuffer();
        if (cal.get(Calendar.HOUR_OF_DAY) < 12) {
            sb.append("早上");
        } else {
            sb.append("下午");
            cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) - 12);
        }
        return sb.append(DATEFORMAT_HM.format(cal.getTime())).toString();
    }
}
