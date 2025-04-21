package com.tapbi.spark.controlcenter.feature.controlios14.view.control;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository;
import com.tapbi.spark.controlcenter.databinding.ItemControlCenterIosBinding;
import com.tapbi.spark.controlcenter.feature.base.CustomImageView;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.ControlCenterIosModel;
import com.tapbi.spark.controlcenter.feature.controlios14.model.controlios.DataSetupViewControlModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.SettingBrightnessView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group4.SettingVolumeView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.EditControlView;
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper;

import java.util.ArrayList;

import timber.log.Timber;

public class ControlCenterIOSAdapter extends RecyclerView.Adapter<ControlCenterIOSAdapter.ViewHolder> {
    private Context context;
    private ArrayList<ControlCenterIosModel> listControl = new ArrayList<>();
    private CreateItemViewControlCenterIOS createItemViewControlCenterIOS;

    private ControlCenterViewIOS18.ItemControlCenterListener itemControlCenterListener;

    public ControlCenterIOSAdapter(Context context, DataSetupViewControlModel dataSetupViewControlModel) {
        this.context = context;
        createItemViewControlCenterIOS = new CreateItemViewControlCenterIOS(context, dataSetupViewControlModel);
    }

    public ControlCenterIOSAdapter(Context context, DataSetupViewControlModel dataSetupViewControlModel,CreateItemViewControlCenterIOS createItemViewControlCenterIOS) {
        this.context = context;
        if (createItemViewControlCenterIOS == null){
            this.createItemViewControlCenterIOS = new CreateItemViewControlCenterIOS(context, dataSetupViewControlModel);
        } else {
            this.createItemViewControlCenterIOS = createItemViewControlCenterIOS;
        }
    }

    public ControlCenterIOSAdapter(Context context, ArrayList<ControlCenterIosModel> listControl1, CreateItemViewControlCenterIOS createItemViewControlCenterIOS) {
        this.context = context;
        this.listControl.clear();
        this.listControl.addAll(listControl1);
        this.createItemViewControlCenterIOS = createItemViewControlCenterIOS;
    }

    public CreateItemViewControlCenterIOS getCreateItemViewControlCenterIOS() {
        return createItemViewControlCenterIOS;
    }

    public void changeListControl(ArrayList<ControlCenterIosModel> listControl1) {
        this.listControl.clear();
        this.listControl.addAll(listControl1);
        notifyDataSetChanged();
    }

    public void setListenerItem(ControlCenterViewIOS18.ItemControlCenterListener itemControlCenterListener) {
        Timber.e("NVQ setListenerItem+++ " + (itemControlCenterListener==null));
        this.itemControlCenterListener = itemControlCenterListener;
    }

    public void setFontTypeFace(Typeface typeface) {
        createItemViewControlCenterIOS.setFontType(typeface);
        for (int i = 0; i < listControl.size(); i++) {
            if (listControl.get(i).getKeyControl().equals(Constant.KEY_CONTROL_MUSIC_4_2) || listControl.get(i).getKeyControl().equals(Constant.KEY_CONTROL_SCREEN_TIME_OUT)) {
                notifyItemChanged(i, listControl.get(i));
            }
        }

    }



    public ArrayList<ControlCenterIosModel> getListControl() {
        return listControl;
    }


    @NonNull
    @Override
    public ControlCenterIOSAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemControlCenterIosBinding binding = ItemControlCenterIosBinding.inflate(LayoutInflater.from(context));
//        addItem(binding);
        Timber.e("NVQ ControlCenterIOSAdapter onCreateViewHolder +++");
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ControlCenterIOSAdapter.ViewHolder holder, int position) {
//        holder.binding.flParent.addView(getView(listControl.get(position).getKeyControl(), context));
        Timber.e("NVQ ControlCenterIOSAdapter onBindViewHolder +++");
        addItem(holder, position);
    }

    @Override
    public int getItemCount() {
        return listControl != null ? listControl.size() : 0;
    }

    private void addItem(ControlCenterIOSAdapter.ViewHolder holder, int position) {
        if (listControl != null && !listControl.isEmpty()) {
            ControlCenterIosModel model = listControl.get(position);
            holder.binding.flParent.removeAllViews();
            Timber.e("NVQ ControlCenterIOSAdapter addItem +++ " + position  + " // " +model);
            View view = getView(model.getKeyControl(), context, model);
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view); // <- fix
            }
            if (view instanceof EditControlView){
                ((EditControlView) view).setItemControlCenterListener(itemControlCenterListener);
            }

            holder.isSingleItem = (position > 6) && (model.getRatioHeight() == 4 && model.getRatioWidght() == 4 && !model.getKeyControl().equals(Constant.KEY_CONTROL_ADD));
            holder.binding.flParent.addView(view);
            holder.loadViewRemove(ThemesRepository.isControlEditing());
            holder.binding.icRemove.setOnClickListener(v -> {
                Timber.e("NVQ itemControlCenterListener +++" + (itemControlCenterListener==null));
                if (itemControlCenterListener != null){
                    ViewHelper.preventTwoClick(v);
                    itemControlCenterListener.onClickRemove(holder.getAdapterPosition());
                }

            });
            holder.binding.flParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemControlCenterListener != null && holder.binding.icRemove.getVisibility() == VISIBLE) {
                        ViewHelper.preventTwoClick(v);
                        Timber.e("NVQ flparent click setOnClickListener");
                        itemControlCenterListener.onClickChange(holder.getAdapterPosition());
                    }
                }
            });

        }
    }

    public View getView(String keyControl, Context context, ControlCenterIosModel controlCenterIosModel) {
        Timber.e("NVQ ControlCenterIOSAdapter keyControl "+keyControl);
        switch (keyControl) {
            case Constant.KEY_CONTROL_SETTINGS_4_1:
//                return new SettingViewHorizontal(context, controlCenterIosModel.getControlSettingIosModel());
                return createItemViewControlCenterIOS.getSettingViewHorizontal(controlCenterIosModel);

            case Constant.KEY_CONTROL_SETTINGS_4_2:
                return createItemViewControlCenterIOS.getSettingView(controlCenterIosModel);

            case Constant.KEY_CONTROL_SETTINGS_2_2:
                return createItemViewControlCenterIOS.getSettingViewSquare(controlCenterIosModel);

            case Constant.KEY_CONTROL_SETTINGS_2_2_TEXT_2:
                return createItemViewControlCenterIOS.getSettingSquareTextView(controlCenterIosModel);

            case Constant.KEY_CONTROL_SETTINGS_2_2_TEXT:
                return createItemViewControlCenterIOS.getSettingViewVertical(controlCenterIosModel);

            case Constant.KEY_CONTROL_MUSIC_4_1:
                return createItemViewControlCenterIOS.getMusicView(controlCenterIosModel);

            case Constant.KEY_CONTROL_MUSIC_2_2:
                return createItemViewControlCenterIOS.getControlMusicIosSquareView(controlCenterIosModel);

            case Constant.KEY_CONTROL_IOS_TOP_2_2:
                return createItemViewControlCenterIOS.getControlViewTopIOS18(controlCenterIosModel);

            case Constant.KEY_CONTROL_MUSIC_IOS_TOP_2_2:
                return createItemViewControlCenterIOS.getControlMusicIOS18(controlCenterIosModel);

            case Constant.KEY_CONTROL_MUSIC_4_2:
                return createItemViewControlCenterIOS.getControlMusicIosView(controlCenterIosModel);

            case Constant.KEY_CONTROL_VOLUME_LANDSCAPE:
                SettingVolumeView settingVolumeView = createItemViewControlCenterIOS.getSettingVolumeView(controlCenterIosModel);
                settingVolumeView.changeIsHorizontal(true);
                return settingVolumeView;

            case Constant.KEY_CONTROL_VOLUME_PORTRAIT:
                return createItemViewControlCenterIOS.getSettingVolumeView(controlCenterIosModel);

            case Constant.KEY_CONTROL_VOLUME_LANDSCAPE_THUMB:
                return createItemViewControlCenterIOS.getSettingVolumeTextView(controlCenterIosModel);

            case Constant.KEY_CONTROL_VOLUME_LANDSCAPE_2:
                return createItemViewControlCenterIOS.getSettingVolumeView2(controlCenterIosModel);

            case Constant.KEY_CONTROL_BRIGHTNESS_LANDSCAPE_THUMB:
                return createItemViewControlCenterIOS.getBrightnessTextView(controlCenterIosModel);

            case Constant.KEY_CONTROL_BRIGHTNESS_LANDSCAPE_2:
                return createItemViewControlCenterIOS.getSettingBrightnessView2(controlCenterIosModel);

            case Constant.KEY_CONTROL_BRIGHTNESS_PORTRAIT:
                return createItemViewControlCenterIOS.getSettingBrightnessView(controlCenterIosModel);

            case Constant.KEY_CONTROL_BRIGHTNESS_LANDSCAPE:
                SettingBrightnessView settingBrightnessView = createItemViewControlCenterIOS.getSettingBrightnessView(controlCenterIosModel);
                settingBrightnessView.changeIsHorizontal(true);
                return settingBrightnessView;

            case Constant.KEY_CONTROL_AIRPLANE_TEXT:
                return createItemViewControlCenterIOS.getControlAirplaneView(controlCenterIosModel);

            case Constant.KEY_CONTROL_ROTATE_RECORD_FLASHT_DARKMODE:
                return createItemViewControlCenterIOS.getControlRotateRecordFlashDarkmode(controlCenterIosModel);

            case Constant.KEY_CONTROL_AIRPLANE_RECORD_SYNDATA:
                return createItemViewControlCenterIOS.getControlAirplaneRecordSynDataView(controlCenterIosModel);

            case Constant.KEY_CONTROL_ROTATE:
                return createItemViewControlCenterIOS.getRotateView(controlCenterIosModel);

            case Constant.KEY_CONTROL_ROTATE_RECTANGLE:
                return createItemViewControlCenterIOS.getRotateRectangleView(controlCenterIosModel);

            case Constant.KEY_CONTROL_RECORD:
                return createItemViewControlCenterIOS.getScreenRecordActionView(controlCenterIosModel);

            case Constant.KEY_CONTROL_RECORD_TEXT:
                return createItemViewControlCenterIOS.getScreenRecordTextView(controlCenterIosModel);

            case Constant.KEY_CONTROL_SCREEN_TIME_OUT:
                return createItemViewControlCenterIOS.getScreenTimeoutAction(controlCenterIosModel);

            case Constant.KEY_CONTROL_SCREEN_TIME_OUT_SQUARE:
                return createItemViewControlCenterIOS.getScreenTimeoutSquareView(controlCenterIosModel);

            case Constant.KEY_CONTROL_CALCULATOR:
                return createItemViewControlCenterIOS.getCalculatorActionView(controlCenterIosModel);

            case Constant.KEY_CONTROL_CALCULATOR_TEXT:
                return createItemViewControlCenterIOS.getCalculatorTextView(controlCenterIosModel);

            case Constant.KEY_CONTROL_CAMERA:
                return createItemViewControlCenterIOS.getCameraAcitonView(controlCenterIosModel);

            case Constant.KEY_CONTROL_CAMERA_TEXT:
                return createItemViewControlCenterIOS.getCameraTextView(controlCenterIosModel);

            case Constant.KEY_CONTROL_NOTE:
                return createItemViewControlCenterIOS.getNoteActionView(controlCenterIosModel);
            case Constant.KEY_CONTROL_NOTE_TEXT:
                return createItemViewControlCenterIOS.getNoteTextView(controlCenterIosModel);
            case Constant.KEY_CONTROL_PIN:
                return createItemViewControlCenterIOS.getLowPowerActionView(controlCenterIosModel);
            case Constant.KEY_CONTROL_PIN_TEXT:
                return createItemViewControlCenterIOS.getLowPowerTextView(controlCenterIosModel);
            case Constant.KEY_CONTROL_DARKMODE:
                return createItemViewControlCenterIOS.getDarkModeActionView(controlCenterIosModel);
            case Constant.KEY_CONTROL_DARKMODE_TEXT:
                return createItemViewControlCenterIOS.getDarkModeTextView(controlCenterIosModel);
            case Constant.KEY_CONTROL_SILENT:
                return createItemViewControlCenterIOS.getSilentView(controlCenterIosModel);
            case Constant.KEY_CONTROL_SILENT_21:
                return createItemViewControlCenterIOS.getSilentRectangleView(controlCenterIosModel);
            case Constant.KEY_CONTROL_ALARM:
                return createItemViewControlCenterIOS.getTimeActionView(controlCenterIosModel);
            case Constant.KEY_CONTROL_ALARM_TEXT:
                return createItemViewControlCenterIOS.getAlarmTextView(controlCenterIosModel);
            case Constant.KEY_CONTROL_FLASH:
                return createItemViewControlCenterIOS.getFlashLightView(controlCenterIosModel);
            case Constant.KEY_CONTROL_ADD:
                return createItemViewControlCenterIOS.getViewPlus(controlCenterIosModel);
            case Constant.KEY_CONTROL_FLASH_TEXT:
                return createItemViewControlCenterIOS.getFlashlightTextView(controlCenterIosModel);
            case Constant.KEY_CONTROL_SYNDATA_TEXT:
                return createItemViewControlCenterIOS.getSynDataTextView(controlCenterIosModel);
            case Constant.KEY_CONTROL_OPEN_APP:
                return createItemViewControlCenterIOS.getCustomControlImageView(controlCenterIosModel);
            default:
                return new CustomImageView(context);
        }


    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemControlCenterIosBinding binding;
        public boolean isSingleItem = false;

        public ViewHolder(@NonNull ItemControlCenterIosBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void loadViewRemove(boolean isEdit){
            if (isEdit && isSingleItem){
                binding.icRemove.setVisibility(VISIBLE);
            } else {
                binding.icRemove.setVisibility(GONE);
            }
        }
    }
}
