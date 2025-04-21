package com.tapbi.spark.controlcenter.feature.controlcenter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.models.ItemAddedNoty;
import com.tapbi.spark.controlcenter.feature.NotyManager;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.DataNotiMi;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.ITouchItemView;
import com.tapbi.spark.controlcenter.feature.controlcenter.view.noty.view.itemnoty.SwipeLayout;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyGroup;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.NotyCenterViewOS;
import com.tapbi.spark.controlcenter.service.NotificationListener;
import com.tapbi.spark.controlcenter.utils.DensityUtils;

import java.util.ArrayList;
import java.util.Objects;

import timber.log.Timber;

public class AdapterNotyMi extends RecyclerView.Adapter<AdapterNotyMi.ViewHolder> {

    private final NotyCenterViewOS.OnNotyCenterCloseListener onNotyCenterCloseListener;
    private final ITouchItemView iTouchItemView;
    private final RecyclerView rcvNoty;
    private ArrayList<NotyGroup> notyGroups = new ArrayList<>();
    private DataNotiMi dataNotiMi;

    public AdapterNotyMi(RecyclerView rcvNoty, NotyCenterViewOS.OnNotyCenterCloseListener onNotyCenterCloseListener, ITouchItemView ITouchItemView) {
        this.rcvNoty = rcvNoty;
        this.iTouchItemView = ITouchItemView;
        this.onNotyCenterCloseListener = onNotyCenterCloseListener;
    }

    public void setData(ArrayList<NotyGroup> notyGroups) {
        this.notyGroups = notyGroups;
        dataNotiMi = () -> this.notyGroups;
        reloadDataAdapter();
    }

    public void updateData() {
        this.notyGroups = NotyManager.INSTANCE.getListNotyGroup();
        dataNotiMi = () -> this.notyGroups;
    }

    public void reloadDataAdapter() {
        rcvNoty.post(() -> {
            if (!rcvNoty.isComputingLayout()) {
                rcvNoty.scrollToPosition(0);
                rcvNoty.getRecycledViewPool().clear();
                notifyDataSetChanged();
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_noty_mi, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

//    if (holder.swipeLayout.first || isNewNoti) {
//      if (position == notyGroups.size() - 1) {
//        isNewNoti = false;
//      }
        NotyModel notyModel = null;
        NotyGroup notyGroup = null;
        int pos = holder.getAbsoluteAdapterPosition();
        if (notyGroups.size() > pos) {
            notyGroup = notyGroups.get(pos);
            if (notyGroup != null && !notyGroup.getNotyModels().isEmpty()) {
                notyModel = notyGroup.getNotyModels().get(0);
            }
        }
        if (notyModel != null) {
            Constraints.LayoutParams layoutParams = new Constraints.LayoutParams(Constraints.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, (int) DensityUtils.pxFromDp(holder.swipeLayout.getContext(), pos == notyGroups.size() - 1 ? 60f : 8f));
            holder.swipeLayout.setLayoutParams(layoutParams);


            holder.swipeLayout.setCallBackUpdateHeight(dataNotiMi, onNotyCenterCloseListener, pos, notyGroup, iTouchItemView);
            holder.imgIcon.setImageDrawable(notyModel.getIconApp());
            holder.imgImageNoty.setImageDrawable(notyModel.getIconApp());


            NotyModel finalNotyModel = notyModel;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.swipeLayout.notiModel == null) {
                        return;
                    }
                    if (holder.swipeLayout.imgState.getVisibility() == View.INVISIBLE || holder.swipeLayout.notiModel.getState() == NotyGroup.STATE.EXPAND) {
                        try {
                            if (finalNotyModel.getPendingIntent() == null || finalNotyModel.getPendingIntent().getIntentSender() == null || finalNotyModel.getPakage() == null) {
                                return;
                            }
                            DensityUtils.sendPendingIntent(App.mContext, finalNotyModel.getPendingIntent(), finalNotyModel.getPakage());
                            onNotyCenterCloseListener.closeEnd();
                            if (NotificationListener.getInstance() != null) {
                                NotificationListener.getInstance().deleteNoty(finalNotyModel.getPakage(), finalNotyModel.getIdNoty(), finalNotyModel.getKeyNoty());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (holder.swipeLayout.notiModel.getState() == NotyGroup.STATE.NONE) {
                        holder.swipeLayout.imgStateClick.onClick(v);
                    }
                }
            });
            ((SwipeLayout) holder.itemView).setItemState(SwipeLayout.ITEM_STATE_COLLAPSED, false);
            holder.swipeLayout.first = false;
        }
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
    public int getItemCount() {
        if (notyGroups != null) {
            return notyGroups.size();
        } else {
            return 0;
        }
    }


    public void removeGroup(int ex) {
        try {
            rcvNoty.postOnAnimation(() -> {
                if (posIsValid(ex)) notifyItemRemoved(ex);
            });
            //notifyItemRangeChanged(ex, getItemCount());
        } catch (Exception e) {
            Timber.e("ex: " + ex + " e: " + e);
            reloadDataAdapter();
        }
    }

    public void removedItemInGroup(int posGroup, NotyGroup notyGroup, int posChild) {
        try {
            this.notyGroups.set(posGroup, notyGroup);
            if (notyGroup.getNotyModels().isEmpty()) {
                removeGroup(posGroup);
            } else {
                removeItemNotyChild(posGroup, posChild);
            }
        } catch (Exception e) {
            Timber.e("posGroup: " + posGroup + " e: " + e);
            reloadDataAdapter();
        }
    }

    private void removeItemNotyChild(int posGroup, int posChild) {
        ViewHolder holderNotify = (ViewHolder) rcvNoty.findViewHolderForAdapterPosition(posGroup);
        if (holderNotify != null) {
            holderNotify.swipeLayout.removeItemChild(posChild);
        }
    }

    private Boolean posIsValid(int pos) {
        return (pos > -1) && (pos < notyGroups.size());
    }

    public void setAddedItem(ItemAddedNoty itemAddedNoty) {
//        if (itemAddedNoty.isNewGroupListNow()) {
//            findAndRemoveFromNoti(itemAddedNoty);
//            if (posIsValid(0)) {
//                updateData();
//                notifyItemInserted(0);
//            }
//        } else {
//            RecyclerView.ViewHolder holder = rcvNoty.findViewHolderForAdapterPosition(itemAddedNoty.getPosGroupNotify());
//            if (holder instanceof ViewHolder) {
//                if (itemAddedNoty.getPosChildRemove() == 0) {
//                    ((ViewHolder) holder).swipeLayout.notifyItemChild(0);
//                } else if (itemAddedNoty.getPosChildRemove() != -1) {
//                    ((ViewHolder) holder).swipeLayout.removeItemChildWhenAdd(itemAddedNoty.getPosChildRemove());
//                    ((ViewHolder) holder).swipeLayout.addedItemChild();
//                } else {
//                    ((ViewHolder) holder).swipeLayout.addedItemChild();
//                }
//            }
//            findAndRemoveFromNoti(itemAddedNoty);
//            updateData();
//        }
////        rcvNoty.scrollToPosition(0);

        setData(NotyManager.INSTANCE.getListNotyGroup());

    }

    private boolean findAndRemoveFromNoti(ItemAddedNoty itemAddedNoty) {
        int posRemove = RecyclerView.NO_POSITION;
        Timber.e("hachung getPosGroupRemove:"+itemAddedNoty.getPosGroupRemove());
        RecyclerView.ViewHolder holder = rcvNoty.findViewHolderForAdapterPosition(itemAddedNoty.getPosGroupRemove());
        Timber.e("hachung holder:" + holder);
        if (holder instanceof ViewHolder) {
            Timber.e("hachung getPakage:" + ((ViewHolder) holder).swipeLayout.notiModel.getPakage());
            if (Objects.equals(((ViewHolder) holder).swipeLayout.notiModel.getPakage(), itemAddedNoty.getPackageName())) {
                posRemove = itemAddedNoty.getPosGroupNotify();
            }
        }
        Timber.e("hachung posRemove:" + posRemove);
        if (posRemove != RecyclerView.NO_POSITION) {
            notifyItemRemoved(itemAddedNoty.getPosGroupNotify());
            return true;
        }
        return false;
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        SwipeLayout swipeLayout;
        ImageView imgIcon;
        ImageView imgImageNoty;

        public ViewHolder(final View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe_layout);
            swipeLayout.setSwipeEnabled(true);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            imgImageNoty = itemView.findViewById(R.id.imgImageNoti);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            return true;
        }
    }
}
