package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.utils.ControlCustomizeManager;

import java.io.File;

public class NoteActionView extends ImageBase {

    private Context context;
    private Handler handler;

    private OnClickSettingListener onClickSettingListener;

    public NoteActionView(Context context) {
        super(context);
        init(context);
    }

    public NoteActionView(Context context, ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public NoteActionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoteActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }

    private void init(Context context) {
        this.context = context;
        handler = new Handler();
        setImageResource(R.drawable.ic_note);
        if (controlSettingIosModel != null && controlSettingIosModel.getIconControl() != null && !controlSettingIosModel.getIconControl().equals(Constant.ICON_DEFAULT)) {
            String pathIcon = Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.getIdCategory() + "/" +
                    dataSetupViewControlModel.getId() + "/" + controlSettingIosModel.getIconControl();
            File file = new File(context.getFilesDir(), pathIcon);
            boolean isAssets = !file.exists();
            String loadPath = isAssets ? pathIcon : file.getAbsolutePath();
            loadImage(context, loadPath, isAssets, R.drawable.ic_note);
        }
        initColorIcon();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        int paddingIcon = (int) (w * 0.25f);
//        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
        int paddingIcon = (int) (w * 0.267);
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
    }

    private void openNote() {
        boolean haveAppNote = ControlCustomizeManager.getActionNote(context);
        if (haveAppNote) {
            Intent i = context.getPackageManager().getLaunchIntentForPackage(ControlCustomizeManager.packageAppNote);
            if (i != null) {
                context.startActivity(i);
            }else {
                Toast.makeText(context, context.getText(R.string.application_not_found), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, context.getText(R.string.application_not_found), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void click() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (onClickSettingListener != null) {
                    onClickSettingListener.onClick();
                }
                openNote();
            }
        }, 300);
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