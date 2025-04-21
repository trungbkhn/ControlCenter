package com.tapbi.spark.controlcenter.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.databinding.ItemColorBinding;
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
    private List<String> listColor = new ArrayList<>();
    private int idColor=-1;
    private IListenerColorFocus listener;


    public void setData(List<String> listColor) {
        this.listColor = listColor;
        notifyDataSetChanged();
    }

    public void setListener(IListenerColorFocus listener) {
        this.listener = listener;
    }

    public void setIDColor(int idColor) {
        int oldPosition = this.idColor;
        this.idColor = idColor;
        if (oldPosition >= 0)
            notifyItemChanged(oldPosition, listColor.get(oldPosition));
        notifyItemChanged(idColor, listColor.get(idColor));
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ColorAdapter.ViewHolder(ItemColorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = holder.getAbsoluteAdapterPosition();
        holder.itemColorBinding.cvColor.setColorFilter(Color.parseColor(listColor.get(pos)));
        holder.itemColorBinding.cvChoseColor.setVisibility(idColor == pos ? View.VISIBLE : View.INVISIBLE);
        holder.itemColorBinding.cvColor.setOnClickListener(v -> {
            ViewHelper.preventTwoClick(v);
            listener.onClickColor(pos, listColor.get(pos));
            if (idColor != pos) {
                setIDColor(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listColor != null ? listColor.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemColorBinding itemColorBinding;

        public ViewHolder(@NonNull ItemColorBinding itemColorBinding) {
            super(itemColorBinding.getRoot());
            this.itemColorBinding = itemColorBinding;
        }
    }
    public interface IListenerColorFocus {
        void onClickColor(int position, String color);
    }
}
