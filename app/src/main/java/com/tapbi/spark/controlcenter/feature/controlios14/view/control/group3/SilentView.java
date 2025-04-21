package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.model.FocusIOS;
import com.tapbi.spark.controlcenter.databinding.LayoutSilientBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlCenterIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.utils.DrawableUtils;

import java.io.IOException;

public class SilentView extends ConstraintLayoutBase {
    private ViewPropertyAnimator scaleY = null;
    private boolean down;
    private Handler handler;
    private boolean isLongClick;
    private FocusListener focusListener;
    private ControlSettingIosModel controlSettingIosModel;
    private LayoutSilientBinding binding;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isLongClick = true;
            longClick();
        }
    };

    private FocusIOS focusIOS;

    public SilentView(Context context) {
        super(context);
        init(context);
    }

    public SilentView(Context context, ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public SilentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SilentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public FocusIOS getFocusIOS() {
        return focusIOS;
    }

    public void setFocusIOS(FocusIOS focusIOS) {
        this.focusIOS = focusIOS;
        updateDoNotDisturbState();
    }

    private void init(Context context) {
        binding = LayoutSilientBinding.inflate(LayoutInflater.from(context), this, true);
        handler = new Handler();
        binding.imgBackground.changeData(controlSettingIosModel);
        updateDoNotDisturbState();
    }

    public void changeData(ControlSettingIosModel controlSettingIosModel){
        this.controlSettingIosModel = controlSettingIosModel;
        binding.imgBackground.changeData(controlSettingIosModel);
    }

    public void updateDoNotDisturbState() {
        Drawable drawable = null;
        if (focusIOS == null) {
            drawable = DrawableUtils.getIconDefaultApp(Constant.DO_NOT_DISTURB, getContext());
            binding.ringer.setImageDrawable(drawable);
            binding.ringer.setColorFilter(Color.WHITE);
//            setBackgroundResource(R.drawable.background_boder_radius_gray);
            binding.imgBackground.changeIsSelect(false);
//            ringer.setBackgroundResource(R.drawable.background_focus_off);
//            tvName.setText(getContext().getString(R.string.focus));
//            tvStatus.setVisibility(GONE);

        } else {
            if (focusIOS.getName().equals(focusIOS.getImageLink())) {
                drawable = DrawableUtils.getIconDefaultApp(focusIOS.getName(), getContext());
            } else {
                try {
                    drawable = Drawable.createFromResourceStream(getContext().getResources(), new TypedValue(), getContext().getAssets().open(focusIOS.getImageLink().substring(22)), null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            binding.ringer.setImageDrawable(drawable);
//            tvName.setText(StringUtils.INSTANCE.getIconDefaultApp(focusIOS.getName(), getContext()));

            if (focusIOS.getStartAutoLocation() || focusIOS.getStartAutoTime() || focusIOS.getStartAutoAppOpen() || focusIOS.getStartCurrent()) {
                binding.ringer.setColorFilter(Color.parseColor(focusIOS.getColorFocus()));
//                setBackgroundResource(R.drawable.background_boder_radius_white);
                binding.imgBackground.changeIsSelect(true);
//                ringer.setBackgroundResource(R.drawable.background_focus_on);
//                tvStatus.setVisibility(VISIBLE);
            } else {
                binding.ringer.setColorFilter(Color.WHITE);
//                ringer.setBackgroundResource(R.drawable.background_focus_off);
//                setBackgroundResource(R.drawable.background_boder_radius_gray);
                binding.imgBackground.changeIsSelect(false);
//                tvStatus.setVisibility(GONE);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                focusListener.onDown();
                onDown();
                handler.postDelayed(runnable, 500);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(runnable);
                onUp();

                if (checkClick(event.getX(), event.getY()) && !isLongClick) {
                    click();
                }
                isLongClick = false;
                focusListener.onUp();
                break;
        }
        return true;
    }



    private void onDown() {
        if (scaleY != null) {
            scaleY.cancel();
        }
        down = true;
        scaleY = animate().scaleX(1.15f).scaleY(1.15f);
//        scaleY = animate().scaleX(1.3f).scaleY(1.3f);
        scaleY.setDuration(150).setInterpolator(new DecelerateInterpolator());
        scaleY.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (down) {
                    ViewPropertyAnimator scaleY = animate().scaleX(1.1f).scaleY(1.1f);
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

    private void onUp() {
        if (scaleY != null) {
            scaleY.cancel();
        }
        down = false;
        scaleY = animate().scaleX(1.0f).scaleY(1.0f);
        scaleY.setDuration(200).setInterpolator(new AccelerateInterpolator());
        scaleY.start();
    }

    public void setFocusListener(FocusListener focusListener) {
        this.focusListener = focusListener;
    }

    private void click() {
        if (focusListener != null) {
            focusListener.onClick();
        }
    }

    private void longClick() {
        if (focusListener != null) {
            focusListener.onLongClick();
        }
    }

    public interface FocusListener {
        void onDown();

        void onUp();

        void onClick();

        void onLongClick();
    }

}
