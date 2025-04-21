package com.tapbi.spark.controlcenter.feature.mishade.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.databinding.ItemControlMiShadeBinding;
import com.tapbi.spark.controlcenter.databinding.ItemTextControlMiShadeBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

import java.util.ArrayList;
import java.util.List;

public class ActionMiShadeAdapter extends RecyclerView.Adapter<ActionMiShadeAdapter.ViewHolder> {

    private List<InfoSystem> listAction = new ArrayList<>();
    private List<InfoSystem> listActionDefault = new ArrayList<>();
    private int height = 0;
    private boolean changing = false;

    private ConstraintLayout constraintLayout;

    private OnMoveItemListener onMoveItemListener;

    public void setOnMoveItemListener(OnMoveItemListener onMoveItemListener) {
        this.onMoveItemListener = onMoveItemListener;
    }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }

    public void setData(List<InfoSystem> listAction, List<InfoSystem> listAction2) {
        listActionDefault = listAction;
        this.listAction.clear();
        this.listAction.addAll(listAction);
        this.listAction.addAll(listAction2);
        this.listAction.add(0, new InfoSystem("TITLE_1", "", "", -1));
        this.listAction.add(listActionDefault.size() + 1, new InfoSystem("TITLE_2", "", "", -1));
        constraintLayout = null;
        notifyDataSetChanged();
    }

    public void setChanging() {
        this.changing = !changing;
    }

    public int getHeight() {
        return height;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case Constant.TYPE_VIEW_HEADER:
                return new ViewHolder(ItemTextControlMiShadeBinding.inflate(LayoutInflater.from(App.mContext), parent, false));
            case Constant.TYPE_VIEW_ACTION_SAVE:
            default:
                return new ViewHolder(ItemControlMiShadeBinding.inflate(LayoutInflater.from(App.mContext), parent, false));
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case Constant.TYPE_VIEW_HEADER:
                if (holder.getAbsoluteAdapterPosition() == 0) {
                    holder.textControlMiShadeBinding.tvTitle.setText(R.string.hold_and_drag_to_rearrange_tiles);
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
                } else {
                    holder.textControlMiShadeBinding.tvTitle.setText(R.string.hold_and_drag_to_add_tiles);
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.color_F2F2F2));
                    constraintLayout = holder.textControlMiShadeBinding.clItem;
                    height = constraintLayout.getHeight();
                }
                break;
            case Constant.TYPE_VIEW_ACTION_SAVE:
                InfoSystem infoSystem = listAction.get(position);

                holder.itemControlMiShadeBinding.imgIcon.setImageResource(infoSystem.getIcon());
                holder.itemControlMiShadeBinding.imgIcon.setColorFilter(Color.parseColor("#7D7D7D"));
                holder.itemControlMiShadeBinding.tvNameAction.setText(MethodUtils.getNameActionShowTextView(holder.itemView.getContext(), infoSystem.getName()));

                break;
        }

        if (position == listAction.size() - 1) {
            onMoveItemListener.onLoadedLastItem();
        }

    }

    @Override
    public int getItemCount() {
        return listAction.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type;

        if (isCheckItem(position)) {
            type = Constant.TYPE_VIEW_HEADER;
        } else {
            type = Constant.TYPE_VIEW_ACTION_SAVE;
        }
        return type;
    }

    private boolean isCheckItem(int position) {
        if (listAction.get(position).getName() != null) {
            return listAction.get(position).getName().equals("TITLE_1") || listAction.get(position).getName().equals("TITLE_2");
        }
        return false;

    }

    public List<InfoSystem> getListAction() {
        return listAction;
    }

    public interface OnMoveItemListener {
        void onLoadedLastItem();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemControlMiShadeBinding itemControlMiShadeBinding;
        private ItemTextControlMiShadeBinding textControlMiShadeBinding;

        public ViewHolder(@NonNull ItemControlMiShadeBinding itemControlMiShadeBinding) {
            super(itemControlMiShadeBinding.getRoot());
            this.itemControlMiShadeBinding = itemControlMiShadeBinding;
        }

        public ViewHolder(@NonNull ItemTextControlMiShadeBinding textControlMiShadeBinding) {
            super(textControlMiShadeBinding.getRoot());
            this.textControlMiShadeBinding = textControlMiShadeBinding;
        }
    }


}
