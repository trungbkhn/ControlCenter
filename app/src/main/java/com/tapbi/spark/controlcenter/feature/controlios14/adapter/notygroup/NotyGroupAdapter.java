package com.tapbi.spark.controlcenter.feature.controlios14.adapter.notygroup;

import android.content.Context;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.ItemAddedNoty;
import com.tapbi.spark.controlcenter.common.models.MessageEvent;
import com.tapbi.spark.controlcenter.databinding.ItemGroupBinding;
import com.tapbi.spark.controlcenter.databinding.ItemViewTitleBinding;
import com.tapbi.spark.controlcenter.feature.NotyManager;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyGroup;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.ItemGroup;
import com.tapbi.spark.controlcenter.interfaces.CallBackOpenNoty;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.helper.rcvhepler.RecyclerViewNoty;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import timber.log.Timber;

public class NotyGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int viewTypeTitle = 2;
    private final int viewTypeNotify = 3;
    private final int posStartNotyNow = 0;
    private final int widthMenuTypeCanDelete = (int) (App.widthScreenCurrent * 0.45);
    private final int widthMenuTypeCanNotDelete = (widthMenuTypeCanDelete) / 2;
    private final Context context;
    private final RecyclerViewNoty rv;
    private final ArrayList<NotyGroup> listGroup = new ArrayList<>();
    private final ArrayList<NotyGroup> listNow = new ArrayList<>();
    private final int sizeItemTitle = 1;
    private final OnGroupNotyClickListener onGroupNotyClickListener;
    private ViewHolderNotify holderNotifyShowMenu = null;
    private NotyAdapter.ViewHolder holderItemChild = null;
    private boolean isShowNoty = true;
    private boolean isEmpty = true;
    RecyclerView.AdapterDataObserver dataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            Timber.e("NVQ onChanged++++++++++++++++");
//            if (ToastTextManager.Companion.getCurrentInstance() != null){
//                ToastTextManager.Companion.getCurrentInstance().show("it: "+getItemCount());
//            }
            checkIfEmpty();
        }
    };

    public NotyGroupAdapter(Context context, RecyclerViewNoty rv, OnGroupNotyClickListener onGroupNotyClickListener) {
        this.context = context;
        this.rv = rv;
        this.onGroupNotyClickListener = onGroupNotyClickListener;
        rv.setAdapter(this);

        updateData();

//        rv.setOnLongClickListener(v -> {
//            iListener.onLongClick();
//            return true;
//        });
        registerAdapterDataObserver(dataObserver);
        rv.setItemViewCacheSize(10);
        rv.setAnimationCacheEnabled(false);
    }

    private void checkIfEmpty() {
        Timber.e("NVQ checkIfEmpty+++++++++++++++++++++++++++++++++ :" + getItemCount());
        if (onGroupNotyClickListener != null) {
            if (getItemCount() == 0) {
                if (!isEmpty) {
                    isEmpty = true;
                    onGroupNotyClickListener.onEmptyNoty(true);
                }
            } else {
                if (isEmpty) {
                    isEmpty = false;
                    onGroupNotyClickListener.onEmptyNoty(false);
                }

            }
        }
    }

    private void updateData() {
        NotyManager notyManager = NotyManager.INSTANCE;
        if (notyManager != null) {
            listNow.clear();
            listGroup.clear();

            List<NotyGroup> notyNow = notyManager.getListNotyNow();
            List<NotyGroup> notyGroup = notyManager.getListNotyGroup();

            if (notyNow != null) {
                for (NotyGroup item : notyNow) {
                    if (item != null) {
                        listNow.add(item);
                    }
                }
            }

            if (notyGroup != null) {
                for (NotyGroup item : notyGroup) {
                    if (item != null) {
                        listGroup.add(item);
                    }
                }
            }
        }

        if (getItemCount() == 0) {
            notifyDataSetChanged();
        }
        checkIfEmpty();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == viewTypeTitle) {
            return new ViewHolderTitle(ItemViewTitleBinding.inflate(LayoutInflater.from(context), parent, false));
        } else if (viewType == viewTypeNotify) {
            return new ViewHolderNotify(ItemGroupBinding.inflate(LayoutInflater.from(context), parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == viewTypeTitle) {
            ViewHolderTitle holderTitle = (ViewHolderTitle) holder;
            holderTitle.bind();
        } else if (holder.getItemViewType() == viewTypeNotify) {
//            int posHolder = holder.getAbsoluteAdapterPosition();
            boolean isTypeNotyNow = position <= listNow.size();
            int indexOfList = isTypeNotyNow ? position : position - listNow.size() - sizeItemTitle;
            try {
                NotyGroup notyGroup = isTypeNotyNow ? listNow.get(indexOfList) : listGroup.get(indexOfList);
                ViewHolderNotify holderNotify = (ViewHolderNotify) holder;
                holderNotify.bind(position, notyGroup, isTypeNotyNow);
            } catch (Exception e) {
                Timber.e("hoangLd: " + e);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == listNow.size()) {
            return viewTypeTitle;
        } else {
            return viewTypeNotify;
        }
    }

    @Override
    public int getItemCount() {
        if (isShowNoty) {
            if (listGroup.isEmpty()) {
                return listNow.size();
            }
            //1 is ViewHolderTitle
            return listGroup.size() + listNow.size() + sizeItemTitle;
        } else {
            return 0;
        }
    }

    public void reloadDataAdapter() {
        updateData();
        closeSwipeAllNoty();
        rv.postOnAnimation(() -> {
            if (!rv.isComputingLayout()){
                rv.getRecycledViewPool().clear();
            }
        });
        notifyDataSetChanged();
    }

    public void removeGroupFromNoti(int pos, boolean isNotyNow, String packageName) {
        try {

            int posRemove = (isNotyNow) ? (pos) : (pos + listNow.size() + sizeItemTitle);
            RecyclerView.ViewHolder holder = rv.findViewHolderForLayoutPosition(posRemove);
            //RecyclerView.ViewHolder holder = rv.findViewHolderForAdapterPosition(posRemove);
            if (holder instanceof ViewHolderNotify) {
                //Timber.e("hoangld posRemove: " + posRemove + " package adapter: " + ((ViewHolderNotify) holder).packageName + " pk remove: "+ packageName + " thread " + Thread.currentThread().getName());
                if (Objects.equals(((ViewHolderNotify) holder).packageName, packageName)) {
                    removeItemRcv(posRemove);
                }
            }
            updateData();
            updateBlurWhenNotifyItem();
        } catch (Exception e) {
            reloadDataAdapter();
        }
    }

    ///hoangld update

    Boolean isPositionValid(int pos) {
        return (pos > -1 && pos < getItemCount());
    }

    public void removeGroup(int pos, boolean isNotyNow) {
        try {
            //Timber.e("hoangld xoa o adapter tong: " + pos + " / " + isNotyNow);
            if (isNotyNow) {
                removeItemRcv(pos);
            } else {
                removeItemRcv(pos + listNow.size() + sizeItemTitle);
            }
            //updateData();
            updateBlurWhenNotifyItem();
            //notifyItemRangeChanged(ex, getItemCount());
        } catch (Exception e) {
            Timber.e("hachung reloadDataAdapter:");
            reloadDataAdapter();
        }
    }

    ///hoangld update

    public void removedItemInGroupFromNoti(int posGroup, String packageName, String idNoty, boolean isNotyNow) {
        //Timber.e("hoangld ");
        Timber.e("hachung updateData:");
        try {
            NotyGroup notyGroup;
            if (isNotyNow) {
                notyGroup = listNow.get(posGroup);
            } else {
                notyGroup = listGroup.get(posGroup);
            }
            if (notyGroup.getNotyModels().size() == 0) {
                //Timber.e("removeGroup pos: " + posGroup + " getItemCount: " + getItemCount() + " size noty: " + notyGroup.getNotyModels().size() + " notyGroups: " + listGroup.size());
                removeGroupFromNoti(posGroup, isNotyNow, packageName);
            } else {
                //Timber.e("notifyItemChanged pos: " + posGroup + " getItemCount: " + getItemCount() + " size noty: " + notyGroup.getNotyModels().size() + " notyGroups: " + listGroup.size());
                removeItemNotyChild(posGroup, idNoty, isNotyNow, packageName);
            }
        } catch (Exception e) {
            Timber.e("hachung reloadDataAdapter:");
            reloadDataAdapter();
        }
    }

    private void removeItemNotyChild(int posGroup, String idNoty, boolean isNotyNow, String packageName) {
        int posInAdapter;
        if (isNotyNow) {
            posInAdapter = posGroup;
        } else {
            posInAdapter = posGroup + listNow.size() + sizeItemTitle;
        }

        ViewHolderNotify holderNotify = (ViewHolderNotify) rv.findViewHolderForAdapterPosition(posInAdapter);

        Timber.e("hachung holderNotify:" + holderNotify);
        if (holderNotify != null && holderNotify.packageName.equals(packageName)) {
            updateData();
            holderNotify.removeItemChild(idNoty);
        }
    }

    private boolean findAndRemoveFromNoti(ItemAddedNoty itemAddedNoty) {
        Timber.e("hachung itemAddedNoty:" + itemAddedNoty.getPosGroupRemove() + "/" + itemAddedNoty.getPosChildRemove());
        int posRemoveFirst = itemAddedNoty.getPosGroupRemove() + listNow.size() + sizeItemTitle;
        int posRemove = RecyclerView.NO_POSITION;
        RecyclerView.ViewHolder holder = rv.findViewHolderForAdapterPosition(posRemoveFirst);
        if (holder instanceof ViewHolderNotify) {
            //Timber.e("hoangld holder " + ((ViewHolderNotify) holder).packageName + " " + itemAddedNoty.getPackageName());
            if (Objects.equals(((ViewHolderNotify) holder).packageName, itemAddedNoty.getPackageName())) {
                posRemove = posRemoveFirst;
            }
        }
        Timber.e("hachung posRemove:" + posRemove);
        if (posRemove != RecyclerView.NO_POSITION) {
            removeItemRcv(posRemove);
            return true;
        }
        return false;
    }


    private boolean findAndRemoveFromChildNoti(ItemAddedNoty itemAddedNoty) {
        int posRemoveFirst = itemAddedNoty.getPosChildRemove() + listNow.size() + sizeItemTitle;
        ViewHolderNotify holderNotify = (ViewHolderNotify) rv.findViewHolderForAdapterPosition(posRemoveFirst);
        if (holderNotify != null && holderNotify.packageName.equals(itemAddedNoty.getPackageName())) {
            updateData();
            holderNotify.removeItemChild(itemAddedNoty.getKeyNoty());
            return true;
        }
        return false;
    }

    private boolean findAllowInsertNow(ItemAddedNoty itemAddedNoty) {
        int posInsert = 0;
        RecyclerView.ViewHolder holder = rv.findViewHolderForAdapterPosition(posInsert);
        if (holder instanceof ViewHolderTitle || getItemCount() == 0) {
            return true;
        }
        if (holder instanceof ViewHolderNotify) {
            //Timber.e("hoangld holder " + ((ViewHolderNotify) holder).packageName + " " + itemAddedNoty.getPackageName());
            return !Objects.equals(((ViewHolderNotify) holder).packageName, itemAddedNoty.getPackageName());
        } else {
            return false;
        }
    }

    public void setAddedItem(ItemAddedNoty itemAddedNoty) {
//      just for debugging
/*      h.removeCallbacks(r);
        h.postDelayed(r, 15000);*/

        //There are 2 cases
        //Case 1 : Item Now is new, added to the list for the first time => itemAddedNoty.isNewGroupListNow() == true
        //Case 2 : Item Now is old, item already exists recyclerview => itemAddedNoty.isNewGroupListNow() == false

        Timber.e("hachung isNewGroupListNow:" + itemAddedNoty.isNewGroupListNow());

        if (itemAddedNoty.isNewGroupListNow()) {
            updateData();
            boolean resultRemoveNoty = true;
            // Save state
            Parcelable recyclerViewState = rv.getLayoutManager().onSaveInstanceState();
            Timber.e("hachung getPosGroupRemove:" + itemAddedNoty.getPosGroupRemove() + "/" + itemAddedNoty.getPosChildRemove());
            if (itemAddedNoty.getPosGroupRemove() != -1) {
                resultRemoveNoty = findAndRemoveFromNoti(itemAddedNoty);
            } else if (itemAddedNoty.getPosChildRemove() != -1) {
                resultRemoveNoty=findAndRemoveFromChildNoti(itemAddedNoty);
            }

            // Restore state
            rv.getLayoutManager().onRestoreInstanceState(recyclerViewState);
//
//
            if (itemAddedNoty.getPosGroupRemove() == -1 && itemAddedNoty.getPosGroupNotify() != -1 && itemAddedNoty.getPosChildRemove() != -1) {
                int posChange = itemAddedNoty.getPosGroupNotify() + listNow.size() + sizeItemTitle;
                Timber.e("hachung notifyItemChanged:" + posChange);
                notifyItemChanged(posChange);
            }

            //check if removed error, reloadDataAdapter
            //Expected resultRemoveNoty == true
            Timber.e("hachung resultRemoveNoty:" + resultRemoveNoty);
            if (resultRemoveNoty) {
                if (findAllowInsertNow(itemAddedNoty)) {
//                    if (!listNow.isEmpty()){
//                        if (listNow.get(0).getPackageName().equals(itemAddedNoty.getPackageName())){
//                            Timber.e("hachung insertItemRcv:"+posStartNotyNow);
//                            insertItemRcv(posStartNotyNow);
//                        }
//                    }

                    //check itemAddedNoty vs item first listNow, May not be the same due to Asynchronous
                    if (!listNow.isEmpty()) {
                        String packageFirst = listNow.get(0).getPackageName();
                        if (packageFirst != null && packageFirst.equals(itemAddedNoty.getPackageName())) {
                            Timber.e("hachung insertItemRcv:" + posStartNotyNow);
                            insertItemRcv(posStartNotyNow);
                        } else if (packageFirst != null && !packageFirst.equals(itemAddedNoty.getPackageName())) {
                            //reload holder list noty now
                            Timber.e("hachung notifyListNotyNow:");
                            notifyListNotyNow();
                        }
                    }

                } else {
                    reloadDataAdapter();
                }
            } else {
                reloadDataAdapter();
            }

        } else {
            RecyclerView.ViewHolder holderFirstNow = rv.findViewHolderForAdapterPosition(posStartNotyNow);
            if (holderFirstNow instanceof ViewHolderNotify) {
                if (Objects.equals(itemAddedNoty.getPackageName(), ((ViewHolderNotify) holderFirstNow).packageName)) {
                    if (itemAddedNoty.getPosGroupNotify() != -1) {
                        Timber.e("hachung notifyItemChild:");
                        ((ViewHolderNotify) holderFirstNow).notifyItemChild(0);
                    } else {
                        Timber.e("hachung addedItemChild:");
                        ((ViewHolderNotify) holderFirstNow).addedItemChild();
                    }
                } else {
                    //if sort list now, update view list now
                    Timber.e("hachung notifyListNotyNow:");
                    notifyListNotyNow();
                }
            } else {
                //if sort list now, update view list now
                Timber.e("hachung notifyListNotyNow:");
                notifyListNotyNow();
            }

            if (itemAddedNoty.getPosGroupRemove() == -1 && itemAddedNoty.getPosGroupNotify() != -1 && itemAddedNoty.getPosChildRemove() != -1) {
                int posChange = itemAddedNoty.getPosGroupNotify() + listNow.size() + sizeItemTitle;
                Timber.e("hachung notifyItemChanged: " + posChange);
                notifyItemChanged(posChange);
            }

            if (itemAddedNoty.getPosGroupRemove() != -1) {
                if (!findAndRemoveFromNoti(itemAddedNoty)) {
                    Timber.e("hachung reloadDataAdapter: ");
                    reloadDataAdapter();
                }
            }
            updateData();
        }
        updateBlurWhenNotifyItem();
    }

    /**
     * Updates the list noty now.
     * Usually used when the list now sorted
     */
    private void notifyListNotyNow() {
        for (int i = 0; i < listNow.size(); i++) {
            if (posIsValid(i + posStartNotyNow)) {
                notifyItemChanged(i + posStartNotyNow);
            }
        }
    }


/*    private Handler h = new Handler();
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            Timber.e("hoangld Runnable -------------------------------- ");
            notifyDataSetChanged();
        }
    };*/

    private Boolean posIsValid(int pos) {
        return (pos > RecyclerView.NO_POSITION && pos < getItemCount());
    }

    public void removeItemRcv(int position) {
//        Timber.e("hoangld position " + position);
//        rv.post(() -> {
//
//        });
        if (posIsValid(position)) {
            notifyItemRemoved(position);
            checkIfEmpty();
        }
        // The recycler view have not started animating yet, so post a message to the
        // message queue that will be run after the recycler view have started animating.
        new Handler().post(waitForAnimationsToFinishRunnable);
    }

    public void insertItemRcv(int position) {
        if (posIsValid(position)) {
            notifyItemInserted(position);
        }

        // The recycler view have not started animating yet, so post a message to the
        // message queue that will be run after the recycler view have started animating.
        new Handler().post(waitForAnimationsToFinishRunnable);
    }

    // When the data in the recycler view is changed all views are animated. If the
// recycler view is animating, this method sets up a listener that is called when the
// current animation finishes. The listener will call this method again once the
// animation is done.
    private void waitForAnimationsToFinish() {
        if (rv.isAnimating()) {
            // The recycler view is still animating, try again when the animation has finished.
            rv.getItemAnimator().isRunning(animationFinishedListener);
            return;
        }

        // The recycler view have animated all it's views
        onRecyclerViewAnimationsFinished();
    }

    // The recycler view is done animating, it's now time to doStuff().
    private void onRecyclerViewAnimationsFinished() {
        doStuff();
    }

    private void doStuff() {
        EventBus.getDefault().post(new MessageEvent(Constant.TYPE_EVENT_UPDATE_ITEM_BLUR));
    }

    public void setShowNoty(boolean isShow) {
        this.isShowNoty = isShow;
        //Timber.e("notyGroups.size(): " + listGroup.size());
        Timber.e("hachung reloadDataAdapter:");
        reloadDataAdapter();
    }    private Runnable waitForAnimationsToFinishRunnable = new Runnable() {
        @Override
        public void run() {
            waitForAnimationsToFinish();
        }
    };

    private void closeSwipeAllNoty() {
        if (holderNotifyShowMenu != null) {
            holderNotifyShowMenu.binding.notyGroup.smoothClose();
            holderNotifyShowMenu = null;
        }

        if (getHolderItemChildSwipeShow() != null) {
            getHolderItemChildSwipeShow().binding.noty.smoothClose();
            setHolderItemChildSwipeShow(null);
        }
    }    // Listener that is called whenever the recycler view have finished animating one view.

    public NotyAdapter.ViewHolder getHolderItemChildSwipeShow() {
        return holderItemChild;
    }

    public void setHolderItemChildSwipeShow(NotyAdapter.ViewHolder holder) {
        this.holderItemChild = holder;
    }    private RecyclerView.ItemAnimator.ItemAnimatorFinishedListener animationFinishedListener = new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
        @Override
        public void onAnimationsFinished() {
            // The current animation have finished and there is currently no animation running,
            // but there might still be more items that will be animated after this method returns.
            // Post a message to the message queue for checking if there are any more
            // animations running.
            new Handler().post(waitForAnimationsToFinishRunnable);
        }
    };

    private void updateBlurWhenNotifyItem() {
        rv.post(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new MessageEvent(Constant.TYPE_EVENT_UPDATE_ITEM_BLUR));
            }
        });
    }

    /**
     * This method is called when get index list now or list old
     *
     * @param group     the item need find
     * @param isTypeNow distinguish 2 types list now or list old
     * @return index list
     */
    private int getIndexOfList(NotyGroup group, boolean isTypeNow) {
        int index = -1;
        if (isTypeNow) {
            for (int i = 0; i < listNow.size(); i++) {
                if (group.getPackageName().equals(listNow.get(i).getPackageName())) {
                    index = i;
                    break;
                }
            }
        } else {
            for (int i = 0; i < listGroup.size(); i++) {
//                Timber.e("hoangLd i: " + i + " --- " + group.getPackageName() + " --- " + listGroup.get(i).getPackageName());
                if (group.getPackageName().equals(listGroup.get(i).getPackageName())) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public interface OnGroupNotyClickListener {
        void onDeleteAll(ArrayList<NotyModel> notiModels);

        void onDeleteNoty(NotyModel notiModel);

        void onClearAllNoty();

        void onItemNotyScrolling(boolean isScrolling);

        void openNoty(NotyModel notyModel);

        void onEmptyNoty(boolean isEmpty);
    }

    public class ViewHolderTitle extends RecyclerView.ViewHolder {
        ItemViewTitleBinding binding;

        public ViewHolderTitle(ItemViewTitleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind() {
            binding.viewTitle.setListener(onGroupNotyClickListener);
        }
    }

    public class ViewHolderNotify extends RecyclerView.ViewHolder {
        public String packageName = "null abc";
        ItemGroupBinding binding;

        public ViewHolderNotify(ItemGroupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int positionLayout, NotyGroup notyGroup, boolean isTypeNotyNow) {
            if (notyGroup.getNotyModels().size() == 0) {
                return;
            }
            //Timber.e("bind positionLayout: "+positionLayout);
            packageName = notyGroup.getPackageName();
            binding.notyGroup.setData(NotyGroupAdapter.this, notyGroup, widthMenuTypeCanDelete, widthMenuTypeCanNotDelete, positionLayout, new ItemGroup.IListenerGroupNoty() {
                @Override
                public void onClickTvView() {
//                    Timber.e(".");
                }

                @Override
                public void onClickTvDelete() {
                    if (onGroupNotyClickListener != null) {
                        try {
                            onGroupNotyClickListener.onDeleteAll(notyGroup.getNotyModels());
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }

                    if (binding.notyGroup.isCanDeleteAll()) {
                        int indexOfList = getIndexOfList(notyGroup, isTypeNotyNow);
                        if (indexOfList != -1) {
                            if (isTypeNotyNow) {
                                if (indexOfList < listNow.size()){
                                    listNow.remove(indexOfList);
                                }else {
                                    reloadDataAdapter();
                                }

                            } else {
                                if (indexOfList< listGroup.size()){
                                    listGroup.remove(indexOfList);
                                }else {
                                    reloadDataAdapter();
                                }
                            }
                            removeGroup(indexOfList, isTypeNotyNow);
                        } else {
                            reloadDataAdapter();
                        }
                        //Timber.e("xoa keo: " + indexOfList + " /loai now: " + isTypeNotyNow + " / " + positionLayout + " / " + posLayout);
                        //notifyItemRangeChanged(positionLayout, getItemCount());
                    } else {
                        binding.notyGroup.deleteAll();
                    }
                    checkIfEmpty();
                }

                @Override
                public void onClickTvDeleteModel(NotyModel notyModel) {
                    //Timber.e("hoangld." + notyModel.getContent());
                    if (onGroupNotyClickListener != null) {
                        try {
                            onGroupNotyClickListener.onDeleteNoty(notyModel);
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }
                }

                @Override
                public void onClickViewGroup(NotyGroup notyGroup) {
                    if (NotyControlCenterServicev614.getInstance() != null) {
                        if (notyGroup.getNotyModels().size() == 0) {
                            reloadDataAdapter();
                            //return;
                        } else {
                            //Timber.e("hoangLd: ");
                            onGroupNotyClickListener.openNoty(notyGroup.getNotyModels().get(0));
                        }
                    }
                }

                @Override
                public void onClickNotyChild(NotyModel notyModel, CallBackOpenNoty callBackOpenNoty) {
                    Timber.e("hoangLd: ");
                    onGroupNotyClickListener.openNoty(notyModel);
                }

                @Override
                public void onScrolling(boolean isScrolling) {
                    onGroupNotyClickListener.onItemNotyScrolling(isScrolling);
                }

                @Override
                public void onStartSwipe(String idNoty) {
                    if (holderNotifyShowMenu != null && !holderNotifyShowMenu.binding.notyGroup.getIdNoty().equals(idNoty)) {
                        holderNotifyShowMenu.binding.notyGroup.smoothClose();
                        holderNotifyShowMenu = null;
                    }

                    //Timber.e("holderItemChild: " + holderItemChild.getLayoutPosition() + " /pos: " + holderItemChild.getPositionLayoutChildScrolling());
                    if (holderItemChild != null && (!holderItemChild.binding.noty.getKeyNoty().equals(idNoty))) {
                        holderItemChild.binding.noty.smoothClose();
                        setHolderItemChildSwipeShow(null);
                    }
                }

                @Override
                public void onEndSwipe() {
                    holderNotifyShowMenu = ViewHolderNotify.this;
                }

                @Override
                public void onItemNotyChildShowMenu(NotyAdapter.ViewHolder holderItemChild) {
                    setHolderItemChildSwipeShow(holderItemChild);
                    //Timber.e("holderItemChild: " + NotyGroupAdapterVer2.this.holderItemChild.getLayoutPosition());
                }

            });
        }

        public void removeItemChild(String idNoty) {
            binding.notyGroup.removeItemNoty(idNoty);
        }

        public void addedItemChild() {
            //Timber.e("hoangld: ");
            binding.notyGroup.addedItemChild();
        }

        public void notifyItemChild(int pos) {
            //Timber.e("hoangld: ");
            if (posIsValid(pos)) binding.notyGroup.notifyItemChild(pos);
        }

        public void removeItemChildWhenAdd(int pos) {
            binding.notyGroup.removeItemChildWhenAdd(pos);
        }

    }






}
