package com.tapbi.spark.controlcenter.feature.controlios14.view.control.base;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.tapbi.spark.controlcenter.data.repository.ThemesRepository;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.utils.AppUtils;

import java.lang.ref.WeakReference;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;


@SuppressLint("AppCompatCustomView")
public class ConstraintLayoutBase extends ConstraintLayout {

    private Paint paint;
    private String colorDefaultBackground;
    private String colorSelectBackground;
    private Boolean isSelect = false;
    private Float cornerBackground = 0f;
    private Boolean isDrawBackground = false;
    private ViewPropertyAnimator scaleY = null;
    private boolean down;
    private ScaleAnimation fade_in;
    protected DataSetupViewControlModel dataSetupViewControlModel;
    public ConstraintLayoutBase(Context context) {
        super(context);
    }
    public CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ConstraintLayoutBase(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ConstraintLayoutBase(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                animationDown();
                break;
            case MotionEvent.ACTION_UP:
                if (checkClick(getX(), getY()) && !ThemesRepository.isControlEditing()){
                    onTouchDown();
                }
            case MotionEvent.ACTION_CANCEL:
                animationUp();
                break;
        }

        return (!ThemesRepository.isControlEditing());
    }

    public boolean checkClick(float x, float y) {
        if ((x < this.getPaddingLeft()) || (x > this.getWidth() - this.getPaddingRight()) || (y > (this.getHeight() - this.getPaddingBottom())) || (y < this.getPaddingTop())) {
            return false;
        }
        return true;
    }


    protected void onTouchDown(){}

    protected void animationDown() {
        if (scaleY != null) {
            scaleY.cancel();
        }
        down = true;
        scaleY = animate().scaleX(0.85f).scaleY(0.85f);
        scaleY.setDuration(200).setInterpolator(new DecelerateInterpolator());
        scaleY.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (down) {
                    ViewPropertyAnimator scaleY = animate().scaleX(1f).scaleY(1f);
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
        scaleY = animate().scaleX(1.0f).scaleY(1.0f);
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
        scaleY = animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300).withEndAction(() -> {
            if (getAlpha() != 1f) {
                setAlpha(1f);
            }
        }).setInterpolator(new DecelerateInterpolator());
        scaleY.start();

    }


    public void setAnimationListener(Animator.AnimatorListener listener) {
        if (scaleY != null) scaleY.setListener(listener);
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


    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
//        super.dispatchDraw(canvas);
//        try {
            if (isDrawBackground) {
                if (paint == null) {
                    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                }
                if (isSelect) {
                    paint.setColor(Color.parseColor(colorSelectBackground));
                } else {
                    paint.setColor(Color.parseColor(colorDefaultBackground));
                }
                float corner = cornerBackground * getHeight();
                canvas.drawRoundRect(0, 0, getWidth(), getHeight(), corner, corner, paint);

            }
//        }catch (RuntimeException ignored){}
        super.dispatchDraw(canvas);
    }

    public void changeIsSelect(boolean isSelect){
        this.isSelect = isSelect;
        invalidate();
    }

    public void changeColorBackground(String colorDefaultBackground, String colorSelectBackground, Float cornerBackground){
        this.colorDefaultBackground = colorDefaultBackground;
        this.colorSelectBackground = colorSelectBackground;
        this.cornerBackground = cornerBackground;
        isDrawBackground = colorDefaultBackground != null;
        invalidate();
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

    protected void stopAniZoom() {
        clearAnimation();

    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            compositeDisposable.clear();
        }
    }

    public void loadImage(Context context, String path, boolean isAssets, int imageError, ImageView imageView) {
        Log.d("duongcvc", "loadImage: "+ path);
        WeakReference<ImageView> imageViewRef = new WeakReference<>(imageView);
        compositeDisposable.add(AppUtils.getBitmapControl(context, path, isAssets)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    ImageView imgView = imageViewRef.get();
                    if (bitmap != null && !bitmap.isRecycled()) {
                        imgView.setImageBitmap(bitmap);
                    } else {
                        imgView.setImageResource(imageError);
                    }
                }, throwable -> {
                    Timber.e(throwable, "Error loading image");
                    imageView.setImageResource(imageError);
                }));
    }



}
