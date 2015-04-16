package com.epeisong.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
/**
 * 监听软键盘弹出隐藏的布局
 * @author poet
 *
 */
public class OnResizeLinearLayout extends LinearLayout {
	
	public static final int STATE_NORMAL = 0;
	public static final int STATE_SMALL = 1;
	private int state;

	public OnResizeLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public OnResizeLinearLayout(Context context) {
		super(context);
	}
	
	public int getState(){
		return state;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		if(listener != null){
			if(h > oldh) {
				listener.onResizeHigher();
			} else if(h < oldh) {
				listener.onResizeLower();
			}
		}
		if(h<oldh){
			state = STATE_SMALL;
		}else{
			state = STATE_NORMAL;
		}
		
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	private OnResizeListener listener;
	
	public void setOnResizeListener(OnResizeListener listener) {
		this.listener = listener;
	}

	public interface OnResizeListener {
		void onResizeHigher();
		void onResizeLower();
	}
}
