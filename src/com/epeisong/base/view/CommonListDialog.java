package com.epeisong.base.view;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.epeisong.EpsApplication;
import com.epeisong.base.adapter.HoldDataBaseAdapter;
import com.epeisong.utils.DimensionUtls;
import com.epeisong.utils.ShapeUtils;
import com.epeisong.utils.ShapeUtils.ShapeParams;

/**
 * 通用ListDialog
 * @author poet
 *
 */
public abstract class CommonListDialog<T> extends AlertDialog implements OnItemClickListener {

    protected ListView mListView;
    protected CommonAdapter mCommonAdapter;

    protected CommonListDialog(Context context) {
        super(context);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListView = new ListView(getContext());
        setContentView(mListView);
        mListView.setAdapter(mCommonAdapter = new CommonAdapter());
        mListView.setOnItemClickListener(this);
        mListView.setBackgroundDrawable(ShapeUtils.getShape(new ShapeParams().setBgColor(Color.WHITE).setCorner(5)));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = (int) (EpsApplication.getScreenWidth() - DimensionUtls.getPixelFromDp(60));
        getWindow().setAttributes(params);
    }

    protected abstract CommonViewHolder<T> onCreateViewHolder();

    public void showAndSetData(List<T> data) {
        show();
        mCommonAdapter.replaceAll(data);
    }

    public class CommonAdapter extends HoldDataBaseAdapter<T> {

        @SuppressWarnings("unchecked")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CommonViewHolder<T> holder = null;
            if (convertView == null) {
                holder = onCreateViewHolder();
                convertView = holder.onCreateView(getContext());
                convertView.setTag(holder);
            } else {
                holder = (CommonViewHolder<T>) convertView.getTag();
            }
            holder.onFillData(getItem(position));
            return convertView;
        }

    }

    public interface CommonViewHolder<T> {
        View onCreateView(Context context);

        void onFillData(T t);
    }
}
