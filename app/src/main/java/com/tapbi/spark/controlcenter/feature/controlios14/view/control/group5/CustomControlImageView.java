package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import timber.log.Timber;


public class CustomControlImageView extends ImageBase {
    private OnCustomControlImageViewListener onCustomControlImageViewListener;
    private Handler handler;
    private Context context;
    public void setOnCustomControlImageViewListener(OnCustomControlImageViewListener onCustomControlImageViewListener) {
        this.onCustomControlImageViewListener = onCustomControlImageViewListener;
    }

    public CustomControlImageView(Context context) {
        super(context);
        init(context);
    }

    public CustomControlImageView(Context context, ControlSettingIosModel controlSettingIosModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        init(context);
    }

    public CustomControlImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomControlImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        handler = new Handler();
        setBackgroundImage();
        initView();
    }

    private void initView(){
        if (controlSettingIosModel != null){
            Drawable drawable = MethodUtils.getIconFromPackageName(context, controlSettingIosModel.getIconControl());
            if (drawable != null) {
                setImageDrawable(drawable);
            }

        }
    }


    public String getPackage(){
        if (controlSettingIosModel != null){
            return controlSettingIosModel.getIconControl();
        }else {
            return "";
        }
    }

    @Override
    public void changeData(ControlSettingIosModel controlSettingIosModel) {
        super.changeData(controlSettingIosModel);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingIcon = (int) (w * 0.3f);
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
    }

    @Override
    protected void click() {
        SettingUtils.intentOtherApp(context, controlSettingIosModel.getIconControl());
        handler.postDelayed(() -> {
            if (onCustomControlImageViewListener != null) {
                onCustomControlImageViewListener.onClick();
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

    public interface OnCustomControlImageViewListener {
        void onClick();
    }
}
