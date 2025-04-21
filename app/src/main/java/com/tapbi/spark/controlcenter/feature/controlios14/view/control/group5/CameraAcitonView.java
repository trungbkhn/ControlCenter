package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.content.Context;
import android.os.Handler;
import androidx.annotation.Nullable;

import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.utils.SettingUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;

import java.io.File;


public class CameraAcitonView extends ImageBase {

    private Context context;

    private OnCameraActionListener onCameraActionListener;
    private Handler handler;
    private OnClickSettingListener onClickSettingListener;

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }

    public CameraAcitonView(Context context) {
        super(context);
        init(context);
    }

    public CameraAcitonView(Context context, ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public CameraAcitonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CameraAcitonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        handler = new Handler();
        setImageResource(R.drawable.ic_camera_ios);
        if (controlSettingIosModel != null && controlSettingIosModel.getIconControl() != null && !controlSettingIosModel.getIconControl().equals(Constant.ICON_DEFAULT)){
            String pathIcon = Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.getIdCategory() + "/" +
                    dataSetupViewControlModel.getId() + "/" + controlSettingIosModel.getIconControl();
            File file = new File(context.getFilesDir(), pathIcon);
            boolean isAssets = !file.exists();
            String loadPath = isAssets ? pathIcon : file.getAbsolutePath();
            loadImage(context, loadPath, isAssets,R.drawable.ic_camera_ios);
        }
        initColorIcon();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingIcon = (int) (w * 0.267f);
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
    }

    @Override
    protected void click() {
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
//                == PackageManager.PERMISSION_DENIED) {
//            if (onClickSettingListener != null) {
//                onClickSettingListener.onClick();
//            }
//            SettingUtils.intentActivityRequestPermission(context, new String[]{Manifest.permission.CAMERA});
//            return;
//        }

                if (onCameraActionListener != null) {
                    onCameraActionListener.onOpenCamera();
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (onClickSettingListener != null) {
                            onClickSettingListener.onClick();
                        }
                        SettingUtils.openCamera(context);
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

    public interface OnCameraActionListener {
        void onOpenCamera();
    }

    public void setOnCameraActionListener(OnCameraActionListener onCameraActionListener) {
        this.onCameraActionListener = onCameraActionListener;
    }
}
