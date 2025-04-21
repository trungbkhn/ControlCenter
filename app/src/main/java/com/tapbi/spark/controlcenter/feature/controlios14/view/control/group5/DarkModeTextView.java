package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.databinding.BaseLayoutControlSingleFunctionTextViewBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.interfaces.IListenActionClick;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import java.io.File;

import timber.log.Timber;

public class DarkModeTextView extends ConstraintLayoutBase {

    private Context context;
    private ControlSettingIosModel controlSettingIOS;
    private BaseLayoutControlSingleFunctionTextViewBinding binding;
    private boolean isSelect = false;
    private OnClickSettingListener onClickSettingListener;

    public DarkModeTextView(Context context) {
        super(context);
        init(context);
    }

    public DarkModeTextView(Context context, ControlSettingIosModel controlSettingIOS, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        this.controlSettingIOS = controlSettingIOS;
        init(context);
    }

    public DarkModeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DarkModeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }

    private void init(Context context) {
        this.context = context;
        binding = BaseLayoutControlSingleFunctionTextViewBinding.inflate(LayoutInflater.from(context), this, true);
        binding.tvFunction.setSelected(true);
        initView();
    }

    public void initView() {
        binding.imgIcon.setImageDrawable(context.getDrawable(R.drawable.ic_darkmode_ios));
        if (controlSettingIOS != null) {
            changeColorBackground(controlSettingIOS.getBackgroundDefaultColorViewParent(), controlSettingIOS.getBackgroundSelectColorViewParent(), controlSettingIOS.getCornerBackgroundViewParent());
            if (controlSettingIOS.getIconControl() != null && !controlSettingIOS.getIconControl().equals(Constant.ICON_DEFAULT)) {
                String pathIcon = Constant.PATH_ASSET_THEME.concat(dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId() + "/" + controlSettingIOS.getIconControl());
                if (dataSetupViewControlModel.getId() > 10000) {
                    File file = new File(context.getFilesDir(), Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId() + "/" + controlSettingIOS.getIconControl());
                    pathIcon = file.getAbsolutePath();
                }
//                Glide.with(context).load(pathIcon).into(binding.imgIcon);
                loadImage(context, pathIcon, pathIcon.contains(Constant.PATH_ASSET_THEME), R.drawable.ic_darkmode_ios, binding.imgIcon);

            }
        }
        binding.tvFunction.setTypeface(dataSetupViewControlModel.getTypefaceText());
        binding.tvFunction.setText(context.getString(R.string.dark_mode));

        if (NotyControlCenterServicev614.getInstance() != null) {
            setStates(NotyControlCenterServicev614.getInstance().isDarkModeOn);
        }


    }




    private void setStates(boolean modeOn) {
        Timber.e("hachung setStates:"+modeOn);
        stopAniZoom();
        this.isSelect = modeOn;
        updateUI();

    }


    private void openNotiFindDarkMode() {
        NotyControlCenterServicev614.getInstance().setHandingAction(new IListenActionClick() {
            @Override
            public void noFindAction() {
                stopAniZoom();
            }

            @Override
            public void actionClicked() {
                stopAniZoom();
            }
        }, Constant.DARK_MODE);
    }



    @Override
    protected void onTouchDown() {
        super.onTouchDown();
        if (NotyControlCenterServicev614.getInstance().allowClickAction()) {
            statAniZoom();
            openNotiFindDarkMode();
        } else {
            if (NotyControlCenterServicev614.getInstance() != null) {
                NotyControlCenterServicev614.getInstance().showToast(context.getString(R.string.wait_until_job_done));
            }
        }
    }

    @Override
    public void setOnLongClickListener(@Nullable OnLongClickListener l) {
        super.setOnLongClickListener(l);
        SettingUtils.intentChangeDisplay(getContext());
        if (onClickSettingListener != null) {
            onClickSettingListener.onClick();
        }
    }


    private void updateUI() {
        if (controlSettingIOS != null) {
            if (isSelect) {
                binding.tvFunction.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitleSelect()));
                binding.imgIcon.setColorFilter(Color.parseColor(controlSettingIOS.getColorSelectIcon()));
            } else {
                binding.tvFunction.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitle()));
                binding.imgIcon.setColorFilter(Color.parseColor(controlSettingIOS.getColorDefaultIcon()));
            }
        }
        changeIsSelect(isSelect);
    }
}