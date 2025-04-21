package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group1;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import java.io.File;

public class SynDataView extends ImageBase {


    public boolean enableSync;
    private Context context;
    private SettingView.OnSettingListener onSettingListener;

    private IListenerUpdate iListenerUpdate;

    public SynDataView(Context context) {
        super(context);
        init(context);
    }

    public void setOnSettingListener(SettingView.OnSettingListener onSettingListener ){
        this.onSettingListener = onSettingListener;
    }

    public SynDataView(Context context, ControlSettingIosModel controlSettingIosModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        init(context);
    }

    public void setIListenerUpdate(IListenerUpdate iListenerUpdate) {
        this.iListenerUpdate = iListenerUpdate;
    }

    public SynDataView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SynDataView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    void init(Context context) {
        this.context = context;
        enableSync = SettingUtils.isSyncAutomaticallyEnable();
        updateSync();
    }

    @Override
    public void changeData(ControlSettingIosModel controlSettingIosModel) {
        super.changeData(controlSettingIosModel);
        if (controlSettingIosModel != null && controlSettingIosModel.getIconControl() != null && !controlSettingIosModel.getIconControl().equals(Constant.ICON_DEFAULT)) {
            String pathIcon = Constant.PATH_ASSET_THEME.concat(dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId() + "/" + controlSettingIosModel.getIconControl());
            if (dataSetupViewControlModel.getId() > 10000) {
                File file = new File(context.getFilesDir(), Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.getIdCategory() + "/" + dataSetupViewControlModel.getId() + "/" + controlSettingIosModel.getIconControl());
                pathIcon = file.getAbsolutePath();
            }
            Glide.with(context).load(pathIcon).placeholder(R.drawable.ic_syndata_ios).into(this);
        }
    }

    private void updateSync() {
        changeIsSelect(enableSync);
        if (iListenerUpdate!=null){
            iListenerUpdate.updateSynData(enableSync);
        }
    }

    @Override
    protected void click() {
//        if (SettingUtils.setDataSaver(context)) {
//            onSettingListener.onHide();
//        }
        SettingUtils.setSyncAutomatically();
        enableSync = !enableSync;
        updateSync();
    }

    @Override
    protected void longClick() {
//        if (App.tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR)) {
//            VibratorUtils.getInstance(context).vibrator(VibratorUtils.TIME_DEFAULT);
//        }
//        SettingUtils.intentChangeSync(getContext());
//        onSettingExpandListener.onHideControl();
    }

    @Override
    protected void onDown() {

    }

    @Override
    protected void onUp() {

    }

    public interface IListenerUpdate {
        void updateSynData(boolean b);
    }
}
