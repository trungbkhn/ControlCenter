package com.tapbi.spark.controlcenter.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.databinding.ItemControlCustomiezeBinding;
import com.tapbi.spark.controlcenter.databinding.ItemMoreControlCustomBinding;
import com.tapbi.spark.controlcenter.databinding.ItemStyleCustomizeControlBinding;
import com.tapbi.spark.controlcenter.databinding.ItemTitleMoreAppCustomizeControlBinding;
import com.tapbi.spark.controlcenter.databinding.ItemVibrateControlBinding;
import com.tapbi.spark.controlcenter.databinding.ItemViewBottomCustomizeControlBinding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize;
import com.tapbi.spark.controlcenter.interfaces.ItemTouchHelperAdapter;
import com.tapbi.spark.controlcenter.interfaces.OnStartDragListener;
import com.tapbi.spark.controlcenter.utils.TinyDB;
import com.tapbi.spark.controlcenter.utils.VibratorUtils;

import java.util.ArrayList;
import java.util.Collections;

import timber.log.Timber;

public class CustomizeControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
    public static final int ITEM_BEFORE_VIEW_INCLUDED = 2;
    private final int typeViewVibrate = 0;
    private final int typeViewStyle = 1;
    private final int typeViewIncluded = 2;
    private final int typeViewTitleMore = 3;
    private final int typeViewMore = 4;
    private final int typeViewBottom = 5;
    private final Context context;
    private final ICustomizeControlClick iClick;
    private final OnStartDragListener onStartDragListener;
    private final RecyclerView recyclerView;
    private int styleSelected;
    private ArrayList<ControlCustomize> included;
    private ArrayList<ControlCustomize> all;

    public CustomizeControlAdapter(Context context, RecyclerView recyclerView, ICustomizeControlClick iClick, TinyDB tinyDB, ArrayList<ControlCustomize> controlCustomizes, ArrayList<ControlCustomize> all, OnStartDragListener onStartDragListener) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.iClick = iClick;
        styleSelected = tinyDB.getInt(Constant.STYLE_CONTROL, Constant.STYLE_CONTROL_TOP);
        this.included = controlCustomizes;
        this.all = all;
        this.onStartDragListener = onStartDragListener;
        notifyDataSetChanged();
    }


    public void changeList(ArrayList<ControlCustomize> controlCustomizes, ArrayList<ControlCustomize> all) {
        this.included = controlCustomizes;
        this.all = all;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == typeViewVibrate) {
            return new HolderVibrate(ItemVibrateControlBinding.inflate(LayoutInflater.from(context), parent, false));
        } else if (viewType == typeViewStyle) {
            return new HolderStyle(ItemStyleCustomizeControlBinding.inflate(LayoutInflater.from(context), parent, false));
        } else if (viewType == typeViewTitleMore) {
            return new HolderTitleMoreApp(ItemTitleMoreAppCustomizeControlBinding.inflate(LayoutInflater.from(context), parent, false), context);
        } else if (viewType == typeViewBottom) {
            return new HolderBottomCustomizeControl(ItemViewBottomCustomizeControlBinding.inflate(LayoutInflater.from(context), parent, false));
        } else if (viewType == typeViewIncluded) {
            return new HolderControlCustomize(ItemControlCustomiezeBinding.inflate(LayoutInflater.from(context), parent, false));
        } else {
            return new HolderMoreControl(ItemMoreControlCustomBinding.inflate(LayoutInflater.from(context), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case typeViewVibrate:
                ((HolderVibrate) holder).bind();
                break;
            case typeViewBottom:
            case typeViewTitleMore:
                break;
            case typeViewStyle:
                ((HolderStyle) holder).bindStyle();
                break;
            case typeViewIncluded:
                ((HolderControlCustomize) holder).bindIncludeControls(holder.getAbsoluteAdapterPosition(), (HolderControlCustomize) holder);
                break;
            case typeViewMore:
                ((HolderMoreControl) holder).bindMoreApp(holder.getAbsoluteAdapterPosition());
                break;
        }
    }

    @Override
    public int getItemCount() {
        //3 is typeViewVibrate, typeViewStyle, typeViewBottom
        return included.size() + all.size() + 4;
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if (position == 0) {
            type = typeViewVibrate;
        } else if (position == 1) {
            type = typeViewStyle;
        } else if (position >= ITEM_BEFORE_VIEW_INCLUDED && position <= included.size() - 1 + ITEM_BEFORE_VIEW_INCLUDED) {
            type = typeViewIncluded;
        } else if (position == (included.size() + ITEM_BEFORE_VIEW_INCLUDED)) {
            //2 is typeViewVibrate, typeViewStyle
            type = typeViewTitleMore;
        } else if (position == getItemCount() - 1) {
            type = typeViewBottom;
        } else {
            type = typeViewMore;
        }
        return type;
    }

    private void setBackgroundIcon(ImageView img, ControlCustomize control) {
        String action = control.getName();
        if (control.getIsDefault() != 0) {
            if (action.equals(context.getString(R.string.flash_light))) {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control_flash);
            } else if (action.equals(context.getString(R.string.clock))) {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control_clock);
            } else if (action.equals(context.getString(R.string.calculator))) {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control_calculator);
            } else if (action.equals(context.getString(R.string.screen_recoding))) {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control_recoder);
            } else if (action.equals(context.getString(R.string.dark_mode))) {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control_dark_mode);
            } else if (action.equals(context.getString(R.string.low_power_mode))) {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control_low_power_mode);
            } else if (action.equals(context.getString(R.string.notes))) {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control_notes);
            } else {
                img.setBackgroundResource(R.drawable.background_icon_cusomize_control);
            }
        } else {
            img.setBackgroundResource(R.drawable.background_icon_cusomize_control);
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(included, fromPosition - ITEM_BEFORE_VIEW_INCLUDED, toPosition - ITEM_BEFORE_VIEW_INCLUDED);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    @Override
    public void onItemDismiss(int position) {
    }

    @Override
    public void onItemChange() {
        recyclerView.post(() -> {
            for (int i = 0; i < included.size(); i++) {
                notifyItemChanged(i, included.get(i));
            }
        });
    }

    public interface ICustomizeControlClick {
        void styleClick(int style);

        void onDelete(int position);

        void onAdd(int position);
    }

    static class HolderVibrate extends RecyclerView.ViewHolder {
        private ItemVibrateControlBinding binding;

        public HolderVibrate(@NonNull ItemVibrateControlBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind() {
            binding.swVibrate.setChecked(App.tinyDB.getBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, Constant.VALUE_DEFAULT_VIBRATOR));

            binding.swVibrate.setOnCheckedChangeListener(isChecked -> {
                App.tinyDB.putBoolean(Constant.VIBRATOR_CONTROL_LONG_CLICK, isChecked);
                if (isChecked) {
                    VibratorUtils.getInstance(binding.swVibrate.getContext()).vibrator(VibratorUtils.TIME_DEFAULT);
                }
            });
        }
    }

    static class HolderTitleMoreApp extends RecyclerView.ViewHolder {

        @SuppressLint("StringFormatInvalid")
        public HolderTitleMoreApp(@NonNull ItemTitleMoreAppCustomizeControlBinding binding, Context context) {
            super(binding.getRoot());
           binding.detailInclude.setText(context.getString(
                   R.string.text_add_organize_control,
                   context.getString(R.string.app_name)
           ));
        }
    }

    static class HolderBottomCustomizeControl extends RecyclerView.ViewHolder {

        public HolderBottomCustomizeControl(@NonNull ItemViewBottomCustomizeControlBinding binding) {
            super(binding.getRoot());
        }
    }

    class HolderStyle extends RecyclerView.ViewHolder {

        private final ItemStyleCustomizeControlBinding binding;

        public HolderStyle(@NonNull ItemStyleCustomizeControlBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        public void bindStyle() {
            setUpValueCheckBox();
            binding.viewClick1.setOnClickListener(v -> {
                styleSelected = Constant.STYLE_CONTROL_TOP;
                setUpValueCheckBox();
                iClick.styleClick(styleSelected);
            });
            binding.viewClick2.setOnClickListener(v -> {
                styleSelected = Constant.STYLE_CONTROL_BOTTOM;
                iClick.styleClick(styleSelected);
                setUpValueCheckBox();
            });
        }

        private void setUpValueCheckBox() {
            if (styleSelected == Constant.STYLE_CONTROL_TOP) {
                binding.cbStyle1.setChecked(true);
                binding.cbStyle2.setChecked(false);
            } else {
                binding.cbStyle1.setChecked(false);
                binding.cbStyle2.setChecked(true);
            }
        }
    }

    class HolderControlCustomize extends RecyclerView.ViewHolder {
        private final ItemControlCustomiezeBinding includeBinding;

        public HolderControlCustomize(@NonNull ItemControlCustomiezeBinding binding) {
            super(binding.getRoot());
            this.includeBinding = binding;
        }

        @SuppressLint("ClickableViewAccessibility")
        public void bindIncludeControls(int pos, HolderControlCustomize holder) {
            int position = pos - ITEM_BEFORE_VIEW_INCLUDED;
            ControlCustomize controlCustomize = included.get(position);
            View view = includeBinding.lineBottom;
            if (position == included.size() - 1) {
                view.setVisibility(View.INVISIBLE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
            includeBinding.name.setText(controlCustomize.getName());

            if (controlCustomize.getIsDefault() != 0) {
                includeBinding.icon.setPadding(12, 12, 12, 12);
            } else {
                includeBinding.icon.setPadding(0, 0, 0, 0);
            }

//            if (controlCustomize.getIcon() == null) {
//                controlCustomize.setIcon(MethodUtils.getIconFromPackagename(context, controlCustomize.getPackageName()));
//            }
//            includeBinding.icon.setImageDrawable(controlCustomize.getIcon());
            Glide.with(itemView.getContext()).load(controlCustomize.getIcon()).into(includeBinding.icon);

            setBackgroundIcon(includeBinding.icon, controlCustomize);

            includeBinding.drag.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    onStartDragListener.onStartDrag(holder);
                }
                return true;
            });

            includeBinding.delete.setOnClickListener(v -> {
                if (iClick != null) {
                    iClick.onDelete(position);
                }
            });
        }
    }

    public class HolderMoreControl extends RecyclerView.ViewHolder {
        ItemMoreControlCustomBinding moreBinding;

        public HolderMoreControl(@NonNull ItemMoreControlCustomBinding moreBinding) {
            super(moreBinding.getRoot());
            this.moreBinding = moreBinding;
        }

        public void bindMoreApp(int pos) {
            int position = pos - ITEM_BEFORE_VIEW_INCLUDED - included.size() -1;
            ControlCustomize controlCustomize = all.get(position);
            View view = moreBinding.lineBottom;
            if (pos == getItemCount() - ITEM_BEFORE_VIEW_INCLUDED) {
                view.setVisibility(View.INVISIBLE);
            } else {
                view.setVisibility(View.VISIBLE);
            }
            moreBinding.name.setText(controlCustomize.getName());

            if (controlCustomize.getIsDefault() != 0) {
                moreBinding.icon.setPadding(12, 12, 12, 12);
            } else {
                moreBinding.icon.setPadding(0, 0, 0, 0);
            }

//            if (controlCustomize.getIcon() == null) {
//                controlCustomize.setIcon(MethodUtils.getIconFromPackagename(context, controlCustomize.getPackageName()));
//            }
//            moreBinding.icon.setImageDrawable(controlCustomize.getIcon());

            Glide.with(itemView.getContext()).load(controlCustomize.getIcon()).into(moreBinding.icon);

            setBackgroundIcon(moreBinding.icon, controlCustomize);

            moreBinding.add.setOnClickListener(v -> {
                if (iClick != null) {
                    Timber.e(".");
                    iClick.onAdd(position);
                }
            });
        }
    }

}
