package com.tapbi.spark.controlcenter.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn;
import com.tapbi.spark.controlcenter.databinding.ItemTurnOnBinding;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.TimeUtils;
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class AutomationAdapter extends RecyclerView.Adapter<AutomationAdapter.ViewHolder> {
    private List<ItemTurnOn> listAutomation = new ArrayList<>();
    private String color = "";
    private IListenerAutomation listenerAutomation;

    public void setData(List<ItemTurnOn> listAutomation, String color) {
        this.listAutomation = listAutomation;
        this.color = color;
        notifyDataSetChanged();
    }

    public void setListAutomation(IListenerAutomation listenerAutomation) {
        this.listenerAutomation = listenerAutomation;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTurnOnBinding binding = ItemTurnOnBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AutomationAdapter.ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        int position = holder.getAbsoluteAdapterPosition();
        ItemTurnOn itemTime = listAutomation.get(position);
        holder.binding.imClock.setColorFilter(Color.parseColor(color));
        if (itemTime.getTypeEvent().equals(Constant.TIME)) {
            holder.binding.imApp.setVisibility(View.GONE);
//            holder.binding.time.text =
//                    TimeUtils.getTimeAutoWithCurrentMini(
//                            holder.itemView.context,
//                            itemTime.hourStart,
//                            itemTime.minuteStart
//                    ) + " - " + TimeUtils.getTimeAutoWithCurrentMini(
//                            holder.itemView.context,
//                            itemTime.hourEnd, itemTime.minuteEnd
//                    )
            holder.binding.time.setText(TimeUtils.getTimeAutoWithCurrentMini(holder.itemView.getContext(), itemTime.getTimeStart()) + "-" + TimeUtils.getTimeAutoWithCurrentMini(holder.itemView.getContext(), itemTime.getTimeEnd()));
            holder.binding.imClock.setImageResource(R.drawable.icon_clock);
            if (itemTime.getMonDay() && itemTime.getTueDay() && itemTime.getWedDay() && itemTime.getThuDay() && itemTime.getFriDay() && itemTime.getSatDay() && itemTime.getSunDay()) {
                holder.binding.day.setText(holder.itemView.getContext().getString(R.string.every_day));
            } else if (!itemTime.getMonDay() && !itemTime.getTueDay() && !itemTime.getWedDay() && !itemTime.getThuDay() && !itemTime.getFriDay() && !itemTime.getSatDay() && !itemTime.getSunDay()) {
                holder.binding.day.setText(holder.itemView.getContext().getString(R.string.no_repeact));
            } else {
                holder.binding.day.setText(holder.itemView.getContext().getString(R.string.every) + " " + TimeUtils.getDay(itemTime.getMonDay(), itemTime.getTueDay(), itemTime.getWedDay(), itemTime.getThuDay(), itemTime.getFriDay(), itemTime.getSatDay(), itemTime.getSunDay(), holder.itemView.getContext()));
            }
        } else if (itemTime.getTypeEvent().equals(Constant.LOCATION)) {
            holder.binding.imApp.setVisibility(View.GONE);

            holder.binding.time.setText(itemTime.getNameLocation());
            holder.binding.imClock.setImageResource(R.drawable.icon_location);
            holder.binding.day.setText(holder.itemView.getContext().getString(R.string.while_at_this_location));
        } else {
            holder.binding.imApp.setVisibility(View.VISIBLE);
            holder.binding.time.setText(itemTime.getNameApp());
            holder.binding.day.setText(holder.itemView.getContext().getString(R.string.While_using) + " " + itemTime.getNameApp());


            holder.binding.imApp.setImageDrawable(MethodUtils.getIconFromPackageName(holder.itemView.getContext(), itemTime.getPackageName()));

        }
        holder.binding.switchTime.setImageResource(itemTime.getStart() ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
        holder.binding.imDelete.setOnClickListener(v -> {
            ViewHelper.preventTwoClick(v);
            listenerAutomation.delete(position);
        });

        holder.binding.switchTime.setOnClickListener(v -> {
            if (itemTime.getStart()) {
                listenerAutomation.finish(position);
            } else {
                listenerAutomation.start(position);
            }
        });
        holder.binding.viewContent.setOnClickListener(v -> {
            ViewHelper.preventTwoClick(v);
            listenerAutomation.edit(position, itemTime.getTypeEvent());
        });

    }

    @Override
    public int getItemCount() {
        return listAutomation != null ? listAutomation.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemTurnOnBinding binding;

        public ViewHolder(@NonNull ItemTurnOnBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface IListenerAutomation {
        void delete(int position);

        void start(int position);

        void finish(int position);

        void edit(int position, String name);
    }
}
