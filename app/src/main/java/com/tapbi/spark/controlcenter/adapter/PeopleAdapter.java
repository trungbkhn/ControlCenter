package com.tapbi.spark.controlcenter.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.model.FocusIOS;
import com.tapbi.spark.controlcenter.data.model.ItemPeople;
import com.tapbi.spark.controlcenter.databinding.ItemAlsoAllowBinding;
import com.tapbi.spark.controlcenter.databinding.ItemMiddleAllowPeopleBinding;
import com.tapbi.spark.controlcenter.databinding.ItemPeopleBinding;
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    private FocusIOS focusIOS;
    private List<ItemPeople> itemPeopleList = new ArrayList<>();
    private IPeopleAllow iPeopleAllow;

    public void setData(FocusIOS focusIOS, List<ItemPeople> itemPeopleList) {
        Log.d("TAG", "setData: itemPeopleList .");
        this.focusIOS = focusIOS;
        this.itemPeopleList.clear();
        this.itemPeopleList.addAll(itemPeopleList);
        this.itemPeopleList.add(0, new ItemPeople("", "", "", "", false, ""));
        this.itemPeopleList.add(0, new ItemPeople("", "", "", "", false, ""));
        notifyDataSetChanged();

    }

    public void setListener(IPeopleAllow iPeopleAllow) {
        this.iPeopleAllow = iPeopleAllow;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Constant.TYPE_TOP) {
            return new ViewHolder(ItemAlsoAllowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else if (viewType == Constant.TYPE_MIDDLE) {
            return new ViewHolder(ItemMiddleAllowPeopleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new ViewHolder(ItemPeopleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    private void setSwitchMode(ItemAlsoAllowBinding topBinding, boolean every, boolean noOne, boolean favourite, boolean allContact) {
        topBinding.viewAlsoAllow.swEveryone.setImageResource(every ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
        topBinding.viewAlsoAllow.swNoOne.setImageResource(noOne ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
        topBinding.viewAlsoAllow.swFavorite.setImageResource(favourite ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
        topBinding.viewAlsoAllow.swAllContact.setImageResource(allContact ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder.getItemViewType() == Constant.TYPE_TOP) {
            switch (focusIOS.getModeAllowPeople()) {
                case Constant.EVERY_ONE:
                    setSwitchMode(holder.itemAlsoAllowBinding, true, false, false, false);
                    break;
                case Constant.NO_ONE:
                    setSwitchMode(holder.itemAlsoAllowBinding, false, true, false, false);
                    break;
                case Constant.FAVOURITE:
                    setSwitchMode(holder.itemAlsoAllowBinding, false, false, true, false);
                    break;
                case Constant.ALL_CONTACT:
                    setSwitchMode(holder.itemAlsoAllowBinding, false, false, false, true);
                    break;

            }
            iPeopleAllow.topType(holder.itemAlsoAllowBinding);
        } else if (holder.getItemViewType() == Constant.TYPE_MIDDLE) {
            iPeopleAllow.middleType(holder.itemMiddleAllowPeopleBinding);
        } else {
            int pos = holder.getAbsoluteAdapterPosition();
            ItemPeople itemPeople = itemPeopleList.get(pos);
            if (itemPeople.getImage().isEmpty()) {
                holder.itemPeopleBinding.tvNameImage.setText(itemPeople.getName().substring(0, 1));
                holder.itemPeopleBinding.tvNameImage.setVisibility(View.VISIBLE);
            } else {
                holder.itemPeopleBinding.tvNameImage.setVisibility(View.GONE);
            }
            holder.itemPeopleBinding.swPeople.setImageResource(itemPeople.isStart() ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
            Glide.with(holder.itemPeopleBinding.imPeople).load(itemPeople.getImage()).into(holder.itemPeopleBinding.imPeople);
            holder.itemPeopleBinding.tvNamePeople.setText(itemPeople.getName());
            holder.itemPeopleBinding.tvPhonePeople.setText(itemPeople.getPhone());

            holder.itemPeopleBinding.swPeople.setOnClickListener(v -> {
                ViewHelper.preventTwoClick(v);
                iPeopleAllow.clickSwitch(pos, itemPeople.isStart());
            });

        }


    }

    @Override
    public int getItemCount() {
        return itemPeopleList != null ? itemPeopleList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return Constant.TYPE_TOP;
        } else if (position == 1) {
            return Constant.TYPE_MIDDLE;
        } else {
            return Constant.TYPE_BOTOM;
        }
    }

    public interface IPeopleAllow {
        void topType(ItemAlsoAllowBinding binding);

        void middleType(ItemMiddleAllowPeopleBinding binding);

        void clickSwitch(int position, boolean isStart);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemAlsoAllowBinding itemAlsoAllowBinding;
        private ItemMiddleAllowPeopleBinding itemMiddleAllowPeopleBinding;
        private ItemPeopleBinding itemPeopleBinding;

        public ViewHolder(@NonNull ItemAlsoAllowBinding itemAlsoAllowBinding) {
            super(itemAlsoAllowBinding.getRoot());
            this.itemAlsoAllowBinding = itemAlsoAllowBinding;
        }

        public ViewHolder(@NonNull ItemMiddleAllowPeopleBinding itemMiddleAllowPeopleBinding) {
            super(itemMiddleAllowPeopleBinding.getRoot());
            this.itemMiddleAllowPeopleBinding = itemMiddleAllowPeopleBinding;
        }

        public ViewHolder(@NonNull ItemPeopleBinding itemPeopleBinding) {
            super(itemPeopleBinding.getRoot());
            this.itemPeopleBinding = itemPeopleBinding;
        }
    }
}
