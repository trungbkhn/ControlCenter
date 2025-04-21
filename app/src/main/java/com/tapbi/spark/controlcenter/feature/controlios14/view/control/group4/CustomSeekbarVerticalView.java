package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.utils.DensityUtils;

import timber.log.Timber;

public class CustomSeekbarVerticalView extends View {

    private LayerDrawable layerDrawable;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float progress = 0.3f;
    private float minProgress = 0f;
    private String colorBackground = "#30FFFFFF";
    private String colorProgress = "#FFFFFF";
    private String colorThumb = "#FFFFFF";
    private boolean isThumb = true;
    private float cornerProgress = 0.5f;
    private float currentProgress = 0;
    private OnCustomSeekbarVerticalListener onCustomSeekbarVerticalListener;
    private boolean isPermission = false;
    private float positionEventDown = 0;
    private float SIZE_10 = DensityUtils.pxFromDp(App.mContext, 10);

    public CustomSeekbarVerticalView(Context context) {
        super(context);
        init();
    }

    public CustomSeekbarVerticalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSeekbarVerticalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setCustomSeekbarVerticalListener(OnCustomSeekbarVerticalListener onCustomSeekbarVerticalListener) {
        this.onCustomSeekbarVerticalListener = onCustomSeekbarVerticalListener;
    }
    private GestureDetector gestureDetector;

    public void changeIsPermission(boolean isPermission) {
        this.isPermission = isPermission;
    }

    private void init() {
        paint.setShadowLayer(5f, -2f, 0f, Color.parseColor("#30000000"));
        paint.setColor(Color.parseColor(colorThumb));

        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                if (onCustomSeekbarVerticalListener != null) {
                    onCustomSeekbarVerticalListener.onLongPress(CustomSeekbarVerticalView.this);
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        layerDrawable.draw(canvas);
        Timber.e("Duongcv " + isThumb + ":" + currentProgress);
        if (isThumb) {
            paint.setShadowLayer(5f, 0f, 2f, Color.parseColor("#30000000"));
            paint.setColor(Color.parseColor(colorThumb));
            canvas.drawCircle(getWidth()/ 2f, (1 - progress) * getHeight(), getWidth() / 2f, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE -> {
                if (isPermission && (Math.abs(event.getY() - positionEventDown) > SIZE_10)) {
                    positionEventDown = event.getY();
                    if (isThumb) {
                        float position = Math.min(event.getY(), getHeight() - getWidth() / 2f);
                        position = Math.max(position, getWidth() / 2f);
//                        progress = 1 - (position / getHeight());
                        currentProgress = 1 - ( (position - getWidth() / 2f) / (getHeight() - getWidth()));
                        Log.d("duongcv", "onTouchEvent: " + currentProgress);
                    } else {
//                        progress = 1 - ( event.getY() / getHeight());
//                        currentProgress = progress;
                        currentProgress = 1 - (event.getY() /getHeight());
                    }
//                    setProgress(progress);
                    setCurrentProgress(currentProgress);
                    if (onCustomSeekbarVerticalListener != null) {
                        onCustomSeekbarVerticalListener.onProgressChanged(this, currentProgress);
                    }
                    invalidate();
                }
                break;
            }
            case MotionEvent.ACTION_DOWN -> {
                positionEventDown = event.getY();
                if (onCustomSeekbarVerticalListener != null) {
                    onCustomSeekbarVerticalListener.onStartTrackingTouch(this);
                }
                break;
            }

            case MotionEvent.ACTION_UP -> {
                if (onCustomSeekbarVerticalListener != null) {
                    onCustomSeekbarVerticalListener.onStopTrackingTouch(this);
                }
            }

        }
        return true;
    }

    protected void setMinProgress(float minProgress){
        this.minProgress = minProgress;
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        super.setOnLongClickListener(l);
        if (onCustomSeekbarVerticalListener != null) {
            onCustomSeekbarVerticalListener.onLongPress(this);
        }
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
        this.currentProgress = Math.min(currentProgress, 1);
        this.currentProgress = Math.max(this.currentProgress, minProgress);
        if (isThumb) {
            progress = (this.currentProgress * (getHeight() - getWidth()) + getWidth() / 2f) / getHeight();
        } else {
            progress = this.currentProgress;
        }
        setProgress(progress);
    }

    public float getCurrentProgress(){
        return  currentProgress;
    }

    private void setProgress(float progress) {
        if (layerDrawable == null) {
            layerDrawable = createLayerDrawable();
        }
        layerDrawable.setLevel((int) (( progress) * 10000));
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
        ClipDrawable clipDrawable = new ClipDrawable(progressDrawable, Gravity.BOTTOM, ClipDrawable.VERTICAL);

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

    public interface OnCustomSeekbarVerticalListener {
        void onStartTrackingTouch(CustomSeekbarVerticalView horizontalSeekBar);

        void onProgressChanged(CustomSeekbarVerticalView horizontalSeekBar, float i);

        void onStopTrackingTouch(CustomSeekbarVerticalView horizontalSeekBar);

        void onLongPress(CustomSeekbarVerticalView horizontalSeekBar);
    }
}
