package com.tapbi.spark.controlcenter.feature.controlcenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.ClickAddOrRemoveAction;
import com.tapbi.spark.controlcenter.feature.controlcenter.textview.TextViewAutoRun;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import static com.tapbi.spark.controlcenter.common.Constant.TYPE_ADD;

public class AdapterAddAction extends RecyclerView.Adapter<AdapterAddAction.ViewHolder> {
    private Context context;
    private List<InfoSystem> infoSystems;
    private int typeAction;
    private float w;
    private ClickAddOrRemoveAction clickAddOrRemoveAction;

    public AdapterAddAction(List<InfoSystem> infoSystems, int typeAction, float w, ClickAddOrRemoveAction ClickAddOrRemoveAction) {
        this.infoSystems = infoSystems;
        this.typeAction = typeAction;
        this.w = w;
        this.clickAddOrRemoveAction = ClickAddOrRemoveAction;
    }

    public void actionItemChange() {
        for (int i = 0; i < infoSystems.size(); i++) {
            notifyItemChanged(i,infoSystems.get(i));
        }
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void setData(List<InfoSystem> infoSystems) {
        this.infoSystems = infoSystems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_action, parent, false);
        return new AdapterAddAction.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        int position = holder.getLayoutPosition();
        InfoSystem infoSystem = infoSystems.get(position);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (w / 4.4));
        layoutParams.setMargins(0, (int) 15, 0, 0);
        holder.itemView.setLayoutParams(layoutParams);
        int newW = (int) ((w / 5) * 0.8f);
        ConstraintLayout.LayoutParams layoutParamsImageAction = (ConstraintLayout.LayoutParams) holder.imgIcon.getLayoutParams();
        layoutParamsImageAction.width = newW;
        layoutParamsImageAction.height = newW;
        holder.imgIcon.setLayoutParams(layoutParamsImageAction);

        holder.imgIcon.setImageResource(infoSystem.getIcon());
        holder.tvNameAction.setText(MethodUtils.getNameActionShowTextView(context, infoSystem.getName()));
        if (typeAction == TYPE_ADD) {
            holder.imgAction.setImageResource(R.drawable.ic_add_action);
        } else {
            holder.imgAction.setImageResource(R.drawable.ic_remove_action);
        }
        View.OnClickListener onClickListener = v -> {
            if (typeAction == TYPE_ADD) {
                clickAddOrRemoveAction.clickAdd(infoSystem, position);
            } else {
                clickAddOrRemoveAction.clickRemove(infoSystem, position);
            }
        };
        holder.imgAction.setOnClickListener(onClickListener);
        holder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return infoSystems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgAction;
        public ImageView imgIcon;
        public TextViewAutoRun tvNameAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAction = itemView.findViewById(R.id.imgAction);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            tvNameAction = itemView.findViewById(R.id.tvNameAction);
        }
    }
}
