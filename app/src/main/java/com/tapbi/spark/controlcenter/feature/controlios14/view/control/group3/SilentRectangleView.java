package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3;

import android.animation.Animator;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.data.model.FocusIOS;
import com.tapbi.spark.controlcenter.databinding.LayoutControlSilentRectangleViewBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;

import timber.log.Timber;

public class SilentRectangleView extends ConstraintLayoutBase {

    private ViewPropertyAnimator scaleY = null;
    private boolean down;
    private Handler handler;
    private boolean isLongClick;
    private SilentView.FocusListener focusListener;
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            isLongClick = true;
            longClick();
        }
    };
    private ControlSettingIosModel controlSettingIosModel;
    private LayoutControlSilentRectangleViewBinding binding;
    private Context context;
    private FocusIOS focusIOS;

    public SilentRectangleView(Context context) {
        super(context);
        init(context);
    }

    public SilentRectangleView(Context context, ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public SilentRectangleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SilentRectangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        this.context = context;
        binding = LayoutControlSilentRectangleViewBinding.inflate(LayoutInflater.from(context), this, true);
        handler = new Handler();
        changeColorBackground(controlSettingIosModel.getBackgroundDefaultColorViewParent(), controlSettingIosModel.getBackgroundSelectColorViewParent(), controlSettingIosModel.getCornerBackgroundViewParent());
        binding.imgBackground.changeData(controlSettingIosModel);
        updateDoNotDisturbState();
        binding.tvDoNotDisturb.setTextColor(Color.parseColor(controlSettingIosModel.getColorTextTitle()));
        binding.ringer.setImageDrawable(context.getDrawable(R.drawable.ic_silent_ios));
        binding.tvDoNotDisturb.setTypeface(dataSetupViewControlModel.getTypefaceText());

    }

    public void changeData(ControlSettingIosModel controlSettingIosModel) {
        this.controlSettingIosModel = controlSettingIosModel;
        binding.imgBackground.changeData(controlSettingIosModel);
        changeColorBackground(controlSettingIosModel.getBackgroundDefaultColorViewParent(), controlSettingIosModel.getBackgroundSelectColorViewParent(), controlSettingIosModel.getCornerBackgroundViewParent());
        binding.tvDoNotDisturb.setTextColor(Color.parseColor(controlSettingIosModel.getColorTextTitle()));
        Timber.e("hachung getTypefaceText:" + dataSetupViewControlModel.getTypefaceText());
//            binding.tvDoNotDisturb.setTypeface(dataSetupViewControlModel.getTypefaceText());
    }

    public void updateDoNotDisturbState() {
//            Drawable drawable = null;
//            if (focusIOS == null) {
//                drawable = DrawableUtils.getIconDefaultApp(Constant.DO_NOT_DISTURB, getContext());
//                binding.ringer.setImageDrawable(drawable);
//                binding.ringer.setColorFilter(Color.parseColor(controlSettingIosModel.getColorDefaultIcon()));
////            setBackgroundResource(R.drawable.background_boder_radius_gray);
//                binding.imgBackground.changeIsSelect(false);
////            ringer.setBackgroundResource(R.drawable.background_focus_off);
////            tvName.setText(getContext().getString(R.string.focus));
////            tvStatus.setVisibility(GONE);
//
//            } else {
//                if (focusIOS.getName().equals(focusIOS.getImageLink())) {
//                    drawable = DrawableUtils.getIconDefaultApp(focusIOS.getName(), getContext());
//                } else {
//                    try {
//                        drawable = Drawable.createFromResourceStream(getContext().getResources(), new TypedValue(), getContext().getAssets().open(focusIOS.getImageLink().substring(22)), null);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                binding.ringer.setImageDrawable(drawable);
//            tvName.setText(StringUtils.INSTANCE.getIconDefaultApp(focusIOS.getName(), getContext()));
        try {
            boolean value = Settings.Global.getInt(context.getContentResolver(), "zen_mode") != 0;
            binding.imgBackground.changeIsSelect(value);
            changeIsSelect(value);
            if (controlSettingIosModel != null) {
                binding.ringer.setColorFilter(Color.parseColor(value ? controlSettingIosModel.getColorSelectIcon() : controlSettingIosModel.getColorDefaultIcon()));
                binding.tvDoNotDisturb.setTextColor(Color.parseColor(value ? controlSettingIosModel.getColorTextTitleSelect() : controlSettingIosModel.getColorTextTitle()));
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
//                if (focusIOS.getStartAutoLocation() || focusIOS.getStartAutoTime() || focusIOS.getStartAutoAppOpen() || focusIOS.getStartCurrent()) {
//                    binding.ringer.setColorFilter(Color.parseColor(focusIOS.getColorFocus()));
////                setBackgroundResource(R.drawable.background_boder_radius_white);
//                    binding.imgBackground.changeIsSelect(true);
////                ringer.setBackgroundResource(R.drawable.background_focus_on);
////                tvStatus.setVisibility(VISIBLE);
//                } else {
//                    binding.ringer.setColorFilter(Color.parseColor(controlSettingIosModel.getColorDefaultIcon()));
////                ringer.setBackgroundResource(R.drawable.background_focus_off);
////                setBackgroundResource(R.drawable.background_boder_radius_gray);
//                    binding.imgBackground.changeIsSelect(false);
////                tvStatus.setVisibility(GONE);
//                }
//            }

    }



    @Override
    protected void onTouchDown() {
        super.onTouchDown();
        click();
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

    public void setInterruptionFilter() {
        NotificationManager notificationManager = (NotificationManager) getContext().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (Settings.Global.getInt(getContext().getContentResolver(), "zen_mode") == 0) {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                } else {
                    notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                }
            } catch (Exception e) {
                Timber.d(e);
            }
        }
    }

    public void setFocusListener(SilentView.FocusListener focusListener) {
        this.focusListener = focusListener;
    }

    private void click() {
//            if (focusListener != null) {
//                focusListener.onClick();
//            }
        setInterruptionFilter();
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
