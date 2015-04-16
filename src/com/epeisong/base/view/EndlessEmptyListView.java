package com.epeisong.base.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.epeisong.base.adapter.EndlessAdapter;

@Deprecated
public class EndlessEmptyListView extends ListView {

    public EndlessEmptyListView(Context context) {
        this(context, null);
    }

    public EndlessEmptyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // @Override
    // public ListAdapter getAdapter() {
    // ListAdapter adapter = super.getAdapter();
    // if (adapter != null && adapter instanceof EndlessAdapter) {
    // return ((EndlessAdapter) adapter).getWrappedAdapter();
    // }
    // return adapter;
    // }

}
