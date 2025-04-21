package com.tapbi.spark.controlcenter.utils.helper.rcvhepler;


import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.utils.DensityUtils;

public class CenterRcv {
    private static final float MILLISECONDS_PER_INCH = 70f;
    private final static int DIMENSION = 2;
    private final static int HORIZONTAL = 0;
    private final static int VERTICAL = 1;

    public static void setMarginItem(int sizeList, int pos, View itemView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        if (pos == 0) {
            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            params.setMargins((int) marginLeftRight( itemView.getMeasuredWidth()), 0, 0, 0);
        } else if (pos == sizeList - 1) {
            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            params.setMargins(0, 0, (int) marginLeftRight( itemView.getMeasuredWidth()), 0);
        } else {
            params.setMargins(0, 0, 0, 0);
        }
        itemView.setLayoutParams(params);
    }

    public static void setMarginItemWithReverseLayout(int sizeList, int pos, View itemView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        if (pos == 0) {
            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            params.setMargins(0, 0, (int) marginLeftRight(itemView.getMeasuredWidth()), 0);
        } else if (pos == sizeList - 1) {
            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            params.setMargins((int) marginLeftRight(itemView.getMeasuredWidth()), 0, 0, 0);
        } else {
            params.setMargins(0, 0, 0, 0);
        }
        itemView.setLayoutParams(params);
    }

    private static float marginLeftRight(float widthItem) {
        return (DensityUtils.getScreenWidth() - widthItem) / 2;
//        return (App.WIDTH_SCREEN - context.getResources().getDisplayMetrics().density * 40) / 2;
    }

    public void scrollToCenter(LinearLayoutManager layoutManager, RecyclerView recyclerList, int clickPosition) {
        RecyclerView.SmoothScroller smoothScroller = createSnapScroller(recyclerList, layoutManager);
        if (smoothScroller != null) {
            smoothScroller.setTargetPosition(clickPosition);
            layoutManager.startSmoothScroll(smoothScroller);
        }
    }

    @Nullable
    private LinearSmoothScroller createSnapScroller(RecyclerView mRecyclerView, final RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        return new LinearSmoothScroller(mRecyclerView.getContext()) {
            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                int[] snapDistances = calculateDistanceToFinalSnap(layoutManager, targetView);
                final int dx = snapDistances[HORIZONTAL];
                final int dy = snapDistances[VERTICAL];


                int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
                if (time > 0) {
                    if (time > 300)
                        time = 300;
                    else if (time < 200)
                        time = 200;

                    action.update(dx, dy, time, mDecelerateInterpolator);
                }


                //action.update(dx, dy, 200, mDecelerateInterpolator);
            }


            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };
    }

    private int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[DIMENSION];
        if (layoutManager.canScrollHorizontally()) {
            out[HORIZONTAL] = distanceToCenter(layoutManager, targetView,
                    OrientationHelper.createHorizontalHelper(layoutManager));
        }

        if (layoutManager.canScrollVertically()) {
            out[VERTICAL] = distanceToCenter(layoutManager, targetView,
                    OrientationHelper.createHorizontalHelper(layoutManager));
        }
        return out;
    }

    private int distanceToCenter(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView, OrientationHelper helper) {
        final int childCenter = helper.getDecoratedStart(targetView)
                + (helper.getDecoratedMeasurement(targetView) / 2);
        final int containerCenter;
        if (layoutManager.getClipToPadding()) {
            containerCenter = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
        } else {
            containerCenter = helper.getEnd() / 2;
        }
        return childCenter - containerCenter;
    }

}

