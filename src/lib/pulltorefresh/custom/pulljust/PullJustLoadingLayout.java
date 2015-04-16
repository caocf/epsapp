package lib.pulltorefresh.custom.pulljust;

import lib.pulltorefresh.PullToRefreshBase.Mode;
import lib.pulltorefresh.PullToRefreshBase.Orientation;
import lib.pulltorefresh.internal.LoadingLayout;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import com.epeisong.R;

public class PullJustLoadingLayout extends LoadingLayout {

	public PullJustLoadingLayout(Context context) {
		this(context, Mode.BOTH, Orientation.VERTICAL, null);
	}

	public PullJustLoadingLayout(Context context, Mode mode,
			Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);
		hideAllViews();
	}

	@Override
	protected int getDefaultDrawableResId() {
		return R.drawable.lib_pull_refresh_transparent;
	}

	@Override
	protected void onLoadingDrawableSet(Drawable imageDrawable) {

	}

	@Override
	protected void onPullImpl(float scaleOfLayout) {

	}

	@Override
	protected void pullToRefreshImpl() {

	}

	@Override
	protected void refreshingImpl() {
	}

	@Override
	protected void releaseToRefreshImpl() {
	}

	@Override
	protected void resetImpl() {
	}

}
