package com.tapbi.spark.controlcenter.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.data.model.ItemApp;
import com.tapbi.spark.controlcenter.databinding.ItemAllowedAppBinding;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

import java.util.ArrayList;
import java.util.List;

public class AllowedAppsAdapter extends RecyclerView.Adapter<AllowedAppsAdapter.ViewHolder> {
    private List<ItemApp> listApps = new ArrayList<>();
    private int countItem = -1;

    public void setData(List<ItemApp> listApps, int countItem) {
        this.listApps = listApps;
        this.countItem = countItem;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAllowedAppBinding binding = ItemAllowedAppBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AllowedAppsAdapter.ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemApp itemApp=listApps.get(holder.getAbsoluteAdapterPosition());
        if (countItem > 1) {
            if (holder.getAbsoluteAdapterPosition() == listApps.size() - 1) {
                holder.binding.imApp.setVisibility( View.GONE);
                holder.binding.tvCountNumber.setVisibility(View.VISIBLE);
                holder.binding.tvCountNumber.setText("+"+countItem);
            } else {
                holder.binding.imApp.setImageDrawable(MethodUtils.getIconFromPackageName(holder.itemView.getContext(), itemApp.getPackageName()));
            }
        } else {
            holder.binding.imApp.setImageDrawable(MethodUtils.getIconFromPackageName(holder.itemView.getContext(), itemApp.getPackageName()));
        }
    }

    @Override
    public int getItemCount() {
        return listApps!=null?listApps.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemAllowedAppBinding binding;
        public ViewHolder(@NonNull ItemAllowedAppBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
