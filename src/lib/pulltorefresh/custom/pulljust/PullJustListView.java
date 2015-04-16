package lib.pulltorefresh.custom.pulljust;

import lib.pulltorefresh.PullToRefreshListView;
import lib.pulltorefresh.internal.LoadingLayout;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

public class PullJustListView extends PullToRefreshListView {

	public PullJustListView(Context context) {
		this(context, null);
	}

	public PullJustListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected LoadingLayout createLoadingLayout(Context context, Mode mode,
			TypedArray attrs) {
		LoadingLayout layout = new PullJustLoadingLayout(context);
		layout.setVisibility(View.INVISIBLE);
		return layout;
	}

}
