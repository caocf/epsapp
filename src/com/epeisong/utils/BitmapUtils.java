package com.epeisong.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.text.TextUtils;

import com.epeisong.EpsApplication;

public class BitmapUtils {

    public static void saveBitmapToFile(String pathDst, Bitmap bmp) {
        saveBitmapToFile(pathDst, bmp, 80);
    }

    public static void saveBitmapToFile(String pathDst, Bitmap bmp, int quality) {
        try {
            FileOutputStream fos = createFileOutputStream(pathDst);
            if (fos != null) {
                bmp.compress(Bitmap.CompressFormat.JPEG, quality, fos);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public static Bitmap scaleBitmapFromRes(Context context, int resId, Rect rect) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);

        if (bitmap == null) {
            return null;
        }

        if (rect == null || (rect.width() == 0 && rect.height() == 0)) {
            return bitmap;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeResource(context.getResources(), resId, options);
        if (options.outWidth > 0) {
            options.inSampleSize = calculateInSampleSize(options, rect.width(), rect.height());
            options.inJustDecodeBounds = false;
            try {
                return BitmapFactory.decodeResource(context.getResources(), resId, options);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } finally {

            }
        }
        return null;
    }

    public static Bitmap decodeBitmapFromResId(Context context, int resId, Rect rect) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);

            if (bitmap == null) {
                return null;
            }

            if (rect == null || (rect.width() == 0 && rect.height() == 0)) {
                return bitmap;
            }

            int needW = rect.width();
            int needH = rect.height();
            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();

            if (bw > needW || bh > needH) {
                if (bw > bh) {
                    int newW = (int) (bh * needW / (double) needH);
                    int x = (bw - newW) / 2;
                    Bitmap newBimap = Bitmap.createBitmap(bitmap, x, 0, newW, bh);
                    bitmap.recycle();
                    bitmap = newBimap;
                } else {
                    int newH = (int) (bw * needH / (double) needW);
                    int y = (bh - newH) / 2;
                    Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, y, bw, newH);
                    bitmap.recycle();
                    bitmap = newBitmap;
                }
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            System.gc();
        }
        return null;
    }

    public static Bitmap decodeSampledBitmapFromFilePath(String path, int reqWidth, int reqHeight) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File f = new File(path);
        if (!f.exists()) {
            return null;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        BitmapFactory.decodeFile(path, options);
        if (options.outWidth > 0) {
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            try {
                return BitmapFactory.decodeFile(path, options);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }

            final float totalPixels = width * height;

            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    public static void checkPicExifRotate(String path) {
        int rotate = 0;
        if ((rotate = getPictureExifRotateAngle(path)) > 0) {
            handlePictureExif(rotate, path, path);
        }
    }

    public static int getPictureExifRotateAngle(String path) {
        int rotate = 0;
        try {
            ExifInterface ei = new ExifInterface(path);
            int ori = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if (ori == ExifInterface.ORIENTATION_ROTATE_180) {
                rotate = 180;
            } else if (ori == ExifInterface.ORIENTATION_ROTATE_270) {
                rotate = 270;
            } else if (ori == ExifInterface.ORIENTATION_ROTATE_90) {
                rotate = 90;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static void handlePictureExif(int rotate, String srcPath, String savePath) {
        if (rotate == -1) {
            rotate = SystemUtils.getPictureExifRotateAngle(srcPath);
        }
        if (rotate != 0) {
            Bitmap bmpOld = SystemUtils.decodeSampledBitmapFromFilePath(srcPath, EpsApplication.getScreenWidth(),
                    EpsApplication.getScreenWidth());
            if (bmpOld != null) {
                Matrix matrix = new Matrix();
                matrix.preRotate(rotate);
                final Bitmap bmpNew = Bitmap.createBitmap(bmpOld, 0, 0, bmpOld.getWidth(), bmpOld.getHeight(), matrix,
                        true);
                FileUtils.saveBitmapToFile(savePath, bmpNew);
            } else {
                FileUtils.copyFile(savePath, srcPath);
            }
        }
    }
}
