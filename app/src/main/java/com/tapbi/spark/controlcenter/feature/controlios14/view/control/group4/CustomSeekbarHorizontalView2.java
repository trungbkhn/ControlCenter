package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.utils.DensityUtils;

import kotlinx.coroutines.Dispatchers;
import okhttp3.Dispatcher;
import timber.log.Timber;

public class CustomSeekbarHorizontalView2 extends View {

    private LayerDrawable layerDrawable;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float progress = 0.3f;
    private int maxProgress = 100;
    private String colorBackground = "#30FFFFFF";
    private String colorProgress = "#FFFFFF";
    private String colorThumb = "#FFFFFF";
    private boolean isThumb = true;
    private float cornerProgress = 0.5f;
    private float currentProgress = 0;
    private OnCustomSeekbarHorizontalListener onCustomSeekbarHorizontalListener;
    private boolean isPermission = false;
    private Bitmap bitmapIcon = null;
    private ColorFilter filter = null;
    private GestureDetector gestureDetector;
    private float positionEventDown = 0;
    private boolean isStartTouch = false;
    private float SIZE_10 = DensityUtils.pxFromDp(App.mContext, 10);
    private Long timeUp = 0L;

    public CustomSeekbarHorizontalView2(Context context) {
        super(context);
        init();
    }

    public CustomSeekbarHorizontalView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomSeekbarHorizontalView2(Context context, AttributeSet attrs, int defStyleAttr) {
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
        paint.setColor(Color.parseColor(colorThumb));
        bitmapIcon = ((BitmapDrawable) getContext().getResources().getDrawable(R.drawable.thum_seekbar_oppo)).getBitmap();
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                if (onCustomSeekbarHorizontalListener != null) {
                    onCustomSeekbarHorizontalListener.onLongPress(CustomSeekbarHorizontalView2.this);
                }
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void changeImage(int drawable, int colorIcon){
        Drawable drawColor = getContext().getResources().getDrawable(drawable);
        filter = new LightingColorFilter(colorIcon, 0);
        bitmapIcon = ((BitmapDrawable) drawColor).getBitmap();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layerDrawable = createLayerDrawable();
        layerDrawable.setBounds(0, 0, getWidth(), getHeight());
        Timber.e("Duongcv " + progress);
        setProgress(progress);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        layerDrawable.draw(canvas);
        Timber.e("Duongcv " + isThumb + ":" + currentProgress + ":"+ progress);
        Timber.e("hachung progress:"+progress+"/currentProgress: "+currentProgress);
        if (isThumb) {

            canvas.drawCircle(progress * getWidth(), getHeight() / 2f, getHeight() / 2f, paint);
            if (bitmapIcon != null) {

                paint.setColorFilter(filter);
                canvas.drawBitmap(bitmapIcon,(progress * getWidth() - bitmapIcon.getHeight()/2f), (getHeight() - bitmapIcon.getHeight())/2f , paint);
                paint.setColorFilter(null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE -> {
                if (isPermission && (Math.abs(event.getX() - positionEventDown) > SIZE_10)) {
                    float oldProgress = progress;
                    if (isThumb) {
                        float position = Math.min(event.getX(), getWidth() - getHeight() / 2f);
                        position = Math.max(position, getHeight() / 2f);
                        progress = position / getWidth();
                        currentProgress = (position - getHeight() / 2f) / (getWidth() - getHeight());

                    } else {
                        progress = event.getX() / getWidth();
                        currentProgress = progress;
                    }
                    if (oldProgress != progress) {
                        setProgress(progress);
                        if (onCustomSeekbarHorizontalListener != null) {
                            onCustomSeekbarHorizontalListener.onProgressChanged(this, currentProgress);
                        }
                        Timber.e("hachung onTouchEvent:"+progress);
                        invalidate();
                    }
                }
                break;
            }
            case MotionEvent.ACTION_DOWN -> {
                isStartTouch = true;
                positionEventDown = event.getX();
                if (onCustomSeekbarHorizontalListener != null) {
                    onCustomSeekbarHorizontalListener.onStartTrackingTouch(this);
                }
                break;
            }

            case MotionEvent.ACTION_UP -> {
                if (onCustomSeekbarHorizontalListener != null) {
                    onCustomSeekbarHorizontalListener.onStopTrackingTouch(this);
                }
                delayRedraw();
            }

            case MotionEvent.ACTION_CANCEL -> {
               delayRedraw();
            }

        }
        return true;
    }

    private void delayRedraw(){
        isStartTouch = false;
        timeUp = System.currentTimeMillis();
    }


    public void changeColor(String colorBackground, String colorProgress, String colorThumb, float cornerProgress) {
        this.colorBackground = colorBackground;
        this.colorProgress = colorProgress;
        this.colorThumb = colorThumb;
        this.cornerProgress = cornerProgress;
        int color = Color.parseColor(colorThumb);
        int alpha = Color.alpha(color);
        isThumb = alpha != 0;
        paint.setColor(Color.parseColor(colorThumb));
        invalidate();
    }

    public void setCurrentProgress(float currentProgress) {
        Log.d("duongcv", "setCurrentProgress: "+ isStartTouch);
        if (!isStartTouch  && System.currentTimeMillis() - timeUp > 1000) {
            this.currentProgress = Math.min(currentProgress, 1);
            this.currentProgress = Math.max(this.currentProgress, 0);
            if (isThumb) {
                progress = (this.currentProgress * (getWidth() - getHeight()) + getHeight() / 2f) / getWidth();
            } else {
                progress = this.currentProgress;
            }
            setProgress(progress);
        }

    }

    public void setProgress(float progress) {
        if (layerDrawable == null) {
            layerDrawable = createLayerDrawable();
        }
        layerDrawable.setLevel((int) (progress * 10000));
        invalidate();
    }

    private LayerDrawable createLayerDrawable() {
        // Tạo Drawable cho nền (background)
        int heightBg = (int) (getHeight() * 0.14);
        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(Color.parseColor(colorBackground));
        backgroundDrawable.setCornerRadius(heightBg /2f);

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

        layerDrawable.setLayerInset(0, 0, (getHeight() - heightBg) / 2, 0, (getHeight() - heightBg) / 2); // Đặt chiều cao cho background

        layerDrawable.setId(1, android.R.id.progress);

        return layerDrawable;
    }

    public interface OnCustomSeekbarHorizontalListener {
        void onStartTrackingTouch(CustomSeekbarHorizontalView2 horizontalSeekBar);

        void onProgressChanged(CustomSeekbarHorizontalView2 horizontalSeekBar, float i);

        void onStopTrackingTouch(CustomSeekbarHorizontalView2 horizontalSeekBar);

        void onLongPress(CustomSeekbarHorizontalView2 horizontalSeekBar);
    }
}