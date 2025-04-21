package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import android.util.AttributeSet;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ImageBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class CalculatorActionView extends ImageBase {

    private Context context;
    private ArrayList<HashMap<String, Object>> items;
    private PackageManager pm;

    private OnClickSettingListener onClickSettingListener;

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }

    public CalculatorActionView(Context context, ControlSettingIosModel controlSettingIosModel, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.controlSettingIosModel = controlSettingIosModel;
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        init(context);
    }

    public CalculatorActionView(Context context) {
        super(context);
        init(context);
    }

    public CalculatorActionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalculatorActionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
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

        if (controlSettingIosModel != null && controlSettingIosModel.getIconControl() != null && !controlSettingIosModel.getIconControl().equals(Constant.ICON_DEFAULT)){
            String pathIcon = Constant.FOLDER_THEMES_ASSETS + "/" + dataSetupViewControlModel.getIdCategory() + "/" +
                    dataSetupViewControlModel.getId() + "/" + controlSettingIosModel.getIconControl();
            File file = new File(context.getFilesDir(), pathIcon);
            boolean isAssets = !file.exists();
            String loadPath = isAssets ? pathIcon : file.getAbsolutePath();
            loadImage(context, loadPath, isAssets,R.drawable.ic_calculator_ios);
        }else {
            setImageResource(R.drawable.ic_calculator_ios);
        }
        initColorIcon();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingIcon = (int) (w * 0.267f);
        setPadding(paddingIcon, paddingIcon, paddingIcon, paddingIcon);
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
    protected void click() {
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
        if (onClickSettingListener != null) {
            onClickSettingListener.onClick();
        }
        openCalculator();
//            }
//        }, 300);
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
