package com.epeisong.base.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 金额输入EditText
 * 
 * @author Jack
 * 
 */

public class MoneyEditText extends EditText {
	
	public MoneyEditText(Context context) {
		super(context);
		initEditText();
	}

	public MoneyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initEditText();
	}

	public MoneyEditText(Context context, AttributeSet attrs, int Int) {
		super(context, attrs, Int);
		initEditText();
	}

	private void initEditText() {
		
		addTextChangedListener( new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				String temp = s.toString();
				int d = temp.indexOf(".");
				if (d < 0) return;
				if (temp.length() - d - 1 > 2){
					s.delete(d + 3, d + 4);
				}else if (d==0) {
					s.delete(d, d+1);
				}
			}
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
		});
	}
	
}
