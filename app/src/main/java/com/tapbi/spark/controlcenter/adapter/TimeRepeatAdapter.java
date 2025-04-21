package com.tapbi.spark.controlcenter.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.data.model.ItemTimeRepeat;
import com.tapbi.spark.controlcenter.databinding.ItemAppBinding;
import com.tapbi.spark.controlcenter.databinding.ItemTimeRepeatBinding;
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class TimeRepeatAdapter extends RecyclerView.Adapter<TimeRepeatAdapter.ViewHolder> {
    private List<ItemTimeRepeat> itemTimeRepeatList=new ArrayList<>();
    private IListenClickDay listenClickDay;

    public void setData(List<ItemTimeRepeat> itemTimeRepeatList) {
        this.itemTimeRepeatList = itemTimeRepeatList;
        notifyDataSetChanged();
    }

    public void setListenClickDay(IListenClickDay listenClickDay) {
        this.listenClickDay = listenClickDay;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTimeRepeatBinding binding = ItemTimeRepeatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TimeRepeatAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemTimeRepeat itemTimeRepeat=itemTimeRepeatList.get(holder.getAbsoluteAdapterPosition());
        holder.itemTimeRepeatBinding.tvDay.setText(itemTimeRepeat.getTime().substring(0,1));
        holder.itemTimeRepeatBinding.tvDay.setBackgroundColor(Color.parseColor(itemTimeRepeat.isSelect()?itemTimeRepeat.getColor():"#eeeeef"));
        holder.itemTimeRepeatBinding.tvDay.setTextColor(Color.parseColor(itemTimeRepeat.isSelect()? "#ffffff":"#717177"));
        holder.itemTimeRepeatBinding.cvMon.setOnClickListener(v -> {
            ViewHelper.preventTwoClick(v);
            listenClickDay.clickDay(holder.getAbsoluteAdapterPosition(),itemTimeRepeat.isSelect());
        });

    }

    @Override
    public int getItemCount() {
        return itemTimeRepeatList != null ? itemTimeRepeatList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemTimeRepeatBinding itemTimeRepeatBinding;

        public ViewHolder(@NonNull ItemTimeRepeatBinding itemTimeRepeatBinding) {
            super(itemTimeRepeatBinding.getRoot());
            this.itemTimeRepeatBinding = itemTimeRepeatBinding;
        }
    }
    public interface IListenClickDay {
        void clickDay(int position,boolean isSelect);
    }
}
