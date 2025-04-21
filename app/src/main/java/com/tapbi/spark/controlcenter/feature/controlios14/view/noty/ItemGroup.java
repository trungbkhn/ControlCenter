package com.tapbi.spark.controlcenter.feature.controlios14.view.noty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.MessageEvent;
import com.tapbi.spark.controlcenter.databinding.ItemNotyGroupVer2Binding;
import com.tapbi.spark.controlcenter.feature.controlios14.adapter.notygroup.NotyAdapter;
import com.tapbi.spark.controlcenter.feature.controlios14.adapter.notygroup.NotyGroupAdapter;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyGroup;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel;
import com.tapbi.spark.controlcenter.interfaces.CallBackOpenNoty;
import com.tapbi.spark.controlcenter.interfaces.IListenerTitleChildNoty;
import com.tapbi.spark.controlcenter.interfaces.OnSwipeListener;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.helper.rcvhepler.NpaLinearLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import timber.log.Timber;

public class ItemGroup extends ConstraintLayout {
    private ItemNotyGroupVer2Binding binding;
    private NotyGroup notyGroup;
    private boolean isCanDeleteGroup;
    private int widthMenu;
    private NotyAdapter notyAdapter;
    private IListenerGroupNoty iListenerGroupNoty;
    private int widthMenuTypeCanDelete;
    private int widthMenuTypeCanNotDelete;
    private int heightNotyFirst = 0;
    private NotyGroupAdapter groupAdapter;

    public ItemGroup(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ItemGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        binding = ItemNotyGroupVer2Binding.inflate(LayoutInflater.from(context), this, true);
    }

    @SuppressLint("SetTextI18n")
    public void setData(NotyGroupAdapter groupAdapter, NotyGroup notyGroup, int widthMenuTypeCanDelete, int widthMenuTypeCanNotDelete, int positionLayoutParent, IListenerGroupNoty iListenerGroupNoty) {
//        binding.tvPos.setText(positionLayoutParent+ "");
        this.groupAdapter = groupAdapter;
        this.widthMenuTypeCanDelete = widthMenuTypeCanDelete;
        this.widthMenuTypeCanNotDelete = widthMenuTypeCanNotDelete;
        this.notyGroup = notyGroup;
        this.iListenerGroupNoty = iListenerGroupNoty;
        //binding.tvPos.setText("" + positionLayoutParent);
        if (notyGroup.getNotyModels().size() == 0) {
            return;
        }
        showMoreItemNoty(false);
        binding.llTop.setVisibility(GONE);
        setValueAnimExpand(0);
        updateContent();

//        binding.background.setItemView(binding.swipeLayout, ImageBackgroundItemView.TypeView.NOTIFY, appName.toUpperCase(), binding.menuRight);
        binding.bgTvView.setItemView(binding.tvViewNoty, ImageBackgroundItemView.TypeView.RIGHT_MENU);
        binding.bgTvRight.setItemView(binding.tvMenuRight, ImageBackgroundItemView.TypeView.RIGHT_MENU);


        binding.swipeLayout.setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipe(int x) {
                //Timber.e("x: " + x);
                binding.menuRight.updateShow(Math.abs(x));
//                binding.background.updateBackgroundHorizontal();
            }

            @Override
            public void onScrolling(boolean isScrolling) {
                iListenerGroupNoty.onScrolling(isScrolling);
            }

            @Override
            public void onStartSwipe() {
                binding.bgTvView.updateBackgroundHorizontal();
                binding.bgTvRight.updateBackgroundHorizontal();
                iListenerGroupNoty.onStartSwipe(getIdNoty());
            }

            @Override
            public void onEndSwipe() {
                iListenerGroupNoty.onEndSwipe();
            }

            @Override
            public void onSwipeToDelete() {
                iListenerGroupNoty.onClickTvDelete();
                post(new Runnable() {
                    @Override
                    public void run() {
                        binding.menuRight.updateShow(0);
//                        binding.background.updateBackgroundHorizontal();
                    }
                });
            }
        });

        binding.tvViewNoty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTvViewNoty();
            }
        });

        binding.tvMenuRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCanDeleteGroup) {
                    //Timber.e("hoangld: ");
                    iListenerGroupNoty.onClickTvDelete();
                    smoothClose();
                } else {
                    clickTvViewNoty();
                }
            }
        });

    }

    public String getIdNoty() {
        if (notyGroup.getNotyModels().size() > 0) {
            return notyGroup.getNotyModels().get(0).getKeyNoty();
        }
        return "-1";
    }

    private void updateContent() {
        setViewMenuRight();
        setLayoutTopShowMore();
        initViewMore();
        resetShowView();
    }

    public void clickTvViewNoty() {
        smoothClose();
        if (notyGroup.getNotyModels().size() > 1) {
            showMoreItemNoty(true);
            notyAdapter.onAniShow();
        } else {
            notyAdapter.showFullContentNoty();
            Timber.e("hoangld TYPE_EVENT_UPDATE_ITEM_BLUR");
            post(() -> EventBus.getDefault().post(new MessageEvent(Constant.TYPE_EVENT_UPDATE_ITEM_BLUR)));
        }
    }

    public void smoothClose() {
        //Timber.e(".");
        binding.swipeLayout.smoothClose();
    }

    public void removeItemNoty(String keyNoty) {
        int indexNotyRemove = RecyclerView.NO_POSITION;
        for (int i = 0; i < notyGroup.getNotyModels().size(); i++) {
            NotyModel noty = notyGroup.getNotyModels().get(i);
            if (noty.getKeyNoty().equals(keyNoty)) {
                indexNotyRemove = i;
                break;
            }
        }
        if (indexNotyRemove != RecyclerView.NO_POSITION) {
            notyAdapter.removeNotyInAdapter(indexNotyRemove);

            //Timber.e("hoangLd: ");
            setViewMenuRight();

            if (notyGroup.getNotyModels().size() < 3) {
                setLayoutTopShowMore();
            }
            //Timber.e("hoangld: " + notyGroup.getNotyModels().size());
            Timber.e("hoangld TYPE_EVENT_UPDATE_ITEM_BLUR");
            post(() -> EventBus.getDefault().post(new MessageEvent(Constant.TYPE_EVENT_UPDATE_ITEM_BLUR)));
        } else {
            //Timber.e("hoangld -1 size: " + notyGroup.getNotyModels().size());
            updateContent();
        }
        //Timber.e("hoangld getNotyModels size: " + notyGroup.getNotyModels().size() + " indexNotyRemove " + indexNotyRemove);
        if (notyGroup.getNotyModels().size() == 0) {
            iListenerGroupNoty.onClickTvDelete();
        } else if (notyGroup.getNotyModels().size() == 1) {
            showMoreItemNoty(false);
            notyAdapter.onAniHide();
        }


    }

    public void removeChildNotyFromNotyNow(int index) {
        if (notyAdapter == null) {
            return;
        }
        notyAdapter.removeChildNotyFromNotyNow(index);
    }

    public void addedItemChild() {
        if (notyGroup.getNotyModels().size() == 1) {
            binding.swipeLayout.setDefaultClose();
        }
        if (notyGroup.getNotyModels().size() > 0) {
            notyAdapter.insertNoty();
        }
    }

    public void notifyItemChild(int pos) {

        if (notyAdapter == null) {
            return;
        }
        binding.rvNoty.post(() -> {
            if (pos>-1 && pos<notyAdapter.getItemCount()) notyAdapter.notifyItemChanged(pos);
        });
    }

    public void removeItemChildWhenAdd(int pos) {
        if (notyAdapter == null) {
            return;
        }
        binding.rvNoty.post(() -> {
            if (pos>-1 && pos<notyAdapter.getItemCount()) notyAdapter.notifyItemRemoved(pos);
        });
    }

    private final OnExpandListener onExpandListener = new OnExpandListener() {
        @Override
        public void onClickExpand() {
            showMoreItemNoty(true);
            notyAdapter.onAniShow();
        }

        @Override
        public void onValueExpanded(float value) {
            setValueAnimExpand(value);
        }

        @Override
        public void onShowExpandDone() {
            binding.llTop.setZ(1);
        }

        @Override
        public void onHideExpandDone() {
            binding.llTop.setVisibility(GONE);
            EventBus.getDefault().post(new MessageEvent(Constant.TYPE_EVENT_UPDATE_ITEM_BLUR));
        }

        @Override
        public void onNotyFirstChangeHeight(int height) {
            heightNotyFirst = height;
            setSizeMenu();
        }
    };


    private boolean isCanDeleteSomeItemsInGroup() {
        if (notyGroup == null) {
            return true;
        }
        for (NotyModel model : notyGroup.getNotyModels()) {
            if (model.isCanDelete()) {
                return true;
            }
        }
        return false;
    }

    public boolean isCanDeleteAll() {
        if (notyGroup == null) {
            return true;
        }
        for (NotyModel model : notyGroup.getNotyModels()) {
            if (!model.isCanDelete()) {
                return false;
            }
        }
        return true;
    }


    public void deleteAll() {
        ArrayList<NotyModel> list = new ArrayList<>();
        int size = notyGroup.getNotyModels().size();
        for (NotyModel model : notyGroup.getNotyModels()) {
            if (model.isCanDelete()) {
                list.add(model);
            }
        }
        for (NotyModel model : list) {
            removeItemNoty(model.getKeyNoty());
        }
        if (list.size() > 0 && notyGroup.getNotyModels().size() < 3) {
            setLayoutTopShowMore();
        }
    }

    //////////////
    private void initViewMore() {
        binding.llTop.setData(notyGroup.getAppName(), isCanDeleteGroup, new IListenerTitleChildNoty() {
            @Override
            public void showLess() {
                if (groupAdapter != null) {
                    NotyAdapter.ViewHolder holderSwipeShow = groupAdapter.getHolderItemChildSwipeShow();
                    if (holderSwipeShow != null && holderSwipeShow.notyModel.getPakage().equals(notyGroup.getPackageName())) {
                        holderSwipeShow.binding.noty.smoothClose();
                        groupAdapter.setHolderItemChildSwipeShow(null);
                    }
                }
                showMoreItemNoty(false);
                notyAdapter.onAniHide();
            }

            @Override
            public void clearAll() {
                iListenerGroupNoty.onClickTvDelete();
            }
        });

        LinearLayoutManager layoutManager = new NpaLinearLayoutManager(getContext());
        binding.rvNoty.setLayoutManager(layoutManager);
        notyAdapter = new NotyAdapter(binding.rvNoty, binding.swipeLayout, getContext(), widthMenuTypeCanDelete, widthMenuTypeCanNotDelete, onExpandListener);
        binding.rvNoty.setAdapter(notyAdapter);
//        binding.rvNoty.setItemAnimator(null);

        binding.rvNoty.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if (posRemoved > 0) {
                    posRemoved = -1;
                    EventBus.getDefault().post(new MessageEvent(Constant.TYPE_EVENT_UPDATE_ITEM_BLUR));
                }
            }
        });

        notyAdapter.init(notyGroup, notyGroup.getAppName(), iListenerGroupNoty, new NotyAdapter.IRemoveItemInAdapter() {
            @Override
            public void onItemInAdapterRemoved(int posRemoved) {
                ItemGroup.this.posRemoved = posRemoved;
                //item first
                setViewMenuRight();
                setLayoutTopShowMore();
                if (notyAdapter.getItemCount() <= 1) {
                    showMoreItemNoty(false);
                    notyAdapter.onAniHide();
                }
            }
        });
    }

    private int posRemoved = -1;

    private void showMoreItemNoty(boolean isShow) {
        binding.swipeLayout.setSwipeEnable(!isShow);
        if (isShow) {
            //notyGroup.setState(NotyGroup.STATE.EXPANDED);
            binding.llTop.setVisibility(VISIBLE);
            binding.menuRight.setVisibility(View.INVISIBLE);
        } else {
            //notyGroup.setState(NotyGroup.STATE.NONE);
            binding.llTop.setZ(0);
            binding.menuRight.setVisibility(View.VISIBLE);
        }
        if (isShow){
            post(() -> EventBus.getDefault().post(new MessageEvent(Constant.TYPE_EVENT_UPDATE_ITEM_BLUR)));
        }
    }

    private final int maxTransYLayoutTitle = (int) MethodUtils.dpToPx(20);
    private final int maxPaddingTopRvNoty = (int) MethodUtils.dpToPx(41);

    public void setValueAnimExpand(float value) {
        int transYLayoutTitle = (int) ((1 - value) * maxTransYLayoutTitle);
        binding.llTop.setTranslationY(transYLayoutTitle);

        int paddingTop = (int) (maxPaddingTopRvNoty * value);
        binding.rvNoty.setPadding(0, paddingTop, 0, 0);
    }

    private void resetShowView() {
        binding.menuRight.setVisibility(View.VISIBLE);
    }

    private void setLayoutTopShowMore() {
        //binding.swipeLayout.setMoreNoty(binding.card1, binding.card2, notyGroup.getNotyModels().size() - 1, binding.menuRight);
        binding.swipeLayout.setMenuRight(binding.menuRight);
    }


    private void setViewMenuRight() {
        isCanDeleteGroup = isCanDeleteSomeItemsInGroup();
        if (isCanDeleteGroup) {
            widthMenu = widthMenuTypeCanDelete;
            if (notyGroup.getNotyModels().size() > 1) {
                binding.tvMenuRight.setText(getContext().getString(R.string.clear_all_noty));
            } else {
                binding.tvMenuRight.setText(getContext().getString(R.string.delete_noty));
            }
            binding.tvViewNoty.setText(getContext().getString(R.string.view_noty));
            binding.tvViewNoty.setVisibility(VISIBLE);
        } else {
            widthMenu = widthMenuTypeCanNotDelete;
            binding.tvMenuRight.setText(getContext().getString(R.string.view_noty));
            binding.tvViewNoty.setVisibility(INVISIBLE);
        }
        setSizeMenu();
    }


    private void setSizeMenu() {
        binding.menuRight.setSize(binding.tvViewNoty, binding.tvMenuRight, binding.bgTvRight, widthMenu, heightNotyFirst, isCanDeleteGroup);
    }



    public interface IListenerGroupNoty {
        void onClickTvView();

        void onClickTvDelete();

        void onClickTvDeleteModel(NotyModel notyModel);

        void onClickViewGroup(NotyGroup notyGroup);

        void onClickNotyChild(NotyModel notyGroup, CallBackOpenNoty callBackOpenNoty);

        void onScrolling(boolean isScrolling);

        void onStartSwipe(String keyNoty);

        void onEndSwipe();

        void onItemNotyChildShowMenu(NotyAdapter.ViewHolder holderItemChild);
    }

    public interface OnExpandListener {
        void onClickExpand();

        void onValueExpanded(float value);

        void onShowExpandDone();

        void onHideExpandDone();

        void onNotyFirstChangeHeight(int height);
    }
}