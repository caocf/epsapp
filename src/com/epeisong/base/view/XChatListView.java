package com.epeisong.base.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.epeisong.R;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.SystemUtils;

/**
 * 聊天界面的ListView
 * 
 * @author poet
 * 
 */
public class XChatListView extends PulldownableListView {

	public XChatListView(Context context) {
		this(context, null);
	}

	public XChatListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCacheColorHint(0x00000000);
		setSelector(new ColorDrawable(0x00000000));
		setDivider(null);
	}

	@Override
	protected View onCreateTopView() {
		return SystemUtils.inflate(R.layout.one_load_more_layout);
	}

	@Override
	protected int getPulldownBeyondHeight() {
		return (int) DimensionUtls.getPixelFromDp(20);
	}

	@Override
	protected void onStartRun() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onEndRun() {
		// TODO Auto-generated method stub

	}

}
