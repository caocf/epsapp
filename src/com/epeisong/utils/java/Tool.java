package com.epeisong.utils.java;

import java.util.Date;
import java.util.List;
import java.util.UUID;

 

/**
 * 通用工具类
 */
public class Tool {
	
	/**
	 * String为null时转换为"",同时过滤掉前后空格
	 */
	public static String filerStr(String str) {
		if (str == null)
			return "";
		return str.trim();
	}
	/**
	 * String为null或者“null”时转换为"",同时过滤掉前后空格
	 */
	public static String filerStrnull(String str) {
		if (str == null || "null".equals(filerStr(str)))
			return "";
		return str.trim();
	}
 
	/**
	 * 判断对象值是否为空： 若对象为字符串，判断对象值是否为null或空格; 若对象为数组，判断对象值是否为null，或数组个数是否为0;
	 * 若对象为List。判断对象值是否为null，或List元素是否个数为0; 其他类型对象，只判断值是否为null.
	 * 
	 * @param value
	 * @return true-是
	 */
	public static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		} else if ((value instanceof String)
				&& (((String) value).trim().length() < 1)) {
			return true;
		} else if (value.getClass().isArray()) {
			if (0 == java.lang.reflect.Array.getLength(value)) {
				return true;
			}
		} else if (value instanceof List) {
			if (((List) value).isEmpty()) {
				return true;
			}
		}
		return false;
	}
	/**
	 *  首字母转大写
	 * @param s
	 * @return
	 */
	public static String toUpperFirst(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(
					Character.toUpperCase(s.charAt(0))).append(s.substring(1))
					.toString();
	}
	/**
	 * 获取特定元素数组下标
	 * @param arry
	 * @param element
	 * @return
	 */
	public static int getArraySuffix(String []array,String element) {
		if(element == null || array == null) {
			return -1;
		}
		for(int i=0;i<array.length;i++) {
			if(element.equals(array[i])) {
				return i;
			}
		}
		return -1;
		
	}
	
	/**
	 * 返回与当前时间(小时)最近的数组元素
	 */
	public static String getEleByArray(String []array,float diffsize) {
		Date  d = new Date() ;
		float curhour = d.getHours();
	 
		float curmin  = d.getMinutes();
		float curtime =curhour+curmin/60;
		for(int i=0;i<array.length;i++) {
			float hour = Float.parseFloat( array[i]);
			if(Math.abs(curtime-hour)<=diffsize) {
				return  array[i];
			}
		}
		return  array[0];
		 
	}
	
	/**
	 * 验证是否为数字
	 */
	public static boolean isNumber(String str)
    {
		if(Tool.isEmpty(str)) {
			return false;
		}
        java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("[0-9]*");
        java.util.regex.Matcher match=pattern.matcher(str);
        if(match.matches()==false)
        {
             return false;
        }
        else
        {
             return true;
        }
    }
	
	/**
	 * 获取GUID
	 */

	public static String getGuid() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();

	}
	

}
