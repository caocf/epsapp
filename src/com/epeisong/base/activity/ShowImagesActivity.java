package com.epeisong.base.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.epeisong.R;
import com.epeisong.base.view.TitleParams;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ShapeUtils;
import com.epeisong.utils.ShapeUtils.ShapeParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 显示图片
 * @author poet
 *
 */
public class ShowImagesActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {

    public static final String EXTRA_URL_LIST = "url_list";
    public static final String EXTRA_CUR_POS = "cur_pos";

    private TextView mTextView;
    private ViewPager mViewPager;
    private DisplayImageOptions mDisplayImageOptions;

    private ArrayList<String> mUrls = new ArrayList<String>();
    private int mCurPos;

    @SuppressWarnings({ "unchecked", "deprecation" })
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ArrayList<String> urls = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_URL_LIST);
        mCurPos = getIntent().getIntExtra(EXTRA_CUR_POS, 0);
        if (urls != null && urls.size() > 0) {
            mUrls.addAll(urls);
        }
        super.onCreate(savedInstanceState);
        mDisplayImageOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_launcher)
                .showImageOnFail(R.drawable.ic_launcher).resetViewBeforeLoading(true).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        mViewPager = new ViewPager(this);
        mViewPager.setBackgroundColor(Color.BLACK);
        mViewPager.setAdapter(new MyAdapter());
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setCurrentItem(mCurPos);
        setContentView(mViewPager);

        mTextView = new TextView(this);
        mTextView.setTextColor(Color.WHITE);
        int p = DimensionUtls.getPixelFromDpInt(30);
        mTextView.setPadding(p, 0, p, 0);
        mTextView.setBackgroundDrawable(ShapeUtils.getShape(new ShapeParams().setStrokeWidth(0)
                .setBgColor(Color.argb(0x88, 0x00, 0x00, 0x00)).setCorner(10)));
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        mTextView.setText((mCurPos + 1) + "/" + mUrls.size());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER_HORIZONTAL
                | Gravity.BOTTOM);
        params.bottomMargin = DimensionUtls.getPixelFromDpInt(15);
        addContentView(mTextView, params);
    }

    @Override
    protected TitleParams getTitleParams() {
        return null;
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public void onPageSelected(int position) {
        if (mTextView != null) {
            mTextView.setText((position + 1) + "/" + mUrls.size());
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = createPageItem(mUrls.get(position));
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private View createPageItem(String url) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
        params.gravity = Gravity.CENTER;
        FrameLayout fl = new FrameLayout(this);
        int p = DimensionUtls.getPixelFromDpInt(1);
        fl.setPadding(p, p, p, p);
        fl.setOnClickListener(this);
        ImageView iv = new ImageView(this);
        fl.addView(iv, params);
        final ProgressBar bar = new ProgressBar(this);
        bar.setVisibility(View.GONE);
        fl.addView(bar, params);
        ImageLoader.getInstance().displayImage(url, iv, mDisplayImageOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                bar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                bar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                bar.setVisibility(View.GONE);
            }
        });
        return fl;
    }

    public static void launch(Context context, ArrayList<String> urls, int curPos) {
        Intent intent = new Intent(context, ShowImagesActivity.class);
        intent.putExtra(EXTRA_URL_LIST, urls);
        intent.putExtra(EXTRA_CUR_POS, curPos);
        context.startActivity(intent);
    }
}
