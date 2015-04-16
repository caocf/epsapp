package com.epeisong.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.epeisong.EpsApplication;

/**
 * 文件工具类
 * @author poet
 *
 */
public class FileUtils {

    private static Context context = EpsApplication.getInstance();

    /**
     * 获取sd卡缓存路径
     * 
     * @return
     */
    public static String getExternalCachePath() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + context.getPackageName()
                + "/cache";
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        return path;
    }

    /**
     * sd卡临时目录
     * @return
     */
    public static String getExternalTempPath() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + context.getPackageName()
                + "/temp";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    /**
     * 获取 从 相册选取的图片的存储路径
     * 
     * @return
     */
    public static String getChoosePictureFilePath() {
        return getExternalTempPath() + File.separator + "choose.jpg";
    }

    /**
     * 获取照相机拍摄图片的存储路径
     * 
     * @return
     */
    public static String getCameraSaveFilePath() {
        return getExternalTempPath() + File.separator + "camera.jpg";
    }

    /**
     * 语音聊天：缓存语音目录
     * @return
     */
    public static String getChatVoiceFileDir() {
        String path = getExternalCachePath() + File.separator + "chat_voice";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    /**
     * 录音文件目录
     * @return
     */
    public static String getRecordFileDir() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + context.getPackageName()
                + "/record";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return path;
    }

    public static void clearRecordFileDir() {
        String path = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + context.getPackageName()
                + "/record";
        File dir = new File(path);
        if (dir.exists()) {
            dir.delete();
        }
    }

    /**
     * 检查指定路径的目录是否存在，不存在则创建
     * 
     * @param path
     */
    public static void checkOrCreateDirectory(String path) {
        File file = new File(path);
        if (!file.exists()) {
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
        }
    }

    public static void deleteFile(String strPath) {
        File file = new File(strPath);
        file.delete();
    }

    public static void deleteFolder(String strPath) {
        File file = new File(strPath);
        if (file.isDirectory()) {
            File fileChilds[] = file.listFiles();
            if (fileChilds == null) {
                file.delete();
            } else {
                final int nLength = fileChilds.length;
                if (nLength > 0) {
                    for (File fileChild : fileChilds) {
                        if (fileChild.isDirectory()) {
                            deleteFolder(fileChild.getAbsolutePath());
                        } else {
                            fileChild.delete();
                        }
                    }
                    file.delete();
                } else {
                    file.delete();
                }
            }
        } else {
            file.delete();
        }
    }

    public static FileOutputStream createFileOutputStream(String strPath) throws Exception {
        final File file = new File(strPath);
        try {
            return new FileOutputStream(file);
        } catch (Exception e) {
            final File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                if (fileParent.mkdirs()) {
                    return new FileOutputStream(file);
                }
            }
        }

        return null;
    }

    public static void saveBitmapToFile(String pathDst, Bitmap bmp) {
        saveBitmapToFile(pathDst, bmp, 80);
    }

    public static void saveBitmapToFile(String pathDst, Bitmap bmp, int quality) {
        try {
            FileOutputStream fos = createFileOutputStream(pathDst);
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isBitmapFile(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        if (options.outWidth > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void copyFile(String strPathDst, String strPathSrc) {
        if (strPathDst != null && !strPathDst.equals(strPathSrc)) {
            FileOutputStream fos = null;
            FileInputStream fis = null;
            try {
                fos = createFileOutputStream(strPathDst);
                fis = new FileInputStream(strPathSrc);
                byte buf[] = new byte[1024];
                int nReadBytes = 0;
                while ((nReadBytes = fis.read(buf, 0, buf.length)) != -1) {
                    fos.write(buf, 0, nReadBytes);
                }
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String readFileToString(String strFilePath) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(strFilePath), "GBK"));
            final StringBuffer sb = new StringBuffer();
            String strLine = null;
            while ((strLine = br.readLine()) != null) {
                sb.append(strLine);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 获取文件扩展名
     * 
     * @param fileName
     * @param def
     * @return
     */
    public static String getFileExt(String fileName, String def) {
        if (fileName == null) {
            return def;
        }
        int pos = fileName.lastIndexOf(".");
        if (pos >= 0) {
            return fileName.substring(pos + 1);
        }
        return def;
    }

    /**
     * 将字符串以文件格式保存到内部存储卡
     * 
     * @param fileName
     * @param content
     */
    public static boolean saveString2File(String fileName, String content) {
        String path = Environment.getExternalStorageDirectory().getPath();
        LogUtils.t(path);
        File file = new File(path, fileName);
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.append(content);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
