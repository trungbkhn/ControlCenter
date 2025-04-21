package com.tapbi.spark.controlcenter.feature.controlios14.view.noty;

import static com.tapbi.spark.controlcenter.feature.controlios14.view.noty.NotyView2.EMPTY_ITEM;
import static com.tapbi.spark.controlcenter.feature.controlios14.view.noty.NotyView2.NOT_EMPTY_ITEM;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.MessageEvent;
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper;
import com.tapbi.spark.controlcenter.eventbus.EventActionSearch;
import com.tapbi.spark.controlcenter.eventbus.EventAppRecent;
import com.tapbi.spark.controlcenter.feature.controlios14.adapter.ViewPagerNotyAdapter;
import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.SuggestAppManager;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.CustomViewpager;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.status.StatusNotyView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.widget.SearchView;
import com.tapbi.spark.controlcenter.interfaces.CallBackOpenNoty;
import com.tapbi.spark.controlcenter.interfaces.ListenerAnim;
import com.tapbi.spark.controlcenter.service.NotificationListener;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.SettingUtils;
import com.tapbi.spark.controlcenter.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import timber.log.Timber;
import com.tapbi.spark.controlcenter.utils.LocaleUtils;

public class NotyCenterViewOS extends RelativeLayout implements ActivityCompat.OnRequestPermissionsResultCallback {
    //    public static int DEFAULT_SCALE = 8;
    public static boolean scrollStarted;
    public ViewPagerNotyAdapter adapterVpgNoty;
    private OnNotyCenterCloseListener onNotyCenterCloseListener;
    private ImageView actionTouchCloseNoty, imageViewSwipe;
    private CustomViewpager viewPager;
    private ImageView background;
    private TextView tvTime, tvDate, tvNoOlderNotification;
    private SimpleDateFormat sdf;
    //    private IntentFilter filterTime;

    private StatusNotyView statusNoty;
    private Handler handlerUpdateWidget;
    private boolean update = true;
    private boolean isOpenSearch;
    private CallBackOpenNoty callBackOpenNoty;
    private boolean isShowNotNoti = true;

    //    public void res() {
//        adapterVpgNoty.notyView.noty();
//    }
    private float yDown, yMove;
    private float progress;
    private boolean close;
    private Handler handler;
    private final OnTouchListener touchCloseNoty = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (isOpenSearch) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    yDown = event.getRawY();
                    close = true;
                    progress = 0;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            close = false;
                        }
                    }, 500);
                    break;
                case MotionEvent.ACTION_MOVE:
                    yMove = event.getRawY();
                    if (yMove > yDown) {
                        return true;
                    }
                    progress = yMove - yDown;
                    setTranslationY((int) progress);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (close && progress < -50) {
                        close = true;
                    } else {
                        close = false;
                    }
                    if (progress < -getHeight() / 3f) {
                        close = true;
                    }
                    if (close) {
                        animationCloseTop();
                    } else {
                        animationShowTopBot(null);
                    }

                    break;
            }
            return true;
        }
    };
    private String packageNameIntent = "";
    private String idEvent = "";

    public NotyCenterViewOS(Context context) {
        super(context);
        init(context);
    }

    public NotyCenterViewOS(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NotyCenterViewOS(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnNotyCenterCloseListener(OnNotyCenterCloseListener onNotyCenterCloseListener) {
        this.onNotyCenterCloseListener = onNotyCenterCloseListener;
    }

    private void init(Context ctx) {
        findView();


//        int w = getResources().getDisplayMetrics().widthPixels;
//        for (int i = 8; i >= 4; i--) {
//            if (w % i == 0) {
//                DEFAULT_SCALE = i;
//                break;
//            }
//        }
    }

    private void findView() {
        int orientation = DensityUtils.getOrientationWindowManager(getContext());
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            LayoutInflater.from(getContext()).inflate(R.layout.noty_center, this, true);
        } else {
            LayoutInflater.from(getContext()).inflate(R.layout.noty_center_land, this, true);
        }
        handlerUpdateWidget = new Handler();
        handler = new Handler();

        statusNoty = findViewById(R.id.statuNoty);
        if (statusNoty != null) {
            if (statusNoty.getLayoutParams() != null) {
                ConstraintLayout.LayoutParams paramsStatusNoty = (ConstraintLayout.LayoutParams) statusNoty.getLayoutParams();
                paramsStatusNoty.height = App.statusBarHeight;
                statusNoty.requestLayout();

                actionTouchCloseNoty = findViewById(R.id.actionTouchCloseNoty);
                imageViewSwipe = findViewById(R.id.imgViewTouch);
                tvTime = findViewById(R.id.tvTime);
                tvDate = findViewById(R.id.tvDate);
                tvNoOlderNotification = findViewById(R.id.tvNoOlderNotification);
                background = findViewById(R.id.background);
                //background.setVisibility(INVISIBLE);
                //Timber.e(".");
                viewPager = findViewById(R.id.viewPager);
                setupViewpagerAdapter();
                setBgNew();
                sdf = new SimpleDateFormat("EEEE, d MMMM_HH:mm", LocaleUtils.getLocaleCompat(getResources()));
                setTime();
                actionTouchCloseNoty.setOnTouchListener(touchCloseNoty);
            }
        }
    }

    private void showTextEmpty() {
        try {
            Timber.e("NVQ showTextEmpty 1 // viewPager.getCurrentItem() "+viewPager.getCurrentItem() );
            if (NotificationListener.getInstance() != null && viewPager.getCurrentItem() == 1) {
                Timber.e("NVQ showTextEmpty 2");
                tvNoOlderNotification.setVisibility(VISIBLE);
            } else {
                Timber.e("NVQ showTextEmpty 3");
                tvNoOlderNotification.setVisibility(GONE);
            }
        } catch (Exception e) {
        }
    }

    private void hideTextEmpty() {
        try {
            Timber.e("NVQ showTextEmpty 4");
            tvNoOlderNotification.setVisibility(GONE);
        } catch (Exception e) {
        }
    }

    private void setupViewpagerAdapter() {
        adapterVpgNoty = new ViewPagerNotyAdapter(getContext(), this, new ViewPagerNotyAdapter.OnNotyCenterListener() {
            @Override
            public void onOpenSearch() {
                isOpenSearch = true;
                if (imageViewSwipe != null) imageViewSwipe.setVisibility(GONE);
            }

            @Override
            public void onCloseSearch() {
                isOpenSearch = false;
                if (imageViewSwipe != null) imageViewSwipe.setVisibility(VISIBLE);
            }

            @Override
            public void onItemNotyScrolling(boolean isScrolling) {

            }
        }, this::animationCloseTop);
        if (viewPager != null) {
            viewPager.setAdapter(adapterVpgNoty);
//            viewPager.setOffscreenPageLimit(3);
            viewPager.setCurrentItem(1);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    int wTime = tvTime.getWidth();
                    int wDay = tvDate.getWidth();
                    int widthGioiHan = getResources().getDisplayMetrics().widthPixels - 50;
                    if (position == 0) {
                        float x = ((((float) widthGioiHan - (float) wTime) / 2) * (1 - positionOffset));
                        float xx = ((((float) widthGioiHan - (float) wDay) / 2) * (1 - positionOffset));
                        tvTime.setTranslationX((int) x);
                        tvDate.setTranslationX((int) xx);
                        adapterVpgNoty.translation(positionOffsetPixels);
                    } else if (position == 1) {
                        tvTime.setTranslationX(-positionOffsetPixels);
                        tvDate.setTranslationX(-positionOffsetPixels);
                        actionTouchCloseNoty.setTranslationX(-positionOffsetPixels);
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 0) {
                        hideTextEmpty();
                        handlerUpdateWidget.postDelayed(() -> {
                            if (update) {
                                update = false;
                                adapterVpgNoty.updateEvent();
                                adapterVpgNoty.updateAppRecent();
                            }
                        }, 500);
                    } else if (position == 2) {
                        openCamera();
                    } else if (position == 1 && isShowNotNoti) {
                        showTextEmpty();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    //                adapterVpgNoty.setCloseItemNoty();
                    scrollStarted = !scrollStarted && state == ViewPager.SCROLL_STATE_DRAGGING;
                }
            });

            //hoangld OverScroll smooth like ios
            OverScrollDecoratorHelper.setUpOverScroll(viewPager);
        }
    }

    private void openCamera() {
        if (viewPager != null) {
            viewPager.postDelayed(() -> {
                Intent intent = new Intent(Constant.ACTION_OPEN_APP);
                intent.putExtra(Constant.PACKAGE_NAME_APP_OPEN, Constant.OPEN_CAMERA);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                animationCloseTop();
                viewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(1);
                        viewPager.setCanScroll(true);
                    }
                }, 1000);
            }, 500);
            viewPager.setCanScroll(false);
        }

    }

    private void openAppWhenUnlock() {
        animationCloseTop();
        try {
            switch (packageNameIntent) {
                case Constant.OPEN_EVENT_NEXT_UP: {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("content://com.android.calendar/events/" + idEvent));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    getContext().startActivity(intent);
                    break;
                }
                case Constant.REQUEST_PERMISSION_APP_RECENT: {
                    try {
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intent);
                    } catch (Exception e) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getContext().startActivity(intent);
                        } catch (Exception ignored) {
                        }
                    }
                    try {
                        final AppOpsManager appOps = (AppOpsManager) getContext().getSystemService(Context.APP_OPS_SERVICE);
                        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getContext().getPackageName());
                        if (mode == AppOpsManager.MODE_ALLOWED) {
                            return;
                        }
                        appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS, getContext().getApplicationContext().getPackageName(), new AppOpsManager.OnOpChangedListener() {
                            @Override
                            public void onOpChanged(String op, String packageName) {
                                try {
                                    int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getContext().getPackageName());
                                    if (mode != AppOpsManager.MODE_ALLOWED) {
                                        return;
                                    }
                                    appOps.stopWatchingMode(this);
                                    EventBus.getDefault().post(new EventAppRecent());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                }
                case Constant.REQUEST_PERMISSION_CALENDAR:
                    SettingUtils.intentActivityRequestPermission(getContext(), new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR});
                    break;
                case Constant.OPEN_CAMERA:
                    SettingUtils.openCamera(getContext());
                    break;
                case Constant.OPEN_APP_NOTY:
//                adapterVpgNoty.openAppNoty();
                    break;
                case Constant.REQUEST_PERMISSION_CAMERA:
                    SettingUtils.intentActivityRequestPermission(getContext(), new String[]{Manifest.permission.CAMERA});
                    break;
                default:
                    if (!packageNameIntent.isEmpty()) {
                        SettingUtils.intentOtherApp(getContext(), packageNameIntent);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setBgNew() {
        if (ThemeHelper.itemControl != null) {
            if (ThemeHelper.itemControl.getTypeBackground().equals(Constant.TRANSPARENT)) {
                background.setImageDrawable(null);
            }  else if (ThemeHelper.itemControl.getTypeBackground().equals(Constant.REAL_TIME)) {
                background.setColorFilter(ContextCompat.getColor(getContext(), R.color.color_background_real_time));
                background.setImageBitmap(BlurBackground.getInstance().getBitmapBgBlur());
            } else {
                background.setImageBitmap(BlurBackground.getInstance().getBitmapBgNotBlur());
            }
        }

    }




    public void animationCloseTop() {
        animate().translationY(-getHeight()).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                Timber.e("hachung :" + "animationCloseTop");
                updateUI();
            }
        });
    }

    private void updateUI() {
        update = true;
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (onNotyCenterCloseListener != null) {
                    onNotyCenterCloseListener.closeEnd();
                    //1 is tab noti
//                    viewPager.setCurrentItem(1);
                }
            }
        }, 300);
    }

    public void animationShowTopBot(ListenerAnim listenerAnim) {
        animate().setDuration(500).translationY(0)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (listenerAnim != null) {
                            listenerAnim.onAnimEnd();
                        }
                    }
                }).start();
    }

    public void updateBitmapBlur() {
        if (adapterVpgNoty != null) {
            if (adapterVpgNoty.widgetView != null) {
                adapterVpgNoty.widgetView.updateBitmapBlur();
            }
            if (adapterVpgNoty.notyView2 != null) {
//                Timber.e("hachung :" + "noty");
                adapterVpgNoty.notyView2.noty();
            }
        }

    }

    public void updateNoty() {
        if (adapterVpgNoty != null) {
            if (adapterVpgNoty.notyView2 != null) {
                adapterVpgNoty.notyView2.noty();
            }
        }
    }

    public void setTime() {
        String date = sdf.format(System.currentTimeMillis());
        String[] dates = date.split("_");
        statusNoty.setTime(dates[1]);
        tvTime.setText(dates[1]);
        tvDate.setText(StringUtils.INSTANCE.uppercaseFirstCharacters(DateUtils.formatDateTime(getContext(), System.currentTimeMillis(), 18)));

    }

    public void setonSignalsChange(int lever) {
        if (statusNoty != null) {
            statusNoty.setonSignalsChange(lever);
        }
    }

    public void setChangeBattery(boolean isChange, int lever) {
        if (statusNoty != null) {
            statusNoty.changeBattery(isChange, lever);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);


    }

    @Subscribe
    public void onEvent(EventActionSearch event) {
        if (event.getAction() != null && !event.getAction().isEmpty()) {
            if (event.getAction().equals(SearchView.ACTION_SHOW_SEARCH)) {
                tvDate.setVisibility(View.GONE);
                tvTime.setVisibility(View.GONE);
                viewPager.setCanScroll(false);
            } else if (event.getAction().equals(EMPTY_ITEM)) {
                Timber.e("NVQ empty++++++++++++++++");
                isShowNotNoti = true;
                showTextEmpty();
            } else if (event.getAction().equals(NOT_EMPTY_ITEM)) {
                Timber.e("NVQ empty----------------");
                isShowNotNoti = false;
                hideTextEmpty();
            } else {
                tvDate.setVisibility(View.VISIBLE);
                tvTime.setVisibility(View.VISIBLE);
                viewPager.setCanScroll(true);
            }


        }

    }

    @Subscribe
    public void onEvent(MessageEvent messageEvent) {
        switch (messageEvent.getTypeEvent()) {
            case Constant.PACKAGE_APP_REMOVE:
                SuggestAppManager.getInstance().setRefresh(false);
                if (adapterVpgNoty != null) {
                    adapterVpgNoty.updateAppRecent();
                }
                break;

        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
        removeAllViews();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    public void showFromEdgeLeftRight() {
        setX(0);
        setY(0);
        clearAnimation();
        setAlpha(0);
        setVisibility(View.VISIBLE);
        animate().alpha(1).setDuration(300).setListener(null).start();
    }

    public void updatePlaneMode(boolean planeEnabled) {
        statusNoty.updatePlaneMode(planeEnabled);
    }

    public void updateWifiMode(boolean wifiEnabled) {
        statusNoty.updateWifi(wifiEnabled);
    }

    public void updateDataMobileMode(boolean enabled) {
        statusNoty.updateDataMobile(enabled);
    }

    public void updateStateSim() {
        statusNoty.updateStateSim();
    }

    public void updateRemoveItem(int positionGroup, String packageName, int positionModel, String idNoty, boolean isRemovedGroup, boolean isNotyNow) {
        if (adapterVpgNoty != null && adapterVpgNoty.notyView2 != null) {
            adapterVpgNoty.notyView2.notiRemoved(positionGroup, packageName, positionModel, idNoty, isRemovedGroup, isNotyNow);
        }
    }

    public void clickOpenNoty(NotyModel notyModel, CallBackOpenNoty callBackOpenNoty) {
        this.callBackOpenNoty = callBackOpenNoty;
        animationCloseTop();
        openAppNoty(notyModel);
    }

    public void openAppNoty(NotyModel notyModel) {
        if (notyModel != null) {
            callBackOpenNoty.onResultNotyOpen(true);
            if (notyModel.getPendingIntent() != null) {
                DensityUtils.sendPendingIntent(App.mContext, notyModel.getPendingIntent(), notyModel.getPakage());
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            animationCloseTop();
        }
        return super.dispatchKeyEvent(event);
    }

    public void updateStatusNotificationAccess() {
        if (adapterVpgNoty != null && adapterVpgNoty.notyView2 != null) {
            adapterVpgNoty.notyView2.checkShowViewPermissionNotification();
        }
    }

    public void actionOpenApp(String pka, String idEvent) {
        if (pka != null) {
            packageNameIntent = pka;
            if (pka.equals(Constant.OPEN_EVENT_NEXT_UP)) {
                this.idEvent = idEvent;
            }
            openAppWhenUnlock();
        }
    }

    public float getTranY() {
        return getTranslationY();
    }

    public void setTranY(float tranY) {
        setTranslationY(tranY);
    }

    public interface OnNotyCenterCloseListener {
        void closeEnd();
    }


}

