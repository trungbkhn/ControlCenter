package com.tapbi.spark.controlcenter.adapter;

import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.databinding.ItemIconFocusBinding;
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {
    private List<String> listICon = new ArrayList<>();
    private int idIcon = -1;
    private IListenerIConFocus listener;

    public void setData(List<String> listICon) {
        this.listICon = listICon;
        notifyDataSetChanged();
    }

    public void setListener(IListenerIConFocus listener) {
        this.listener = listener;
    }

    public void setIdIcon(int idIcon) {
        int oldPosition = this.idIcon;
        this.idIcon = idIcon;
        if (oldPosition >= 0)
            notifyItemChanged(oldPosition, listICon.get(oldPosition));
        notifyItemChanged(idIcon, listICon.get(idIcon));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IconAdapter.ViewHolder(ItemIconFocusBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            holder.binding.imFocus.setImageDrawable(
                    Drawable.createFromResourceStream(
                            holder.itemView.getContext().getResources(), new TypedValue(),
                            holder.itemView.getContext().getAssets().open(
                                    listICon.get(holder.getAbsoluteAdapterPosition()).substring(22)
                            ), null
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (idIcon == holder.getAbsoluteAdapterPosition()) {
            holder.binding.imChoseIcon.setVisibility(View.VISIBLE);
            holder.binding.viewIcon.setBackgroundColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.color_CCE4FF
                    )
            );
            holder.binding.imFocus.setColorFilter(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.color_007AFF
                    )
            );
        } else {
            holder.binding.imChoseIcon.setVisibility(View.INVISIBLE);
            holder.binding.viewIcon.setBackgroundColor(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.color_E5E4E9
                    )
            );
            holder.binding.imFocus.setColorFilter(
                    ContextCompat.getColor(
                            holder.itemView.getContext(),
                            R.color.color_807F85
                    )
            );
        }
        holder.binding.imFocus.setOnClickListener(v -> {
            ViewHelper.preventTwoClick(v);
            listener.onClickIcon(holder.getAbsoluteAdapterPosition(), listICon.get(holder.getAbsoluteAdapterPosition()));
            if (idIcon != holder.getAbsoluteAdapterPosition()) {
                setIdIcon(holder.getAbsoluteAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listICon != null ? listICon.size() : 0;
    }

    public interface IListenerIConFocus {
        void onClickIcon(int position, String icon);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemIconFocusBinding binding;

        public ViewHolder(@NonNull ItemIconFocusBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
