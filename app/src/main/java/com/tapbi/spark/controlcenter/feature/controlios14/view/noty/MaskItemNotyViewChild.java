package com.tapbi.spark.controlcenter.feature.controlios14.view.noty;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MaskItemNotyViewChild extends ConstraintLayout {

    private Context context;

    private Path path;
    private int radius;
    private int widthLayoutSwipe;
    private int x = 0;
    private int heightLayoutMore = 0;

    public MaskItemNotyViewChild(Context context) {
        super(context);
        init(context, null);
    }

    public MaskItemNotyViewChild(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaskItemNotyViewChild(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray array = context.obtainStyledAttributes(attributeSet,
            R.styleable.MaskItemNotyViewChild);
        if ( attributeSet == null){
                    radius = MethodUtils.dp2px(context, 15);
        }else {
            radius = (int) array.getDimension(R.styleable.MaskItemNotyViewChild_radiusBoder,
                15f);

        }

        this.context = context;
        path = new Path();

        post(() -> {
            path.addRoundRect(new RectF(
                            0,
                            0,
                            getWidth(),
                            getHeight() - heightLayoutMore),
                    radius, radius,
                    Path.Direction.CW);

            path.addRoundRect(new RectF(
                            getWidth(),
                            0,
                            getWidth() + widthLayoutSwipe,
                            getHeight() - heightLayoutMore),
                    radius, radius,
                    Path.Direction.CW);
        });
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
    }

    public void setCard(View card1, View card2) {
    }

    public void setWidthLayout(int widthLayoutSwipe) {
        this.widthLayoutSwipe = widthLayoutSwipe;
    }

    public void update() {
        post(new Runnable() {
            @Override
            public void run() {
                setTranslationX(x);
            }
        });

    }

}
