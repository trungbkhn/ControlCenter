package com.tapbi.spark.controlcenter.utils.helper.rcvhepler;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import me.everything.android.ui.overscroll.IOverScrollDecor;

public class RecyclerViewNoty extends RecyclerView {

    private IOverScrollDecor decor;
    private NpaLinearLayoutManager layoutManager;
    private boolean isOverScroll = true;

    public RecyclerViewNoty(@NonNull Context context) {
        super(context);
        init(context);
    }

    public RecyclerViewNoty(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecyclerViewNoty(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        layoutManager = new NpaLinearLayoutManager(context);
        setLayoutManager(layoutManager);
        setHasFixedSize(false);
//        decor = OverScrollDecoratorHelper.setUpOverScroll(this, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }


    //use this function to fix conflicts between recycler(use OverScroll) and view pager
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
////        if (b < App.widthHeightScreen.w) {
////            if (isOverScroll) {
////                isOverScroll = false;
////                decor.detach();
////            }
////        } else {
////            if (!isOverScroll) {
////                isOverScroll = true;
////                decor.attachView();
////            }
////        }
//    }

    public void scrollToTop() {
        layoutManager.scrollToPosition(0);
    }

}
