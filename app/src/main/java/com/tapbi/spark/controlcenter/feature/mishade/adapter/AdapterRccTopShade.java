package com.tapbi.spark.controlcenter.feature.mishade.adapter;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.MessageEvent;
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem;
import com.tapbi.spark.controlcenter.feature.controlcenter.adapter.AdapterSettingMiControl;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView;
import com.tapbi.spark.controlcenter.feature.controlcenter.textview.TextViewAutoRun;
import com.tapbi.spark.controlcenter.feature.mishade.view.ItemCollapseRecyclerviewShade;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AdapterRccTopShade extends RecyclerView.Adapter<AdapterRccTopShade.Holder> {
    public int size;
    private AdapterSettingMiControl.TouchItemView touchItemView;
    private List<InfoSystem> infoSystems;
    private CloseMiControlView closeMiControlView;
    private float w;
    private float valueF = 1f;
    private int orientation;
    private int posRcc;

    private ArrayList<ItemCollapseRecyclerviewShade> holderViewList = new ArrayList<>();

    public AdapterRccTopShade(List<InfoSystem> infoSystems, CloseMiControlView closeMiControlView, float w, AdapterSettingMiControl.TouchItemView touchItemView, int size, int orientation, int posRcc) {
        if (infoSystems == null) {
            this.infoSystems = new ArrayList<>();
        } else {
            this.infoSystems = infoSystems;
        }
        this.orientation = orientation;
        this.closeMiControlView = closeMiControlView;
        this.w = w;
        this.touchItemView = touchItemView;
        this.size = size;
        this.posRcc = posRcc;
    }

    public float getValueF() {
        return valueF;
    }

    public void setValueF(float valueF) {
        this.valueF = valueF;
        //Timber.e("setValueF");
        for (int i = 0; i < infoSystems.size(); i++) {
            notifyItemChanged(i, infoSystems.get(i));
        }
//        notifyDataSetChanged();
    }

    public void unregister() {

        holderViewList.clear();
    }


    public void updateActionView(String action, boolean b) {
        for (int i = 0; i < holderViewList.size(); i++) {
            ItemCollapseRecyclerviewShade view = holderViewList.get(i);
            if (action.equals(MethodUtils.getAction(view.getContext(), view.getBinding().tvNameAction.getText().toString()))) {
                if (action.equals(Constant.STRING_ACTION_BATTERY)) {
                    EventBus.getDefault().post(new MessageEvent(Constant.CHANGE_LOW_POWER));
                }
                view.setStageAction(action, b);
                notifyItemChanged(i, holderViewList.get(i));
                break;
            }
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collapse_shade, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int pos) {
        int position = holder.getLayoutPosition();
        if (holder.parent.getFirst()) {
            holderViewList.add(holder.parent);
            holder.parent.data(infoSystems.get(position), position, closeMiControlView);
            int newW;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                newW = (int) ((w / 5) * 0.7f);
            } else {
                newW = (int) ((w / 4) * 0.7f);
            }
            ConstraintLayout.LayoutParams layoutParamsImageAction = (ConstraintLayout.LayoutParams) holder.imageView.getLayoutParams();
            layoutParamsImageAction.width = newW;
            layoutParamsImageAction.height = newW;
            holder.imageView.setLayoutParams(layoutParamsImageAction);
        }

        ConstraintLayout.LayoutParams layoutParams;
        if (valueF > 100) {
            valueF = 100f;
        } else if (valueF < 0) {
            valueF = 0f;
        }
        holder.textView.setAlpha((valueF / 100f));

        if (position == 4 && size == 5) {
            holder.itemView.setAlpha(1f - (valueF / 100f));
            layoutParams = new ConstraintLayout.LayoutParams((int) ((w / 5) - (valueF / 100 * (w / 5))), (int) (w / 5));
        } else {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutParams = new ConstraintLayout.LayoutParams((int) ((w / 5) + (valueF / 100 * (w / 5)) * 0.25f), (int) (w / 5));
            } else {
                layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutParams.topMargin = (int) (w / 5 * 0.2f);
        } else {
            layoutParams.topMargin = (int) (w / 5 * 0.1f);
        }
        holder.itemView.setLayoutParams(layoutParams);
        holder.parent.setFirst(false);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class Holder extends RecyclerView.ViewHolder {
        ItemCollapseRecyclerviewShade parent;
        ConstraintLayout constraintLayout;
        TextViewAutoRun textView;
        ImageView imageView;


        public Holder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.itemCollapse);
            constraintLayout = itemView.findViewById(R.id.layoutItemCollapse);
            textView = constraintLayout.findViewById(R.id.tvNameAction);
            imageView = constraintLayout.findViewById(R.id.imgIcon);

        }
    }

}
