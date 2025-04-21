package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group3;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;

import java.io.File;

import timber.log.Timber;

public class SilentImageView extends ImageBase {
    private Context context;

    public SilentImageView(Context context) {
        super(context);
        init(context);
    }


    public SilentImageView(Context context, ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public SilentImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SilentImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.context = context;
        setImageDrawable(context.getDrawable(R.drawable.ic_silent_ios));
        if (controlSettingIosModel != null && controlSettingIosModel.getIconControl() != null && !controlSettingIosModel.getIconControl().equals(Constant.ICON_DEFAULT)) {
            String pathIcon = Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.getIdCategory() + "/" +
                    dataSetupViewControlModel.getId() + "/" + controlSettingIosModel.getIconControl();
            File file = new File(context.getFilesDir(), pathIcon);
            boolean isAssets = !file.exists();
            String loadPath = isAssets ? pathIcon : file.getAbsolutePath();
            loadImage(context, loadPath, isAssets,R.drawable.ic_silent_ios);
        }
        updateDoNotDisturbState();


    }

    public void updateDoNotDisturbState() {
        try {
            boolean value = Settings.Global.getInt(context.getContentResolver(), "zen_mode") != 0;
            changeIsSelect(value);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
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

    private void setStates(boolean b) {
        stopAniZoom();
        changeIsSelect(b);

    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingIcon = (int) (w * 0.267f);
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
    }

    @Override
    protected void click() {
        setInterruptionFilter();
    }

    @Override
    protected void longClick() {

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
