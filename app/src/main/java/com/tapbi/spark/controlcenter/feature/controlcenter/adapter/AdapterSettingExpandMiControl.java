package com.tapbi.spark.controlcenter.feature.controlcenter.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView;
import com.tapbi.spark.controlcenter.feature.controlcenter.view.control.micenter.view.ItemExpandRecyclerview;
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class AdapterSettingExpandMiControl extends RecyclerView.Adapter<AdapterSettingExpandMiControl.Holder> {
    private final AdapterSettingMiControl.TouchItemView touchItemView;
    private final CloseMiControlView closeMiControlView;
    private final float heightRcc;
    private List<InfoSystem> infoSystems;
    private final DensityUtils densityUtils = new DensityUtils();
    private final ArrayList<ItemExpandRecyclerview> holderViewList = new ArrayList<>();

    public AdapterSettingExpandMiControl(List<InfoSystem> infoSystems, AdapterSettingMiControl.TouchItemView touchItemView, float heightRcc, CloseMiControlView closeMiControlView) {

        this.heightRcc = heightRcc;
        this.touchItemView = touchItemView;
        this.infoSystems = infoSystems;
        this.closeMiControlView = closeMiControlView;
    }

    public void setNewData(List<InfoSystem> infoSystems) {
        this.infoSystems = infoSystems;
        notifyDataSetChanged();
    }


    public void unregister() {
//        for (ItemExpandRecyclerview view : holderViewList) {
//            view.unRegisterReceiver();
//        }
        holderViewList.clear();
    }


    public void updateActionViewExpand(String action, boolean b) {
        for (int i = 0; i < holderViewList.size(); i++) {
            ItemExpandRecyclerview view = holderViewList.get(i);
            if (action.equals(MethodUtils.getAction(view.context, view.nameAction.getText().toString()))) {
                Timber.e("hachung action:"+action+"/b : "+b);
                view.setStageAction(action, b,i);
                notifyItemChanged(i,holderViewList.get(i));
                break;
            }
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_setting_control_mi, parent, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int pos) {
        int position = holder.getLayoutPosition();
        int valueMarin = (int) densityUtils.pxFromDp(holder.itemView.getContext(), 8f);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (heightRcc / 2) - valueMarin);
        switch (position) {
            case 0:
                layoutParams.setMargins(0, 0, (int) (valueMarin * 0.75), valueMarin);
                break;
            case 1:
                layoutParams.setMargins((int) (valueMarin * 0.75), 0, 0, valueMarin);
                break;
            case 2:
                layoutParams.setMargins(0, (int) (valueMarin * 0.5), (int) (valueMarin * 0.75), 0);
                break;
            case 3:
                layoutParams.setMargins((int) (valueMarin * 0.75), (int) (valueMarin * 0.5), 0, 0);
                break;
        }

        holder.itemView.setLayoutParams(layoutParams);
        ItemExpandRecyclerview itemExpandRecyclerview = holder.itemView.findViewById(R.id.parentItemExpand);
        if (itemExpandRecyclerview.isFirst) {
            holderViewList.add(itemExpandRecyclerview);
        }
        itemExpandRecyclerview.setData(infoSystems.get(position), position, closeMiControlView);
        holder.itemView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                touchItemView.down(event.getRawY());
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                touchItemView.up();
            }
            return false;
        });
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return infoSystems.size();
    }


    public class Holder extends RecyclerView.ViewHolder {
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
