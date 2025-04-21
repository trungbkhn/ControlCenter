package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.utils.DensityUtils;

import timber.log.Timber;

public class CustomSeekbarHorizontalView extends View {

    private LayerDrawable layerDrawable;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float progress = 0f;
    private int maxProgress = 100;
    private String colorBackground = "#30FFFFFF";
    private String colorProgress = "#FFFFFF";
    private String colorThumb = "#FFFFFF";
    private boolean isThumb = true;
    private float cornerProgress = 0.5f;
    private float currentProgress = 0;
    private OnCustomSeekbarHorizontalListener onCustomSeekbarHorizontalListener;
    private boolean isPermission = false;
    private boolean isTouch = true;
    private GestureDetector gestureDetector;
    private float positionEventDown = 0;
    private float SIZE_10 = DensityUtils.pxFromDp(App.mContext, 10);
    private boolean isRedraw = true;
    private boolean isStartTouch = false;
    private Long timeUp = System.currentTimeMillis();
    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isRedraw = true;
        }
    };


    public CustomSeekbarHorizontalView(Context context) {
        super(context);
        init();
    }

    public CustomSeekbarHorizontalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSeekbarHorizontalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnCustomSeekbarHorizontalListener(OnCustomSeekbarHorizontalListener onCustomSeekbarHorizontalListener) {
        this.onCustomSeekbarHorizontalListener = onCustomSeekbarHorizontalListener;
    }

    public void changeIsPermission(boolean isPermission) {
        this.isPermission = isPermission;
    }

    private void init() {
        handler = new Handler();
        paint.setShadowLayer(5f, -2f, 0f, Color.parseColor("#30000000"));
        paint.setColor(Color.parseColor(colorThumb));
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                if (onCustomSeekbarHorizontalListener != null) {
                    onCustomSeekbarHorizontalListener.onLongPress(CustomSeekbarHorizontalView.this);
                }
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layerDrawable = createLayerDrawable();
        layerDrawable.setBounds(0, 0, getWidth(), getHeight());

        setProgress(progress);

        invalidate();

        Timber.e("NVQ invalidate123+++++ // ");
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        layerDrawable.draw(canvas);
        Timber.e("Duongcv " + isThumb + ":" + currentProgress);
        if (isThumb) {
            paint.setShadowLayer(5f, -2f, 0f, Color.parseColor("#30000000"));
            paint.setColor(Color.parseColor(colorThumb));
            canvas.drawCircle(progress * getWidth(), getHeight() / 2f, getHeight() / 2f, paint);
        }
        Timber.e("NVQ invalidate123+++++ // ");
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE -> {
                if (isPermission && (Math.abs(event.getX() - positionEventDown) > SIZE_10)) {
                    if (isThumb) {
                        float position = Math.min(event.getX(), getWidth() - getHeight() / 2f);
                        position = Math.max(position, getHeight() / 2f);
                        progress = position / getWidth();
                        currentProgress = (position - getHeight() / 2f) / (getWidth() - getHeight());

                    } else {
                        progress = event.getX() / getWidth();
                        currentProgress = progress;
                    }

                    isRedraw = true;
                    setProgress(progress);
                    if (onCustomSeekbarHorizontalListener != null) {
                        onCustomSeekbarHorizontalListener.onProgressChanged(this, currentProgress);
                    }
//                    invalidate();
                }
                break;
            }
            case MotionEvent.ACTION_DOWN -> {
                positionEventDown = event.getX();
                isStartTouch = true;
                if (onCustomSeekbarHorizontalListener != null) {
                    onCustomSeekbarHorizontalListener.onStartTrackingTouch(this);
                }
                break;
            }

            case MotionEvent.ACTION_UP -> {
                delayRedraw();
                if (onCustomSeekbarHorizontalListener != null) {
                    onCustomSeekbarHorizontalListener.onStopTrackingTouch(this);
                }
            }

            case MotionEvent.ACTION_CANCEL -> {
                delayRedraw();
            }


        }
        return isTouch;
    }

    private void delayRedraw() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                isStartTouch = false;
            }
        }, 50);
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        super.setOnLongClickListener(l);
        if (onCustomSeekbarHorizontalListener != null) {
            onCustomSeekbarHorizontalListener.onLongPress(CustomSeekbarHorizontalView.this);
        }
    }

    public void changeIsTouch(boolean isTouch) {
        this.isTouch = isTouch;
    }


    public void changeColor(String colorBackground, String colorProgress, String colorThumb, float cornerProgress) {
        this.colorBackground = colorBackground;
        this.colorProgress = colorProgress;
        this.colorThumb = colorThumb;
        this.cornerProgress = cornerProgress;
        int color = Color.parseColor(colorThumb);
        int alpha = Color.alpha(color);
        isThumb = alpha != 0;

        invalidate();
    }

    public void setCurrentProgress(float currentProgress) {
        if (!isStartTouch) {
            this.currentProgress = Math.min(currentProgress, 1);
            this.currentProgress = Math.max(this.currentProgress, 0);
            if (isThumb) {
                progress = (currentProgress * (getWidth() - getHeight()) + getHeight() / 2f) / getWidth();
            } else {
                progress = currentProgress;
            }
            setProgress(progress);

        }
    }


    private void setProgress(float progress) {
        if (layerDrawable == null) {
            layerDrawable = createLayerDrawable();
        }
//        if (isRedraw) {
//            if (isStartTouch) {
//                isRedraw = false;
//                handler.removeCallbacks(runnable);
//                handler.postDelayed(runnable, 100);
//            }
        layerDrawable.setLevel((int) (progress * 10000));
//        }
        invalidate();
    }

    private LayerDrawable createLayerDrawable() {
        // Tạo Drawable cho nền (background)
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(Color.parseColor(colorBackground));
        backgroundDrawable.setCornerRadius(getHeight() * cornerProgress);

        // Tạo Drawable cho progress
        GradientDrawable progressDrawable = new GradientDrawable();
        progressDrawable.setColor(Color.parseColor(colorProgress));
        progressDrawable.setCornerRadius(getHeight() * cornerProgress);

        // Bọc progressDrawable trong ClipDrawable để tạo hiệu ứng clipping
        ClipDrawable clipDrawable = new ClipDrawable(progressDrawable, Gravity.START, ClipDrawable.HORIZONTAL);

        // Tạo LayerDrawable và thêm các item
        Drawable[] layers = new Drawable[2];
        layers[0] = backgroundDrawable; // Nền
        layers[1] = clipDrawable;       // Progress

        // Tạo LayerDrawable từ mảng Drawable
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        // Gán ID cho từng lớp, tương tự như trong file XML
        layerDrawable.setId(0, android.R.id.background);
        layerDrawable.setId(1, android.R.id.progress);

        return layerDrawable;
    }

    public interface OnCustomSeekbarHorizontalListener {
        void onStartTrackingTouch(CustomSeekbarHorizontalView horizontalSeekBar);

        void onProgressChanged(CustomSeekbarHorizontalView horizontalSeekBar, float i);

        void onStopTrackingTouch(CustomSeekbarHorizontalView horizontalSeekBar);

        void onLongPress(CustomSeekbarHorizontalView horizontalSeekBar);
    }
}
