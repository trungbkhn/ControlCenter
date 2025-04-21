package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.FlashUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;

import java.io.File;

import timber.log.Timber;


public class FlashLightView extends ImageBase {

    public boolean clickFlash = false;
    private Context context;
    //    private FlashUtils flashUtils;
    private CallBackUpdateUi callBackUpdateUi = new CallBackUpdateUi() {
        @Override
        public void stage(String valueRegister, boolean b, int pos) {
            setBg(b);
        }
    };


    public FlashLightView(Context context) {
        super(context);
        init(context);
    }

    public FlashLightView(Context context, ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }


    public FlashLightView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlashLightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setImageResource(R.drawable.flashlight_off);
        if (controlSettingIosModel != null && controlSettingIosModel.getIconControl() != null && !controlSettingIosModel.getIconControl().equals(Constant.ICON_DEFAULT)) {
            String pathIcon = Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.getIdCategory() + "/" +
                    dataSetupViewControlModel.getId() + "/" + controlSettingIosModel.getIconControl();
            File file = new File(context.getFilesDir(), pathIcon);
            boolean isAssets = !file.exists();
            String loadPath = isAssets ? pathIcon : file.getAbsolutePath();
            loadImage(context, loadPath, isAssets, R.drawable.flashlight_off);
        }
        if (NotyControlCenterServicev614.getInstance() != null) {
            setBg(NotyControlCenterServicev614.getInstance().isFlashOn);
        } else {
            setBg(false);
        }

    }

    public void setBg(boolean b) {
        clickFlash = b;
//        if (!b) {
//            setBackgroundResource(R.drawable.background_boder_radius_gray);
//            setImageResource(R.drawable.flashlight_off);
//        } else {
//            setBackgroundResource(R.drawable.background_boder_radius_white);
//            setImageResource(R.drawable.flashlight_on);
//        }
        changeIsSelect(b);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        int paddingIcon = (int) (w * 0.25f);
//        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
        int paddingIcon = (int) (w * 0.267);
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
    }


    @Override
    protected void click() {
        if (!FlashUtils.checkFlashShip(context)) {
            Toast.makeText(context, context.getText(R.string.no_flash), Toast.LENGTH_SHORT).show();
            return;
        }
        if (NotyControlCenterServicev614.getInstance() != null) {
            NotyControlCenterServicev614.getInstance().setFlashOnOff();
        }
    }

    @Override
    protected void longClick() {

    }

    public void updateFlash() {
        callBackUpdateUi.stage(Constant.STRING_ACTION_FLASH_LIGHT, clickFlash, 0);
    }

    @Override
    protected void onDown() {
        animationDown();
    }

    @Override
    protected void onUp() {
        animationUp();
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
