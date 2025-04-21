package com.tapbi.spark.controlcenter.feature.edge;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TouchLeft extends BaseTouchView {

    public TouchLeft(Context context) {
        super(context);
    }

    public TouchLeft(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchLeft(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        pathColor.reset();
        // Move to the top-left corner
        pathColor.moveTo(0, line / 2f);

        // Vẽ cong ở phía trên bên phải
        pathColor.arcTo(wColor - 2 * rColor - line / 2f, line / 2f, wColor - line / 2f, 2 * rColor + line / 2f, -90, 90, false);

        // Vẽ cong ở phía dưới bên phải
        pathColor.arcTo(wColor - 2 * rColor - line / 2f, hColor - 2 * rColor - line / 2f, wColor - line / 2f, hColor - line / 2f, 0, 90, false);

        // Bottom line
        pathColor.lineTo(0, hColor - line / 2f);
        if (isShowEdit) {
            // ve background color khi trong man hinh edit edge
            pathBg.reset();
            // Move to the top-left corner
            pathBg.moveTo(0, 0);
            // Vẽ cong ở phía tren bên phai
            pathBg.arcTo(wBg - 2 * rBg, 0, wBg, 2 * rBg, -90, 90, false);
            // Vẽ cong ở phía duoi bên phai
            pathBg.arcTo(wBg - 2 * rBg, hBg - 2 * rBg, wBg, hBg, 0, 90, false);
            // Bottom line
            pathBg.lineTo(0, hBg);
        }
        super.onDraw(canvas);
    }
}
