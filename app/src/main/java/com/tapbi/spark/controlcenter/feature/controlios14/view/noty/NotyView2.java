package com.tapbi.spark.controlcenter.feature.controlios14.view.noty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.ItemAddedNoty;
import com.tapbi.spark.controlcenter.eventbus.EventActionSearch;
import com.tapbi.spark.controlcenter.eventbus.EventShowHideRootView;
import com.tapbi.spark.controlcenter.feature.NotyManager;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.FlashUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.adapter.ViewPagerNotyAdapter;
import com.tapbi.spark.controlcenter.feature.controlios14.adapter.notygroup.NotyGroupAdapter;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.PermissionNotificationView;
import com.tapbi.spark.controlcenter.service.NotificationListener;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.helper.rcvhepler.RecyclerViewNoty;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import timber.log.Timber;

public class NotyView2 extends ConstraintLayout {
    public static final String EMPTY_ITEM = "empty_item";
    public static final String NOT_EMPTY_ITEM = "not_empty_item";
    private final ViewPagerNotyAdapter.OnNotyCenterListener onNotyCenterListener;
    private final PermissionNotificationView.ClickListener clickListener;
    public RecyclerViewNoty rcvNoty;
    private PermissionNotificationView permissionNotificationView;
    //    public boolean clickFlash = false;
    private Context context;
    private NotyGroupAdapter notyGroupAdapter;
    //    private FlashUtils flashUtils;
    private ImageView actionFlash;
    private final CallBackUpdateUi callBackUpdateUi = new CallBackUpdateUi() {
        @Override
        public void stage(String valueRegister, boolean b, int pos) {
            updateFlash(b);
        }
    };
    private ImageView actionCamera;
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == actionFlash) {
//                if (NotyControlCenterServicev614.getInstance().flashUtils == null) {
//                    return;
//                }
//                if (clickFlash) {
//                    NotyControlCenterServicev614.getInstance().flashUtils.flashOff();
//                } else {
//                    NotyControlCenterServicev614.getInstance().flashUtils.flashOn();
//                }
                if (NotyControlCenterServicev614.getInstance() != null) {
                    NotyControlCenterServicev614.getInstance().setFlashOnOff();
                }
            } else if (v == actionCamera) {
                openCamera();
            }
        }
    };


    public NotyView2(Context context, ViewPagerNotyAdapter.OnNotyCenterListener onNotyCenterListener, PermissionNotificationView.ClickListener clickListener) {
        super(context);
        this.onNotyCenterListener = onNotyCenterListener;
        this.clickListener = clickListener;
        init(context);

    }

    public void noty() {
        if (notyGroupAdapter != null) {
            resetShowRcvNoty();
//            Timber.e("hachung :" + "noty");
            notyGroupAdapter.reloadDataAdapter();
        }
    }

    private void setUpAdapter() {
        notyGroupAdapter = new NotyGroupAdapter(context, rcvNoty, new NotyGroupAdapter.OnGroupNotyClickListener() {
            public void onDeleteAll(ArrayList<NotyModel> notiModels) {
                String[] keys = new String[notiModels.size()];
                for (int k = 0; k < notiModels.size(); k++) {
                    keys[k] = notiModels.get(k).getKeyNoty();
                }
                if (NotificationListener.getInstance() != null) {
                    NotificationListener.getInstance().deleteGroup(keys);
                }

            }

            @Override
            public void onDeleteNoty(NotyModel notiModel) {
                if (NotificationListener.getInstance() != null) {
                    NotificationListener.getInstance().deleteNoty(notiModel.getPakage(), 0, notiModel.getKeyNoty());
                }
            }

            @Override
            public void onClearAllNoty() {
                if (NotificationListener.getInstance() != null) {
                    NotificationListener.getInstance().deleteAllNoty();
                }

            }

            @Override
            public void onItemNotyScrolling(boolean isScrolling) {
                onNotyCenterListener.onItemNotyScrolling(isScrolling);
            }

            @Override
            public void openNoty(NotyModel notyModel) {
                NotyControlCenterServicev614.getInstance().openNoty(notyModel, allowOpen -> {
                    if (allowOpen) {
                        onDeleteNoty(notyModel);
                    }
                });
            }

            @Override
            public void onEmptyNoty(boolean isEmpty) {
                Timber.e("NVQ onEmptyNoty" + isEmpty);
                if (isEmpty) {
                    EventBus.getDefault().post(new EventActionSearch(EMPTY_ITEM));
                } else {
                    EventBus.getDefault().post(new EventActionSearch(NOT_EMPTY_ITEM));
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_noty_viewpager_2, this, true);
        actionFlash = findViewById(R.id.actionFlash);
        actionCamera = findViewById(R.id.actionCamera);
        rcvNoty = findViewById(R.id.rcvNoty);
        permissionNotificationView = findViewById(R.id.permissionNotiView);
        setUpAdapter();
//        setupFirstUse();
        actionFlash.setOnClickListener(onClickListener);
        actionCamera.setOnClickListener(onClickListener);
        permissionNotificationView.setClickListener(clickListener);
    }


    private void openCamera() {
        Intent intent = new Intent(Constant.ACTION_OPEN_APP);
        intent.putExtra(Constant.PACKAGE_NAME_APP_OPEN, Constant.OPEN_CAMERA);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void setupFirstUse() {

        if (!MethodUtils.checkServiceNoty(context)) {
            NotyManager.INSTANCE.clearAll();
        } else {
            update();
        }

        updateFlash(FlashUtils.enabled);

    }

    public void translation(int positionOffsetPixels) {
        rcvNoty.setTranslationX(-positionOffsetPixels);
    }

    public void update() {
        //Timber.e("hoangld: ");
//        ArrayList<NotyGroup> newNotyGroups = NotyManager.getInstance().getNotyGroup();
        post(() -> {
//            notyGroups = newNotyGroups;
            //Timber.e("hoangld update");
//            Timber.e("hachung :" + "update");
            Timber.e("hachung reloadDataAdapter:");
            notyGroupAdapter.reloadDataAdapter();
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
        loadNoti();
//        flashUtils = new FlashUtils(getContext(), callBackUpdateUi, Constant.STRING_ACTION_FLASH_LIGHT,0);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            updateFlash(NotyControlCenterServicev614.getInstance().flashUtils.isEnabledApi22());
//        } else {
//            updateFlash(FlashUtils.enabled);
//        }
        if (NotyControlCenterServicev614.getInstance() != null) {
            updateFlash(NotyControlCenterServicev614.getInstance().isFlashOn);
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
//        try {
//            if (flashUtils != null) {
//                flashUtils.unRegisterListener();
//                flashUtils = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Subscribe
    public void eventOnShowHideRootView(EventShowHideRootView eventShowHideRootView) {
    }

    private void loadNoti() {
        if (NotificationListener.getInstance() != null) {
            if (NotificationListener.getInstance().isFirstLoad) {
                NotificationListener.getInstance().loadInFirstUse();
            }
        }
        checkShowViewPermissionNotification();
    }

    public void checkShowViewPermissionNotification() {
        permissionNotificationView.setVisibility(NotificationListener.getInstance() != null ? View.GONE : View.VISIBLE);
        rcvNoty.setVisibility(NotificationListener.getInstance() != null ? View.VISIBLE : View.GONE);
        if (permissionNotificationView.getVisibility() == View.VISIBLE) {
            EventBus.getDefault().post(new EventActionSearch(NOT_EMPTY_ITEM));
        }
    }


    public void destroy() {

    }

    public void notiRemoved(int positionGroup, String packageName, int positionModel, String idNoty, boolean isRemovedGroup, boolean isNotyNow) {
//        post(() -> {
//
//        });
        Timber.e("hachung notiRemoved:"+positionGroup+"/"+packageName+"/"+positionModel+"/"+idNoty+"/"+isRemovedGroup);
        if (notyGroupAdapter != null) {
            if (isRemovedGroup) {
                notyGroupAdapter.removeGroupFromNoti(positionGroup, isNotyNow, packageName);
            } else {
                notyGroupAdapter.removedItemInGroupFromNoti(positionGroup, packageName, idNoty, isNotyNow);
            }
        }
    }

    public void notyAdded(ItemAddedNoty itemAddedNoty) {
//        post(() -> {
//
//        });

        if (notyGroupAdapter != null) {
            notyGroupAdapter.setAddedItem(itemAddedNoty);
        }
    }

    public void reloadNoty() {
        if (notyGroupAdapter != null) {
            notyGroupAdapter.reloadDataAdapter();
        }
    }

    public void resetShowRcvNoty() {
//        NotyManager.getInstance().setStateExpandNoty();
        rcvNoty.scrollToTop();
    }

    public void updateFlash(boolean enable) {
//        clickFlash = enable;
        if (!enable) {
            actionFlash.setImageResource(R.drawable.flashlight_off);
            actionFlash.setBackgroundResource(R.drawable.background_gray_circle_noty);
        } else {
            actionFlash.setImageResource(R.drawable.flashlight_on);
            actionFlash.setBackgroundResource(R.drawable.background_flash_on);
        }
    }

}
