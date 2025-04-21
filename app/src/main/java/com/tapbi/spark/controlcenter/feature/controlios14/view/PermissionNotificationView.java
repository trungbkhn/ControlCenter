package com.tapbi.spark.controlcenter.feature.controlios14.view;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.databinding.ViewPermissionNotitficationIosBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.ImageBackgroundItemView;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

public class PermissionNotificationView extends FrameLayout {

    private ViewPermissionNotitficationIosBinding binding;
    private ClickListener clickListener;
    private boolean isControlCenter = true;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public PermissionNotificationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public PermissionNotificationView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ViewPermissionNotification);
        isControlCenter = typedArray.getBoolean(R.styleable.ViewPermissionNotification_isControlCenter, true);
        typedArray.recycle();
        binding = ViewPermissionNotitficationIosBinding.inflate(LayoutInflater.from(getContext()), this, true);
        if (isControlCenter) {
            binding.background.setItemView(this, ImageBackgroundItemView.TypeView.NOTIFY);
        } else {
            binding.background.setVisibility(View.GONE);
        }
        binding.tvRestart.setOnClickListener(v -> {
            NotyControlCenterServicev614.getInstance().performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG);
            if (clickListener != null) {
                clickListener.onClick();
            }
        });

        binding.tvVerify.setOnClickListener(v -> {
            SettingUtils.intentPermissionNotificationListener(getContext());
            if (clickListener != null) {
                clickListener.onClick();
            }
        });
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (isControlCenter && isShown()) {
            binding.background.updateBackgroundHorizontal();
        }
    }

    public interface ClickListener {
        void onClick();
    }
}
