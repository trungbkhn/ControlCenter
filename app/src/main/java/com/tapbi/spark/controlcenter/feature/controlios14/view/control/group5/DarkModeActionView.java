package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;


import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.CreateItemViewControlCenterIOS;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.interfaces.IListenActionClick;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import java.io.File;


public class DarkModeActionView extends ImageBase {

    private Context context;

    private OnClickSettingListener onClickSettingListener;


    public DarkModeActionView(Context context) {
        super(context);
        init(context);
    }

    public DarkModeActionView(Context context, ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public DarkModeActionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DarkModeActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }

    private void init(Context context) {
        this.context = context;
        if (controlSettingIosModel != null && controlSettingIosModel.getIconControl() != null &&
                !controlSettingIosModel.getIconControl().equals(Constant.ICON_DEFAULT)) {

            String pathIcon = Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.getIdCategory() + "/" +
                    dataSetupViewControlModel.getId() + "/" + controlSettingIosModel.getIconControl();
            File file = new File(context.getFilesDir(), pathIcon);
            boolean isAssets = !file.exists();
            String loadPath = isAssets ? pathIcon : file.getAbsolutePath();
            loadImage(context, loadPath, isAssets, R.drawable.ic_dark_mode_on);
        }else {
            setImageResource(R.drawable.ic_dark_mode_on);
        }
        if (NotyControlCenterServicev614.getInstance() != null) {
            setStates(NotyControlCenterServicev614.getInstance().isDarkModeOn);
        }

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingIcon = (int) (w * 0.267f);
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
    }

    private void setStates(boolean modeOn) {
        stopAniZoom();
//        if (modeOn) {
//            setBackgroundResource(R.drawable.background_boder_radius_white);
//            setImageResource(R.drawable.ic_dark_mode_on);
//        } else {
//            setBackgroundResource(R.drawable.background_boder_radius_gray);
//            setImageResource(R.drawable.ic_dark_mode);
//        }

        changeIsSelect(modeOn);

    }


    private void openNotiFindDarkMode() {
        NotyControlCenterServicev614.getInstance().setHandingAction(new IListenActionClick() {
            @Override
            public void noFindAction() {
                CreateItemViewControlCenterIOS.isTouchDarkmore = false;
                stopAniZoom();
            }

            @Override
            public void actionClicked() {
                CreateItemViewControlCenterIOS.isTouchDarkmore = false;
                stopAniZoom();
            }
        }, Constant.DARK_MODE);
    }

    @Override
    protected void click() {
        if (NotyControlCenterServicev614.getInstance().allowClickAction()) {
            CreateItemViewControlCenterIOS.isTouchDarkmore = true;
            statAniZoom();
            openNotiFindDarkMode();
        } else {
            if (NotyControlCenterServicev614.getInstance() != null) {
                NotyControlCenterServicev614.getInstance().showToast(context.getString(R.string.wait_until_job_done));
            }
        }

    }

    @Override
    protected void longClick() {
        SettingUtils.intentChangeDisplay(getContext());
        if (onClickSettingListener != null) {
            onClickSettingListener.onClick();
        }
    }

    @Override
    protected void onDown() {
        animationDown();
    }

    @Override
    protected void onUp() {
        animationUp();
    }


}
