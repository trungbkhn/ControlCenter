package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.databinding.BaseLayoutControlSingleFunctionTextViewBinding;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.FlashUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;

import java.io.File;

public class FlashlightTextView extends ConstraintLayoutBase {


    public boolean clickFlash = false;
    private BaseLayoutControlSingleFunctionTextViewBinding binding;
    private ControlSettingIosModel controlSettingIOS;
    private Context context;
    //    private FlashUtils flashUtils;
    private CallBackUpdateUi callBackUpdateUi = new CallBackUpdateUi() {
        @Override
        public void stage(String valueRegister, boolean b, int pos) {
            setBg(b);
        }
    };


    public FlashlightTextView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public FlashlightTextView(Context context, ControlSettingIosModel controlSettingIOS, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.context = context;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        this.controlSettingIOS = controlSettingIOS;
        init(context);
    }

    public FlashlightTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public FlashlightTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    private void init(Context context) {
        binding = BaseLayoutControlSingleFunctionTextViewBinding.inflate(LayoutInflater.from(context), this, true);
        binding.tvFunction.setSelected(true);
        initView();
        if (NotyControlCenterServicev614.getInstance() != null) {
            setBg(NotyControlCenterServicev614.getInstance().isFlashOn);
        } else {
            setBg(false);
        }

    }

    public void initView() {
        binding.imgIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_flash_ios));
        if (controlSettingIOS != null) {
            changeColorBackground(controlSettingIOS.getBackgroundDefaultColorViewParent(), controlSettingIOS.getBackgroundSelectColorViewParent(), controlSettingIOS.getCornerBackgroundViewParent());
            if (controlSettingIOS.getIconControl() != null && !controlSettingIOS.getIconControl().equals(Constant.ICON_DEFAULT)) {
                String pathIcon = Constant.PATH_ASSET_THEME.concat(dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId() + "/" + controlSettingIOS.getIconControl());
                if (dataSetupViewControlModel.getId() > 10000) {
                    File file = new File(context.getFilesDir(), Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId() + "/" + controlSettingIOS.getIconControl());
                    pathIcon = file.getAbsolutePath();
                }
//                Glide.with(context).load(pathIcon).into(binding.imgIcon);
                loadImage(context, pathIcon, pathIcon.contains(Constant.PATH_ASSET_THEME), R.drawable.ic_flash_ios, binding.imgIcon);
            }
            updateUI();

        }
//        binding.imgFlash.changeIsTouch(false);
        binding.tvFunction.setTypeface(dataSetupViewControlModel.getTypefaceText());
        binding.tvFunction.setText(context.getString(R.string.flashlight));
    }

    private void updateUI() {
        if (controlSettingIOS != null) {
            if (clickFlash) {
                binding.tvFunction.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitleSelect()));
                binding.imgIcon.setColorFilter(Color.parseColor(controlSettingIOS.getColorSelectIcon()));
            } else {
                binding.tvFunction.setTextColor(Color.parseColor(controlSettingIOS.getColorTextTitle()));
                binding.imgIcon.setColorFilter(Color.parseColor(controlSettingIOS.getColorDefaultIcon()));
            }
        }
        changeIsSelect(clickFlash);
    }

    public void setBg(boolean b) {
        clickFlash = b;
        updateUI();
        changeIsSelect(b);
    }



    public void changeData(ControlSettingIosModel controlSettingIOS) {
        this.controlSettingIOS = controlSettingIOS;
        initView();
    }


    @Override
    protected void onTouchDown() {
        super.onTouchDown();
        if (!FlashUtils.checkFlashShip(context)) {
            Toast.makeText(context, context.getText(R.string.no_flash), Toast.LENGTH_SHORT).show();
            return;
        }
        if (NotyControlCenterServicev614.getInstance() != null) {
            NotyControlCenterServicev614.getInstance().setFlashOnOff();
        }
    }

    public void updateFlash() {
        callBackUpdateUi.stage(Constant.STRING_ACTION_FLASH_LIGHT, clickFlash, 0);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        flashUtils = new FlashUtils(getContext(), callBackUpdateUi, Constant.STRING_ACTION_FLASH_LIGHT,0);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        try {
//            if (flashUtils != null) {
//                flashUtils.unRegisterListener();
//                flashUtils = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
