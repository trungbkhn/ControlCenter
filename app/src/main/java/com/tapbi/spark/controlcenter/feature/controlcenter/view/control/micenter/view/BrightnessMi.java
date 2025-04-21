package com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view;

import android.Manifest;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import timber.log.Timber;

public class BrightnessMi extends View {
    public float width;
    public float height;
    private Paint paintRect;
    private float valueX;
    private RectF rect;
    private float valueBrightness;
    private CallBackUpdateBg callBackUpdateBg = null;
    private ControlCenterIOSView.OnControlCenterListener onControlCenterListener;

    private int orientation = Configuration.ORIENTATION_PORTRAIT;
    private float oldDown;
    private float oldValueX;

    private boolean isTouching = false;
    private int maxBrightness = 255;
    private int spaceMove = MethodUtils.dpToPx(6);

    private int colorBackground = ContextCompat.getColor(getContext(), R.color.colorBackgroundContentWidget);


    private int colorProgress = Color.WHITE;

    public BrightnessMi(Context context) {
        super(context);
        init(null);
    }

    public BrightnessMi(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BrightnessMi(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public BrightnessMi(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public void setOrientation(int i) {
        this.orientation = i;
        invalidate();
    }

    public void setValueBrightnessMax(int maxBrightness) {
        this.maxBrightness = maxBrightness;
    }

    public void setCallBackUpdateBg(CallBackUpdateBg callBackUpdateBg) {
        this.callBackUpdateBg = callBackUpdateBg;
    }

    private void init(AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray array = getContext().obtainStyledAttributes(attributeSet,
                    R.styleable.BrightnessMi);
            orientation = array.getInteger(R.styleable.BrightnessMi_orientationView, 1);
        }

        setAnimation(null);

        paintRect = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paintRect.setAntiAlias(false);
        paintRect.setColor(colorProgress);
        paintRect.setStyle(Paint.Style.FILL);
        rect = new RectF(0, 0, getWidth(), getHeight());
        setBackgroundColor(colorBackground);
    }

    public void setColorBackground(int colorBackground) {
        this.colorBackground = colorBackground;
        setBackgroundColor(colorBackground);
    }

    public void setColorProgress(int colorProgress) {
        this.colorProgress = colorProgress;
        paintRect.setColor(colorProgress);
    }


    public void setOnControlCenterListener(ControlCenterIOSView.OnControlCenterListener onControlCenterListener) {
        this.onControlCenterListener = onControlCenterListener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        valueBrightness = SettingUtils.getValueBrightness(getContext());
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            valueX = ((valueBrightness / (float) maxBrightness) * (float) 100 / (float) 100) * width;
        } else {
            valueX = ((valueBrightness / (float) maxBrightness) * (float) 100 / (float) 100) * height;
        }
    }

    public void setValueProcess(float value) {
        valueX = value;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Timber.e(".");
        clearAnimation();

        if (callBackUpdateBg != null) {
            callBackUpdateBg.onChange();
        }
        rect.bottom = getHeight();
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            rect.right = (int) valueX;
        } else {
            rect.right = (int) width;
            rect.top = (int) ((int) height - valueX);
        }
        canvas.drawRect(rect, paintRect);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    oldDown = event.getX();
                } else {
                    oldDown = event.getY();
                }

                oldValueX = valueX > 0 ? valueX : 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(getContext())) {
                        SettingUtils.intentActivityRequestPermission(getContext(), new String[]{Manifest.permission.WRITE_SETTINGS});
                        onControlCenterListener.onExit();
                        return true;
                    }
                }
                if (callBackUpdateBg != null) {
                    callBackUpdateBg.onBrightnessDown();
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                Timber.e("hachung ACTION_MOVE:");
                isTouching = true;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {

                    if (Math.abs(oldDown - event.getX()) < spaceMove) {
                        break;
                    }
                    valueX = oldValueX + event.getX() - oldDown;//                    if (valueX <= 0 || valueX >= width) {
//                        break;
//                    }
                    valueBrightness = ((valueX / width) * (float) 100 / 100f) * (float) maxBrightness;
                } else {
                    if (Math.abs(oldDown - event.getY()) < spaceMove) {
                        break;
                    }
                    valueX = oldValueX - event.getY() + oldDown;
//                    if (valueX <= 0 || valueX >= height) {
//                        break;
//                    }
                    valueBrightness = (valueX / height * (float) 100 / 100f) * (float) maxBrightness;
                }
                if (valueBrightness > maxBrightness) {
                    valueBrightness = maxBrightness;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.System.canWrite(getContext())) {
                        SettingUtils.setValueBrightness(getContext(), (int) valueBrightness);
                    }
                } else {
                    SettingUtils.setValueBrightness(getContext(), (int) valueBrightness);
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Timber.e("hachung :");
                isTouching = false;
                if (callBackUpdateBg != null) {
                    callBackUpdateBg.onBrightnessUp();
                }
                break;
        }

        return true;
    }

    public boolean isTouching() {
        return isTouching;
    }


    public interface CallBackUpdateBg {
        void onChange();

        void onBrightnessDown();

        void onBrightnessUp();
    }
}
