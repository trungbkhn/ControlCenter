package com.tapbi.spark.controlcenter.feature.edge;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TouchTop extends BaseTouchView {

    public TouchTop(Context context) {
        super(context);
    }

    public TouchTop(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchTop(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        pathColor.reset();
        // Move to the top-left corner
        pathColor.moveTo(line / 2f, 0);

        // Vẽ cong ở phía duoi bên trai
        pathColor.arcTo(line / 2f, - rColor,  2 * rColor + line / 2f, hColor - line / 2f, 180, -90, false);
        // Vẽ cong ở phía dưới bên phải
        pathColor.arcTo(wBg - 2 * rColor - line / 2f, - rColor, wBg - line / 2f, hColor - line / 2f, 90, -90, false);

        // Right line
        pathColor.lineTo(wBg - line / 2, 0);
        if (isShowEdit) {
            // ve background color khi trong man hinh edit edge
            pathBg.reset();
            // Move to the top-left corner
            pathBg.moveTo(0, 0);
            // Vẽ cong ở phía duoi bên trai
            pathBg.arcTo(0, hBg - 2 * rBg, 2 * rBg, hBg, 180, -90, false);
            // Vẽ cong ở phía duoi bên phai
            pathBg.arcTo(wBg - 2 * rBg, hBg - 2 * rBg, wBg, hBg, 90, -90, false);
            // Right line
            pathBg.lineTo(wBg, 0);
        }
        super.onDraw(canvas);
    }
}
