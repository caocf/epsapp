package com.epeisong.utils;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.epeisong.EpsApplication;

/**
 * 系统工具类：功能有待分类
 * 
 * @author poet
 * 
 */
public class SystemUtils {

	private static Context context = EpsApplication.getInstance();

	/**
	 * 网络是否可用
	 * 
	 * @return
	 */
	public static boolean isNetworkAvaliable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) EpsApplication.getInstance()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo net = connectivityManager.getActiveNetworkInfo();
		if (net != null && net.isAvailable() && net.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取设备IMEI
	 * 
	 * @return
	 */
	public static String getImei() {
		TelephonyManager telephonyManager = (TelephonyManager) EpsApplication.getInstance()
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	/**
	 * 判断某个服务是否开启
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isServiceRunning(String name) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> list = manager.getRunningServices(30);
		for (RunningServiceInfo info : list) {
			if (info.service.getClassName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static View inflate(int resId) {
		return LayoutInflater.from(context).inflate(resId, null);
	}

	public static View find(View v, int id) {
		return v.findViewById(id);
	}

	/**
	 * 从指定路径下，解码出指定宽高的Bitmap
	 * 
	 * @param path
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromFilePath(String path,
			int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inPurgeable = true;
		BitmapFactory.decodeFile(path, options);
		if (options.outWidth > 0) {
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);
			options.inJustDecodeBounds = false;
			try {
				return BitmapFactory.decodeFile(path, options);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 压缩图片
	 * 
	 * @param dstPath
	 *            压缩到的位置
	 * @param srcPath
	 *            图片原位置
	 * @param reqWidth
	 *            要压缩的宽度
	 * @param reqHeight
	 *            要压缩的高度
	 * @return
	 */
	public static boolean compressBitmapFile(String dstPath, String srcPath,
			int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcPath, options);
		if (options.outWidth > 0) {
			if (options.outWidth > reqWidth || options.outHeight > reqHeight) {
				options.inSampleSize = calculateInSampleSize(options, reqWidth,
						reqHeight);
				options.inJustDecodeBounds = false;
				try {
					Bitmap bmp = BitmapFactory.decodeFile(srcPath, options);
					FileUtils.saveBitmapToFile(dstPath, bmp, 40);
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					return false;
				}
			} else {
				FileUtils.copyFile(dstPath, srcPath);
			}
		} else {
			return false;
		}
		return true;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
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

	/**
	 * 处理图片的exif，改变其旋转角度
	 * 
	 * @param rotate
	 *            可为null，默认获取图片本身的旋转角度
	 * @param srcPath
	 * @param savePath
	 */
	public static void handlePictureExif(Integer rotate, String srcPath,
			String savePath) {
		if (rotate == null) {
			rotate = getPictureExifRotateAngle(srcPath);
		}
		if (rotate != null && rotate != 0) {
			Bitmap oldBmp = decodeSampledBitmapFromFilePath(srcPath,
					EpsApplication.getScreenWidth(),
					EpsApplication.getScreenHeight());
			if (oldBmp != null) {
				Matrix matrix = new Matrix();
				matrix.preRotate(rotate);
				final Bitmap newBmp = Bitmap.createBitmap(oldBmp, 0, 0,
						oldBmp.getWidth(), oldBmp.getHeight(), matrix, true);
				FileUtils.saveBitmapToFile(savePath, newBmp);
			} else {
				FileUtils.copyFile(savePath, srcPath);
			}
		}
	}

	/**
	 * 获取图片的旋转角度
	 * 
	 * @param path
	 * @return
	 */
	public static int getPictureExifRotateAngle(String path) {
		int rotate = 0;
		try {
			ExifInterface ei = new ExifInterface(path);
			int ori = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
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

	public static void showInputMethod(View view) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, 0);
	}

	public static boolean hideInputMethod(View view) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			return true;
		}
		return false;
	}

	/**
	 * 获取状态栏的高度
	 * 
	 * @param activity
	 * @return
	 */
	public static int getStatusBarHeight(Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		return frame.top;
	}

	/**
	 * 获取标题栏的高度
	 * 
	 * @param activity
	 * @return
	 */
	public static int getTitleBarHeight(Activity activity) {
		int contentTop = activity.getWindow()
				.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		return contentTop - getStatusBarHeight(activity);
	}

	public static Rect getViewBoundsOnScreen(View view, Activity activity) {
		int[] loc = new int[2];
		view.getLocationOnScreen(loc);
		int width = view.getMeasuredWidth();
		int height = view.getMeasuredHeight();
		Rect rect = new Rect(loc[0], loc[1], loc[0] + width, loc[1] + height);
		return rect;
	}
}
