package com.tapbi.spark.controlcenter.feature.edge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground;
import com.tapbi.spark.controlcenter.interfaces.OnTouchViewListener;

public class BaseTouchView extends View {

    private final PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    private final Handler handleTouch = new Handler(Looper.getMainLooper());
    //draw
    public Paint paintColor;
    public Paint paintPath;
    public Paint paintBg;
    public Path pathColor;
    public Path pathBg;
    public float line;
    public float sizeColor;
    public int width;
    public int height;
    public float wColor; // width color
    public float hColor; // height color
    public float rColor; // radius color
    public float wBg; // width background
    public int hBg; // height background
    public float rBg; // radius background
    //show edit when custom edge, in SettingTouchFragment
    public boolean isShowEdit = false;
    public boolean isTouchDown = false;
    private final Runnable runnableTouch = () -> {
        isTouchDown = false;
    };
    int color;
    private OnTouchViewListener onTouchViewListener;
    private boolean isTouchNoty;
    private int typeEdge;

    public BaseTouchView(Context context) {
        super(context);
        init();
    }

    public BaseTouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseTouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnTouch(OnTouchViewListener onTouchViewListener, boolean isTouchNoty, int typeEdge) {
        this.onTouchViewListener = onTouchViewListener;
        this.isTouchNoty = isTouchNoty;
        this.typeEdge = typeEdge;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        height = getHeight();
        wBg = width;
        hBg = height;
        if (typeEdge == Constant.EDGE_LEFT || typeEdge == Constant.EDGE_RIGHT) {
            wColor = sizeColor;
            hColor = height;
            rColor = sizeColor;
            rBg = Math.min(wBg, hBg / 2f);
        } else {
            wColor = width;
            hColor = sizeColor;
            rColor = sizeColor;
            rBg = Math.min(wBg / 2, hBg);
        }

    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        sizeColor = getResources().getDimension(R.dimen.size_color_edge);
        line = getResources().getDimension(R.dimen.size_stroke_edge);

        paintColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintColor.setStyle(Paint.Style.FILL_AND_STROKE);
        paintColor.setColor(ContextCompat.getColor(getContext(), R.color.transparent));
        paintColor.setStrokeWidth(line);

        paintPath = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPath.setStyle(Paint.Style.STROKE);
        paintPath.setColor(ContextCompat.getColor(getContext(), R.color.color_stroke_edge));
        paintPath.setStrokeWidth(line);

        paintBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBg.setStyle(Paint.Style.FILL_AND_STROKE);
        paintBg.setColor(ContextCompat.getColor(getContext(), R.color.color_bg_touch_edge));
        paintBg.setStrokeWidth(line);

        pathColor = new Path();
        pathBg = new Path();
    }

    public void setColorEdge(int colorEdge) {
        color = colorEdge;
        paintColor.setColor(colorEdge);
        invalidate();
    }

    //in SettingTouchFragment
    public void setShowEdit(boolean isShow) {
        this.isShowEdit = isShow;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!BlurBackground.getInstance().backgroundLoaded()) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                countTimeTouch();
                if (onTouchViewListener != null) {
                    onTouchViewListener.onDown(isTouchNoty, typeEdge, event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (onTouchViewListener != null) {
                    onTouchViewListener.onMove(isTouchNoty, typeEdge, event);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (onTouchViewListener != null) {
                    onTouchViewListener.onUp(isTouchNoty, typeEdge, event);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (isShowEdit) {
            //draw background in SettingTouchFragment
            paintBg.setXfermode(null);
            canvas.drawPath(pathBg, paintBg);
            paintBg.setXfermode(porterDuffXfermode);
            canvas.drawPath(pathColor, paintBg);
        }

        //always draw color edge
        canvas.drawPath(pathColor, paintColor);

        if (isShowEdit) {
            //draw border edge in SettingTouchFragment
            canvas.drawPath(pathColor, paintPath);
        }
    }

    private void countTimeTouch() {
        isTouchDown = true;
        handleTouch.removeCallbacksAndMessages(null);
        handleTouch.postDelayed(runnableTouch, 500);
    }

    public boolean isTouchDown() {
        return isTouchDown;
    }
}
