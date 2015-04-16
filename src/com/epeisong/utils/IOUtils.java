package com.epeisong.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.text.TextUtils;

/**
 * 流处理工具类
 * 
 * @author poet
 * 
 */
public class IOUtils {

	/**
	 * 简单数据处理，不关闭流
	 * 
	 * @param in
	 * @return 没有读到任何数据，并且该流已到末尾，返回 “-1”
	 */
	public static String readIn(InputStream in) {
		try {
			byte[] buf = new byte[1024];
			int len = in.read(buf);
			if (len < 0) {
				return "-1";
			}
			return new String(buf, 0, len, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * InputStream 转为 String
	 * 
	 * @param in
	 * @param charset
	 * @return
	 */
	public static String in2String(InputStream in, String charset) {
		if (in == null) {
			return null;
		}
		InputStreamReader reader;
		if (TextUtils.isEmpty(charset)) {
			reader = new InputStreamReader(in);
		} else {
			try {
				reader = new InputStreamReader(in, charset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				reader = new InputStreamReader(in);
			}
		}
		StringBuilder sb = new StringBuilder();
		char[] buf = new char[100];
		int len;
		try {
			while ((len = reader.read(buf)) > 0) {
				sb.append(buf, 0, len);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static byte[] getByteArrayFromFile(String filename) {
		try {
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(filename));
			LogUtils.t("IOUtils.getByteArrayFromFile.avaibale:" + in.available());
			ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
			byte[] buf = new byte[1024];
			int len = 0;
			while((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			byte[] result = out.toByteArray();
			out.close();
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean saveByteArrayToFile(byte[] data, String filePath,
			String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath, fileName);
//			file.createNewFile();
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(data);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return false;
	}
}
