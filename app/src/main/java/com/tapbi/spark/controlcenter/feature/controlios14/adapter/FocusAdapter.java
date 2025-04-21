package com.tapbi.spark.controlcenter.feature.controlios14.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.model.FocusIOS;
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn;
import com.tapbi.spark.controlcenter.databinding.ItemControlAddFocusBinding;
import com.tapbi.spark.controlcenter.databinding.ItemControlFocusBinding;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.DrawableUtils;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.StringUtils;
import com.tapbi.spark.controlcenter.utils.TimeUtils;

import java.util.ArrayList;

public class FocusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_FOCUS = 0;
    private final int TYPE_ADD = 1;
    private ItemClickListener clickListener;
    private ArrayList<FocusIOS> list = new ArrayList<>();
    //    private View currentView;
    private String currentFocusOpen = "";
    private ItemTurnOn itemTurnOn;
    private int positionOpened = -1;
    private boolean animationRunning;
    private RecyclerView recyclerView;

    public FocusAdapter() {

    }

    public void setItemTurnOn(ItemTurnOn itemTurnOn) {
        this.itemTurnOn = itemTurnOn;
    }

    public ItemTurnOn getItemTurnOn() {
        return itemTurnOn;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(ArrayList<FocusIOS> list) {
        this.list.clear();
        this.list.addAll(list);
        this.list.add(new FocusIOS());
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOCUS) {
            ItemControlFocusBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_control_focus, parent, false);
            return new FocusHolder(binding);
        } else {
            ItemControlAddFocusBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_control_add_focus, parent, false);
            return new AddHolder(binding);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int pos) {
        int position = holder.getAbsoluteAdapterPosition();
        if (holder instanceof FocusHolder) {
            FocusIOS focusIOS = list.get(position);
            if (focusIOS == null) {
                return;
            }
            FocusHolder focusHolder = (FocusHolder) holder;
            focusHolder.bindData(focusIOS, itemTurnOn, position);

            focusHolder.binding.imMenu.setOnClickListener(view -> {
                if (animationRunning) {
                    return;
                }
                if (NotyControlCenterServicev614.getInstance().isDoubleClick()) {
                    return;
                }

                boolean currentClickType = focusIOS.getName().equals(Constant.SLEEP);
                boolean lastClickType = currentFocusOpen.equals(Constant.SLEEP);

                if (/*currentFocusOpen.equals(focusIOS.getName())*/ positionOpened == position) {
                    setLayoutParams(holder.itemView.getContext(), focusHolder.binding.cvContent, 2, lastClickType);

//                    currentView = null;
                    positionOpened = -1;
                    currentFocusOpen = "";
                } else {
                    int positionBefore = positionOpened;

//                    currentView = focusHolder.binding.cvContent;
                    positionOpened = position;
                    currentFocusOpen = focusIOS.getName();

                    if (recyclerView != null && positionBefore != -1) {
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(positionBefore);
                        if (viewHolder instanceof FocusHolder) {
                            setLayoutParams(holder.itemView.getContext(), ((FocusHolder) viewHolder).binding.cvContent, 2, lastClickType);
                            notifyItemChanged(positionBefore);
                        }
                    }
                    setLayoutParams(holder.itemView.getContext(), focusHolder.binding.cvContent, 1, currentClickType);
                }
                notifyItemChanged(position);

            });

            focusHolder.binding.vHeader.setOnClickListener(view -> {
                if (positionOpened != position) {
                    clickListener.onTitleClick(focusIOS, position);
                }
            });

            focusHolder.binding.layoutChild.tvHour.setOnClickListener(view -> clickListener.onHourClick(focusIOS, position));

            focusHolder.binding.layoutChild.tvEvening.setOnClickListener(view -> clickListener.onEveningClick(focusIOS, position));

//            focusHolder.binding.layoutChild.vLocation.setOnClickListener(view -> clickListener.onLocationClick(focusIOS, position));

            focusHolder.binding.tvSettings.setOnClickListener(view -> clickListener.onSettingsClick(focusIOS, position));
        } else {
            AddHolder addHolder = (AddHolder) holder;
            addHolder.binding.imNewFocus.setOnClickListener(view -> clickListener.onNewClick());
            addHolder.binding.tvNewFocus.setOnClickListener(view -> clickListener.onNewClick());
            addHolder.binding.clItem.setOnClickListener(view -> clickListener.onCloseView());
        }
    }

    void setLayoutParams(Context context, View container, int scale, boolean isSleep) {
        if (scale == 0 || container == null) return;

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) container.getLayoutParams();
        layoutParams.topToTop = ((View) container.getParent()).getId();
        layoutParams.startToStart = ((View) container.getParent()).getId();

        float start = 0;
        float end = 0;


        if (scale == 1) {
            start = MethodUtils.dpToPx(context, 70);
            end = MethodUtils.dpToPx(context, isSleep ? 120 : 223);


        } else if (scale == 2) {
            start = MethodUtils.dpToPx(context, isSleep ? 120 : 223);
            end = MethodUtils.dpToPx(context, 70);
        }
        ValueAnimator valueAnimator1 = ValueAnimator.ofFloat(start, end);
        valueAnimator1.setDuration(300);

        valueAnimator1.addUpdateListener(valueAnimator -> {
            float v = (float) valueAnimator.getAnimatedValue();
            layoutParams.height = (int) (v);
            container.setLayoutParams(layoutParams);

        });
        valueAnimator1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                animationRunning = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animationRunning = false;
            }
        });
        valueAnimator1.start();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == list.size() - 1) {
            return TYPE_ADD;
        }
        return TYPE_FOCUS;
    }

    public class FocusHolder extends RecyclerView.ViewHolder {

        private ItemControlFocusBinding binding;

        public FocusHolder(@NonNull ItemControlFocusBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bindData(FocusIOS focusIOS, ItemTurnOn itemTurnOn, int position) {

            binding.clItem.setPadding(0, position == 0 ? MethodUtils.dpToPx(itemView.getContext(), 40) : 0, 0, 0);

            if (!animationRunning) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.cvContent.getLayoutParams();
                if (positionOpened == position) {
                    params.height = MethodUtils.dpToPx(itemView.getContext(), focusIOS.getName().equals(Constant.SLEEP) ? 120f : 223f);
                } else {
                    params.height = MethodUtils.dpToPx(itemView.getContext(), 70f);
                }
                binding.cvContent.setLayoutParams(params);
            }

            if (position == positionOpened) {
                binding.tvTitle.setTextColor(Color.WHITE);
                binding.vHeader.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.color_66000000));

                binding.tvTime.setVisibility(View.GONE);

                binding.imMenu.setColorFilter(Color.WHITE);

                binding.imIconFocus.setColorFilter(Color.WHITE);

                binding.imMenu.setBackgroundResource(R.drawable.background_menu_focus);

                binding.layoutChild.tvEvening.setText(TimeUtils.isEvening()
                        ? itemView.getContext().getString(R.string.until_tomorrow_morning)
                        : itemView.getContext().getString(R.string.until_this_evening));

                if (focusIOS.getStartAutoAppOpen() || focusIOS.getStartAutoTime() || focusIOS.getStartAutoLocation() || focusIOS.getStartCurrent()) {
                    if (itemTurnOn != null && focusIOS.getName().equals(itemTurnOn.getNameFocus())) {

                        if (itemTurnOn.getType() == 1) {
                            binding.layoutChild.imTickHour.setVisibility(View.VISIBLE);
                            binding.layoutChild.imTickEvening.setVisibility(View.GONE);
//                            binding.layoutChild.imTickLocation.setVisibility(View.GONE);
//                            binding.layoutChild.tvNameLocation.setVisibility(View.GONE);
//                            binding.layoutChild.tvLocation.setGravity(Gravity.CENTER_VERTICAL);
                        } else if (itemTurnOn.getType() == 2) {
                            binding.layoutChild.imTickEvening.setVisibility(View.VISIBLE);
                            binding.layoutChild.imTickHour.setVisibility(View.GONE);
//                            binding.layoutChild.imTickLocation.setVisibility(View.GONE);
//                            binding.layoutChild.tvLocation.setGravity(Gravity.CENTER_VERTICAL);
//                            binding.layoutChild.tvNameLocation.setVisibility(View.GONE);
                        } else if (itemTurnOn.getType() == 3) {
//                            binding.layoutChild.imTickLocation.setVisibility(View.VISIBLE);
                            binding.layoutChild.imTickHour.setVisibility(View.GONE);
                            binding.layoutChild.imTickEvening.setVisibility(View.GONE);
//                            binding.layoutChild.tvLocation.setGravity(Gravity.BOTTOM);
//                            binding.layoutChild.tvNameLocation.setVisibility(View.VISIBLE);
//                            binding.layoutChild.tvNameLocation.setText(itemTurnOn.getNameLocation());
                        }

                    } else {
                        binding.layoutChild.imTickHour.setVisibility(View.GONE);
                        binding.layoutChild.imTickEvening.setVisibility(View.GONE);
//                        binding.layoutChild.imTickLocation.setVisibility(View.GONE);
//                        binding.layoutChild.tvNameLocation.setVisibility(View.GONE);
//                        binding.layoutChild.tvLocation.setGravity(Gravity.CENTER_VERTICAL);
                    }

                } else {

                    binding.layoutChild.imTickHour.setVisibility(View.GONE);
                    binding.layoutChild.imTickEvening.setVisibility(View.GONE);
//                    binding.layoutChild.imTickLocation.setVisibility(View.GONE);
//                    binding.layoutChild.tvNameLocation.setVisibility(View.GONE);
//                    binding.layoutChild.tvLocation.setGravity(Gravity.CENTER_VERTICAL);
                }

            } else {
                if (focusIOS.getStartAutoAppOpen() || focusIOS.getStartAutoTime() || focusIOS.getStartAutoLocation() || focusIOS.getStartCurrent()) {
                    binding.tvTitle.setTextColor(Color.BLACK);
                    binding.vHeader.setBackgroundColor(Color.WHITE);

                    binding.imMenu.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.color_33000000));

                    binding.imIconFocus.setColorFilter(null);

                    binding.tvTime.setVisibility(View.VISIBLE);

                    if (itemTurnOn == null) {
                        binding.tvTime.setText(itemView.getContext().getString(R.string.on));
                    } else {
                        if (itemTurnOn.getType() == ItemTurnOn.TYPE_LOCATION) {
                            binding.tvTime.setText(itemView.getContext().getString(R.string.on_until_you_leave_this_location));
                        } else {
                            binding.tvTime.setText(itemView.getContext().getString(R.string.on_until) + " " + TimeUtils.formatTOHourAndMinute(itemTurnOn.getTimeEnd()));
                        }

                    }

                } else {
                    binding.tvTitle.setTextColor(Color.WHITE);
                    binding.vHeader.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.color_66000000));

                    binding.imMenu.setColorFilter(Color.WHITE);

                    binding.imIconFocus.setColorFilter(Color.WHITE);

                    binding.tvTime.setVisibility(View.GONE);
                }

                binding.imMenu.setBackgroundResource(0);
            }

            binding.tvTitle.setText(StringUtils.INSTANCE.getIconDefaultApp(focusIOS.getName(), itemView.getContext()));

            Drawable drawable = DrawableUtils.getIconDefaultApp(focusIOS.getImageLink(), itemView.getContext());
            if (drawable == null) {
                Glide.with(binding.imIconFocus).load(focusIOS.getImageLink()).into(binding.imIconFocus);
            } else {
                binding.imIconFocus.setImageDrawable(drawable);
            }

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) binding.clViewChild.getLayoutParams();

            if (focusIOS.getName().equals(Constant.DRIVING) || focusIOS.getName().equals(Constant.SLEEP)) {
                layoutParams.height = dpToPx(itemView.getContext(), 1);
                binding.clViewChild.setVisibility(View.INVISIBLE);
            } else {
                layoutParams.height = dpToPx(itemView.getContext(), 103);
                binding.clViewChild.setVisibility(View.VISIBLE);
            }
            binding.clViewChild.setLayoutParams(layoutParams);

        }

    }

    public class AddHolder extends RecyclerView.ViewHolder {
        private ItemControlAddFocusBinding binding;

        public AddHolder(@NonNull ItemControlAddFocusBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    public interface ItemClickListener {
        void onMenuClick(FocusIOS focusIOS, int position);

        void onTitleClick(FocusIOS focusIOS, int position);

        void onHourClick(FocusIOS focusIOS, int position);

        void onEveningClick(FocusIOS focusIOS, int position);

        void onLocationClick(FocusIOS focusIOS, int position);

        void onSettingsClick(FocusIOS focusIOS, int position);

        void onNewClick();

        void onCloseView();
    }
}
