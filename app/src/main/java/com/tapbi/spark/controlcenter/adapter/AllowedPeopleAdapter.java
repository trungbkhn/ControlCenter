package com.tapbi.spark.controlcenter.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.data.model.ItemPeople;
import com.tapbi.spark.controlcenter.databinding.ItemAllowedPeopleBinding;

import java.util.ArrayList;
import java.util.List;


public class AllowedPeopleAdapter extends RecyclerView.Adapter<AllowedPeopleAdapter.AllowedPeopleHolder> {
    private List<ItemPeople> listPeople = new ArrayList<>();
    private int countItem = -1;

    public void setData(List<ItemPeople> listPeople, int countItem) {
        this.listPeople.clear();
        this.listPeople.addAll(listPeople);
        this.countItem = countItem;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AllowedPeopleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAllowedPeopleBinding binding = ItemAllowedPeopleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AllowedPeopleHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AllowedPeopleHolder holder, int position) {
        ItemPeople item = listPeople.get(holder.getAbsoluteAdapterPosition());
        if (countItem > 1) {
            if (holder.getAbsoluteAdapterPosition() == listPeople.size() - 1) {
                holder.binding.imPeople.setVisibility(View.INVISIBLE);
                holder.binding.imFame.setVisibility(View.VISIBLE);
                holder.binding.tvCountNumber.setVisibility(View.VISIBLE);
                holder.binding.tvName.setVisibility(View.INVISIBLE);
                holder.binding.imFame.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bacground_count_allow));
                holder.binding.tvCountNumber.setText("+" + countItem);
            } else {
                if (!item.getImage().isEmpty()) {
                    Glide.with(holder.itemView.getContext()).load(item.getImage()).into(holder.binding.imPeople);
                } else {
                    showViewPeople(holder, item);
                }
            }
        } else {
            if (!item.getImage().isEmpty()) {
                Glide.with(holder.itemView.getContext()).load(item.getImage()).into(holder.binding.imPeople);
            } else {
                showViewPeople(holder, item);
            }
        }
    }

    private void showViewPeople(AllowedPeopleHolder holder, ItemPeople item) {
        holder.binding.imPeople.setVisibility(View.INVISIBLE);
        holder.binding.imFame.setVisibility(View.VISIBLE);
        holder.binding.tvCountNumber.setVisibility(View.INVISIBLE);
        holder.binding.tvName.setVisibility(View.VISIBLE);
        holder.binding.imFame.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bacground_imgae_people));
        if (!item.getName().isEmpty()) {
            holder.binding.tvName.setText(item.getName().substring(0, 1));
        } else {
            holder.binding.tvName.setText(item.getPhone().substring(0, 1));
        }
    }

    @Override
    public int getItemCount() {
        return listPeople != null ? listPeople.size() : 0;
    }

    public class AllowedPeopleHolder extends RecyclerView.ViewHolder {
        private final ItemAllowedPeopleBinding binding;

        public AllowedPeopleHolder(@NonNull ItemAllowedPeopleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
