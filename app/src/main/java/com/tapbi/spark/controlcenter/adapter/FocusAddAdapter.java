package com.tapbi.spark.controlcenter.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.model.FocusIOS;
import com.tapbi.spark.controlcenter.databinding.ItemFocusAddBinding;
import com.tapbi.spark.controlcenter.utils.DrawableUtils;
import com.tapbi.spark.controlcenter.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FocusAddAdapter extends RecyclerView.Adapter<FocusAddAdapter.FocusHolder> {

    private List<FocusIOS> list = new ArrayList<>();
    private ItemClickListener clickListener;

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<FocusIOS> list) {
        if (list != null) {
            FocusIOS focusIOS = new FocusIOS(Constant.CUSTOM, Constant.CUSTOM, "#5756CE", Constant.NO_ONE, false, false, false, false, false);
            if (list.size() > 0) {
                if (list.get(0) != null && !list.get(0).getName().equals(Constant.CUSTOM)) {
                    list.add(0, focusIOS);
                }
            } else {
                list.add(focusIOS);
            }
            this.list = list;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public FocusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFocusAddBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_focus_add, parent, false);
        return new FocusHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FocusHolder holder, int position) {
        holder.bindData(holder.getAbsoluteAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FocusHolder extends RecyclerView.ViewHolder {

        private ItemFocusAddBinding binding;

        public FocusHolder(@NonNull ItemFocusAddBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindData(int pos) {
            FocusIOS focusIOS = list.get(pos);
            binding.imIcon.setImageDrawable(DrawableUtils.getIconDefaultApp(focusIOS.getName(), itemView.getContext()));
            binding.tvName.setText(StringUtils.INSTANCE.getIconDefaultApp(focusIOS.getName(), itemView.getContext()));
            itemView.setOnClickListener(view -> clickListener.onItemClick(focusIOS));
        }

    }

    public interface ItemClickListener {
        void onItemClick(FocusIOS focusIOS);
    }

}
