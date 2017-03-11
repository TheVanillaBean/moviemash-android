package com.example.appdaddy.moviemash.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.example.appdaddy.moviemash.util.RecyclerUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 2/18/2017.
 */

public class CustomRecyclerView extends RecyclerView{

    private List<View> mNonEmptyViews = Collections.emptyList();
    private List<View> mEmptyViews = Collections.emptyList();
    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            toggleViews();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            toggleViews();
        }
    };

    private void toggleViews() {
        Log.d("TAG", "toggle called");

        if (getAdapter() != null && !mEmptyViews.isEmpty() ) {
            if (getAdapter().getItemCount() == 0) {
                Log.d("TAG", "toggle called1");

                //show all the empty views
                RecyclerUtil.showViews(mEmptyViews);
                //hide the RecyclerView
                setVisibility(View.GONE);

                //hide all the views which are meant to be hidden
            } else {
                Log.d("TAG", "toggle called2");

                //hide all the empty views

                //show the RecyclerView
                setVisibility(View.VISIBLE);

                //hide all the views which are meant to be hidden
                RecyclerUtil.hideViews(mEmptyViews);
            }
        }
    }

    public CustomRecyclerView(Context context) {
        super(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }
        mObserver.onChanged();
    }

    public void hideIfEmpty(View... views) {
        mNonEmptyViews = Arrays.asList(views);
    }

    public void showIfEmpty(View... emptyViews) {
        mEmptyViews = Arrays.asList(emptyViews);
    }

}
