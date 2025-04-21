package com.tapbi.spark.controlcenter.feature.controlcenter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyGroup;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.NotyCenterViewOS;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.ITouchItemView;
import com.tapbi.spark.controlcenter.feature.controlcenter.view.noty.view.itemnoty.ChildSwipeLayout;
import com.tapbi.spark.controlcenter.feature.controlcenter.view.noty.view.itemnoty.SwipeLayout;
import com.tapbi.spark.controlcenter.service.NotificationListener;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.MethodUtils;

import java.util.ArrayList;

public class AdapterChildNotyMi extends RecyclerView.Adapter<AdapterChildNotyMi.Holder> {
    private ArrayList<NotyModel> notyModels;
    private NotyCenterViewOS.OnNotyCenterCloseListener onNotyCenterCloseListener;
    private ITouchItemView iTouchItemView;
    private RecyclerView rvChild;

    public AdapterChildNotyMi(RecyclerView rvChild, NotyGroup notyGroup, NotyCenterViewOS.OnNotyCenterCloseListener onNotyCenterCloseListener, ITouchItemView iTouchItemView) {
        this.rvChild = rvChild;
        this.notyModels = notyGroup.getNotyModels();
        this.onNotyCenterCloseListener = onNotyCenterCloseListener;
        this.iTouchItemView = iTouchItemView;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child_noty_mi, parent, false);
        return new AdapterChildNotyMi.Holder(v);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int pos) {
        int position = holder.getAbsoluteAdapterPosition();
        if (position < 0 || position >= notyModels.size()) {
            return;
        }
        NotyModel notyModel = notyModels.get(position);
        Constraints.LayoutParams layoutParams = new Constraints.LayoutParams(Constraints.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (position == notyModels.size() - 1) {
            layoutParams.setMargins(0, 0, 0, 0);
        } else {
            layoutParams.setMargins(0, 0, 0, (int) DensityUtils.pxFromDp(holder.childSwipeLayout.getContext(), 8f));
        }
        holder.childSwipeLayout.setLayoutParams(layoutParams);
        //if (holder.childSwipeLayout.first) {
        holder.childSwipeLayout.setCallBackUpdateHeight(onNotyCenterCloseListener, position, notyModel, iTouchItemView);
        holder.imgIcon.setImageDrawable(notyModel.getIconApp());
        holder.imgImageNoty.setImageDrawable(notyModel.getIconApp());
        holder.tvTimeNoty.setText(MethodUtils.getTimeAgo(holder.itemView.getContext(),notyModel.getTime()));
        //}

        ((ChildSwipeLayout) holder.itemView).setItemState(SwipeLayout.ITEM_STATE_COLLAPSED, false);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.childSwipeLayout.imgState.getVisibility() == View.INVISIBLE || holder.childSwipeLayout.notiModel.getState() == NotyGroup.STATE.EXPAND) {
                    try {
                        if (notyModel.getPendingIntent() == null || notyModel.getPendingIntent().getIntentSender() == null || notyModel.getPakage() == null) {
                            return;
                        }
                        DensityUtils.sendPendingIntent(App.mContext,notyModel.getPendingIntent(),notyModel.getPakage());
                        onNotyCenterCloseListener.closeEnd();
                        if (NotificationListener.getInstance() != null) {
                            NotificationListener.getInstance().deleteNoty(notyModel.getPakage(), notyModel.getIdNoty(), notyModel.getKeyNoty());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (holder.childSwipeLayout.notiModel.getState() == NotyGroup.STATE.NONE) {
                    holder.childSwipeLayout.imgStateClick.onClick(v);
                }


//        Intent intent = holder.itemView.getContext().getPackageManager().getLaunchIntentForPackage(notyModel.getPendingIntent());
            }
        });
        holder.childSwipeLayout.first = false;
    }

    public boolean isPosValid(int pos) {
        return (pos > -1) && (pos < getItemCount());
    }

    @Override
    public int getItemCount() {
        return notyModels.size();
    }

    public void removeNotyInAdapter(int index) {
        rvChild.post(() -> {
                try {
                    if (posIsValid(index)){
                        notifyItemRemoved(index);
                    }
                    if (index == 0) {
                        notifyItemChanged(0);
                    }
                } catch (Exception e){
                }
        });
//        notifyItemRangeChanged(index, getItemCount());
    }

    private Boolean posIsValid(int pos) {
        return (pos > -1) && (pos < notyModels.size());
    }

    public void insertNoty() {
        rvChild.post(() -> {
            if (posIsValid(0)){
                notifyItemInserted(0);
                notifyItemRangeChanged(0, getItemCount());
            }
        });
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public ChildSwipeLayout childSwipeLayout;
        public ImageView imgIcon;
        public ImageView imgImageNoty;
        public TextView tvTimeNoty;

        public Holder(@NonNull View itemView) {
            super(itemView);
            childSwipeLayout = itemView.findViewById(R.id.child_swipe_layout);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            imgImageNoty = itemView.findViewById(R.id.imgImageNoti);
            tvTimeNoty = itemView.findViewById(R.id.tvTimeNoty);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onLongClick(View v) {
            return true;
        }
    }
}
