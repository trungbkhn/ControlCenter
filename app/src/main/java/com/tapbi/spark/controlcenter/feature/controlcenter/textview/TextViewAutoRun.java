package com.tapbi.spark.controlcenter.feature.controlcenter.textview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;

@SuppressLint("AppCompatCustomView")
public class TextViewAutoRun extends TextView {
    public TextViewAutoRun(Context context) {
        super(context);
        init(context, null);
    }

    public TextViewAutoRun(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TextViewAutoRun(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private int color = Color.WHITE;

    public void setNewColor(int color) {
        this.color = color;
        invalidate();
    }

    private void init(Context context, AttributeSet attrs) {
        if (isHardwareAccelerated()) {
            setLayerType(LAYER_TYPE_HARDWARE, null);
        }
        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TextViewAutoRunRobotoRegular, 0, 0);
            color = typedArray.getColor(0, Color.WHITE);
        }
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        setTextColor(color);
        setGravity(Gravity.CENTER);
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setMarqueeRepeatLimit(-1);
        setHorizontallyScrolling(true);
        setSelected(true);
    }

}
