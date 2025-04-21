package com.tapbi.spark.controlcenter.adapter;

import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.data.model.FocusIOS;
import com.tapbi.spark.controlcenter.databinding.ItemFocusBinding;
import com.tapbi.spark.controlcenter.utils.DrawableUtils;
import com.tapbi.spark.controlcenter.utils.StringUtils;
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper;

import java.io.IOException;
import java.util.List;

public class FocusAdapter extends RecyclerView.Adapter<FocusAdapter.ViewHolder> {
    private List<FocusIOS> focusIOSList;
    private IClickItemFocus clickItemFocus;

    public void setData(List<FocusIOS> focusIOSList) {
        this.focusIOSList = focusIOSList;
        notifyDataSetChanged();

    }

    public void setClickItemFocus(IClickItemFocus clickItemFocus) {
        this.clickItemFocus = clickItemFocus;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFocusBinding binding = ItemFocusBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FocusAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FocusIOS focusIOS = focusIOSList.get(holder.getAbsoluteAdapterPosition());

        holder.binding.tvFocus.setText(StringUtils.INSTANCE.getIconDefaultApp(focusIOS.getName(), holder.itemView.getContext()));
        if (focusIOS.getName().equals(focusIOS.getImageLink())) {
            holder.binding.imFocus.setImageDrawable(DrawableUtils.getIconDefaultApp(focusIOS.getName(), holder.itemView.getContext()));
            holder.binding.imFocus.clearColorFilter();
        } else {
            try {
                holder.binding.imFocus.setImageDrawable(
                        Drawable.createFromResourceStream(
                                holder.itemView.getContext().getResources(), new TypedValue(),
                                holder.itemView.getContext().getAssets().open(
                                        focusIOS.getImageLink().substring(22)
                                ), null
                        )
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (holder.getAbsoluteAdapterPosition() == 0) {
            holder.binding.viewFocus.setVisibility(View.GONE);
        }
        if (focusIOS.getStartAutoTime() || focusIOS.getStartAutoAppOpen() || focusIOS.getStartAutoLocation() || focusIOS.getStartCurrent()) {
            holder.binding.tvOnFocus.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvOnFocus.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(v -> {
            ViewHelper.preventTwoClick(v);
            clickItemFocus.clickItem(focusIOSList.get(position));
        });

    }

    @Override
    public int getItemCount() {
        return focusIOSList != null ? focusIOSList.size() : 0;
    }

    public interface IClickItemFocus {
        void clickItem(FocusIOS focusIOS);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFocusBinding binding;

        public ViewHolder(@NonNull ItemFocusBinding itemFocusBinding) {
            super(itemFocusBinding.getRoot());
            binding = itemFocusBinding;
        }
    }
}
