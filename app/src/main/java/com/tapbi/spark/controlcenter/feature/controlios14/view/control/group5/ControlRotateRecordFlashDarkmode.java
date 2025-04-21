package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.databinding.LayoutControlRotateRecordFlashDarkmodeBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3.RotateView;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;

import java.io.File;

public class ControlRotateRecordFlashDarkmode extends ConstraintLayoutBase {

    private LayoutControlRotateRecordFlashDarkmodeBinding binding;
    private ControlSettingIosModel controlSettingIOS;
    private Context context;

    public ControlRotateRecordFlashDarkmode(Context context) {
        super(context);
        init(context);
    }

    public ControlRotateRecordFlashDarkmode(Context context, ControlSettingIosModel controlSettingIOS, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIOS = controlSettingIOS;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public ControlRotateRecordFlashDarkmode(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ControlRotateRecordFlashDarkmode(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        binding = LayoutControlRotateRecordFlashDarkmodeBinding.inflate(LayoutInflater.from(context), this, true);
        initView();
//        binding.rotateAction.setOnRotateChangeListener(() -> statusControlView.updateRotate());
    }

    public void initView() {
        if (controlSettingIOS != null) {
            changeColorBackground(controlSettingIOS.getBackgroundDefaultColorViewParent(), controlSettingIOS.getBackgroundSelectColorViewParent(), controlSettingIOS.getCornerBackgroundViewParent());
            binding.rotateAction.changeData(controlSettingIOS);
            binding.darkmodeAction.changeData(controlSettingIOS);
            binding.recordAction.changeData(controlSettingIOS);
            binding.flashAction.changeData(controlSettingIOS);
            binding.recordAction.setColorTextCount(Color.parseColor(controlSettingIOS.getColorTextTitle()));
            binding.recordAction.setOnClickSettingListener(() -> {
                if (NotyControlCenterServicev614.getInstance() != null) {
                    NotyControlCenterServicev614.getInstance().closeNotyCenter();
                }
            });
            if (controlSettingIOS != null && controlSettingIOS.getIconControl() != null && !controlSettingIOS.getIconControl().equals(Constant.ICON_DEFAULT)) {
//                String pathIcon = Constant.PATH_ASSET_THEME.concat(dataSetupViewControlModel.getIdCategory()+"/"+dataSetupViewControlModel.getId()+"/ic_rotate.png");
                String pathIconFlash = Constant.PATH_ASSET_THEME.concat(dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId() + "/ic_flash.png");
                String pathIconDarkmode = Constant.PATH_ASSET_THEME.concat(dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId() + "/ic_darkmode.png");
                if (dataSetupViewControlModel.getId() > 10000) {
                    File file = new File(context.getFilesDir(), Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId());
//                    pathIcon =  file.getAbsolutePath()+"/ic_rotate.png";
                    pathIconFlash = file.getAbsolutePath() + "/ic_flash.png";
                    pathIconDarkmode = file.getAbsolutePath() + "/ic_darkmode.png";
                }
//                Glide.with(getContext()).load(pathIcon).into(binding.rotateAction.getImageIcon());
//                Glide.with(getContext()).load(pathIconFlash).into(binding.flashAction);
//                Glide.with(getContext()).load(pathIconDarkmode).into(binding.darkmodeAction);
                loadImage(context, pathIconFlash, pathIconFlash.contains(Constant.PATH_ASSET_THEME), R.drawable.flashlight_off, binding.flashAction);
                loadImage(context, pathIconDarkmode, pathIconDarkmode.contains(Constant.PATH_ASSET_THEME), R.drawable.ic_darkmode_ios, binding.darkmodeAction);
            }
        }
    }
    public void setBgFlash(boolean isFlash) {
        binding.flashAction.setBg(isFlash);

    }

    public RotateView getRotateView() {
        return binding.rotateAction;
    }


    public void changeControlSettingIos(ControlSettingIosModel controlSettingIOS) {
        this.controlSettingIOS = controlSettingIOS;
        initView();
    }
}
