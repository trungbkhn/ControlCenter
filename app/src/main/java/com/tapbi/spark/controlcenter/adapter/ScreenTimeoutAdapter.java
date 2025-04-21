package com.tapbi.spark.controlcenter.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.databinding.ItemScreenTimeoutBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.TimeoutScreen;

import java.util.ArrayList;

public class ScreenTimeoutAdapter extends RecyclerView.Adapter<ScreenTimeoutAdapter.Holder> {

    private final Context context;
    private final IClickScreenTimeout iClickScreenTimeout;
    private ArrayList<TimeoutScreen> listTimeout;
    private int timeSelected;
    private Typeface typeface;

    public ScreenTimeoutAdapter(Context context, IClickScreenTimeout iClickScreenTimeout) {
        this.context = context;
        this.iClickScreenTimeout = iClickScreenTimeout;


    }

    public void changeFont(Typeface typeface){
        this.typeface = typeface;
        notifyDataSetChanged();
    }

    public void setData(ArrayList<TimeoutScreen> listTimeout, int timeSelected) {
        this.listTimeout = listTimeout;
        this.timeSelected = timeSelected;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemScreenTimeoutBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int pos) {
        int position = holder.getLayoutPosition();
//        Timber.e("hachung :" + "onBindViewHolder");
        holder.binView(position);
    }

    @Override
    public int getItemCount() {
        return listTimeout.size();
    }

    public interface IClickScreenTimeout {
        void timeOut(int posSelected, TimeoutScreen time);
    }

    public class Holder extends RecyclerView.ViewHolder {
        ItemScreenTimeoutBinding binding;

        public Holder(@NonNull ItemScreenTimeoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void binView(int position) {

            TimeoutScreen timeItem = listTimeout.get(position);
            if (typeface != null){
                binding.tvTime.setTypeface(typeface);
            }
            binding.tvTime.setText(timeItem.getTitle());
//            Timber.e("hachung timeSelected:" + timeSelected + "/getTime: " + timeItem.getTime());
            if (timeSelected == (timeItem.getTime())) {
                binding.imgIcTick.setVisibility(View.VISIBLE);
            } else {
                binding.imgIcTick.setVisibility(View.INVISIBLE);
            }


            binding.vLineTransparent.setBackgroundColor(position == getItemCount() - 1 ? ContextCompat.getColor(context, R.color.colorBackgroundRadiusNew) : ContextCompat.getColor(context, R.color.transparent));


            binding.getRoot().setOnClickListener(v -> {
                timeSelected = timeItem.getTime();
                notifyDataSetChanged();
                iClickScreenTimeout.timeOut(position, timeItem);
            });
        }
    }
}
