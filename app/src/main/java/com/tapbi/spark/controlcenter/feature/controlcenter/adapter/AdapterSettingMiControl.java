package com.tapbi.spark.controlcenter.feature.controlcenter.adapter;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseMiControlView;
import com.tapbi.spark.controlcenter.feature.controlcenter.textview.TextViewAutoRun;
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem;
import com.tapbi.spark.controlcenter.feature.mishade.view.ItemCollapseRecyclerviewShade;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class AdapterSettingMiControl extends RecyclerView.Adapter<AdapterSettingMiControl.Holder> {
    public float valueF = 0f;
    private ValueAuto valueAuto;
    private float w;
    private float margin;
    private List<InfoSystem> infoSystems;
    private CloseMiControlView closeMiControlView;
    private boolean unregister = false;
    private int orientation = Configuration.ORIENTATION_PORTRAIT;
    private ArrayList<ItemCollapseRecyclerviewShade> holderViewList = new ArrayList<>();
    private RecyclerView rcv;

    public AdapterSettingMiControl(RecyclerView rcv, float valueF, List<InfoSystem> infoSystems, ValueAuto valueAuto, float w, float margin, CloseMiControlView closeMiControlView, int orientation) {
        this.rcv = rcv;
        this.valueAuto = valueAuto;
        this.w = w;
        this.valueF = valueF;
        this.margin = margin;
        this.infoSystems = infoSystems;
        this.closeMiControlView = closeMiControlView;
        this.orientation = orientation;
    }

    public boolean isUnregister() {
        return unregister;
    }

    public void setUnregister(boolean unregister) {
        this.unregister = unregister;
        unregister();
        notifyDataSetChanged();
    }

    public void setNewData(List<InfoSystem> infoSystems) {
        this.infoSystems = infoSystems;
        notifyDataSetChanged();
    }

    public void unregister() {
        holderViewList.clear();
    }

    public void updateActionView(String action, boolean b) {
        for (int i = 0; i < holderViewList.size(); i++) {
            ItemCollapseRecyclerviewShade view = holderViewList.get(i);
            if (action.equals(MethodUtils.getAction(view.getContext(), view.getBinding().tvNameAction.getText().toString()))) {
                view.setStageAction(action, b);
                notifyItemChanged(i, holderViewList.get(i));
                break;
            }
        }
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collapse_setting_control_mi, parent, false));
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
    public void onBindViewHolder(@NonNull Holder holder, int pos) {
        int position = holder.getAdapterPosition();
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return infoSystems.size();
    }

    public void endValue() {
        if (valueF > 0.5) {
            valueAuto.value(true);
            for (float i = valueF; i < 1.1f; i += 0.1f) {
                valueF = i;
                notyItem();
            }
        } else {
            valueAuto.value(false);
            for (float i = valueF; i > -0.1f; i -= 0.1f) {
                valueF = i;
                notyItem();
            }
        }
        if (Float.isNaN(valueF)) {
            valueF = 0f;
        }
    }

    public void notyItem() {
        for (int i = 0; i < 13; i++) {
            notifyItemChanged(i);
        }
    }

    public void notifyItemWhenTouchParent() {
        for (int i = 0; i < getItemCount(); i++) {
            RecyclerView.ViewHolder holder = rcv.findViewHolderForAdapterPosition(i);
            if (holder instanceof Holder) {
                ((Holder) holder).updateWhenTouchParent();
            }
        }
    }

    public void passValue(float f) {
        if (valueF > 1.1f || valueF < -0.1f) return;
        valueF = f;
        if (Float.isNaN(valueF)) {
            valueF = 0f;
        }
    }

    public interface TouchItemView {
        void down(float value);

        void up();
    }

    public interface ValueAuto {
        void value(boolean value);
    }

    public class Holder extends RecyclerView.ViewHolder {
        private int position;

        public Holder(@NonNull View itemView) {
            super(itemView);
//      ((ItemCollapseRecyclerview)itemView).unRegisterReceiver();
        }

        private void bind(int pos) {
            this.position = pos;
            ItemCollapseRecyclerviewShade parent = itemView.findViewById(R.id.itemCollapse);
            if (position >= infoSystems.size()) return;
            parent.data(infoSystems.get(position), position, closeMiControlView);
            ConstraintLayout constraintLayout = parent.findViewById(R.id.layoutItemCollapse);
            TextViewAutoRun textView = constraintLayout.findViewById(R.id.tvNameAction);
            ImageView imageView = constraintLayout.findViewById(R.id.imgIcon);
            if (parent.getFirst()) {
                holderViewList.add(parent);
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (w / 4.4));
                if (orientation == Configuration.ORIENTATION_LANDSCAPE && position != 0 && position != 1 && position != 2 && position != 3) {
                    layoutParams.setMargins(0, (int) (int) margin, 0, 0);

                } else {
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        if (position != 0 && position != 1 && position != 2 && position != 3) {
                            layoutParams.setMargins(0, (int) margin, 0, 0);
                        } else {
                            layoutParams.setMargins(0, (int) ((int) margin + margin * 0.5), 0, 0);
                        }
                    } else {
                        layoutParams.setMargins(0, 0, 0, 0);
                    }
                }
                itemView.setLayoutParams(layoutParams);

                int newW = (int) (0.8f * (w / 5));
                ConstraintLayout.LayoutParams layoutParamsImageAction = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
                layoutParamsImageAction.width = newW;
                layoutParamsImageAction.height = newW;
                imageView.setLayoutParams(layoutParamsImageAction);
            }

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            float biasedValue = 0.5f;
            switch (position) {
                case 0:
                case 4:
                case 8:
                    biasedValue = 0f;
                    break;
                case 1:
                case 5:
                case 9:
                    biasedValue = 0.30f;
                    break;
                case 2:
                case 6:
                case 10:
                    biasedValue = 0.70f;
                    break;
                case 3:
                case 7:
                case 11:
                    biasedValue = 1f;
                    break;
            }
            constraintSet.setHorizontalBias(R.id.imgIcon, biasedValue);
            if (position != 0 && position != 1 && position != 2 && position != 3) {
                float heightOfGuildTopTextAction = 0.2f * (w / 5);
                float lengthInProportion = valueF * heightOfGuildTopTextAction;
                float lengthAfterPlus = ((w / 5) - lengthInProportion) / (w / 5) * 100f;
                constraintSet.setGuidelinePercent(R.id.gdTopTextAcition, lengthAfterPlus / 100f);
            }
            constraintSet.applyTo(constraintLayout);
            textView.setAlpha(valueF);
            if (position == 10 || position == 8 || position == 9 || position == 11) {
                itemView.setAlpha(valueF);
            }
            if (unregister) {
//                parent.unRegisterReceiver();
            }
            parent.setFirst(false);
        }

        public void updateWhenTouchParent() {
            TextViewAutoRun textView = itemView.findViewById(R.id.tvNameAction);
            if (position != 0 && position != 1 && position != 2 && position != 3) {
                ConstraintLayout constraintLayout = itemView.findViewById(R.id.layoutItemCollapse);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                float heightOfGuildTopTextAction = 0.2f * (w / 5);
                float lengthInProportion = valueF * heightOfGuildTopTextAction;
                float lengthAfterPlus = ((w / 5) - lengthInProportion) / (w / 5) * 100f;
                constraintSet.setGuidelinePercent(R.id.gdTopTextAcition, lengthAfterPlus / 100f);
                constraintSet.applyTo(constraintLayout);
            }
            textView.setAlpha(valueF);
            if (position == 10 || position == 8 || position == 9 || position == 11) {
                itemView.setAlpha(valueF);
            }
        }
    }
}
