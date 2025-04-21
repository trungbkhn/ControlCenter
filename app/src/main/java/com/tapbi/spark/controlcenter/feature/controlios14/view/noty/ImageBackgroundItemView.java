package com.tapbi.spark.controlcenter.feature.controlios14.view.noty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.MessageEvent;
import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground;
import com.tapbi.spark.controlcenter.feature.controlios14.view.MenuRightLayout;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Objects;

import timber.log.Timber;

@SuppressLint("AppCompatCustomView")
public class ImageBackgroundItemView extends ImageView {
    private final int scale = Constant.SCALE_BITMAP_BLUR;
    private final int[] positionOld = new int[2];
    private final int[] positionCurrent = new int[2];
    String name = "";
    private Rect rect = new Rect();
    private Rect beforeRect = new Rect();
    private View itemView;
    private int margin = 0;
    private MenuRightLayout menuRightLayout;
    private TypeView typeView = TypeView.RIGHT_MENU;
    private final ViewTreeObserver.OnScrollChangedListener listener = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {
            if (itemView == null) return;
            itemView.getLocationOnScreen(positionCurrent);
//            if (name.contains("Lock") || name.contains("iControl")){
//                Timber.e("hoangld getX: " + getX() + " y: " + getY() + " / pos: " + positionCurrent[1] + " y: " + y + " /progressExpandNoty: "+ NotyControlCenterServicev614.getInstance().getNotyIosTranslateY() + " hScreen: " + App.widthHeightScreen.h + " name: " + name);
//            }
            if (positionCurrent[1] != positionOld[1]) {
//                if (typeView == TypeView.NOTIFY && name.contains("Security")) {
//                    Timber.e("hoangld: isAllowUpdateRect " + " /: "+positionCurrent[0] + " / position[1]: "+positionCurrent[1] );
//                }
                updateBackgroundVertical(positionCurrent);
            }
        }
    };
    private final ViewTreeObserver.OnGlobalLayoutListener listenerGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
//            if (name.contains("iControl")){
//                Timber.e("hoangld getX: " + getX() + " y: " + getY() + " / pos: " + positionOld[1] + " /translateYCurrent: "+ NotyControlCenterServicev614.getInstance().getNotyIosTranslateY() + " name: " + name);
//            }
            if (itemView == null) return;
            itemView.getLocationOnScreen(positionCurrent);

//            if (typeView == TypeView.NOTIFY && name.contains("Security")) {
//                Timber.e("hoangld: positionCurrent " + " /: "+positionCurrent[1] + " / positionOld[1]: "+positionOld[1] );
//            }

            if (positionCurrent[1] != positionOld[1]) {
                updateBackgroundVertical(positionCurrent);
            }
        }
    };
    private int colorDefault;

    public ImageBackgroundItemView(Context context) {
        super(context);
        init();
    }

    public ImageBackgroundItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageBackgroundItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //setBackgroundColor(Color.RED);
        if (getContext() != null) {
            colorDefault = ContextCompat.getColor(getContext(), R.color.color_background_item_default);
        }
    }

    public boolean isItemDoNotTranslate() {
        if (menuRightLayout == null) {
            return true;
        }
        return Float.isNaN(menuRightLayout.getAlpha()) || menuRightLayout.getAlpha() == 0;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateBackgroundHorizontal();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (BlurBackground.getInstance().getTypeBackground().equals(Constant.TRANSPARENT)) {
            canvas.drawColor(colorDefault);
        } else {
            canvas.drawColor(Color.TRANSPARENT);
            if (BlurBackground.getInstance().getBitmapBlurScale()!=null ){
                if ((rect.left >= 0 || rect.right >= 0)) {
                    if (rect.left >= App.widthHeightScreenCurrent.w / Constant.SCALE_BITMAP_BLUR) {
                        int left = margin;
                        rect.left = left / scale;
                        rect.right = (left + getWidth()) / scale;
                    }
                    if (typeView == TypeView.RIGHT_MENU) {
                        canvas.drawBitmap(BlurBackground.getInstance().getBitmapBlurScale(), rect, new Rect(0, 0, (int) (getWidth() * 1.5F), getHeight()), null);
                    } else {
                        canvas.drawBitmap(BlurBackground.getInstance().getBitmapBlurScale(), rect, new Rect(0, 0, getWidth(), getHeight()), null);
                    }
                }
            }

        }
        super.dispatchDraw(canvas);
    }

    private boolean checkRect() {
        if (rect.left != beforeRect.left || rect.top != beforeRect.top || rect.right != beforeRect.right || rect.bottom != beforeRect.bottom) {
            return false;
        }
        return true;
    }

    public void setBitmap() {
        recycled();
//        this.bitmap = BackgroundHelper.Companion.getInstance().getBlur();
        //this.bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        invalidate();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (typeView == TypeView.NOTIFY || typeView == TypeView.ITEM_CHILD || typeView == TypeView.ITEM_TITLE_CHILD) {
            getViewTreeObserver().addOnScrollChangedListener(listener);
            getViewTreeObserver().addOnGlobalLayoutListener(listenerGlobalLayoutListener);
        }
        setBitmap();
        EventBus.getDefault().register(this);
        //Timber.e("event name: "+name);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (typeView == TypeView.NOTIFY || typeView == TypeView.ITEM_CHILD || typeView == TypeView.ITEM_TITLE_CHILD) {
            getViewTreeObserver().removeOnScrollChangedListener(listener);
            getViewTreeObserver().removeOnGlobalLayoutListener(listenerGlobalLayoutListener);
        }
        recycled();
        EventBus.getDefault().unregister(this);
        //Timber.e("event name: "+name);
    }

    @Subscribe
    public void OnMessageEvent(MessageEvent event) {
        if (event.getTypeEvent() == Constant.TYPE_EVENT_UPDATE_ITEM_BLUR) {
//            if (checkLog()) {
//                Timber.e("hoangld: ");
//            }
            updateBackgroundHorizontal();
        }
    }

//    @Override
//    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
//        super.onVisibilityChanged(changedView, visibility);
//        if (visibility == VISIBLE){
//            Timber.e("hoangld: "+positionOld[0]+ " / " + positionOld[1]);
//        }
//    }

    private void recycled() {
//        if (bitmap != null && !bitmap.isRecycled()) {
//            bitmap.recycle();
//            bitmap = null;
//        }
    }


    public void setItemView(View itemView, TypeView typeView, MenuRightLayout menuRightLayout) {
        this.menuRightLayout = menuRightLayout;
        setItemView(itemView, typeView);
    }

    public void setItemView(View itemView, TypeView typeView, String name, MenuRightLayout menuRightLayout) {
        this.menuRightLayout = menuRightLayout;
        setItemView(itemView, typeView, name);
    }

    public void setItemView(View itemView, TypeView typeView, String name) {
        this.name = name;
        setItemView(itemView, typeView);
    }

    public void setItemView(View itemView, TypeView typeView) {
        if (this.itemView == null) {
            this.itemView = itemView;
        }

        this.typeView = typeView;
        if (typeView == TypeView.NOTIFY) {
            margin = (int) MethodUtils.dpToPx(10);
        } else {
            margin = 0;
        }
//        if (typeView == TypeView.NOTIFY || typeView == TypeView.ITEM_CHILD) {
//            getViewTreeObserver().addOnScrollChangedListener(listener);
//        }


        updateBackgroundHorizontal();
    }

    public void updateBackgroundHorizontal() {
        if (itemView == null) return;
        itemView.getLocationOnScreen(positionCurrent);
        updateBackground(positionCurrent);
    }

    public void updateBackgroundVertical(int[] position) {
        updateBackground(position);
    }

    public void updateBackground(int[] position) {
//        if (checkLog()) {
//            Timber.e("hoangld update background");
//        }

//        if (typeView == TypeView.NOTIFY) {
//            isAllowUpdateRect = position[1] > -getHeight()/* && position[0] <= 0*/;
//        } else {
//            isAllowUpdateRect = position[1] > -getHeight();
//        }
//        if (typeView == TypeView.NOTIFY && name.contains("Security")) {
//            Timber.e("hoangld: isAllowUpdateRect " + " /: "+position[0] + " / position[1]: "+position[1] );
//        }
        if (NotyControlCenterServicev614.getInstance() == null) {
            return;
        }
        if (NotyControlCenterServicev614.getInstance().getTransYNotyViewOS() != 0) {
            position[1] = position[1] - NotyControlCenterServicev614.getInstance().getTransYNotyViewOS();
        }

        if (checkLog()) {
            Timber.e("hoangld y: " + NotyControlCenterServicev614.getInstance().getTransYNotyViewOS());
        }

        boolean isAllowUpdateRect = position[1] > -getHeight();
        if (isAllowUpdateRect) {
            if (getWidth() > 0 && getHeight() > 0 && BlurBackground.getInstance().getBitmapBlurScale() != null) {
                if (typeView == TypeView.NOTIFY /*&& position[0] > 0*/) {
                    //set don't swipe left right
                    position[0] = 0;
                } else if (typeView == TypeView.ITEM_TITLE_CHILD) {
                    //check if pager parent scroll overflows from the screen
                    if (position[0] < 0) {
                        position[0] = 0;
                    } else if (position[0] + getWidth() > App.widthScreenCurrent) {
                        position[0] = App.widthScreenCurrent - getWidth();
                    }
                }
                int left = (position[0] + margin);
                int right = left + getWidth();
//                if (typeView == TypeView.NOTIFY && name.contains("iControl")) {
//                    Timber.e("hoangld ---position[0]: " + position[0] + " ---position[1]:" + position[1] + " ---width: " + getWidth()+ " name:"+ name);
//                }
                rect = new Rect((int) ((float) left / scale), (position[1]) / scale, (int) ((float) right / scale), (int) ((position[1] + getHeight()) / scale));
                //if (!checkRect()) {
                beforeRect = rect;
                this.positionOld[0] = position[0];
                this.positionOld[1] = position[1];
                invalidate();
                //}
            }
        }
    }

    public enum TypeView {NOTIFY, RIGHT_MENU, ITEM_CHILD, ITEM_TITLE_CHILD}

    private boolean checkLog() {
        //Timber.e("hoangld " + name + " typeView " + typeView);
        if (Objects.equals(name, "test") && typeView == TypeView.NOTIFY) {
            return true;
        }
        return false;
    }
}
