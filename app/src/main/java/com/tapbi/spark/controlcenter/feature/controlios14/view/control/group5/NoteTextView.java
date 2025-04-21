package com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
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
import com.tapbi.spark.controlcenter.databinding.LayoutControlNoteTextViewBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlSettingIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.ConstraintLayoutBase;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.callback.OnClickSettingListener;
import com.tapbi.spark.controlcenter.utils.ControlCustomizeManager;

import java.io.File;

import timber.log.Timber;

public class NoteTextView extends ConstraintLayoutBase {

    private Context context;
    private ControlSettingIosModel controlSettingIOS;
    private BaseLayoutControlSingleFunctionTextViewBinding binding;
    private boolean isSelect = false;
    private Handler handler;

    private OnClickSettingListener onClickSettingListener;

    public void setOnClickSettingListener(OnClickSettingListener onClickSettingListener) {
        this.onClickSettingListener = onClickSettingListener;
    }

    public NoteTextView(Context context) {
        super(context);
        init(context);
    }

    public NoteTextView(Context context, ControlSettingIosModel controlSettingIOS, DataSetupViewControlModel dataSetupViewControlModel) {
        super(context);
        this.dataSetupViewControlModel = dataSetupViewControlModel;
        this.controlSettingIOS = controlSettingIOS;
        init(context);
    }

    public NoteTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NoteTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        binding = BaseLayoutControlSingleFunctionTextViewBinding.inflate(LayoutInflater.from(context), this, true);
        binding.tvFunction.setSelected(true);
        handler = new Handler();
//        binding.imgNote.setImageResource(R.drawable.ic_note);
        initView();
    }



    private void openNote() {
        boolean haveAppNote = ControlCustomizeManager.getActionNote(context);
        Timber.e("hachung haveAppNote:"+haveAppNote);
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
    protected void onTouchDown() {
        super.onTouchDown();
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

    public void initView(){
        binding.imgIcon.setImageResource(R.drawable.ic_note_ios);
        if (controlSettingIOS != null){
            changeColorBackground(controlSettingIOS.getBackgroundDefaultColorViewParent(), controlSettingIOS.getBackgroundSelectColorViewParent(), controlSettingIOS.getCornerBackgroundViewParent());
            if (controlSettingIOS.getIconControl() != null && !controlSettingIOS.getIconControl().equals(Constant.ICON_DEFAULT)){
                String pathIcon = Constant.PATH_ASSET_THEME.concat(dataSetupViewControlModel.getIdCategory()+"/"+dataSetupViewControlModel.getId()+"/"+controlSettingIOS.getIconControl());
                if (dataSetupViewControlModel.getId() > 10000){
                    File file = new File(context.getFilesDir(), Constant.FOLDER_THEMES_ASSETS +"/"+ dataSetupViewControlModel.getIdCategory()+"/"+dataSetupViewControlModel.getId()+"/"+controlSettingIOS.getIconControl());
                    pathIcon =  file.getAbsolutePath();
                }
                Glide.with(context).load(pathIcon).placeholder(R.drawable.ic_note_ios).into(binding.imgIcon);
            }
        }
        binding.tvFunction.setTypeface(dataSetupViewControlModel.getTypefaceText());
        binding.tvFunction.setText(context.getString(R.string.notes));
        updateUI();
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