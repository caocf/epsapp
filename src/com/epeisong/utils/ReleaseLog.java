package com.epeisong.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.os.Environment;
import android.util.Log;

/**
 * 发布后的测试log
 * 文件路径：sd卡/sys_epeisong/release_log.txt
 * @author poet
 *
 */
public class ReleaseLog {

    private static boolean first = true;

    private final static String lineSeparator = System.getProperty("line.separator");

    public static void log(String tag, Throwable tr) {
        log(tag, Log.getStackTraceString(tr));
    }

    public static void log(String tag, String log) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String content = DateUtil.long2YMDHMSS(System.currentTimeMillis()) + lineSeparator + tag + lineSeparator
                    + log + lineSeparator + lineSeparator;
            writeLogToFile(content);
        }
    }

    private synchronized static void writeLogToFile(String content) {
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + "sys_epeisong");

        if (!dir.exists()) {
            dir.mkdir();
        }
        String fileName = "release_log" + ".txt";

        if (first) {
            first = false;
            File file = new File(dir, fileName);
            file.delete();
        }

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(new File(dir, fileName), "rw");
            raf.seek(raf.length());
            raf.write(content.getBytes("GBK"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
