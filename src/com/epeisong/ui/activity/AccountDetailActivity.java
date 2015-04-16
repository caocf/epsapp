package com.epeisong.ui.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ParseException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.epeisong.base.activity.BaseActivity;

public abstract class AccountDetailActivity extends BaseActivity {
	public final static int ACCOUNT_DETAIL_ALL = 0;
	public final static int ACCOUNT_DETAIL_DAY = 1;
	public final static int ACCOUNT_DETAIL_MONTH = 2;
	protected final static int ACCOUNT_DETAIL_YEAR = 3;

    public TextView tv_select_time;
	
	/**
	 * 重写datePicker 1.只显示 年-月 2.title 只显示 年-月
	 */
	public class MonPickerDialog extends DatePickerDialog {
		public MonPickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
			super(context, callBack, year, monthOfYear, dayOfMonth);
			this.setTitle(year + "年" + (monthOfYear+1) + "月");
			
			((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
		}

		@Override
		public void onDateChanged(DatePicker view, int year, int month, int day) {
			super.onDateChanged(view, year, month, day);

			this.setTitle(year + "年" + (month+1) + "月");
		}

	}
	
	/**
	 * 重写datePicker 1.只显示 年-月 2.title 只显示 年-月
	 */
	public class YearPickerDialog extends DatePickerDialog {
		public YearPickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
			super(context, callBack, year, monthOfYear, dayOfMonth);
			this.setTitle((year) + "年");
			
			((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
			((ViewGroup) ((ViewGroup) this.getDatePicker().getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
		}

		@Override
		public void onDateChanged(DatePicker view, int year, int month, int day) {
			super.onDateChanged(view, year, month, day);

			this.setTitle((year) + "年");
		}

	}
	
    
	// 字符串类型日期转化成date类型
	public static Date strToDate(String style, String date) throws java.text.ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(style);
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
	}

	public static String dateToStr(String style, Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(style);
		return formatter.format(date);
	}
	
}
