package com.epeisong.base.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.ParseException;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.epeisong.R;
import com.epeisong.utils.ToastUtils;

/**
 * 日期时间选择控件 使用方法： private EditText inputDate;//需要设置的日期时间文本编辑框 private String
 * initDateTime="2012年9月3日 14:44",//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 * 
 * @Override public void onClick(View v) { DateTimePickDialogUtil
 *           dateTimePicKDialog=new
 *           DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime);
 *           dateTimePicKDialog.dateTimePicKDialog(inputDate);
 * 
 *           } });
 * 
 * @author
 */
public class DateTimePickDialogUtil implements OnDateChangedListener,
		OnTimeChangedListener {
	private DatePicker datePicker;
	private AlertDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;

	/**
	 * 日期时间弹出选择框构造函数
	 * 
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DateTimePickDialogUtil(Activity activity) {
		this.activity = activity;

	}

	public void init(DatePicker datePicker) {
		Calendar calendar = Calendar.getInstance();
		if (!(null == initDateTime || "".equals(initDateTime))) {
			calendar = this.getCalendarByInintData(initDateTime);
		} else {
			initDateTime = calendar.get(Calendar.YEAR) + "年"
					+ calendar.get(Calendar.MONTH) + "月"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "日 ";
		}

		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), this);
	}

	/**
	 * 弹出日期时间选择框方法
	 * 
	 * @param inputDate
	 *            :为需要设置的日期时间文本编辑框
	 * @return
	 */
	public AlertDialog dateTimePicKDialog(final TextView inputDate) {
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.common_datetime, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		init(datePicker);
//		compareTo();
		ad = new AlertDialog.Builder(activity)
				.setTitle("选择日期")
				.setView(dateTimeLayout)
				.setPositiveButton("设置", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						int compare = compareTo();
						try {
							if(compare > 0){
								ToastUtils.showToast("选择日期不能大于当前日期，请重新选择");
							}else if(compare == 0 || compare == -1){
								inputDate.setText(dateTime);
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
//						inputDate.setText("");
					}
				}).show();
	
			onDateChanged(null, 0, 0, 0);
			return ad;
		
	}
	
	//比较两个日期的大小
	public int compareTo(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		Calendar calendar1 = Calendar.getInstance();
		datePicker.clearFocus();  // 如果不加上这句代码，手动输入日期输入框焦点将不能失去，就无法获得输入的内容
		calendar1.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth());
		String selectTime = sdf.format(calendar1.getTime());
		if (!(null == selectTime || "".equals(selectTime))) {
			calendar1 = this.getCalendarByInintData(selectTime);
		} else {
			selectTime = calendar1.get(Calendar.YEAR) + "年"
					+ calendar1.get(Calendar.MONTH) + "月"
					+ calendar1.get(Calendar.DAY_OF_MONTH) + "日 ";
		}
		
		Date curDate = new Date(System.currentTimeMillis()); //获取当前时间     
		String curTime = sdf.format(curDate);
		Calendar calendar2 = Calendar.getInstance();
		if (!(null == curTime || "".equals(curTime))) {
			calendar2 = this.getCalendarByInintData(curTime);
		} else {
			curTime = calendar2.get(Calendar.YEAR) + "年"
					+ calendar2.get(Calendar.MONTH) + "月"
					+ calendar2.get(Calendar.DAY_OF_MONTH) + "日 ";
		}
//		Calendar calendar1 = getCalendarByInintData(dateTime);
//		Calendar calendar2 = getCalendarByInintData(curTime);
		int c = calendar1.compareTo(calendar2);
		return c;
	}

	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}

	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// 获得日历实例
		Calendar calendar = Calendar.getInstance();

		calendar.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

		dateTime = sdf.format(calendar.getTime());
		
//		ad.setTitle(dateTime);
	}

	/**
	 * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
	 * 
	 * @param initDateTime
	 *            初始日期时间值 字符串型
	 * @return Calendar
	 */
	private Calendar getCalendarByInintData(String initDateTime) {
		Calendar calendar = Calendar.getInstance();

		// 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
		String date = spliteString(initDateTime, "日", "index", "front"); // 日期

		String yearStr = spliteString(date, "年", "index", "front"); // 年份
		String monthAndDay = spliteString(date, "年", "index", "back"); // 月日

		String monthStr = spliteString(monthAndDay, "月", "index", "front"); // 月
		String dayStr = spliteString(monthAndDay, "月", "index", "back"); // 日

		int currentYear = Integer.valueOf(yearStr.trim()).intValue();
		int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
		int currentDay = Integer.valueOf(dayStr.trim()).intValue();

		calendar.set(currentYear, currentMonth, currentDay);
		return calendar;
	}

	/**
	 * 截取子串
	 * 
	 * @param srcStr
	 *            源串
	 * @param pattern
	 *            匹配模式
	 * @param indexOrLast
	 * @param frontOrBack
	 * @return
	 */
	public static String spliteString(String srcStr, String pattern,
			String indexOrLast, String frontOrBack) {
		String result = "";
		int loc = -1;
		if (indexOrLast.equalsIgnoreCase("index")) {
			loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
		} else {
			loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
		}
		if (frontOrBack.equalsIgnoreCase("front")) {
			if (loc != -1)
				result = srcStr.substring(0, loc); // 截取子串
		} else {
			if (loc != -1)
				result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
		}
		return result;
	}

}
