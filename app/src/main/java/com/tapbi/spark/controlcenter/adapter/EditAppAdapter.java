package com.tapbi.spark.controlcenter.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.data.model.ItemApp;
import com.tapbi.spark.controlcenter.databinding.ItemAppBinding;
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class EditAppAdapter extends RecyclerView.Adapter<EditAppAdapter.ViewHolder> {
    private List<ItemApp> listAppIns = new ArrayList<>();
    private IListenClickApp listener;
    private int id=-1;

    public void setData(List<ItemApp> listAppIns) {
        this.listAppIns = listAppIns;
        notifyDataSetChanged();
    }
    public void setIdApp(int id) {
        int oldPosition = this.id;
        this.id = id;
        if (oldPosition >= 0)
            notifyItemChanged(oldPosition, listAppIns.get(oldPosition));
        notifyItemChanged(id, listAppIns.get(id));
    }

    public void setListener(IListenClickApp listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppBinding binding = ItemAppBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new EditAppAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = holder.getAbsoluteAdapterPosition();
        holder.binding.imApp.setImageDrawable(listAppIns.get(pos).getIconApp());
        holder.binding.tvApp.setText(listAppIns.get(pos).getName());
        holder.binding.swApp.setImageResource(id==holder.getAbsoluteAdapterPosition() ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
        holder.binding.swApp.setOnClickListener(v -> {
            if (pos < 0 || pos >= listAppIns.size()) {
                return;
            }
            ViewHelper.preventTwoClick(v);
            listener.clickSwitch(
                    pos,
                    listAppIns.get(pos).isStart()
            );
            if (id!=holder.getAbsoluteAdapterPosition()){
                setIdApp(holder.getAbsoluteAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listAppIns != null ? listAppIns.size() : 0;
    }

    public interface IListenClickApp {
        void clickSwitch(int position, boolean isStart);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAppBinding binding;

        public ViewHolder(@NonNull ItemAppBinding itemAppBinding) {
            super(itemAppBinding.getRoot());
            binding = itemAppBinding;
        }
    }
}
