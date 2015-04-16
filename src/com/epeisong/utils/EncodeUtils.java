package com.epeisong.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密工具
 * 
 * @author poet
 * 
 */
public class EncodeUtils {

	public static String md5base64(String s) {
		try {
			MessageDigest dm = MessageDigest.getInstance("md5");
			byte[] md5 = dm.digest(s.getBytes("utf-8"));
			// String result = Base64.encodeToString(md5, 0);
			StringBuilder hex = new StringBuilder(md5.length * 2);
			for (byte b : md5) {
				if ((b & 0xFF) < 0x10)
					hex.append("0");
				hex.append(Integer.toHexString(b & 0xFF));
			}
			return hex.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
