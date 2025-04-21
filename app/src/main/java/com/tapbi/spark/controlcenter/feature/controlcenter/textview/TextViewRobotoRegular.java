package com.tapbi.spark.controlcenter.feature.controlcenter.textview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class TextViewRobotoRegular extends TextView {
    public TextViewRobotoRegular(Context context) {
        super(context);
        init(context);
    }

    public TextViewRobotoRegular(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextViewRobotoRegular(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setSingleLine(true);
        setMarqueeRepeatLimit(-1);
        setSelected(true);
    }

}
