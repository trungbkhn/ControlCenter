package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;


import com.bumptech.glide.Glide;
import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.databinding.BaseLayoutControlSingleFunctionTextViewBinding;
import com.tapbi.spark.controlcenter.databinding.LayoutControlCalculatorTextViewBinding;
import com.tapbi.spark.controlcenter.databinding.LayoutControlCameraTextViewBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import java.io.File;

public class CameraTextView extends ConstraintLayoutBase {

    private Context context;
    private ControlSettingIosModel controlSettingIOS;
    private BaseLayoutControlSingleFunctionTextViewBinding binding;
    private boolean isSelect = false;
    private CameraAcitonView.OnCameraActionListener onCameraActionListener;
    private Handler handler;
    private OnClickSettingListener onClickSettingListener;

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }


    public CameraTextView(Context context) {
        super(context);
        init(context);
    }

    public CameraTextView(Context context, ControlSettingIosModel controlSettingIOS, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        this.controlSettingIOS = controlSettingIOS;
        init(context);
    }

    public CameraTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CameraTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        binding = BaseLayoutControlSingleFunctionTextViewBinding.inflate(LayoutInflater.from(context), this, true);
        binding.tvFunction.setSelected(true);
        initView();
    }

    public void initView(){
        handler = new Handler();
        binding.imgIcon.setImageResource(R.drawable.ic_camera_ios);
        if (controlSettingIOS != null){
            changeColorBackground(controlSettingIOS.getBackgroundDefaultColorViewParent(), controlSettingIOS.getBackgroundSelectColorViewParent(), controlSettingIOS.getCornerBackgroundViewParent());
            if (controlSettingIOS.getIconControl() != null && !controlSettingIOS.getIconControl().equals(Constant.ICON_DEFAULT)){
                String pathIcon = Constant.PATH_ASSET_THEME.concat(dataSetupViewControlModel.getIdCategory()+"/"+dataSetupViewControlModel.getId()+"/"+controlSettingIOS.getIconControl());
                if (dataSetupViewControlModel.getId() > 10000){
                    File file = new File(context.getFilesDir(), Constant.FOLDER_THEMES_ASSETS +"/"+ dataSetupViewControlModel.getIdCategory()+"/"+dataSetupViewControlModel.getId()+"/"+controlSettingIOS.getIconControl());
                    pathIcon =  file.getAbsolutePath();
                }
                Glide.with(context).load(pathIcon).placeholder(R.drawable.ic_camera_ios).into(binding.imgIcon);
            }
        }
        binding.tvFunction.setTypeface(dataSetupViewControlModel.getTypefaceText());
        binding.tvFunction.setText(context.getString(R.string.camera));
        updateUI();


    }




    @Override
    protected void onTouchDown() {
        super.onTouchDown();
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

    public interface OnCameraActionListener {
        void onOpenCamera();
    }

    public void setOnCameraActionListener(CameraAcitonView.OnCameraActionListener onCameraActionListener) {
        this.onCameraActionListener = onCameraActionListener;
    }

    private void updateUI(){
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