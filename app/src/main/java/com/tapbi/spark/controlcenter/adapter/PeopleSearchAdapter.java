package com.tapbi.spark.controlcenter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.data.model.ItemPeople;
import com.tapbi.spark.controlcenter.databinding.ItemPeopleBinding;
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class PeopleSearchAdapter extends RecyclerView.Adapter<PeopleSearchAdapter.ViewHolder> {
    private List<ItemPeople> listSearchPeople = new ArrayList<>();
    private IListenClickPeople listener;

    public void setData(List<ItemPeople> listSearchPeople) {
        this.listSearchPeople = listSearchPeople;
        notifyDataSetChanged();
    }

    public void setListener(IListenClickPeople listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPeopleBinding binding = ItemPeopleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PeopleSearchAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemPeople itemPeople = listSearchPeople.get(holder.getAbsoluteAdapterPosition());
        if (itemPeople.getImage().isEmpty()) {
            holder.binding.tvNameImage.setText(itemPeople.getName().substring(0, 1));
            holder.binding.tvNameImage.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvNameImage.setVisibility(View.GONE);
        }
        holder.binding.swPeople.setImageResource(itemPeople.isStart() ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
        Glide.with(holder.binding.imPeople).load(itemPeople.getImage()).into(holder.binding.imPeople);
        holder.binding.tvNamePeople.setText(itemPeople.getName());
        holder.binding.tvPhonePeople.setText(itemPeople.getPhone());
        holder.binding.swPeople.setOnClickListener(v -> {
            ViewHelper.preventTwoClick(v);
            listener.clickSwitch(holder.getAbsoluteAdapterPosition(), itemPeople.isStart());
        });
    }

    @Override
    public int getItemCount() {
        return listSearchPeople != null ? listSearchPeople.size() : 0;
    }

    public interface IListenClickPeople {
        void clickSwitch(int position, boolean isStart);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPeopleBinding binding;

        public ViewHolder(@NonNull ItemPeopleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
