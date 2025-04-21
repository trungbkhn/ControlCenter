package com.tapbi.spark.controlcenter.feature.controlios14.adapter.notygroup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.databinding.ItemNotyVer2Binding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyGroup;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.SwipeLayoutNoty;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.ItemGroup;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.ItemNoty;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;

import java.util.ArrayList;
import java.util.List;

public class NotyAdapter extends RecyclerView.Adapter<NotyAdapter.ViewHolder> {
    private final int widthMenuTypeCanDelete;
    private final int widthMenuTypeCanNotDelete;
    private final RecyclerView rcv;
    private final int marginHorizontalCardMore;
    private final float marginVerticalItem;
    private final ItemGroup.OnExpandListener onExpandListener;
    private final int timeAni = 300;
    private final ValueAnimator aniShow = ValueAnimator.ofFloat(0f, 1f);
    private final ValueAnimator aniHide = ValueAnimator.ofFloat(1f, 0f);
    private final int sizeMaxAnim = 15;
    private ArrayList<NotyModel> listNoty = new ArrayList<>();
    private String appName = "";
    private ItemGroup.IListenerGroupNoty iListenerGroupNoty;
    private int posScrolling;
    private IRemoveItemInAdapter iRemoveItemInAdapter;
    private NotyGroup notyGroup;
    private SwipeLayoutNoty swipeParent;
    private StatusExpanded statusExpanded = StatusExpanded.hidden;
    private final AnimatorListenerAdapter listenerAniShow = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            List<ViewHolder> list = getListHolderAnim(true);
            statusExpanded = StatusExpanded.expanded;
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setAniValue(1);
                list.get(i).setSwipeEnable(true);
            }
            onExpandListener.onShowExpandDone();
        }
    };
    private final AnimatorListenerAdapter listenerAniHide = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            statusExpanded = StatusExpanded.hidden;
            onExpandListener.onHideExpandDone();
        }
    };

    public NotyAdapter(RecyclerView rcv, SwipeLayoutNoty swipeParent, Context context, int widthMenuTypeCanDelete, int widthMenuTypeCanNotDelete, ItemGroup.OnExpandListener onExpandListener) {
        this.rcv = rcv;
        this.swipeParent = swipeParent;
        this.widthMenuTypeCanDelete = widthMenuTypeCanDelete;
        this.widthMenuTypeCanNotDelete = widthMenuTypeCanNotDelete;
        this.onExpandListener = onExpandListener;
        marginHorizontalCardMore = (int) context.getResources().getDimension(R.dimen.margin_horizontal_bottom_more_item_noty) * 2;
        marginVerticalItem = context.getResources().getDimension(R.dimen.size_bottom_more_item_noty) - 1;
//        marginVerticalItem = 0;
    }

    public void init(NotyGroup notyGroup, String appName, ItemGroup.IListenerGroupNoty iListenerGroupNoty, IRemoveItemInAdapter iRemoveItemInAdapter) {
        this.iListenerGroupNoty = iListenerGroupNoty;
        this.listNoty = notyGroup.getNotyModels();
        this.notyGroup = notyGroup;
        this.appName = appName;
        this.iRemoveItemInAdapter = iRemoveItemInAdapter;
        rcv.post(() -> {
            if (!rcv.isComputingLayout()) {
                rcv.getRecycledViewPool().clear();
                rcv.getAdapter().notifyDataSetChanged();
            }
        });
    }

    public void removeNotyInAdapter(int index) {
        if (index < getItemCount()) {
            listNoty.remove(index);
            removeChildNotyFromNotyNow(index);
        }

    }

    public void removeChildNotyFromNotyNow(int index) {
        rcv.post(() -> {
            notifyItemRemoved(index);
            new Handler().post(waitForAnimationsToFinishRunnable);
            if (index == 0) {
                notifyItemChanged(0);
            }
            notifyItemRangeChanged(index, getItemCount());
            if (iRemoveItemInAdapter != null) {
                iRemoveItemInAdapter.onItemInAdapterRemoved(index);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemNotyVer2Binding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        int itemCount = listNoty.size();
        if (statusExpanded == StatusExpanded.hidden) {
            return Math.min(itemCount, 3);
        }
        if (statusExpanded == StatusExpanded.animationHidden || statusExpanded == StatusExpanded.animationExpanded) {
            return Math.min(itemCount, sizeMaxAnim);
        }
        return itemCount;
    }

    private List<ViewHolder> getListHolderAnim(boolean aniShow) {
        List<ViewHolder> list = new ArrayList<>();
        int size = Math.min(getItemCount(), sizeMaxAnim);
        for (int i = 0; i < size; i++) {
            ViewHolder viewHolder = getHolderByPos(i);
            if (viewHolder != null) {
                list.add(viewHolder);
            } else {
                break;
            }
        }
        return list;
    }

    public void showFullContentNoty() {
        for (int i = 0; i < getItemCount(); i++) {
            ViewHolder viewHolder = getHolderByPos(i);
            if (viewHolder != null) {
                viewHolder.showFullContent();
            } else {
                break;
            }
        }
    }

    public void onAniShow() {
        statusExpanded = StatusExpanded.animationExpanded;
        List<ViewHolder> list = getListHolderAnim(true);

        int sizeNeedAnim = Math.min(getItemCount(), sizeMaxAnim);
        final boolean[] updateListInAnimRunning = {list.size() < sizeNeedAnim};

        aniHide.cancel();
        aniShow.cancel();
        aniShow.removeAllUpdateListeners();
        aniShow.removeListener(listenerAniShow);
        aniShow.addUpdateListener(animation -> {
            if (updateListInAnimRunning[0]) {
                if (getHolderByPos(list.size()) != null) {
                    for (int i = list.size(); i < sizeNeedAnim; i++) {
                        ViewHolder holder = getHolderByPos(i);
                        if (holder != null) {
                            list.add(holder);
                        } else {
                            break;
                        }
                    }
                    updateListInAnimRunning[0] = false;
                }
            }
            float value = (float) animation.getAnimatedValue();
            onExpandListener.onValueExpanded(value);
            for (int i = 1; i < list.size(); i++) {
                ViewHolder viewHolder = list.get(i);
                viewHolder.setAniValue(value);
            }
        });

        aniShow.addListener(listenerAniShow);
        aniShow.setDuration(timeAni).start();
    }

    public void onAniHide() {
        statusExpanded = StatusExpanded.animationHidden;
        List<ViewHolder> list = getListHolderAnim(false);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSwipeEnable(false);
        }
        aniShow.cancel();
        aniHide.cancel();
        aniHide.removeAllUpdateListeners();
        aniHide.removeListener(listenerAniHide);
        aniHide.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            onExpandListener.onValueExpanded(value);
            for (int i = 1; i < list.size(); i++) {
                ViewHolder viewHolder = list.get(i);
                viewHolder.setAniValue(value);
            }
        });
        aniHide.addListener(listenerAniHide);
        aniHide.setDuration(timeAni).start();
    }

    private ViewHolder getHolderByPos(int pos) {
        RecyclerView.ViewHolder holder = rcv.findViewHolderForAdapterPosition(pos);
        if (holder instanceof ViewHolder) {
            return (ViewHolder) rcv.findViewHolderForAdapterPosition(pos);
        }
        return null;
    }

    private int getIndexOfList(NotyModel notyModel) {
        for (int i = 0; i < listNoty.size(); i++) {
            if (listNoty.get(i).getKeyNoty().equals(notyModel.getKeyNoty())) {
                return i;
            }
        }
        return -1;
    }

    public void insertNoty() {
        rcv.post(() -> {
            rcv.getRecycledViewPool().clear();
            notifyDataSetChanged();
        });
//        if (statusExpanded == StatusExpanded.hidden) {
//            notifyDataSetChanged();
//        } else {
//            ViewHolder holderFirst = getHolderByPos(0);
//            if (holderFirst != null) {
//                holderFirst.pos = 1;
//                holderFirst.setAniValue(1);
//            }
//            notifyItemInserted(0);
//            notifyItemRangeChanged(1, getItemCount());
//        }
    }

    // When the data in the recycler view is changed all views are animated. If the
// recycler view is animating, this method sets up a listener that is called when the
// current animation finishes. The listener will call this method again once the
// animation is done.
    private void waitForAnimationsToFinish() {
        if (rcv.isAnimating()) {
            // The recycler view is still animating, try again when the animation has finished.
            rcv.getItemAnimator().isRunning(animationFinishedListener);
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
        for (int i = 0; i < getItemCount(); i++) {
            ViewHolder viewHolder = getHolderByPos(i);
            if (viewHolder != null) {
                viewHolder.updateBlur();
            }
        }
    }    private Runnable waitForAnimationsToFinishRunnable = new Runnable() {
        @Override
        public void run() {
            waitForAnimationsToFinish();
        }
    };

    public enum StatusExpanded {expanded, hidden, animationExpanded, animationHidden}

    public interface IRemoveItemInAdapter {
        void onItemInAdapterRemoved(int posRemoved);
    }    // Listener that is called whenever the recycler view have finished animating one view.
    private RecyclerView.ItemAnimator.ItemAnimatorFinishedListener animationFinishedListener = new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
        @Override
        public void onAnimationsFinished() {
            // The current animation have finished and there is currently no animation running,
            // but there might still be more items that will be animated after this method returns.
            // Post a message to the message queue for checking if there are any more
            // animations running.
            new Handler().post(waitForAnimationsToFinishRunnable);
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemNotyVer2Binding binding;
        public NotyModel notyModel;
        private int pos = 0;

        public ViewHolder(ItemNotyVer2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int pos) {
            this.pos = pos;
            if (pos < 0 || pos >= listNoty.size()) {
                return;
            }
            notyModel = listNoty.get(pos);

            if (pos == 0 || pos >= sizeMaxAnim || statusExpanded == StatusExpanded.animationExpanded || statusExpanded == StatusExpanded.expanded) {
                setScale(1);
                setTopMargin(0);
                setAlphaBgColorBack(1f);
            }

            itemView.setZ(100 - pos);
            setSwipeEnable(statusExpanded != StatusExpanded.hidden && getItemCount() > 1);
            binding.noty.setData(pos, notyModel, appName, widthMenuTypeCanDelete, widthMenuTypeCanNotDelete, new ItemNoty.IListenerItemNoty() {
                @Override
                public void onClickTvView() {

                }

                @Override
                public void onClickTvDeleteModel(NotyModel notyModel) {
                    iListenerGroupNoty.onClickTvDeleteModel(notyModel);
                    int index = getIndexOfList(notyModel);
                    if (index != -1) {
                        removeNotyInAdapter(index);
                    }
                }

                @Override
                public void onClickViewNoty(NotyModel notyModel) {
                    if (swipeParent.isTranslate()) {
                        return;
                    }
                    if (statusExpanded == StatusExpanded.hidden && getItemCount() > 1) {
                        onExpandListener.onClickExpand();
                    } else {
                        if (NotyControlCenterServicev614.getInstance() != null) {
                            if (listNoty.size() == 1) {
                                iListenerGroupNoty.onClickViewGroup(notyGroup);
                            } else {
                                iListenerGroupNoty.onClickNotyChild(notyModel, allowOpen -> {
                                    removeNotyInAdapter(pos);
                                });
//                                LockScreenService.instance.openNoty(notyModel, allowOpen -> {
//                                    removeNotyInAdapter(pos);
//                                });
                            }

                        }
                    }
                }

                @Override
                public void onScrolling(boolean isScrolling) {
                    iListenerGroupNoty.onScrolling(isScrolling);
                }

                @Override
                public void onStartSwipe() {
                    posScrolling = pos;
                    //Timber.e("onStartSwipe child");
                    iListenerGroupNoty.onStartSwipe(binding.noty.getKeyNoty());
                }

                @Override
                public void onEndSwipe() {
                    //Timber.e("onEndSwipe child");
                    iListenerGroupNoty.onItemNotyChildShowMenu(ViewHolder.this);
                }

                @Override
                public void onHeightChange(int height) {
                    if (pos == 0 || pos >= sizeMaxAnim || statusExpanded == StatusExpanded.animationExpanded || statusExpanded == StatusExpanded.expanded) {

                    } else {
                        setAniValue(0);
                    }
                    if (pos == 0) {
                        onExpandListener.onNotyFirstChangeHeight(height);
                    }
                }
            });
            binding.noty.setDefaultClose();
        }

        public int getPositionLayoutChildScrolling() {
            return posScrolling;
        }

        public void setAniValue(float value) {
            float marginHori;
            int marginTop;
            float scale = 1;
            if (pos == 0) {
                marginHori = 0;
                marginTop = 0;
            } else if (pos <= 2) {
                marginHori = (1 - value) * (marginHorizontalCardMore * pos);
                marginTop = (int) (getMarginNormal() * (1 - value));
                scale = 1 - marginHori / binding.noty.getWidthView();
            } else {
                marginHori = (1 - value) * (marginHorizontalCardMore * 2);
                marginTop = (int) ((-marginVerticalItem + getMarginNormal()) * (1 - value));
                scale = 1 - marginHori / binding.noty.getWidthView();
            }
            setScale(scale);
            setTopMargin(marginTop);
            setAlphaBgColorBack(value);
        }

        public void setTopMargin(int margin) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            lp.topMargin = margin;
            itemView.requestLayout();
        }

        public void setScale(float scale) {
            if (Float.isNaN(scale) || scale < 0.5f) {
                return;
            }
            itemView.setScaleX(scale);
        }

        public void setAlphaBgColorBack(float value) {
            binding.noty.setAlphaBgColorBack(value);
        }

        public int getMarginNormal() {
            return -binding.noty.getHeightView() /*+ (pos * context.getResources().getDimensionPixelSize(R.dimen.size_bottom_more_item_noty))*/;
        }

        public void setSwipeEnable(boolean isSwipeEnable) {
            binding.noty.setSwipeEnable(isSwipeEnable);
        }

        public void showFullContent() {
            binding.noty.showFullContent();
        }

        public void updateBlur() {
            binding.noty.updateBackgroundHorizontal();
        }
    }




}