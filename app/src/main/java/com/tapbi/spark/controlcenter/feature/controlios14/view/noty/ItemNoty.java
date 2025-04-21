package com.tapbi.spark.controlcenter.feature.controlios14.view.noty;

import static com.tapbi.spark.controlcenter.utils.MethodUtils.getTimeAgo;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.databinding.LayoutNotyVer2Binding;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel;
import com.tapbi.spark.controlcenter.interfaces.OnSwipeListener;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.Utils;

import timber.log.Timber;


public class ItemNoty extends ConstraintLayout {
    private LayoutNotyVer2Binding binding;
    private NotyModel notyModel;
    private IListenerItemNoty iListenerNoty;
    private int widthMenu;
    private int heightView = 0;
    private int widthView = 0;
    private int pos;

    public ItemNoty(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ItemNoty(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ItemNoty(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public int getHeightView() {
        return heightView;
    }

    public int getWidthView() {
        return widthView;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (iListenerNoty != null && h != oldh) {
            heightView = h;
            widthView = w;
            iListenerNoty.onHeightChange(heightView);
        }
    }

    private void init(Context context) {

        binding = LayoutNotyVer2Binding.inflate(LayoutInflater.from(context), this, true);
    }

    public void setData(int pos, NotyModel notyModel, String appName, int widthMenuTypeCanDelete, int widthMenuTypeCanNotDelete, IListenerItemNoty iListenerNoty) {
        this.iListenerNoty = iListenerNoty;
        this.notyModel = notyModel;
        this.pos = pos;
        if (notyModel.isCanDelete()) {
            widthMenu = widthMenuTypeCanDelete;
            binding.tvMenuRight.setText(getContext().getString(R.string.delete_noty));
            binding.tvViewNoty.setVisibility(VISIBLE);
        } else {
            widthMenu = widthMenuTypeCanNotDelete;
            binding.tvMenuRight.setText(getContext().getString(R.string.view_noty));
            binding.tvViewNoty.setVisibility(INVISIBLE);
        }

        if (pos == 0) {
            binding.viewBgColor.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.transparent));
        } else if (pos == 1) {
            binding.viewBgColor.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_bg_card1));
        } else {
            binding.viewBgColor.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_bg_card2));
        }

        binding.background.setItemView(binding.swipeLayout, ImageBackgroundItemView.TypeView.NOTIFY, notyModel.getTitle(), binding.menuRight);
        binding.bgTvView.setItemView(binding.tvViewNoty, ImageBackgroundItemView.TypeView.RIGHT_MENU, notyModel.getContent());
        binding.bgTvRight.setItemView(binding.tvMenuRight, ImageBackgroundItemView.TypeView.RIGHT_MENU, notyModel.getContent());

        binding.swipeLayout.setMenuRight(binding.menuRight, "child");

        setSizeMenu();

        binding.swipeLayout.setOnSwipeListener(new OnSwipeListener() {

            @Override
            public void onSwipe(int x) {
                //Timber.e("x: " + x);
                binding.menuRight.updateShow(Math.abs(x));
                updateBackgroundHorizontal();
            }

            @Override
            public void onScrolling(boolean isScrolling) {
                ItemNoty.this.iListenerNoty.onScrolling(isScrolling);
            }

            @Override
            public void onStartSwipe() {
                //Timber.e("hoangLd: ");
                binding.bgTvView.updateBackgroundHorizontal();
                binding.bgTvRight.updateBackgroundHorizontal();
                ItemNoty.this.iListenerNoty.onStartSwipe();
            }

            @Override
            public void onEndSwipe() {
                //Timber.e("hoangLd: ");
                ItemNoty.this.iListenerNoty.onEndSwipe();
            }

            @Override
            public void onSwipeToDelete() {
                ItemNoty.this.iListenerNoty.onClickTvDeleteModel(notyModel);
            }
        });

//        binding.remoteViewContainer.removeAllViews();
//        if (notyModel.getRemoteView() != null) {
//            binding.remoteViewContainer.addView(notyModel.getRemoteView().apply(binding.remoteViewContainer.getContext(), binding.remoteViewContainer));
//        }


        binding.icon.setImageDrawable(notyModel.getIconApp());
        binding.tvAppName.setText(appName);
        if (notyModel.getTitle().isEmpty()) {
            if (notyModel.getTitle().isEmpty() && (!notyModel.getContent().isEmpty() || notyModel.getRemoteView() != null)) {
                binding.tvTitle.setText(Utils.INSTANCE.getAppNameFromPackage(App.mContext, notyModel.getPakage()));
            } else binding.tvTitle.setText(notyModel.getTitle());
        } else binding.tvTitle.setText(notyModel.getTitle());
        binding.tvContent.setText(notyModel.getContent());
        binding.tvPostTime.setText(getTimeAgo(getContext(),notyModel.getTime()));

        binding.tvNumNoty.setText("1 " + getContext().getString(R.string.notifications));

        if (notyModel.getImaBitmap() != null && NotyControlCenterServicev614.isShowPreviewNoty) {
            binding.imgImage.setVisibility(View.VISIBLE);
            binding.imgImage.setImageBitmap(notyModel.getImaBitmap());
        } else {
            binding.imgImage.setVisibility(View.GONE);
        }

        binding.groupNotyShowContent.setVisibility(NotyControlCenterServicev614.isShowPreviewNoty ? VISIBLE : GONE);
        binding.tvNumNoty.setVisibility(NotyControlCenterServicev614.isShowPreviewNoty ? GONE : VISIBLE);
        binding.tvAppName.setVisibility(NotyControlCenterServicev614.isShowPreviewNoty || notyModel.getTitle() == null || notyModel.getTitle().isEmpty() ? GONE : VISIBLE);

        binding.tvViewNoty.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTvViewNoty();
            }
        });

        binding.tvMenuRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notyModel.isCanDelete()) {
                    ItemNoty.this.iListenerNoty.onClickTvDeleteModel(notyModel);
                } else {
                    clickTvViewNoty();
                }
            }
        });

        binding.viewExpand.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final int[] positionCurrent = new int[2];
                getLocationOnScreen(positionCurrent);
                if ((Float.isNaN(binding.menuRight.getAlpha()) || binding.menuRight.getAlpha() == 0) && positionCurrent[0] >= 0 && !binding.swipeLayout.isTranslate()) {
                    if (NotyControlCenterServicev614.getInstance() != null && NotyControlCenterServicev614.getInstance().isDoubleClick()) {
                        return;
                    }
                    ItemNoty.this.iListenerNoty.onClickViewNoty(notyModel);
                }
            }
        });

    }

    private void showMoreItemNoty() {

    }

    public void clickTvViewNoty() {
        smoothClose();
        binding.tvContent.setMaxLines(Integer.MAX_VALUE);
        binding.tvTitle.setMaxLines(Integer.MAX_VALUE);
        binding.swipeLayout.update();
        setSizeMenu();
    }

    private void setSizeMenu() {
        binding.viewExpand.post(() -> {
            binding.menuRight.setSize(binding.tvViewNoty, binding.tvMenuRight, binding.bgTvRight, widthMenu, binding.viewExpand.getHeight(), notyModel.isCanDelete());
            //Timber.e("getHeight(): "+getHeight());
        });
    }

    public void smoothClose() {
        //Timber.e(".");
        binding.swipeLayout.smoothClose();
    }

    public void setDefaultClose() {
        //Timber.e(".");
        binding.swipeLayout.setDefaultClose();
    }

    public String getKeyNoty() {
        if (notyModel != null) {
            return notyModel.getKeyNoty();
        }
        return "-1";
    }

    public void setSwipeEnable(boolean isSwipeEnable) {
        binding.swipeLayout.setSwipeEnable(isSwipeEnable);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        //Timber.e("hoangld: " + visibility + " /swipeLayout: " + binding.swipeLayout.getVisibility() + " name: " + appName);

        if (notyModel != null) {
            binding.tvPostTime.setText(getTimeAgo(getContext(),notyModel.getTime()));
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setAlphaBgColorBack(float value) {
        binding.viewBgExpand.setAlpha(value);
        binding.viewBgColor.setAlpha(1 - value);
    }

    public void showFullContent() {
        clickTvViewNoty();
    }

    public void updateBackgroundHorizontal() {
        binding.background.updateBackgroundHorizontal();
    }
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Subscribe
//    public void eventOnShowHideRootView(EventShowHideRootView eventShowHideRootView) {
//        if (eventShowHideRootView.isShow()) {
//            if (isShowPreviewNoty != LockScreenService.isShowPreviewNoty) {
//                isShowPreviewNoty = LockScreenService.isShowPreviewNoty;
//                binding.groupNotyShowContent.setVisibility(isShowPreviewNoty ? VISIBLE : GONE);
//                binding.tvNumNotyNotShowContent.setVisibility(isShowPreviewNoty ? GONE : VISIBLE);
//            }
//        }
//    }

    public interface IListenerItemNoty {
        void onClickTvView();

        void onClickTvDeleteModel(NotyModel notyModel);

        void onClickViewNoty(NotyModel notyModel);

        void onScrolling(boolean isScrolling);

        void onStartSwipe();

        void onEndSwipe();

        void onHeightChange(int height);
    }

}