package com.tapbi.spark.controlcenter.feature.controlios14.view.control.base;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.EditControlView;
import com.tapbi.spark.controlcenter.utils.AppUtils;

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;


@SuppressLint("AppCompatCustomView")
public abstract class ImageBase extends ImageView {

    public boolean anotherViewTouching = false;
    public CompositeDisposable compositeDisposable = new CompositeDisposable();
    protected ControlSettingIosModel controlSettingIosModel;
    protected DataSetupViewControlModel dataSetupViewControlModel;
    private ViewPropertyAnimator scaleY = null;
    private boolean down;
    private Handler handler;
    private boolean isLongClick;
    private ScaleAnimation fade_in;
    private boolean isSelect = false;
    private String pathBackground = "";
    private Paint paint;
    private boolean isTouch = true;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isLongClick = true;
            longClick();
        }
    };
    //use viewParent to animate, fix the view container as ScrollView
    private View viewParent;

    public ImageBase(Context context) {
        super(context);
        init(context);
    }

    public ImageBase(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImageBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        handler = new Handler();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        initColorIcon();

    }

    protected abstract void click();

    protected abstract void longClick();

    protected abstract void onDown();

    protected abstract void onUp();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onDown();
                if (!ThemesRepository.isControlEditing()) handler.postDelayed(runnable, ViewConfiguration.getLongPressTimeout());
                break;
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(runnable);
                onUp();
                break;
            case MotionEvent.ACTION_UP:
                handler.removeCallbacks(runnable);
                onUp();
                if (checkClick(event.getX(), event.getY()) && !isLongClick && !anotherViewTouching && (this instanceof EditControlView || !ThemesRepository.isControlEditing())) {
                    click();
                }
                isLongClick = false;
                break;
        }

        return (this instanceof EditControlView || !ThemesRepository.isControlEditing());
    }

    public void changeIsTouch(boolean isTouch) {
        this.isTouch = isTouch;
    }

    private boolean checkClick(float x, float y) {
        if ((x < this.getPaddingLeft()) || (x > this.getWidth() - this.getPaddingRight()) || (y > (this.getHeight() - this.getPaddingBottom())) || (y < this.getPaddingTop())) {
            return false;
        }
        return true;
    }

    public void setParentView(View view) {
        this.viewParent = view;
    }

    protected void animationDown() {
        if (scaleY != null) {
            scaleY.cancel();
        }
        down = true;
        if (viewParent == null) {
            scaleY = animate().scaleX(0.85f).scaleY(0.85f);
        } else {
            scaleY = viewParent.animate().scaleX(0.85f).scaleY(0.85f);
        }
        scaleY.setDuration(200).setInterpolator(new DecelerateInterpolator());
        scaleY.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (down) {
                    ViewPropertyAnimator scaleY;
                    if (viewParent == null) {
                        scaleY = animate().scaleX(1.0f).scaleY(1.0f);
                    } else {
                        scaleY = viewParent.animate().scaleX(1.0f).scaleY(1.0f);
                    }
                    scaleY.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                down = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleY.start();
    }

    protected void animationUp() {
        if (scaleY != null) {
            scaleY.cancel();
        }
        down = false;
        if (viewParent == null) {
            scaleY = animate().scaleX(1.0f).scaleY(1.0f);
        } else {
            scaleY = viewParent.animate().scaleX(1.0f).scaleY(1.0f);
        }
        scaleY.setDuration(200).setInterpolator(new AccelerateInterpolator());
        scaleY.start();
    }

    public void animationShow() {
        if (scaleY != null) {
            scaleY.cancel();
        }
        setScaleX(0.8f);
        setScaleY(0.8f);
        setAlpha(0f);
        scaleY = animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300).setInterpolator(new DecelerateInterpolator());
        scaleY.start();
    }

    public void animationHide() {
        if (scaleY != null) {
            scaleY.cancel();
        }
        setScaleX(1f);
        setScaleY(1f);
        setAlpha(1f);
        scaleY = animate().scaleX(0.8f).scaleY(0.8f).alpha(0f).setDuration(300).setInterpolator(new AccelerateInterpolator());
        scaleY.start();
    }

    protected void statAniZoom() {
        stopAniZoom();
        if (fade_in == null) {
            fade_in = new ScaleAnimation(0.8f, 1f, 0.8f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            fade_in.setDuration(1000);     // animation duration in milliseconds
            fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
            fade_in.setRepeatMode(Animation.REVERSE);
            fade_in.setRepeatCount(5);
            fade_in.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    stopAniZoom();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        startAnimation(fade_in);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (controlSettingIosModel != null) {
            if (controlSettingIosModel.getBackgroundImageViewItem() == null || controlSettingIosModel.getBackgroundImageViewItem().isEmpty() || controlSettingIosModel.getBackgroundImageViewItem().equals(Constant.SHAPE_DEFAULT)) {
                if (isSelect) {
                    if (controlSettingIosModel.getBackgroundColorSelectViewItem() != null) {
                        paint.setColor(Color.parseColor(controlSettingIosModel.getBackgroundColorSelectViewItem()));
                    }
                } else {
                    if (controlSettingIosModel.getBackgroundColorDefaultViewItem() != null) {
                        paint.setColor(Color.parseColor(controlSettingIosModel.getBackgroundColorDefaultViewItem()));
                    }
                }
                float corner = controlSettingIosModel.getCornerBackgroundViewItem() * getHeight();
                canvas.drawRoundRect(new RectF(0f, 0f, getWidth(), getHeight()), corner, corner, paint);
            }
        }
        super.onDraw(canvas);
    }

    protected void setBackgroundImage() {
        if (controlSettingIosModel != null) {
            String backgroundImageViewItem = controlSettingIosModel.getBackgroundImageViewItem();

            if (backgroundImageViewItem != null && !backgroundImageViewItem.isEmpty() &&
                    !backgroundImageViewItem.equals(Constant.SHAPE_DEFAULT)) {
                String assetPath = "file:///android_asset/iconShade/" + backgroundImageViewItem;
                if (pathBackground.isEmpty() || !pathBackground.equals(assetPath)) {
                    pathBackground = assetPath;

                    Glide.with(getContext())
                            .load(assetPath)
                            .diskCacheStrategy(DiskCacheStrategy.NONE) // Không cache nếu không cần
                            .override(100, 100) // Kích thước mục tiêu
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    if (controlSettingIosModel.isFilterBackgroundViewItem()) {
                                        int color = isSelect ?
                                                Color.parseColor(controlSettingIosModel.getBackgroundColorSelectViewItem()) :
                                                Color.parseColor(controlSettingIosModel.getBackgroundColorDefaultViewItem());
                                        resource.setTint(color);
                                    }
                                    setBackground(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    setBackground(null);
                                }
                            });
                } else {
                    Drawable drawable = getBackground();
                    if (drawable != null) {
                        int color = isSelect ?
                                Color.parseColor(controlSettingIosModel.getBackgroundColorSelectViewItem()) :
                                Color.parseColor(controlSettingIosModel.getBackgroundColorDefaultViewItem());
                        drawable.setTint(color);
                        setBackground(drawable);
                    }
                }
            } else {
                pathBackground = "";
                setBackground(null);
            }
        }
    }

    public void changeIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
        initColorIcon();
        invalidate();
    }

    protected void initColorIcon() {
        setBackgroundImage();
        if (isSelect) {
            if (controlSettingIosModel != null && controlSettingIosModel.getColorSelectIcon() != null) {
                setColorFilter(Color.parseColor(controlSettingIosModel.getColorSelectIcon()));
            } else {
                setColorFilter(null);
            }
        } else {
            if (controlSettingIosModel != null && controlSettingIosModel.getColorDefaultIcon() != null) {
                setColorFilter(Color.parseColor(controlSettingIosModel.getColorDefaultIcon()));
            } else {
                setColorFilter(null);

            }
        }

    }

    public void changeData(ControlSettingIosModel controlSettingIosModel) {
        this.controlSettingIosModel = controlSettingIosModel;
        initColorIcon();
        invalidate();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != VISIBLE) {
            stopAniZoom();
        }
    }

    protected void stopAniZoom() {

        clearAnimation();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void loadImage(Context context, String path, boolean isAssets, int imageError) {
        compositeDisposable.add(AppUtils.getBitmapControl(context, path, isAssets)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    if (bitmap != null && !bitmap.isRecycled()) {
                        setImageBitmap(bitmap);
                    } else {
                        setImageResource(imageError);
                    }
                }, throwable -> {
                    Timber.e(throwable, "Error loading image");
                    setImageResource(imageError);
                }));
    }

    public interface OnAnimationListener {
        void onDown();

        void onUp();

        void onClick();

        void onLongClick();

        void onClose();
    }
}
