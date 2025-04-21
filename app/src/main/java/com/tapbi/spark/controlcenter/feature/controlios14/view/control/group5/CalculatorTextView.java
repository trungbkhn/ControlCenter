package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.databinding.BaseLayoutControlSingleFunctionTextViewBinding;
import com.tapbi.spark.controlcenter.databinding.LayoutControlCalculatorTextViewBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CalculatorTextView extends ConstraintLayoutBase {

    private Context context;
    private ControlSettingIosModel controlSettingIOS;
    private BaseLayoutControlSingleFunctionTextViewBinding binding;
    private boolean isSelect = false;
    private ArrayList<HashMap<String, Object>> items;
    private PackageManager pm;

    private OnClickSettingListener onClickSettingListener;

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }

    public CalculatorTextView(Context context) {
        super(context);
        init(context);
    }

    public CalculatorTextView(Context context, ControlSettingIosModel controlSettingIOS, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        this.controlSettingIOS = controlSettingIOS;
        init(context);
    }

    public CalculatorTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalculatorTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        binding.imgIcon.setImageResource(R.drawable.ic_calculator_ios);
        if (controlSettingIOS != null){
            changeColorBackground(controlSettingIOS.getBackgroundDefaultColorViewParent(), controlSettingIOS.getBackgroundSelectColorViewParent(), controlSettingIOS.getCornerBackgroundViewParent());
            if (controlSettingIOS.getIconControl() != null && !controlSettingIOS.getIconControl().equals(Constant.ICON_DEFAULT)){
                String pathIcon = Constant.PATH_ASSET_THEME.concat(dataSetupViewControlModel.getIdCategory()+"/"+dataSetupViewControlModel.getId()+"/"+controlSettingIOS.getIconControl());
                if (dataSetupViewControlModel.getId() > 10000){
                    File file = new File(context.getFilesDir(), Constant.FOLDER_THEMES_ASSETS +"/"+ dataSetupViewControlModel.getIdCategory()+"/"+dataSetupViewControlModel.getId()+"/"+controlSettingIOS.getIconControl());
                    pathIcon =  file.getAbsolutePath();
                }
                Glide.with(context).load(pathIcon).placeholder(R.drawable.ic_calculator_ios).into(binding.imgIcon);
            }
        }
        binding.tvFunction.setTypeface(dataSetupViewControlModel.getTypefaceText());
        binding.tvFunction.setText(context.getString(R.string.calculator));
        items = new ArrayList<HashMap<String, Object>>();
        pm = context.getPackageManager();
        Completable.fromAction(() -> {
            List<PackageInfo> packs = pm.getInstalledPackages(0);
            for (PackageInfo pi : packs) {
                if (pi.packageName.toLowerCase().contains("calcul")) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("appName", pi.applicationInfo.loadLabel(pm));
                    map.put("packageName", pi.packageName);
                    items.add(map);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
        updateUI();

    }



    private void openCalculator() {
        if (!items.isEmpty()) {
            String packageName = (String) items.get(0).get("packageName");
            Intent i = pm.getLaunchIntentForPackage(packageName);
            if (i != null) {
                try {
                    context.startActivity(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, context.getText(R.string.application_not_found), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(context, context.getText(R.string.application_not_found), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onTouchDown() {
        super.onTouchDown();
        if (onClickSettingListener != null) {
            onClickSettingListener.onClick();
        }
        openCalculator();
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
