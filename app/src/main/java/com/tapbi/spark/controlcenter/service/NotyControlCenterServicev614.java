package com.tapbi.spark.controlcenter.service;

import static com.tapbi.spark.controlcenter.App.tinyDB;
import static com.tapbi.spark.controlcenter.common.Constant.DARK;
import static com.tapbi.spark.controlcenter.common.Constant.DEFAULT_IS_ENABLE;
import static com.tapbi.spark.controlcenter.common.Constant.IS_DISABLE;
import static com.tapbi.spark.controlcenter.common.Constant.IS_ENABLE;
import static com.tapbi.spark.controlcenter.common.Constant.LIGHT;
import static com.tapbi.spark.controlcenter.common.Constant.VALUE_CONTROL_CENTER;
import static com.tapbi.spark.controlcenter.common.Constant.VALUE_CONTROL_CENTER_OS;
import static com.tapbi.spark.controlcenter.common.Constant.VALUE_PIXEL;
import static com.tapbi.spark.controlcenter.common.Constant.VALUE_SAMSUNG;
import static com.tapbi.spark.controlcenter.common.Constant.VALUE_SHADE;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.UiModeManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.location.LocationManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Process;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.Battery;
import com.tapbi.spark.controlcenter.common.models.EventCustomEdge;
import com.tapbi.spark.controlcenter.common.models.ItemAddedNoty;
import com.tapbi.spark.controlcenter.data.model.ActionClick;
import com.tapbi.spark.controlcenter.data.model.ItemControl;
import com.tapbi.spark.controlcenter.data.model.TextActionClick;
import com.tapbi.spark.controlcenter.data.model.ThemeControl;
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper;
import com.tapbi.spark.controlcenter.eventbus.EventSaveControl;
import com.tapbi.spark.controlcenter.feature.NotyManager;
import com.tapbi.spark.controlcenter.feature.controlcenter.ControlCenterView;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;
import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CloseNotyControl;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.DataMobileUtils;
import com.tapbi.spark.controlcenter.feature.controlcenter.utils.FlashUtils;
import com.tapbi.spark.controlcenter.feature.controlcenter.view.noty.NotyCenterView;
import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.AudioManagerUtils;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterIOSView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.ControlCenterViewIOS18;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.base.BaseControlCenterIos;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.ScreenRecordActionView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.control.group5.ScreenRecordTextView;
import com.tapbi.spark.controlcenter.feature.controlios14.view.noty.NotyCenterViewOS;
import com.tapbi.spark.controlcenter.feature.edge.GroupTouchBottom;
import com.tapbi.spark.controlcenter.feature.edge.GroupTouchLeft;
import com.tapbi.spark.controlcenter.feature.edge.GroupTouchRight;
import com.tapbi.spark.controlcenter.feature.edge.GroupTouchTop;
import com.tapbi.spark.controlcenter.feature.mishade.ControlNotyMiShade;
import com.tapbi.spark.controlcenter.interfaces.CallBackOpenNoty;
import com.tapbi.spark.controlcenter.interfaces.IListenActionClick;
import com.tapbi.spark.controlcenter.interfaces.ListenerAnim;
import com.tapbi.spark.controlcenter.interfaces.OnTouchViewListener;
import com.tapbi.spark.controlcenter.receiver.ActionAirplaneModeChange;
import com.tapbi.spark.controlcenter.receiver.ActionDoNotDisturb;
import com.tapbi.spark.controlcenter.receiver.ActionLocationBroadcastReceiver;
import com.tapbi.spark.controlcenter.receiver.ActionWifiHostPostReceiver;
import com.tapbi.spark.controlcenter.receiver.BatteryBroadCast;
import com.tapbi.spark.controlcenter.receiver.BluetoothReceiver;
import com.tapbi.spark.controlcenter.receiver.ContentObBrightness;
import com.tapbi.spark.controlcenter.receiver.ContentObDataMobile;
import com.tapbi.spark.controlcenter.receiver.LanguageChangeReceiver;
import com.tapbi.spark.controlcenter.receiver.LowPowerModeChangeBroadcast;
import com.tapbi.spark.controlcenter.receiver.MyPhoneStateListener;
import com.tapbi.spark.controlcenter.receiver.TimeReceiver;
import com.tapbi.spark.controlcenter.receiver.UnLockOpenAppReceiver;
import com.tapbi.spark.controlcenter.receiver.VolumeReceiver;
import com.tapbi.spark.controlcenter.receiver.WifiBroadcastReceiver;
import com.tapbi.spark.controlcenter.ui.RequestPermissionActivity;
import com.tapbi.spark.controlcenter.ui.main.edgetriggers.SettingTouchFragment;
import com.tapbi.spark.controlcenter.utils.DensityUtils;
import com.tapbi.spark.controlcenter.utils.ExtensionsKt;
import com.tapbi.spark.controlcenter.utils.ImageTransmogrifier;
import com.tapbi.spark.controlcenter.utils.LocaleUtils;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.SettingUtils;
import com.tapbi.spark.controlcenter.utils.VibratorUtils;
import com.tapbi.spark.controlcenter.utils.WidthHeightScreen;
import com.tapbi.spark.controlcenter.views.ViewDialogContent;
import com.tapbi.spark.controlcenter.views.ViewDialogOpenSystem;
import com.tapbi.spark.controlcenter.views.ViewToastTextNoty;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class NotyControlCenterServicev614 extends AccessibilityService {
    public static final boolean isShowPreviewNoty = true;
    //capture screenshot
    public static final String EXTRA_ACTION = "extra_action";
    public static final int ACTION_CAPTURE = 111;
    public static final int ACTION_CANCEL_CAPTURE = 112;
    public static final int ACTION_RECORD = 113;
    public static final int ACTION_STOP_RECORD = 114;
    public static final String EXTRA_RESULT_CODE = "resultCode";
    public static final String EXTRA_RESULT_INTENT = "resultIntent";
    private static final String CHANNEL_ID = "chanel_id";
    private static final String CHANNEL_ID_BG = "chanel_id";
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    public static int ID_NOTI_RECORD = 4;
    public static boolean isErrorService = false;
    public static String evenTypeClickNoty = "";
    public static boolean isShowNoty;
    @SuppressLint("StaticFieldLeak")
    private static NotyControlCenterServicev614 instance;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public final String packageSetting = "com.android.systemui";
    private final CompositeDisposable disposableProcessEvent = new CompositeDisposable();
    private final List<AccessibilityNodeInfo> listNodeInfo = new ArrayList<>();
    private final Handler handlerActionBattery = new Handler();
    private final Handler handlerDataMobile = new Handler();
    private final Handler handlerClickNoti = new Handler(Looper.getMainLooper());
    private final Handler handlerOpenNoty = new Handler(Looper.getMainLooper());
    private final Handler handlerHideNoty = new Handler(Looper.getMainLooper());
    /**
     * Set max time process when click action, ex: Dark mode, Battery saver,... If max time, finish process and show viewDialogOpenSystem
     */
    private final Handler handlerTimeProcessAction = new Handler(Looper.getMainLooper());
    //api 31
    //using when open noty of app, then Notifications system also open, we need close NotificationsPanel system
    private final Handler handlerCloseNotiSystemApi31 = new Handler();
    private final Handler handlerCloseNotiSystemApi31Done = new Handler();
    private final IntentFilter filterLanguageChange = new IntentFilter("android.intent.action.LOCALE_CHANGED");
    private final OrientationBroadcastReceiver orientationBR = new OrientationBroadcastReceiver();
    //Create and initialize a new IntentFilter
    private final IntentFilter orientationIF = new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED);
    private final int flagManager = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public int leverSim = 0;
    public BaseControlCenterIos controlCenterIOSView;
    public ControlCenterView controlCenterView;
    //noty
    public NotyCenterViewOS notyCenterViewOS;
    public NotyCenterView notyCenterView;
    public int typeChoose = VALUE_CONTROL_CENTER;
    public ScreenRecordActionView.STATE stateRecordScreen = ScreenRecordActionView.STATE.NONE;
    public boolean isChangeLocale = false;
    public boolean isSwiping = false;
    public BluetoothReceiver bluetoothReceiver;
    public ActionLocationBroadcastReceiver actionLocationBroadcastReceiver;
    public ActionDoNotDisturb actionDoNotDisturb = null;
    public WindowManager windowManager;
    public Battery battery;
    public ActionWifiHostPostReceiver actionWifiHostPostReceiver = null;
    public FlashUtils flashUtils;

    public boolean isFlashOn = false;
    public boolean isDarkModeOn = false;
    boolean resultClick = false;
    boolean resultClickDone = false;
    boolean resultClickAirPlane = false;
    boolean resultClickDataMobi = false;
    boolean api31 = android.os.Build.VERSION.SDK_INT >= 31;
    int count = 0;
    private MyPhoneStateListener waveListener;
    private TelephonyManager telephonyManager;
    private boolean isDispatchTouchEvent = false;
    private Timer timer;
    private ActionClick actionClick;
    private LayoutInflater inflater;
    private WindowManager.LayoutParams paramsManager;
    private View rootView;
    private GroupTouchTop groupTouchTop;
    private GroupTouchLeft groupTouchLeft;
    private GroupTouchRight groupTouchRight;
    private GroupTouchBottom groupTouchBottom;
    private View viewBgBlackOpenNotySystem;
    private int orientation = Configuration.ORIENTATION_PORTRAIT;
    private int heightSupportRecordScreen = App.widthHeightScreenCurrent.h;
    private int widthSupportRecordScreen = App.widthHeightScreenCurrent.w;
    //control center
    private boolean checkAllowTouch = true;
    private ViewDialogOpenSystem viewDialogOpenSystem;
    private ViewDialogContent viewDialogContent;
    private ViewToastTextNoty viewToast;
    private int height;
    private int width;
    private float yDown;
    private float xDown;
    //  mi shade
    private ControlNotyMiShade controlNotyMiShade;
    private RelativeLayout.LayoutParams paramsNoty;
    private int progressExpandNoty;
    private int maxAnimationNoty;
    private VirtualDisplay virtualDisplayBlur;
    private HandlerThread handlerThread = null;
    private Handler handler;
    private MediaProjectionManager mgr;
    private ImageTransmogrifier it;
    private int resultCode;
    private Intent resultDataMediaProjection;
    private Disposable currentTask;
    /**
     * From android 14, Reusing the same Intent to retrieve multiple MediaProjections is not possible
     */
    private boolean isResultDataMPUsed = false;
    private NotificationManager mNotifyMgr;
    private String pathFolderExectCurrent;
    private MediaRecorder mMediaRecorder;
    private MediaProjection mMediaProjection;
    private int mScreenDensity;
    private VirtualDisplay virtualDisplayRecordVideo;
    private MediaProjection.Callback mMediaProjectionCallback;
    private String currentFileRecorder = "";
    private boolean isTypeRealtimeBackground = false;
    private boolean airPlaneModeEnabled = false;
    private boolean hasSimCard = true;
    private String stringLanguageOld = "";
    private IListenActionClick iListenActionClick;
    private IListenActionClick iListenActionClickWaiting;
    private ActionAirplaneModeChange actionAirplaneModeChange;
    private WifiBroadcastReceiver wifiBroadcastReceiver;
    private ContentObDataMobile contentDataMobile = null;
    private SimChangeBroadcastReceiver simChangeBroadcastReceiver;
    private int countRootNodeLevel = 0;
    private int countChildNodeLevel = 0;
    private boolean allowHandingAction = true;
    private boolean isFirstClickAction = true;
    private boolean isAllowClick2nd = true;
    private boolean cancelAction = false;
    private int countCheckBatteryMode = 0;
    private AccessibilityNodeInfo nodeCloseWifiApi31 = null;
    private AccessibilityNodeInfo nodeCloseDataApi31 = null;
    private AccessibilityNodeInfo nodeCloseBattery = null;
    private DataMobileUtils dataMobileUtils;
    private int countRunnableData = 0;
    private boolean wifiChange;
    private boolean dataChange;
    private boolean batteryChange;
    private boolean batteryOld;
    private final Runnable runnableActionBattery = new Runnable() {
        @Override
        public void run() {
            countCheckBatteryMode++;
            if (batteryOld != SettingUtils.isPowerSaveMode(NotyControlCenterServicev614.this)) {
                batteryChange = true;
                handlerActionBattery.removeCallbacks(this);
            } else {
                handlerActionBattery.postDelayed(this, 500);
            }

            if (countCheckBatteryMode > 10) {
                batteryChange = true;
                handlerActionBattery.removeCallbacks(this);
            }
        }
    };
    private String typeClickWaiting = "";
    private boolean allowOpenNoty = true;
    private final Runnable runnableHideNoty = new Runnable() {
        @Override
        public void run() {
            if (viewBgBlackOpenNotySystem != null) {
                setAlphaBgViewNoty(false, viewBgBlackOpenNotySystem);
                allowOpenNoty = true;
            }
        }
    };
    private boolean runningOpenNotyToClickAction = false;
    private final Runnable runnableTimeProcessAction = new Runnable() {
        @Override
        public void run() {
            cancelAction = true;
            closeAction(false);
        }
    };
    private boolean notyOpened = false;
    private final Runnable runnableOpenNoty = () -> {
        isFirstClickAction = true;
        notyOpened = false;
        SettingUtils.expandNotificationsPanel(NotyControlCenterServicev614.this);
        new Handler(Looper.getMainLooper()).postDelayed(() -> notyOpened = true, 300);
    };
    private boolean isNotiSystemApi31Closed = true;
    private final Runnable runnableCloseNotiSystemApi31Done = () -> isNotiSystemApi31Closed = true;
    private final Runnable runnableCloseNotiSystemApi31 = () -> {
        isNotiSystemApi31Closed = false;
        handlerCloseNotiSystemApi31Done.removeCallbacks(runnableCloseNotiSystemApi31Done);
        dismissNotyShade();
        handlerCloseNotiSystemApi31Done.postDelayed(runnableCloseNotiSystemApi31Done, 2000);
    };
    //--------------------screen capture-----------------------------------
    private boolean capture;
    private boolean isOnStop;
    private LanguageChangeReceiver languageChangeReceiver;
    private PressedHomeReceiver pressedHomeReceiver;
    private TimeReceiver timeReceiver;
    private ContentObBrightness contentObBrightness;
    private long lastClick = 0;
    private DeviceUnlockedReceiver unlockedReceiver;
    private boolean isInEditSettingTouch;
    private String titleNotySystemUi = null;
    private boolean autoHideNotyPanelSystemStyleShade = Constant.VALUE_DEFAULT_AUTO_HIDE_NOTI_PANEL_SYSTEM_SHADE;
    private String titleOld = "";
    private boolean isPreventShowRootView = false;
    private boolean isChangeAppShow = false;
    private LowPowerModeChangeBroadcast lowPowerModeChange;
    private VolumeReceiver volumeReceiver;
    private BatteryBroadCast batteryBroadCast;
    private UnLockOpenAppReceiver unLockOpenAppReceiver;
    private SnoozedReceiver snoozedReceiver = null;
    private boolean isFirstSetView = false;


    public static synchronized NotyControlCenterServicev614 getInstance() {
        return instance;
    }

    public static String getLocaleStringResource(int resourceId, Context context) {
        String result;
        // use latest api
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            locale = Resources.getSystem().getConfiguration().locale;
        }
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        result = context.createConfigurationContext(config).getText(resourceId).toString();
        return result;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(adjustFontScale(newBase, true));

    }

    public boolean isTypeRealtimeBackground() {
        return isTypeRealtimeBackground;
    }

    public void setTypeRealtimeBackground(boolean isRealtime) {
        this.isTypeRealtimeBackground = isRealtime;
    }

    public void updateTextTitle() {
        if (controlCenterView != null) {
            controlCenterView.updateTextTitle();
        }
    }

    public void setValueChange(int i) {
        typeChoose = i;
    }

    public void loadThemeEdit(ItemControl theme){
        if (controlCenterIOSView != null){
            controlCenterIOSView.reloadTheme(theme);
        }
    }

    public Intent getResultDataMediaProjection() {
        return resultDataMediaProjection;
    }

    public boolean isResultDataMPUsed() {
        return isResultDataMPUsed;
    }

    public void updateGroupNotyRemoved(int positionGroup, String packageName, int positionModel, String idNoty, boolean isRemovedGroup, boolean isNotyNow) {
        if (typeChoose == VALUE_CONTROL_CENTER || typeChoose == VALUE_PIXEL || typeChoose == VALUE_SAMSUNG) {
            if (notyCenterView != null) {
                notyCenterView.notiRemoved(positionGroup, positionModel, isRemovedGroup, idNoty);
            }
        } else if (typeChoose == VALUE_SHADE) {
            if (controlNotyMiShade != null) {
                controlNotyMiShade.notyRemoved(positionGroup, positionModel, isRemovedGroup, idNoty);
            }
        } else {
            if (notyCenterViewOS != null) {
                notyCenterViewOS.updateRemoveItem(positionGroup, packageName, positionModel, idNoty, isRemovedGroup, isNotyNow);
            }
        }

    }

    public void addNoty(ItemAddedNoty itemAddedNoty) {

        if (typeChoose == VALUE_CONTROL_CENTER || typeChoose == VALUE_PIXEL || typeChoose == VALUE_SAMSUNG) {
            if (notyCenterView != null && itemAddedNoty != null) {
                notyCenterView.notyAdded(itemAddedNoty);
            }
        } else if (typeChoose == VALUE_SHADE) {
            if (controlNotyMiShade != null && itemAddedNoty != null) {
                controlNotyMiShade.notyAdded(itemAddedNoty);
            }
        } else {
            if (notyCenterViewOS != null && notyCenterViewOS.adapterVpgNoty != null && notyCenterViewOS.adapterVpgNoty.notyView2 != null && itemAddedNoty != null) {
                notyCenterViewOS.adapterVpgNoty.notyView2.notyAdded(itemAddedNoty);
            }
        }
    }

    public void reloadAllNoty() {
        if (typeChoose == VALUE_CONTROL_CENTER || typeChoose == VALUE_PIXEL || typeChoose == VALUE_SAMSUNG) {
            if (notyCenterView != null) {
                notyCenterView.reloadNoty();
            }
        } else if (typeChoose == VALUE_SHADE) {
            if (controlNotyMiShade != null) {
                controlNotyMiShade.reloadNoty();
            }
        } else {
            if (notyCenterViewOS != null && notyCenterViewOS.adapterVpgNoty != null && notyCenterViewOS.adapterVpgNoty.notyView2 != null) {
                notyCenterViewOS.adapterVpgNoty.notyView2.reloadNoty();
            }
        }
    }

    @Override
    public void onCreate() {
        boolean isResourceNull = false;
        if (getResources() != null) {
            isResourceNull = true;
            setTheme(R.style.service);
        }
        super.onCreate();
        if (!isResourceNull) {
            try {
                setTheme(R.style.service);
            } catch (Exception ignored) {
            }
        }
        instance = this;
        if (App.ins.focusUtils != null) {
            App.ins.focusUtils.onCreate();
        }
        EventBus.getDefault().register(this);
    }

    public void enableWindow() {
        if (inflater == null) {
            return;
        }
        cancelJob();
        isResultDataMPUsed = false;
        currentTask = Completable.fromRunnable(() -> {
            findRootLayout();
            paramsManager.flags = flagManager;
            findViews();
            addTouchAndRootView();
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                showStatusBarNavigation();
            }


        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();

    }

    public void cancelJob() {
        try {
            if (currentTask != null && !currentTask.isDisposed()) {
                currentTask.dispose();
            }
        } catch (Exception e) {
        }
    }

    private void loadActionClickControl() {
        Completable.fromRunnable(() -> actionClick = App.ins.languageRepository.getListAction(this)).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NonNull Throwable e) {
                Timber.e(e);
            }
        });
    }

    private void findRootLayout() {
//        try {
        rootView = null;
        viewBgBlackOpenNotySystem = null;
        viewDialogOpenSystem = null;
        viewToast = null;

        controlCenterIOSView = null;
        notyCenterView = null;
        controlCenterView = null;
        notyCenterViewOS = null;
        controlNotyMiShade = null;
        if (typeChoose == VALUE_CONTROL_CENTER || typeChoose == VALUE_PIXEL || typeChoose == VALUE_SAMSUNG) {
            rootView = inflater.inflate(R.layout.mi_control_center, null);
        } else if (typeChoose == VALUE_SHADE) {
            rootView = inflater.inflate(R.layout.mi_shade_control_center, null);
        } else {
           try {
               if (ThemeHelper.itemControl.isThemeIos18()){
                   Timber.e("NVQ onViewInit+++");
                   rootView = inflater.inflate(R.layout.control_center_ios18, null);
               } else {
                   Timber.e("NVQ onViewInit+++");
                   rootView = inflater.inflate(R.layout.control_center, null);
               }
           } catch (Exception e){
               Timber.e("NVQ onViewInit+++");
               rootView = inflater.inflate(R.layout.control_center, null);
           }

        }
        isFirstSetView = true;
        viewBgBlackOpenNotySystem = inflater.inflate(R.layout.view_background_blur_when_expand_noty_system, null);
        viewDialogOpenSystem = new ViewDialogOpenSystem(this);
        viewDialogContent = new ViewDialogContent(this);
        viewToast = new ViewToastTextNoty(this);
//        } catch (Exception e) {
//            Timber.e("hachung Exception:" + e);
//        }
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            // Lấy khoảng trống của toàn bộ system bars (bao gồm cả navigation bar)
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Giữ nguyên kích thước view nhưng tránh navigation bar
            v.setPadding(0, 0, 0, systemBarsInsets.bottom);

            return insets;
        });
    }

    private void setUpEdgeTop() {
        if (groupTouchTop == null) {
            groupTouchTop = (GroupTouchTop) inflater.inflate(R.layout.layout_touch_top, null);
            groupTouchTop.setSizeTouch();
            groupTouchTop.setTouchListener(onTouchViewListener);
            groupTouchTop.setIsEdit(isInEditSettingTouch);
            //testCheckHideStatusBar();
        }
        groupTouchTop.updateStateEnable();
    }

    private void testCheckHideStatusBar() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            lp.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        lp.format = PixelFormat.TRANSLUCENT;
        lp.gravity = Gravity.TOP | Gravity.START;
        setWidthParams(lp, 100, WindowManager.LayoutParams.MATCH_PARENT);
        //lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        int flagsOld = lp.flags;
        int flagsNew = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        lp.flags = flagsNew;
        Timber.e("hoangld lp.flags " + lp.flags + " flagsOld " + flagsOld + " flagsNew ");
        View view = new View(this);
        view.setBackgroundColor(Color.RED);
        view.setAlpha(0.3f);

        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Timber.e("hoangld ");
            }
        });

        view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @androidx.annotation.NonNull
            @Override
            public WindowInsets onApplyWindowInsets(@androidx.annotation.NonNull View v, @androidx.annotation.NonNull WindowInsets insets) {
                Timber.e("hoangld insets " + insets);
                return insets.consumeSystemWindowInsets();
            }
        });

        view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                Timber.e("hoangld visibility " + visibility);
            }
        });

        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Timber.e("hoangld ");
            }
        });
        ExtensionsKt.addLayout(windowManager, view, lp);
    }

    private void setUpEdgeLeft() {
        if (groupTouchLeft == null) {
            groupTouchLeft = (GroupTouchLeft) inflater.inflate(R.layout.layout_touch_left, null);
            groupTouchLeft.setSizeTouch();
            groupTouchLeft.setTouchListener(onTouchViewListener);
            groupTouchLeft.setIsEdit(isInEditSettingTouch);
        }
        groupTouchLeft.updateStateEnable();
    }

    private void setUpEdgeRight() {
        if (groupTouchRight == null) {
            groupTouchRight = (GroupTouchRight) inflater.inflate(R.layout.layout_touch_right, null);
            groupTouchRight.setSizeTouch();
            groupTouchRight.setTouchListener(onTouchViewListener);
            groupTouchRight.setIsEdit(isInEditSettingTouch);
        }
        groupTouchRight.updateStateEnable();
    }

    private void setUpEdgeBottom() {
        if (groupTouchBottom == null) {
            groupTouchBottom = (GroupTouchBottom) inflater.inflate(R.layout.layout_touch_bottom, null);
            groupTouchBottom.setSizeTouch();
            groupTouchBottom.setTouchListener(onTouchViewListener);
            groupTouchBottom.setIsEdit(isInEditSettingTouch);
        }
        groupTouchBottom.updateStateEnable();
    }

    public void invalidateControl() {
        MethodUtils.changeBackgroundNotiCenter(() -> {
            if (typeChoose == VALUE_CONTROL_CENTER || typeChoose == VALUE_PIXEL || typeChoose == VALUE_SAMSUNG) {
                if (notyCenterView != null) {
                    notyCenterView.setUpBg();
                }
            } else if (typeChoose == VALUE_SHADE) {
                if (controlNotyMiShade != null) {
                    controlNotyMiShade.setUpBg();
                }
            } else {
                if (notyCenterViewOS != null) {
                    notyCenterViewOS.updateBitmapBlur();
                    notyCenterViewOS.setBgNew();
                }
                if (controlCenterIOSView != null) {
                    controlCenterIOSView.setBgNew();
                }
            }


        });

    }

    public void changeStyleDarkLight(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager.getNightMode() != UiModeManager.MODE_NIGHT_YES && uiModeManager.getNightMode() != UiModeManager.MODE_NIGHT_NO) {
            int nightModeFlags = App.ins.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            checkDarkMode(nightModeFlags != Configuration.UI_MODE_NIGHT_YES);
        } else {
            checkDarkMode(uiModeManager.getNightMode() != UiModeManager.MODE_NIGHT_YES);
        }
    }

    private void checkDarkMode(boolean isCheck) {
        isDarkModeOn = !isCheck;
        if (isCheck) {
            tinyDB.putInt(Constant.STYLE_SELECTED, LIGHT);
        } else {
            tinyDB.putInt(Constant.STYLE_SELECTED, DARK);
        }
//        if (controlCenterIOSView != null) {
//            controlCenterIOSView.updateViewDarkMode(isDarkModeOn);
//        }
//        Timber.e("hachung controlCenterView:"+controlCenterView);
//        if (controlCenterView != null) {
//            controlCenterView.updateActionView(Constant.DARK_MODE, isDarkModeOn);
//        }
//        if (controlNotyMiShade != null) {
//            controlNotyMiShade.updateActionView(Constant.DARK_MODE, isDarkModeOn);
//        }
        updateBg();
    }

    public void updateIcon() {

        if (typeChoose == VALUE_SHADE && controlNotyMiShade != null) {
            controlNotyMiShade.updateBgIcon(false);
        } else if (controlCenterView != null) {
            controlCenterView.updateIcon();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = this;
        if (intent != null) {
            captureScreenControl(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (rootView == null) {
            return;
        }
        final int eventType = event.getEventType();
        String packageName = event.getPackageName() + "";
        handingWhenShowNotySystem(eventType, packageName, event);
        //testFullScreenEvent(event);
        //handing click action in noty system
        if (notyOpened && !evenTypeClickNoty.isEmpty() && event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (!allowHandingAction) {
                return;
            }
            AccessibilityNodeInfo nodeInfo;
            try {
                nodeInfo = event.getSource();
            } catch (Exception e) {
                Timber.e(e);
                return;
            }
            if (nodeInfo == null || packageName.equals(getPackageName())) {
                return;
            }
            try {
                if (nodeInfo.getParent() != null) {
                    countRootNodeLevel = 0;
                    nodeInfo = getRootNodeInfo(nodeInfo);
                }
            } catch (Exception ignored) {
            }
            processAction(nodeInfo);
        }


    }

    private boolean isStatusBarCurrentlyVisible() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return false;

        List<AccessibilityNodeInfo> statusBarNodes = root.findAccessibilityNodeInfosByText("StatusBar");
        return statusBarNodes != null && !statusBarNodes.isEmpty();
    }

    private void testFullScreenEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo nodeCurrent = event.getSource();
        AccessibilityNodeInfo nodeRoot = event.getSource();
        if (nodeCurrent != null) {
            Rect rect = new Rect();
            nodeCurrent.getBoundsInScreen(rect);
        }

        if (nodeRoot != null) {
            Rect rect = new Rect();
            nodeRoot.getBoundsInScreen(rect);
        }
    }

    private void handingWhenShowNotySystem(int eventType, String packageName, AccessibilityEvent event) {
        if (rootView == null || event == null) {
            return;
        }

        if (typeChoose == VALUE_SHADE && !autoHideNotyPanelSystemStyleShade) {
            return;
        }

        if (eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return;
        }
        if (event.getContentChangeTypes() != AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED) {
            return;
        }
        AccessibilityNodeInfo source;
        try {
            source = event.getSource();
            if (source == null || source.getWindow() == null) {
                return;
            }
        } catch (Exception e) {
            Timber.e(e);
            return;
        }
//        if (packageSetting.equals(packageName)) {
//            String textTitle = event.getText().size() > 0 ? String.valueOf(event.getText().get(0)) : "null.";
//            Timber.e("hoangld textTitle " + textTitle + " titleNotySystemUi " + titleNotySystemUi);
//        }

        //Timber.e("hoangld eventType " + eventType + " packageName " + packageName + " titleCurrent " + titleOld + " textTitle " + textTitle + " titleNotySystemUi " + titleNotySystemUi + " eventType " + eventType);
        String textTitle = !event.getText().isEmpty() ? String.valueOf(event.getText().get(0)) : "null.";
        if (groupTouchTop != null && groupTouchTop.isTouchViewInv()) {
            //check case disable Notifications or Control in Home Fragment and touch it, if touch region Invisible don't show RootView
            isPreventShowRootView = true;
            titleOld = textTitle;
            return;
        }

        isChangeAppShow = !Objects.equals(titleOld, textTitle);

        if (!isChangeAppShow && isPreventShowRootView) {
            return;
        }
        isPreventShowRootView = false;

        if (!runningOpenNotyToClickAction && packageSetting.equals(packageName) && titleNotySystemUi.contentEquals(textTitle)) {
            AccessibilityWindowInfo windowInfo = source.getWindow();
            boolean isShowSystemNotyPanel = false;
            if (windowInfo != null) {
                Rect rect = new Rect();
                windowInfo.getBoundsInScreen(rect);
                if (rect.width() == App.widthHeightScreenCurrent.w && rect.height() == App.widthHeightScreenCurrent.h) {
                    isShowSystemNotyPanel = true;
                }
            }

            if (rootView.getVisibility() != View.VISIBLE) {
                if (isShowSystemNotyPanel) {
                    if (!Objects.equals(titleOld, textTitle)) {
                        boolean showControl = groupTouchTop != null && groupTouchTop.isTouchDownViewControl();
                        if (typeChoose == VALUE_SHADE) {
                            showViewNotyOrControl(showControl, Constant.EDGE_TOP, true, this::dismissNotyShade);
                        } else {
                            boolean enabledTouchNoty = App.tinyDB.getBoolean(Constant.ENABLE_NOTY, Constant.DEFAULT_ENABLE_NOTY);
                            if (enabledTouchNoty) {
                                showViewNotyOrControl(showControl, Constant.EDGE_TOP, true, this::dismissNotyShade);
                            }
                        }

                    }
                }
            }
            dismissNotyShade();
        }
        titleOld = textTitle;
    }

    private AccessibilityNodeInfo getRootNodeInfo(AccessibilityNodeInfo nodeInfo) {
        countRootNodeLevel++;
        //20 is max level root mode, avoid error stack size
        if (nodeInfo.getParent() != null && countRootNodeLevel < 20) {
            return getRootNodeInfo(nodeInfo.getParent());
        } else {
            return nodeInfo;
        }
    }

    private void processAction(AccessibilityNodeInfo nodeInfo) {
        disposableProcessEvent.clear();
        Completable.fromAction(() -> {
            if (nodeInfo != null && nodeInfo.refresh()) {
                handingAction(nodeInfo);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                cancelAction = false;
                disposableProcessEvent.add(d);
                allowHandingAction = false;
            }

            @Override
            public void onComplete() {
                if (cancelAction) {
                    allowHandingAction = true;
                    return;
                }

                if (evenTypeClickNoty.equals(Constant.STRING_ACTION_DATA_MOBILE) && api31) {
                    if (dataChange) {
                        closeAction(false);
                        Timber.e("hachung nodeCloseDataApi31:" + nodeCloseDataApi31);
                        if (nodeCloseDataApi31 != null) {
                            performActionClick(nodeCloseDataApi31);
                        }
                    }
                } else if (evenTypeClickNoty.equals(Constant.STRING_ACTION_BATTERY)) {
                    if (batteryChange) {
                        closeAction(false);
                        Timber.e("hachung nodeCloseBattery:" + nodeCloseBattery);
                        if (nodeCloseBattery != null) {
                            nodeCloseBattery.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                } else {
                    closeAction(false);
                }
                allowHandingAction = true;
            }

            @Override
            public void onError(@NonNull Throwable e) {
                allowHandingAction = true;
            }
        });

    }

    private void closeAction(boolean whenCloseNoty) {
        handlerTimeProcessAction.removeCallbacksAndMessages(null);
        if (!whenCloseNoty) {
            setViewDialogOpenSystem();
        }
        setEvenClick("", null);
        evenTypeClickNoty = "";
        resultClickDone = false;
    }

    private void handingAction(AccessibilityNodeInfo nodeInfo) {
        listNodeInfo.clear();
        countChildNodeLevel = 0;
        getListNodeInfo(nodeInfo);
        //check listNodeInfo.size() > 25 to avoid case nodeInfoRoot is status bar(some devices)
        if (listNodeInfo.size() > 25 || !isFirstClickAction) {
            clickAction(evenTypeClickNoty);
            isFirstClickAction = false;
        } else {
            cancelAction = true;
        }
    }

    private void clickAction(String evenTypeClickNoty) {
        String action = "";
        String action2 = "";
        String action3 = "";
        String withoutKey = "";
        String withoutKey2 = "";
        if (actionClick != null) {
            Timber.e("hachung actionClick:" + actionClick.getLanguageCode());
            for (TextActionClick textActionClick : actionClick.getActionClickList()) {
                if (textActionClick.getEvenTypeClickNoty().equals(evenTypeClickNoty)) {
                    action = textActionClick.getAction().toLowerCase();
                    action2 = textActionClick.getAction2().toLowerCase();
                    action3 = textActionClick.getAction3().toLowerCase();
                    withoutKey = textActionClick.getWithoutKey().toLowerCase();
                    withoutKey2 = textActionClick.getWithoutKey2().toLowerCase();
                    break;
                }
            }
        }
        Timber.e("hachung action:" + action + "/action2: " + action2 + "/action3: " + action3 + "/withoutKey: " + withoutKey + "/withoutKey2: " + withoutKey2);
        handingClickNoty(action, action2, action3, withoutKey, withoutKey2);


    }

    private void setViewDialogOpenSystem() {
        if (viewDialogOpenSystem != null) {
            Timber.e("hachung resultClick:" + resultClick);
            if (resultClick) {
//            Timber.e("false " + resultClick + " listNodeInfo.size(): " + listNodeInfo.size());
                viewDialogOpenSystem.setVisible(false);
                if (iListenActionClick != null) {
                    iListenActionClick.actionClicked();
                }
                if (evenTypeClickNoty.equals(Constant.STRING_ACTION_AIRPLANE_MODE)) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (resultClickAirPlane == SettingUtils.isAirplaneModeOn(NotyControlCenterServicev614.this)) {
                            closeNotyCenter();
                        }
                    }, 2000);
                } else if (evenTypeClickNoty.equals(Constant.STRING_ACTION_DATA_MOBILE)) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (resultClickDataMobi == new DataMobileUtils(NotyControlCenterServicev614.this).isDataEnable()) {
                            closeNotyCenter();
                        }
                    }, 1000);
                }


            } else {
                viewDialogOpenSystem.setContent(evenTypeClickNoty);
                viewDialogOpenSystem.setVisible(true);
                setListenToViewCancelAnimation();
            }
        }
    }

    private void setListenToViewCancelAnimation() {
        if (iListenActionClick != null) {
            iListenActionClick.noFindAction();
        }
    }

    private void setListenToViewCancelAnimation(IListenActionClick iListenActionClick) {
        if (iListenActionClick != null) {
            iListenActionClick.noFindAction();
        }
        runningOpenNotyToClickAction = false;

    }

    private void getListNodeInfo(AccessibilityNodeInfo nodeInfo) {
        countChildNodeLevel++;
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {

            if (nodeInfo.getChild(i) == null) {
                continue;
            }

            listNodeInfo.add(nodeInfo.getChild(i));
            //check countChildNodeLevel to avoid StackOverflowError
            if (nodeInfo.getChild(i).getChildCount() > 0 && countChildNodeLevel < 150) {
                getListNodeInfo(nodeInfo.getChild(i));
            }
        }
    }

    private void setEvenClick(String event, IListenActionClick iListenActionClick) {
        evenTypeClickNoty = event;
        resultClickDone = false;
        if (event.isEmpty()) {
            hideNotySystem();
        }

        if (iListenActionClick != null) {
            this.iListenActionClick = iListenActionClick;
        }
    }

    private void handingClickNoty(String action, String action2, String action3, String withoutKey, String withoutKey2) {
        if (isFirstClickAction) {
            resultClick = false;
        }
        boolean typeClickData = evenTypeClickNoty.equals(Constant.STRING_ACTION_DATA_MOBILE);
        boolean actionDataApi31 = typeClickData && api31;
        boolean actionBattery = evenTypeClickNoty.equals(Constant.STRING_ACTION_BATTERY);
        boolean typeAirPlane = evenTypeClickNoty.equals(Constant.STRING_ACTION_AIRPLANE_MODE);

        if (actionBattery && isFirstClickAction) {
            countCheckBatteryMode = 0;
            handlerActionBattery.removeCallbacks(runnableActionBattery);
            handlerActionBattery.postDelayed(runnableActionBattery, 1000);
        } else if (actionDataApi31) {
            action2 = getLocaleStringResource(R.string.key_wifi_2, this).toLowerCase();
        }
        if (typeAirPlane) {
            resultClickAirPlane = SettingUtils.isAirplaneModeOn(this);
        } else if (typeClickData) {
            resultClickDataMobi = new DataMobileUtils(this).isDataEnable();
        }
        AccessibilityNodeInfo nodeInfoReserve = null;
        for (int i = 0; i < listNodeInfo.size(); i++) {

            AccessibilityNodeInfo nodeInfo = listNodeInfo.get(i);
            if (isFirstClickAction) {
                isAllowClick2nd = true;
                String ctDes = "";
                if (nodeInfo.getContentDescription() != null) {
                    ctDes = nodeInfo.getContentDescription().toString().toLowerCase().replace(",", " ");
                } else if (nodeInfo.getText() != null) {
                    ctDes = nodeInfo.getText().toString().toLowerCase().replace(",", " ");
                }
                if (!ctDes.isEmpty()) {
                    Timber.e("hachung ctDes:" + ctDes);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (MethodUtils.containsIgnoreCase(ctDes, action) || MethodUtils.containsIgnoreCase(ctDes, action2) || MethodUtils.containsIgnoreCase(ctDes, action3)) {
                            if ((withoutKey != null && !withoutKey.isEmpty() && MethodUtils.containsIgnoreCase(ctDes, withoutKey)) || (withoutKey2 != null && !withoutKey2.isEmpty() && MethodUtils.containsIgnoreCase(ctDes, withoutKey2))) {
                                continue;
                            }
                            if (nodeInfo.getClassName().toString().contains("TextView")) {
                                nodeInfoReserve = nodeInfo;
                                //continue;
                            }
                            resultClick = true;
                            if (!resultClickDone) {
                                resultClickDone = performActionClick(nodeInfo);
                            }
                            if (actionBattery && !resultClickDone) {
                                batteryChange = true;
                            }

                            //fix click data mobile samsung s20 api31
                            if ((actionDataApi31) && resultClickDone && MethodUtils.containsIgnoreCase(ctDes, action)) {
                                isAllowClick2nd = false;
                                dataChange = true;
                                wifiChange = true;
                                this.resultClick = true;

                            }

                        }
                    } else {
                        if (ctDes.contains(action) || ctDes.contains(action2) || ctDes.contains(action3)) {
                            if (withoutKey != null && !withoutKey.isEmpty() && ctDes.contains(withoutKey) || nodeInfo.getClassName().toString().contains("TextView")) {
                                continue;
                            }

                            resultClick = true;
                            performActionClick(nodeInfo);
                        }
                    }
                }
            } else if (isAllowClick2nd) {
                if (actionDataApi31) {
                    processActionDataAPI31(nodeInfo);
                } else if (actionBattery) {
                    processActionBattery(nodeInfo);
                }
            }
        }

        if (nodeInfoReserve != null && !resultClick) {
            performActionClick(nodeInfoReserve);
            this.resultClick = true;
        }

    }

    private void processActionDataAPI31(AccessibilityNodeInfo nodeInfo) {
        String ctText = "";
        if (nodeInfo.getText() != null) {
            ctText = nodeInfo.getText().toString().toLowerCase();
        } else if (nodeInfo.getContentDescription() != null) {
            ctText = nodeInfo.getContentDescription().toString().toLowerCase();
        }
        if (!ctText.isEmpty()) {
            if (MethodUtils.containsIgnoreCase(ctText, getLocaleStringResource(R.string.key_data_mobile_mode, this).toLowerCase()) || MethodUtils.containsIgnoreCase(ctText, getLocaleStringResource(R.string.key_data_mobile_2, this).toLowerCase())) {
                if (performActionClick(nodeInfo)) {
                    dataChange = true;
                    resultClick = true;
                }
            }

            if (MethodUtils.containsIgnoreCase(ctText, getLocaleStringResource(R.string.key_wifi_done, this).toLowerCase())) {
                nodeCloseDataApi31 = nodeInfo;
            }
        }
    }

    private void processActionBattery(AccessibilityNodeInfo nodeInfo) {
//        Timber.e("processActionBattery.getText()+ " + nodeInfo.getText());
        if (nodeInfo.getText() != null) {
            String ctText = nodeInfo.getText().toString().toLowerCase();

            if (MethodUtils.containsIgnoreCase(ctText, getLocaleStringResource(R.string.key_battery_done, this).toLowerCase())) {
                nodeCloseBattery = nodeInfo;
                batteryChange = true;
                resultClick = true;
            }
        }
    }

    private boolean performActionClick(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo != null && nodeInfo.refresh()) {
            if (nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                return true;
            } else {
                if (nodeInfo.getParent() != null) {
                    return nodeInfo.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }
        return false;
    }

    public void setHandingAction(IListenActionClick iListenActionClick, String typeAction) {
        runningOpenNotyToClickAction = true;
        if (isNotiSystemApi31Closed) {
            switch (typeAction) {
                case Constant.STRING_ACTION_DATA_MOBILE:
                    setMobileDataModeNoty(iListenActionClick);
                    break;
                case Constant.DARK_MODE:
                    setDarkModeNoty(iListenActionClick);
                    break;
                case Constant.STRING_ACTION_BATTERY:
                    setLowPowerModeNoty(iListenActionClick);
                    break;
                case Constant.STRING_ACTION_AIRPLANE_MODE:
                    setAirPlaneModeNoty(iListenActionClick);
                    break;
                case Constant.STRING_ACTION_LOCATION:
                    setLocationNoty(iListenActionClick);
                    break;
                case Constant.STRING_ACTION_HOST_POST:
                    setHotspotNoty(iListenActionClick);
                    break;
            }
        } else {
            this.typeClickWaiting = typeAction;
            this.iListenActionClickWaiting = iListenActionClick;
            handlerClickNoti.removeCallbacks(runnableClickNoty);
            handlerClickNoti.postDelayed(runnableClickNoty, 2000);
        }

    }

    public void setDarkModeNoty(IListenActionClick iListenActionClick) {
        if (!allowOpenNoty) {
            return;
        }
        openNotySystem();
        new Handler().postDelayed(() -> setEvenClick(Constant.DARK_MODE, iListenActionClick), 200);

    }

    public void setLowPowerModeNoty(IListenActionClick iListenActionClick) {
        if (!allowOpenNoty) {
            return;
        }

        batteryOld = SettingUtils.isPowerSaveMode(this);
        batteryChange = SettingUtils.isPowerSaveMode(this);
        handlerActionBattery.removeCallbacks(runnableActionBattery);
        openNotySystem();
        new Handler().postDelayed(() -> setEvenClick(Constant.STRING_ACTION_BATTERY, iListenActionClick), 500);
    }

    public void setAirPlaneModeNoty(IListenActionClick iListenActionClick) {
        if (!allowOpenNoty) {
            return;
        }
        openNotySystem();
        new Handler().postDelayed(() -> setEvenClick(Constant.STRING_ACTION_AIRPLANE_MODE, iListenActionClick), 200);
    }

    public boolean isAirPlaneModeEnabled() {
        return airPlaneModeEnabled;
    }

    public void setMobileDataModeNoty(IListenActionClick iListenActionClick) {
        if (!allowOpenNoty) {
            return;
        }
        if (airPlaneModeEnabled) {
            showToast(getString(R.string.can_not_do_this_action_when_ari_plane_mode_enabled));
            setListenToViewCancelAnimation(iListenActionClick);
            return;
        }
        if (!hasSimCard) {
            showToast(getString(R.string.no_sim));
            setListenToViewCancelAnimation(iListenActionClick);
            return;
        }

        openNotySystem();
        new Handler().postDelayed(() -> setEvenClick(Constant.STRING_ACTION_DATA_MOBILE, iListenActionClick), 200);


        if (dataMobileUtils == null) {
            dataMobileUtils = new DataMobileUtils(this);
        }

        dataChange = false;
        countRunnableData = 0;
        handlerDataMobile.removeCallbacks(runnableData);
        handlerDataMobile.postDelayed(runnableData, 1000);

    }

    public void setWifiNoty() {
        Timber.e("NVQ onHideControl3");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            SettingUtils.settingWifi(this);
        } else {
//            Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
//            panelIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(panelIntent);
            SettingUtils.intentChangeWifi(this);
            onControlCenterListener.onClose();
        }
    }
    public void closeCenterWhenClick3G(){
        if (onControlCenterListener != null){
            onControlCenterListener.onClose();
        }
    }

    public void setLocationNoty(IListenActionClick iListenActionClick) {
        if (!allowOpenNoty) {
            return;
        }

        openNotySystem();
        new Handler().postDelayed(() -> setEvenClick(Constant.STRING_ACTION_LOCATION, iListenActionClick), 200);
    }

    public void setHotspotNoty(IListenActionClick iListenActionClick) {
        if (!allowOpenNoty) {
            return;
        }
        if (airPlaneModeEnabled) {
            showToast(getString(R.string.can_not_do_this_action_when_ari_plane_mode_enabled));
            setListenToViewCancelAnimation(iListenActionClick);
            return;
        }
//        if (!hasSimCard) {
//            showToast(getString(R.string.no_sim));
//            setListenToViewCancelAnimation(iListenActionClick);
//            return;
//        }
        openNotySystem();
        new Handler().postDelayed(() -> setEvenClick(Constant.STRING_ACTION_HOST_POST, iListenActionClick), 200);
    }

    public void openNotySystem() {
        if (allowOpenNoty) {
            allowOpenNoty = false;
            if (viewBgBlackOpenNotySystem != null) {
                setAlphaBgViewNoty(true, viewBgBlackOpenNotySystem);
            }
            setInfoTimeout(true);
            handlerTimeProcessAction.removeCallbacksAndMessages(null);
            handlerTimeProcessAction.postDelayed(runnableTimeProcessAction, 5000);

            handlerOpenNoty.removeCallbacks(runnableOpenNoty);
            handlerHideNoty.removeCallbacks(runnableHideNoty);
            handlerOpenNoty.post(runnableOpenNoty);
        }
    }

    public void hideNotySystem() {
        setInfoTimeout(false);
        runningOpenNotyToClickAction = false;
        if (resultClick) {
            dismissNotyShade();
        } else if (!tinyDB.getBoolean(Constant.AUTO_OPEN_NOTY_SYSTEM, false)) {
            dismissNotyShade();
        }

        handlerHideNoty.removeCallbacks(runnableHideNoty);
        handlerHideNoty.postDelayed(runnableHideNoty, 500);
    }

    private void setAlphaBgViewNoty(boolean isShow, View viewBackground) {
        float alphaResult = isShow ? 1 : 0;

        if (isShow) {
            viewBackground.setVisibility(View.VISIBLE);
        }

        viewBackground.animate().alpha(alphaResult).setDuration(100).withEndAction(() -> {
            if (!isShow) {
                viewBackground.setVisibility(View.GONE);
            }
        });
    }

    public void dismissNotyShade() {
        int isEnable = tinyDB.getInt(Constant.IS_ENABLE, IS_DISABLE);
        if (isEnable == Constant.DEFAULT_IS_ENABLE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                performGlobalAction(GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE);
            } else {
                SettingUtils.collapseNotificationsPanel(NotyControlCenterServicev614.this);
            }
        }
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        LocaleUtils.INSTANCE.setCurrentResources(this, null);
        setUpTitleNotySystem();
        isErrorService = false;
        setupService();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int orientationChanged =  DensityUtils.getOrientationWindowManager(this);
        if (App.widthHeightScreenCurrent.getSize() != new WidthHeightScreen(this).getSize() && !isChangeLocale) {
            // check case device change screen resolution. Ex: device samsung s7 with battery save mode
            App.setUpWidthHeightAndBitMapTransparent(this);
            Timber.e("hachung newConfig 3:");
            newConfig(orientationChanged);
        }
    }

    public Context adjustFontScale(Context context, boolean create) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.fontScale = 1f;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
//        int snap = 20;
//        float exactDpi = (metrics.xdpi + metrics.ydpi) / 2;
//        int targetDpi = (int) (Math.round(exactDpi / snap) * snap);
//        metrics.densityDpi = targetDpi;
//        configuration.densityDpi = targetDpi;
        metrics.setTo(metrics);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && create) {
            context = context.createConfigurationContext(configuration);
        } else {
            context.getResources().updateConfiguration(configuration, metrics);
        }

        return context;
    }

    private void setupService() {
        MethodUtils.intentToCheckPermission(this);
        stateRecordScreen = ScreenRecordActionView.STATE.NONE;
        App.setUpWidthHeightAndBitMapTransparent(this);
        setWidthHeight();
        registerReceiver(orientationBR, orientationIF);
        registerTimeReceiver();
        registerBatteryReceiver();
        registerActionDoNotDisturb();
        registerLanguageReceiver();
        startWatchHome();
        registerUnlocked();
        setUpSnoozed();
        registerUnlockedOpenApp();
        registerAirplane();
        registerWifi();
        registerHostPostReceiver();
        registerDataMobile();
        registerSimStateChange();
        registerBrightness();
        registerBluetooth();
        registerLocation();
        registerLowPowerMode();
        registerListenPhoneState();
        registerVolume();

        registerFlashLight();
//        ThemeHelper.Companion.getThemeCurrentApply(this);

        orientation =  DensityUtils.getOrientationWindowManager(getApplicationContext());
//                typeChoose = tinyDB.getInt(Constant.TYPE_NOTY, VALUE_CONTROL_CENTER);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        initManager();
        changeStyleDarkLight(this);
        if (ThemeHelper.itemControl != null) {
            ThemeHelper.Companion.enableWindow();
        }

//        MethodUtils.changeBackgroundNotiCenter(() -> {
//            new Handler(Looper.getMainLooper()).post(() -> {
//                orientation = new DensityUtils().getOrientationWindowManager(getApplicationContext());
////                typeChoose = tinyDB.getInt(Constant.TYPE_NOTY, VALUE_CONTROL_CENTER);
//                windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//                inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//                mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                initManager();
//                changeStyleDarkLight(this);
////                if (tinyDB.getInt(IS_ENABLE, DEFAULT_IS_ENABLE) == DEFAULT_IS_ENABLE) {
////                    findRootLayout();
////                    findViews();
////                    addTouchAndRootView();
////                }
//
//            });
//
//        });

        getLastNotyToShow();
        loadActionClickControl();

    }

    private void registerFlashLight() {
        flashUtils = new FlashUtils(this, (valueRegister, b, pos) -> {
            if (valueRegister.equals(Constant.STRING_ACTION_FLASH_LIGHT)) {
                isFlashOn = b;
                if (controlCenterView != null) {
                    controlCenterView.updateActionViewExpand(valueRegister, b);
                }
                if (controlNotyMiShade != null) {
                    controlNotyMiShade.updateActionView(valueRegister, b);
                }
                if (controlCenterIOSView != null) {
                    controlCenterIOSView.updateViewFlashlight(b);
                }
                if (notyCenterViewOS != null && notyCenterViewOS.adapterVpgNoty != null && notyCenterViewOS.adapterVpgNoty.notyView2 != null) {
                    notyCenterViewOS.adapterVpgNoty.notyView2.updateFlash(b);
                }
            }
        }, Constant.STRING_ACTION_FLASH_LIGHT, 0);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            isFlashOn = flashUtils.isEnabledApi22();
        } else {
            isFlashOn = FlashUtils.enabled;
        }
    }

    public void setFlashOnOff() {
        if (flashUtils == null) {
            return;
        }
        if (isFlashOn) {
            flashUtils.flashOff();
            isFlashOn = false;
        } else {
            flashUtils.flashOn();
            isFlashOn = true;
        }
    }
//    private void registerLocation(){
//        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
//        registerReceiver(locationSwitchStateReceiver, filter);
//    }
    private void registerVolume() {
        if (volumeReceiver != null) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter(Constant.VOLUME_CHANGED_ACTION);
        volumeReceiver = new VolumeReceiver(new VolumeReceiver.IVolumeChange() {
            @Override
            public void volumeChange(int volume) {
                float v = AudioManagerUtils.getInstance(App.ins).getVolume();
                Log.d("duongcvc", "volumeChange: " + v + ":" + volume);
                if (controlCenterIOSView != null) {
                    controlCenterIOSView.updateVolume(volume);
                }
                if (controlCenterView != null) {
                    controlCenterView.updateVolume(volume);
                }
            }
        });
        registerReceiver(volumeReceiver, intentFilter);
    }

    private void registerUnlocked() {
        if (unlockedReceiver == null) {
            unlockedReceiver = new DeviceUnlockedReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        try {
            registerReceiver(unlockedReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpSnoozed() {
        snoozedReceiver = new SnoozedReceiver();
        IntentFilter filter = new IntentFilter(Constant.ACTION_NOTY_SNOOZED);
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(snoozedReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterNotySnoozed() {
        try {
            if (snoozedReceiver != null) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(snoozedReceiver);
                snoozedReceiver = null;
            }
        } catch (Exception e) {
            Timber.e("error");
        }

    }

    private void unregisterHostPostReceiver() {
        try {
            if (actionWifiHostPostReceiver != null) {
                unregisterReceiver(actionWifiHostPostReceiver);

                actionWifiHostPostReceiver = null;
            }
        } catch (Exception e) {
            Timber.d(e);
        }

    }

    private void registerUnlockedOpenApp() {
        if (unLockOpenAppReceiver == null) {
            unLockOpenAppReceiver = new UnLockOpenAppReceiver((pka, idEvent) -> {
                if (notyCenterViewOS != null) {
                    notyCenterViewOS.actionOpenApp(pka, idEvent);
                }
            });
        }
        IntentFilter filterUnLockOpenApp = new IntentFilter();
        filterUnLockOpenApp.addAction(Constant.ACTION_OPEN_APP);
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(unLockOpenAppReceiver, filterUnLockOpenApp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterUnlockedOpenApp() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(unLockOpenAppReceiver);
            unLockOpenAppReceiver = null;
        } catch (Exception e) {
            Timber.d(e);
        }

    }

    private void getLastNotyToShow() {
        if (NotificationListener.getInstance() != null) {
            NotificationListener.getInstance().loadInFirstUse();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        executor.shutdown();
        if (App.ins.focusUtils != null) {
            App.ins.focusUtils.onDestroy();
        }
        disableWindow();

        stopWatchHome();

        try {
            unregisterReceiver(orientationBR);
        } catch (Exception e) {
            Timber.d(e);
        }
        unregisterActionAirplaneModeChange();
        unregisterWifiReceiver();
        unregisterActionDoNotDisturb();
        unregisterDataReceiver();
        unregisterSimReceiver();
        unregisterLanguageChangeReceiver();
        unregisterUnlockedReceiver();
        unregisterReceiverBrightness();
        unregisterReceiverTimeChange();
        unregisterReceiverActionLocation();
        unregisterReceiverBluetooth();
        unregisterReceiverLowPowerModeChange();
        setListenNonePhoneState();
        unregisterVolumeReceiver();
        unregisterBatteryReceiver();
        unregisterUnlockedOpenApp();
        unregisterNotySnoozed();
        unregisterHostPostReceiver();
        unregisterFlashUtils();
        instance = null;
    }

    private void unregisterReceiverLowPowerModeChange() {
        try {
            if (lowPowerModeChange != null) {
                unregisterReceiver(lowPowerModeChange);
                lowPowerModeChange = null;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void unregisterActionAirplaneModeChange() {
        try {
            if (actionAirplaneModeChange != null) {
                unregisterReceiver(actionAirplaneModeChange);

                actionAirplaneModeChange = null;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void unregisterWifiReceiver() {
        try {
            if (wifiBroadcastReceiver != null) {
                unregisterReceiver(wifiBroadcastReceiver);

                wifiBroadcastReceiver = null;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void unregisterBatteryReceiver() {
        try {
            if (batteryBroadCast != null) {
                unregisterReceiver(batteryBroadCast);
                battery = null;
                batteryBroadCast = null;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void unregisterActionDoNotDisturb() {
        try {
            if (actionDoNotDisturb != null) {
                unregisterReceiver(actionDoNotDisturb);

                actionDoNotDisturb = null;
            }
        } catch (Exception e) {
            Timber.d(e);
        }
    }

    private void unregisterVolumeReceiver() {
        try {
            if (volumeReceiver != null) {
                unregisterReceiver(volumeReceiver);

                volumeReceiver = null;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void unregisterFlashUtils() {
        try {
            if (flashUtils != null) {
                flashUtils.unRegisterListener();
                flashUtils = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterDataReceiver() {
        try {
            if (contentDataMobile != null) {
                try {
                    getContentResolver().unregisterContentObserver(contentDataMobile);
                    contentDataMobile = null;
                } catch (Exception e) {
                    Timber.d(e);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void unregisterSimReceiver() {
        try {
            if (simChangeBroadcastReceiver != null) {
                unregisterReceiver(simChangeBroadcastReceiver);

                simChangeBroadcastReceiver = null;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void unregisterLanguageChangeReceiver() {
        try {
            if (languageChangeReceiver != null) {
                unregisterReceiver(languageChangeReceiver);

                languageChangeReceiver = null;
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void unregisterUnlockedReceiver() {
        try {
            if (unlockedReceiver != null) {
                unregisterReceiver(unlockedReceiver);
                unlockedReceiver = null;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void unregisterReceiverBrightness() {
        try {
            if (contentObBrightness != null) {
                getContentResolver().unregisterContentObserver(contentObBrightness);
                contentObBrightness = null;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void unregisterReceiverTimeChange() {
        try {
            if (timeReceiver != null) {
                unregisterReceiver(timeReceiver);

                timeReceiver = null;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void unregisterReceiverBluetooth() {
        try {
            if (bluetoothReceiver != null) {
                unregisterReceiver(bluetoothReceiver);
                bluetoothReceiver = null;
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void unregisterReceiverActionLocation() {
        try {
            if (actionLocationBroadcastReceiver != null) {
                unregisterReceiver(actionLocationBroadcastReceiver);
                actionLocationBroadcastReceiver = null;
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void newConfig(int orientation) {
        setWidthHeight();
        stringLanguageOld = getString(R.string.low_power_mode);

        this.orientation = orientation;
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            LocaleUtils.INSTANCE.setCurrentResources(this, null);
            if (ThemeHelper.itemControl != null) {
                ThemeHelper.Companion.enableWindow();
            }
        }, 300);


    }


    private void initManager() {
        paramsManager = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            paramsManager.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        } else {
            paramsManager.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }
        paramsManager.format = PixelFormat.TRANSPARENT;
        paramsManager.gravity = Gravity.TOP;
        setWidthParams(paramsManager, WindowManager.LayoutParams.MATCH_PARENT, 0);
        paramsManager.flags = flagManager;
    }

    private WindowManager.LayoutParams setWidthParams(WindowManager.LayoutParams paramsManager, int w, int h) {
        paramsManager.width = w;
        paramsManager.height = h;
        return paramsManager;
    }

    private void setStateViewMiControl(boolean showControl) {
        if (controlCenterView == null || notyCenterView == null) return;

        int controlVisibility = showControl ? View.VISIBLE : View.INVISIBLE;
        int notyVisibility = showControl ? View.INVISIBLE : View.VISIBLE;

        controlCenterView.setVisibility(controlVisibility);
        notyCenterView.setVisibility(notyVisibility);
    }

    private void setStateViewControlNotyIos14(boolean showControl) {
        if (controlCenterIOSView == null || notyCenterViewOS == null) return;
        if (showControl) {
            controlCenterIOSView.show();
            controlCenterIOSView.setVisibility(View.VISIBLE);
            notyCenterViewOS.setVisibility(View.INVISIBLE);
        } else {
            controlCenterIOSView.hide();
            controlCenterIOSView.setVisibility(View.INVISIBLE);
            notyCenterViewOS.setVisibility(View.VISIBLE);
        }
    }

    private void findViews() {
        RelativeLayout.LayoutParams paramsControl;
        if (typeChoose == VALUE_CONTROL_CENTER || typeChoose == VALUE_PIXEL || typeChoose == VALUE_SAMSUNG) {
            controlCenterView = rootView.findViewById(R.id.miControlCenter);
            paramsControl = (RelativeLayout.LayoutParams) controlCenterView.getLayoutParams();
            controlCenterView.addView(viewBgBlackOpenNotySystem, 0);
        } else if (typeChoose == VALUE_SHADE) {
            controlNotyMiShade = rootView.findViewById(R.id.layoutShade);
            paramsControl = (RelativeLayout.LayoutParams) controlNotyMiShade.getLayoutParams();
            controlNotyMiShade.addView(viewBgBlackOpenNotySystem, 0);
        } else {
            controlCenterIOSView = rootView.findViewById(R.id.controlcenter);
            paramsControl = (RelativeLayout.LayoutParams) controlCenterIOSView.getLayoutParams();
            controlCenterIOSView.addView(viewBgBlackOpenNotySystem, 0);
        }

        paramsControl.width = width;
        paramsControl.height = height;


        if (typeChoose == VALUE_CONTROL_CENTER_OS) {
            controlCenterIOSView.setLayoutParams(paramsControl);
            controlCenterIOSView.setOnControlCenterListener(onControlCenterListener);
        } else if (typeChoose == VALUE_CONTROL_CENTER || typeChoose == VALUE_PIXEL || typeChoose == VALUE_SAMSUNG) {
            controlCenterView.setLayoutParams(paramsControl);
            controlCenterView.setOnControlCenterListener(onControlCenterListener);
        } else if (typeChoose == VALUE_SHADE) {
            controlNotyMiShade.setLayoutParams(paramsControl);
            controlNotyMiShade.setOnControlCenterListener(onControlCenterListener);
        }


        if (typeChoose == VALUE_CONTROL_CENTER_OS) {
            notyCenterViewOS = rootView.findViewById(R.id.notyCenter);
            notyCenterViewOS.setOnNotyCenterCloseListener(onNotyCenterCloseListener);
            paramsNoty = (RelativeLayout.LayoutParams) notyCenterViewOS.getLayoutParams();
        } else if (typeChoose == VALUE_CONTROL_CENTER || typeChoose == VALUE_PIXEL || typeChoose == VALUE_SAMSUNG) {
            notyCenterView = rootView.findViewById(R.id.notyCenter);
            notyCenterView.setOnNotyCenterCloseListener(onNotyCenterCloseListener);
            paramsNoty = (RelativeLayout.LayoutParams) notyCenterView.getLayoutParams();
        }

        if (typeChoose != VALUE_SHADE) {
            paramsNoty.width = width;
            paramsNoty.height = height;
        }

        if (typeChoose == VALUE_CONTROL_CENTER_OS) {
            notyCenterViewOS.setLayoutParams(paramsNoty);
            notyCenterViewOS.setTranY(-paramsNoty.height);
        } else if (typeChoose == VALUE_CONTROL_CENTER || typeChoose == VALUE_PIXEL || typeChoose == VALUE_SAMSUNG) {
            notyCenterView.setLayoutParams(paramsNoty);
        }

        setAirPlaneToView(SettingUtils.isAirplaneModeOn(this));
        setWifiToView(SettingUtils.isEnableWifi(this));
        setDataMobileToView(new DataMobileUtils(this).isDataEnable());
    }

    private void closeControlCenter() {
        checkAllowTouch = true;
        evenTypeClickNoty = "";
        if (controlCenterIOSView != null){
            controlCenterIOSView.hide();
        }
        hideControlCenter();
    }

    public void updateBg() {
        MethodUtils.changeBackgroundNotiCenter(() -> new Handler(getMainLooper()).post(() -> {
            if (typeChoose == VALUE_CONTROL_CENTER_OS && controlCenterIOSView != null && notyCenterViewOS != null) {
                controlCenterIOSView.setBgNew();
                notyCenterViewOS.setBgNew();
                notyCenterViewOS.updateBitmapBlur();
            } else if (typeChoose == VALUE_SHADE && controlNotyMiShade != null) {
                controlNotyMiShade.setUpBg();
            } else if (controlCenterView != null && notyCenterView != null) {
                controlCenterView.setUpBg();
                notyCenterView.setUpBg();
            }
        }));
    }

    private void showViewNotyOrControl(boolean isShowControl, int typeEdge, boolean showFullAndAnim, ListenerAnim listenerAnim) {
        progressBlurRealTime();
        if (isShowControl) {
            handleShowControl(typeEdge, listenerAnim);
        } else {
            handleHideControl(typeEdge, showFullAndAnim, listenerAnim);
            isShowNoty = true;
        }
        handleVibration(typeEdge);
        hideStatusBarNavigationBar();


    }

    private void hideKeyboard(View view) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {

        }
    }

    private void handleShowControl(int typeEdge, ListenerAnim listenerAnim) {
        switch (typeChoose) {
            case VALUE_CONTROL_CENTER_OS:
                if (controlCenterIOSView != null) {
                    setStateViewControlNotyIos14(true);
                }
                break;
            case VALUE_CONTROL_CENTER:
            case VALUE_PIXEL:
            case VALUE_SAMSUNG:
                if (controlCenterView != null) {
                    setStateViewMiControl(true);
                    controlCenterView.show();
                }
                break;
            case VALUE_SHADE:
                if (controlNotyMiShade != null) {
                    controlNotyMiShade.show(listenerAnim);
                }
                break;
        }
    }

    private void handleHideControl(int typeEdge, boolean showFullAndAnim, ListenerAnim listenerAnim) {
        switch (typeChoose) {
            case VALUE_CONTROL_CENTER_OS:
                if (notyCenterViewOS != null) {
                    setStateViewControlNotyIos14(false);
                    if (typeEdge == Constant.EDGE_TOP) {
                        maxAnimationNoty = height;
                        notyCenterViewOS.setTranslationX(0);
                        notyCenterViewOS.setTranY(-maxAnimationNoty);
                    } else {
                        notyCenterViewOS.setTranslationX(0);
                        notyCenterViewOS.setTranY(0);
                    }
                    if (typeEdge == Constant.EDGE_TOP) {
                        progressExpandNoty = -maxAnimationNoty + 60;
                    } else {
                        progressExpandNoty = -maxAnimationNoty;
                    }
                    if (showFullAndAnim) {
                        notyCenterViewOS.post(() -> {
                            if (notyCenterViewOS != null) {
                                notyCenterViewOS.animationShowTopBot(listenerAnim);
                            }

                        });
                    }
                }
                break;
            case VALUE_CONTROL_CENTER:
            case VALUE_PIXEL:
            case VALUE_SAMSUNG:
                if (notyCenterView != null) {
                    setStateViewMiControl(false);
                    notyCenterView.touchShow(listenerAnim);
                }
                break;
            case VALUE_SHADE:
                if (controlNotyMiShade != null) {
                    controlNotyMiShade.show(listenerAnim);
                }
                break;
        }
    }

    private void adjustNotyCenterViewOS(int typeEdge) {
        int maxAnimationNoty = height;
        notyCenterViewOS.setTranslationX(0);
        if (typeEdge == Constant.EDGE_TOP) {
            notyCenterViewOS.setTranY(-maxAnimationNoty);
        } else {
            notyCenterViewOS.setTranY(0);
        }
    }

    private void handleVibration(int typeEdge) {
        boolean vibrate = false;
        switch (typeEdge) {
            case Constant.EDGE_TOP:
                vibrate = tinyDB.getBoolean(Constant.VIBRATOR_EDGE_TOP, Constant.VALUE_DEFAULT_VIBRATOR);
                break;
            case Constant.EDGE_LEFT:
                vibrate = tinyDB.getBoolean(Constant.VIBRATOR_EDGE_LEFT, Constant.VALUE_DEFAULT_VIBRATOR);
                break;
            case Constant.EDGE_RIGHT:
                vibrate = tinyDB.getBoolean(Constant.VIBRATOR_EDGE_RIGHT, Constant.VALUE_DEFAULT_VIBRATOR);
                break;
            case Constant.EDGE_BOT:
                vibrate = tinyDB.getBoolean(Constant.VIBRATOR_EDGE_BOTTOM, Constant.VALUE_DEFAULT_VIBRATOR);
                break;
        }
        if (vibrate) {
            VibratorUtils.getInstance(getApplicationContext()).vibrator(VibratorUtils.TIME_DEFAULT);
        }
    }

    private void progressBlurRealTime() {
        if (resultDataMediaProjection != null && ThemeHelper.itemControl != null && ThemeHelper.itemControl.getTypeBackground().equals(Constant.REAL_TIME)) {
            setTypeRealtimeBackground(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (isResultDataMPUsed) {
                    return;
                }
            }
            capture = false;

            if (!isTypeMediaProjection()) {
                return;
            }
            startCapture();
        } else {
            setTypeRealtimeBackground(false);
            if (ThemeHelper.itemControl != null && ThemeHelper.itemControl.getTypeBackground().equals(Constant.REAL_TIME)) {
                new Handler().postDelayed(() -> {
                    SettingUtils.intentActivityRequestPermissionRealTimeBackGround(NotyControlCenterServicev614.this, new String[]{RequestPermissionActivity.RECORD_VIDEO});
                }, 300);
            }
        }
    }

    private boolean isTypeMediaProjection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return getForegroundServiceType() == ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION;
        }
        return true;
    }

    /**
     * Check move touch to Show noty or control
     *
     * @param isShowControl is type show noty or control
     * @param event         is the event from BaseTouchView @see #BaseTouchView.onTouchEvent()
     * @param typeEdge      is the edge Top, left, right, bottom
     */
    private void onMoveTouch(boolean isShowControl, MotionEvent event, int typeEdge) {
        if (rootView != null) {
            if (checkAllowTouch) {
                if (typeEdge == Constant.EDGE_LEFT || typeEdge == Constant.EDGE_RIGHT) {
                    float xCurrent = event.getRawX();
                    float spaceSwipe = MethodUtils.dpToPx(4);
                    if (Math.abs(xCurrent - xDown) < spaceSwipe) {
                        return;
                    }
                } else if (typeEdge == Constant.EDGE_BOT || typeEdge == Constant.EDGE_TOP) {
                    float yCurrent = event.getRawY();
                    float spaceSwipe = MethodUtils.dpToPx(4);
                    if (Math.abs(yCurrent - yDown) < spaceSwipe) {
                        return;
                    }
                }
            }
            checkAllowTouch = false;
            if (isShowControl) {
                if (rootView != null && rootView.getVisibility() != View.VISIBLE) {
                    showViewNotyOrControl(true, typeEdge, false, null);
                }
            } else {
                if (typeChoose == VALUE_CONTROL_CENTER_OS) {
                    if (typeEdge == Constant.EDGE_TOP) {
                        float yCurrent = event.getRawY();
                        float y = yCurrent - yDown;
                        progressExpandNoty = -maxAnimationNoty + (int) y;
                        if (!isDispatchTouchEvent) {
                            notyCenterViewOS.setTranY(progressExpandNoty);
                        }
                        if (rootView.getVisibility() != View.VISIBLE) {
                            showViewNotyOrControl(false, typeEdge, false, null);
                        }

                    } else if (typeEdge == Constant.EDGE_LEFT || typeEdge == Constant.EDGE_RIGHT || typeEdge == Constant.EDGE_BOT) {
                        if (rootView.getVisibility() != View.VISIBLE) {
                            notyCenterViewOS.showFromEdgeLeftRight();
                            showViewNotyOrControl(false, typeEdge, false, null);
                        }
                    }
                } else if (typeChoose == VALUE_CONTROL_CENTER || typeChoose == VALUE_PIXEL || typeChoose == VALUE_SAMSUNG) {
                    if (rootView.getVisibility() != View.VISIBLE) {
                        showViewNotyOrControl(false, typeEdge, false, null);
                    }
                }
            }
        }
    }

    public void changeDispatchTouchEvent() {
        isDispatchTouchEvent = true;
        executor.execute(() -> {
            try {
                Thread.sleep(500);
                isDispatchTouchEvent = false;
            } catch (InterruptedException e) {
            }
        });

    }

    private void onUpTouch(boolean isTouchControl, int typeEdge) {
        isSwiping = false;
        if (checkAllowTouch) {
            return;
        }
        if (!isTouchControl) {
            onUpNoty(typeEdge);
        }
    }

    private void onUpNoty(int typeEdge) {
        if (typeEdge == Constant.EDGE_TOP || typeEdge == Constant.EDGE_LEFT) {
            isShowNoty = isShowNoty && progressExpandNoty > (-maxAnimationNoty + 50);
            if (progressExpandNoty > -3 * maxAnimationNoty / 4) {
                isShowNoty = true;
            }
        } else if (typeEdge == Constant.EDGE_BOT || typeEdge == Constant.EDGE_RIGHT) {
            isShowNoty = isShowNoty && progressExpandNoty < (-maxAnimationNoty - 50);
            if (progressExpandNoty < -3 * maxAnimationNoty / 4) {
                isShowNoty = true;
            }
        }
        if (isShowNoty) {
            if (typeChoose == VALUE_CONTROL_CENTER_OS) {
                if (typeEdge == Constant.EDGE_TOP) {
                    notyCenterViewOS.animationShowTopBot(null);
                }
            }
        } else {
            if (typeChoose == VALUE_CONTROL_CENTER_OS) {
                if (typeEdge == Constant.EDGE_TOP) {
                    if ((-notyCenterViewOS.getTranY()) == maxAnimationNoty) {
                        onNotyCenterCloseListener.closeEnd();
                    } else {
                        notyCenterViewOS.post(() -> notyCenterViewOS.animationCloseTop());
                    }
                }
            }
        }
    }

    private void hideControlCenter() {
        showStatusBarNavigation();
        if (viewDialogOpenSystem != null) {
            viewDialogOpenSystem.setVisible(false);
        }
        if (viewDialogContent != null) {
            viewDialogContent.setHide();
        }
    }

    public void closeNotyCenter() {
        showStatusBarNavigation();
    }

    private void showStatusBarNavigation() {
        if (rootView == null) {
            return;
        }

        runningOpenNotyToClickAction = false;
        closeAction(true);

        try {
            // Ẩn rootView (đảm bảo không chắn lên các view khác)
            rootView.setVisibility(View.GONE);
            rootView.requestLayout();
            isShowNoty = false;
            NotyManager.INSTANCE.clearNotyNow();

            if (notyCenterViewOS != null) {
                notyCenterViewOS.updateBitmapBlur();
            } else {
                reloadAllNoty();
            }
            if (controlCenterIOSView != null) {
                controlCenterIOSView.setHideViewExpand();
            }

        } catch (Exception e) {
            Timber.d(e);
        }

        // Hủy bỏ tất cả các callback chưa hoàn thành
        if (api31) {
            handlerCloseNotiSystemApi31.removeCallbacks(runnableCloseNotiSystemApi31);
        }

        handlerDataMobile.removeCallbacks(runnableData);

        allowOpenNoty = true;

        // Đảm bảo paramsManager không null
        if (paramsManager == null) {
            paramsManager = new WindowManager.LayoutParams();
        }

        // Thiết lập chiều rộng và chiều cao cho cửa sổ
        setWidthParams(paramsManager, WindowManager.LayoutParams.MATCH_PARENT, 0);

        // Cập nhật System UI visibility để đảm bảo thanh điều hướng và thanh trạng thái không bị ẩn
        rootView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | // Đảm bảo không ẩn thanh điều hướng
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Ẩn thanh điều hướng khi vuốt
        );

        try {
            windowManager.updateViewLayout(rootView, paramsManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Đảm bảo cài đặt các flag trong paramsManager
        paramsManager.flags = flagManager;
        if (rootView.getParent() != null) {
            try {
                windowManager.updateViewLayout(rootView, paramsManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Kiểm tra loại hình nền và thiết lập lại nền nếu cần
        if (isTypeRealtimeBackground()) {
            // Đặt nền trong suốt cho các loại nền thời gian thực
            BlurBackground.getInstance().setBitmapTrans();

            if (typeChoose == VALUE_CONTROL_CENTER_OS) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (notyCenterViewOS != null) {
                        notyCenterViewOS.setBgNew();
                        notyCenterViewOS.updateBitmapBlur();
                    }
                    if (controlCenterIOSView != null) {
                        controlCenterIOSView.setBgNew();
                    }
                });
            } else if (typeChoose == VALUE_CONTROL_CENTER || typeChoose == VALUE_PIXEL || typeChoose == VALUE_SAMSUNG) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (controlCenterView != null) {
                        controlCenterView.setUpBg();
                    }
                    if (notyCenterView != null) {
                        notyCenterView.setUpBg();
                    }
                });
            } else if (typeChoose == VALUE_SHADE) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (controlNotyMiShade != null) {
                        controlNotyMiShade.setUpBg();
                    }
                });
            }
        }
    }

    private void hideStatusBarNavigationBar() {
        if (api31) {
            handlerCloseNotiSystemApi31.removeCallbacks(runnableCloseNotiSystemApi31);
            handlerCloseNotiSystemApi31.postDelayed(runnableCloseNotiSystemApi31, 500);
        }

        hideViewBgBlackOpenNotySystem();
        showRootView();

        setWindowLayoutParams();
        setSystemUiVisibility();


    }

    private void hideViewBgBlackOpenNotySystem() {
        if (viewBgBlackOpenNotySystem != null) {
            viewBgBlackOpenNotySystem.setVisibility(View.GONE);
        }
    }

    private void showRootView() {
        try {
            rootView.setVisibility(View.VISIBLE);
            isShowNoty = true;
            if (isFirstSetView) {
                isFirstSetView = false;
            } else {
                EventBus.getDefault().post(new EventSaveControl(Constant.EVENT_UPDATE_STATE_VIEW_CONTROL));
            }
        } catch (Exception e) {
            Timber.d(e);
        }
    }

    private void setWindowLayoutParams() {
        setWidthParams(paramsManager, WindowManager.LayoutParams.MATCH_PARENT, height);
        paramsManager.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

        try {
            windowManager.updateViewLayout(rootView, paramsManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSystemUiVisibility() {
        int flag = View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        rootView.setSystemUiVisibility(flag);
        rootView.requestLayout();
    }

    private void updateLpEdgeTop() {
        try {
            if (groupTouchTop != null) {
                windowManager.updateViewLayout(groupTouchTop, groupTouchTop.getLayoutParams());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLpEdgeLeft() {
        try {
            if (groupTouchLeft != null) {
                windowManager.updateViewLayout(groupTouchLeft, groupTouchLeft.getLayoutParams());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLpEdgeRight() {
        try {
            if (groupTouchRight != null) {
                windowManager.updateViewLayout(groupTouchRight, groupTouchRight.getLayoutParams());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLpEdgeBottom() {
        try {
            if (groupTouchBottom != null) {
                windowManager.updateViewLayout(groupTouchBottom, groupTouchBottom.getLayoutParams());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void captureScreenControl(Intent intent) {
        int action = intent.getIntExtra(EXTRA_ACTION, 0);
        if ((action == ACTION_CAPTURE || action == ACTION_RECORD)) {
            if (mgr == null) {
                mgr = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
                handlerThread = new HandlerThread(getClass().getSimpleName(), Process.THREAD_PRIORITY_BACKGROUND);
                handlerThread.start();
                handler = new Handler(handlerThread.getLooper());
            }
            resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 1337);
            resultDataMediaProjection = intent.getParcelableExtra(EXTRA_RESULT_INTENT);
            isResultDataMPUsed = false;
            if (action != ACTION_CAPTURE) {
                record();
            } else {
                startCapture();
            }
        } else if (action == ACTION_CANCEL_CAPTURE) {
            mgr = null;
            resultDataMediaProjection = null;
            if (handlerThread != null) {
                handlerThread.quit();
                handlerThread = null;
            }
        } else if (action == ACTION_STOP_RECORD) {
            stopRecord();
        }
    }


    ;

    public void processImage(final byte[] png) {
        if (!capture && ThemeHelper.itemControl.getTypeBackground().equals(Constant.REAL_TIME)) {
            capture = true;
            Bitmap bm = BitmapFactory.decodeByteArray(png, 0, png.length);
            BlurBackground.getInstance().setBitmapBgBlurRealTime(bm);
            if (typeChoose == VALUE_CONTROL_CENTER_OS) {
                if (controlCenterIOSView != null) {
                    controlCenterIOSView.post(() -> {
                        controlCenterIOSView.setBgNew();
                    });
                }
                if (notyCenterViewOS != null) {
                    notyCenterViewOS.post(() -> {
//                        Bitmap bm1 = BitmapFactory.decodeByteArray(png, 0, png.length);
//                        BlurBackground.getInstance().setBitmapBgRealTime(bm1);
                        notyCenterViewOS.setBgNew();
                        notyCenterViewOS.updateBitmapBlur();
                    });
                }
            } else if (typeChoose == VALUE_SHADE) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (controlNotyMiShade != null) {
                        controlNotyMiShade.setUpBg();
                    }

                });
            } else {
                if (controlCenterView != null) {
                    controlCenterView.post(() -> controlCenterView.setUpBg());
                }
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (notyCenterView != null) {
                        notyCenterView.setUpBg();
                    }

                });
            }
        }
        if (stateRecordScreen == ScreenRecordActionView.STATE.NONE) {
            stopCapture();
        }
    }

    public void stopCapture() {
        if (mMediaProjection != null) {
            try {
                cancelNoti();
                virtualDisplayBlur.release();
                mMediaProjection.stop();
                mMediaProjection = null;
            } catch (Exception e) {
                Timber.e(e);
            }

        }

    }

    public void startCapture() {
        showNotificationToSetBg();
        if (resultDataMediaProjection != null) {
            if (mMediaProjection == null) {
                try {
                    mMediaProjection = mgr.getMediaProjection(resultCode, resultDataMediaProjection);

                    mMediaProjectionCallback = new MediaProjectionCallback();
                    mMediaProjection.registerCallback(mMediaProjectionCallback, null);
                } catch (Exception e) {
                    return;
                }
            }
            if (it == null) {
                it = new ImageTransmogrifier(NotyControlCenterServicev614.this, handler,
                        getResources().getDisplayMetrics().widthPixels,
                        getResources().getDisplayMetrics().heightPixels +
                                MethodUtils.getNavigationBarHeight(NotyControlCenterServicev614.this));
            }
            try {
                virtualDisplayBlur = mMediaProjection.createVirtualDisplay("andshooter",
                        it.getWidth(), it.getHeight(),
                        getResources().getDisplayMetrics().densityDpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        it.getSurface(), null, handler);
                isResultDataMPUsed = true;
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    //screen record_circle_small
    private PendingIntent genPendingIntent() {
        Intent switchIntent = new Intent(this, NotyControlCenterServicev614.class);
        switchIntent.putExtra(EXTRA_ACTION, ACTION_STOP_RECORD);
        int flag = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flag = PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.getService(this, 0, switchIntent, flag);
    }

    private void showNotification() {
        NotificationCompat.Builder mNotifyBuilder;

        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = createChannel(App.mContext, false);
            mNotifyBuilder = new NotificationCompat.Builder(App.mContext, channel);
        } else {
            mNotifyBuilder = new NotificationCompat.Builder(App.mContext);
        }

        mNotifyBuilder.setAutoCancel(true);
        mNotifyBuilder.setContentTitle(getString(R.string.screen_recoding));
        mNotifyBuilder.setContentText(getString(R.string.click_to_stop_record_screen));
        mNotifyBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.color_text_content_dark));
        // Set the intent that will fire when the user taps the notification
        mNotifyBuilder.setContentIntent(genPendingIntent());

        mNotifyBuilder.setSmallIcon(R.drawable.ic_videocam_white_24dp).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_videocam_white_24dp)).setTicker("Noification is created").setPriority(Notification.PRIORITY_MAX);
        Notification foregroundNote = mNotifyBuilder.build();

        foregroundNote.defaults |= Notification.DEFAULT_LIGHTS;
        foregroundNote.defaults |= Notification.DEFAULT_SOUND;
        foregroundNote.flags = Notification.FLAG_NO_CLEAR;
        foregroundNote.when = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(ID_NOTI_RECORD, foregroundNote, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
        } else {
            startForeground(ID_NOTI_RECORD, foregroundNote);
        }
    }

    private void showNotificationToSetBg() {
        NotificationCompat.Builder mNotifyBuilder;
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = createChannel(App.mContext, true);
            mNotifyBuilder = new NotificationCompat.Builder(App.mContext, channel);
        } else {
            mNotifyBuilder = new NotificationCompat.Builder(App.mContext);
        }

        mNotifyBuilder.setAutoCancel(true);
        mNotifyBuilder.setContentTitle(getString(R.string.screen_real_time_background));
        mNotifyBuilder.setColor(ContextCompat.getColor(getApplicationContext(), R.color.color_text_content_dark));
        mNotifyBuilder.setSmallIcon(R.drawable.ic_videocam_white_24dp).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_videocam_white_24dp)).setTicker("Noification is created").setPriority(Notification.PRIORITY_MAX);
        Notification foregroundNote = mNotifyBuilder.build();
        foregroundNote.defaults |= Notification.DEFAULT_LIGHTS;
        foregroundNote.defaults |= Notification.DEFAULT_SOUND;
        foregroundNote.flags = Notification.FLAG_NO_CLEAR;
        foregroundNote.when = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(ID_NOTI_RECORD, foregroundNote, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
        } else {
            startForeground(ID_NOTI_RECORD, foregroundNote);
        }
    }

    private VirtualDisplay createVirtualDisplay() {
        if (mMediaProjection != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    mMediaProjection.registerCallback(mMediaProjectionCallback, null);
                }
                isResultDataMPUsed = true;
                return mMediaProjection.createVirtualDisplay("VideoMessageActivity", widthSupportRecordScreen, heightSupportRecordScreen, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private void cancelNoti() {
        stopForeground(true);
        if (mNotifyMgr != null) {
            mNotifyMgr.cancel(ID_NOTI_RECORD);
        }
    }

    public void stopRecord() {
        if (virtualDisplayRecordVideo == null || mMediaRecorder == null) {
            return;
        }
        virtualDisplayRecordVideo.release();
        destroyMediaProjection();
        if (!isOnStop) {
            try {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        cancelNoti();


        ScreenRecordActionView screenRecordActionView = ScreenRecordActionView.getInstance();
        if (screenRecordActionView != null) {
            screenRecordActionView.setIconStopRecord();
        }


        ScreenRecordTextView screenRecordTextView = ScreenRecordTextView.getInstance();
        if (screenRecordTextView != null) {
            screenRecordTextView.setIconStopRecord();
        }

        MediaScannerConnection.scanFile(this, new String[]{currentFileRecorder}, null, (path, uri) -> {
        });

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentFileRecorder));
            intent.setDataAndType(Uri.parse(currentFileRecorder), "video/mp4");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Timber.e("hachung Exception:" + e);
            Timber.e(e);
        }

    }

    private void destroyMediaProjection() {
        if (mMediaProjection != null) {
            try {
                mMediaProjection.unregisterCallback(mMediaProjectionCallback);
                mMediaProjection.stop();
                mMediaProjection = null;
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void shareScreen() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        initRecorder();
        virtualDisplayRecordVideo = createVirtualDisplay();
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.start();
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    private void initRecorder() {
        createFolder();
        try {
            WidthHeightScreen widthHeightScreen = App.widthHeightScreenCurrent;
            heightSupportRecordScreen = widthHeightScreen.h;
            widthSupportRecordScreen = widthHeightScreen.w;
            int videoBitRate = 3000000;
            int frameRate = 24;
            int audioSamplingRate = 44100;
            int audioEncodingBitRate = 96000;

            // Use the same size for recording profile.
            try {
                CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                heightSupportRecordScreen = profile.videoFrameWidth;
                widthSupportRecordScreen = profile.videoFrameHeight;
                videoBitRate = profile.videoBitRate;
                frameRate = profile.videoFrameRate;
                audioSamplingRate = profile.audioSampleRate;
                audioEncodingBitRate = profile.audioBitRate;

                if (frameRate > 24) {
                    frameRate = 24;
                }
            } catch (Exception e) {
                Timber.e(e);
            }
            if (mMediaRecorder == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    mMediaRecorder = new MediaRecorder(getApplicationContext());
                } else {
                    mMediaRecorder = new MediaRecorder();
                }

            }
            mMediaRecorder.reset();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

            mMediaRecorder.setVideoSize(widthSupportRecordScreen, heightSupportRecordScreen);
            mMediaRecorder.setVideoFrameRate(frameRate);
            currentFileRecorder = pathFolderExectCurrent + "/video_record_" + Calendar.getInstance().getTimeInMillis() + ".mp4";
            mMediaRecorder.setOutputFile(currentFileRecorder);
            mMediaRecorder.setAudioSamplingRate(audioSamplingRate);
            mMediaRecorder.setAudioEncodingBitRate(audioEncodingBitRate);

            mMediaRecorder.setVideoEncodingBitRate(videoBitRate);

            int rotation = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mMediaRecorder.setOrientationHint(orientation);
            mMediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(26)
    private synchronized String createChannel(Context context, boolean isSetBackground) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = "service is running ";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel(isSetBackground ? CHANNEL_ID_BG : CHANNEL_ID, name, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        }
        return CHANNEL_ID;
    }

    private void createFolder() {
        //pathFolderExectCurrent = Environment.getExternalStorageDirectory().getAbsolutePath() + "/video_screen";
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString();
        File myDir = new File(root + File.separator + Constant.RECORD_SCREEN);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        pathFolderExectCurrent = myDir.getAbsolutePath();
    }

    public void record() {
        try {
            setStatusRecordScreen(ScreenRecordActionView.STATE.RECORD);
            showNotification();
            if (!isTypeMediaProjection()) {
                return;
            }
            isOnStop = false;
            mMediaProjectionCallback = new MediaProjectionCallback();
            MediaProjectionManager mProjectionManager;
            mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            this.mMediaProjection = mProjectionManager.getMediaProjection(resultCode, resultDataMediaProjection);
            mMediaProjection.registerCallback(mMediaProjectionCallback, null);
            shareScreen();
        } catch (Exception e) {
            setStatusRecordScreen(ScreenRecordActionView.STATE.NONE);
            cancelNoti();
        }
    }

    public void setStatusRecordScreen(ScreenRecordActionView.STATE status) {
        stateRecordScreen = status;
    }

    //enbale noty, control
    public void updateEnabledTouchEdge() {
        if (groupTouchTop != null) {
            groupTouchTop.updateStateEnable();
        }
        if (groupTouchLeft != null) {
            groupTouchLeft.updateStateEnable();
        }
        if (groupTouchRight != null) {
            groupTouchRight.updateStateEnable();
        }
        if (groupTouchBottom != null) {
            groupTouchBottom.updateStateEnable();
        }
    }

    public void removeViewTouchEdge() {
        removeEdgeTop();
        removeEdgeLeft();
        removeEdgeRight();
        removeEdgeBottom();
    }

    public void removeEdgeTop() {
        if (groupTouchTop != null && groupTouchTop.getParent() != null && groupTouchTop.getWindowToken() != null) {
            try {
                Log.d("duongcv", "removeEdgeTop: ");
                windowManager.removeViewImmediate(groupTouchTop);
                groupTouchTop = null;
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    public void removeEdgeLeft() {
        if (groupTouchLeft != null && groupTouchLeft.getParent() != null && groupTouchLeft.getWindowToken() != null) {
            try {
                Log.d("duongcv", "removeEdgeLeft: ");
                windowManager.removeViewImmediate(groupTouchLeft);
                groupTouchLeft = null;
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    public void removeEdgeRight() {
        if (groupTouchRight != null && groupTouchRight.getParent() != null && groupTouchRight.getWindowToken() != null) {
            try {
                Log.d("duongcv", "removeEdgeRight: ");
                windowManager.removeViewImmediate(groupTouchRight);
                groupTouchRight = null;
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    public void removeEdgeBottom() {
        if (groupTouchBottom != null && groupTouchBottom.getParent() != null && groupTouchBottom.getWindowToken() != null) {
            try {
                Log.d("duongcv", "removeEdgeBottom: ");
                windowManager.removeViewImmediate(groupTouchBottom);
                groupTouchBottom = null;
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    public void disableWindow() {
        removeViewTouchEdge();
        if (rootView != null && rootView.getParent() != null && rootView.getWindowToken() != null) {
            try {
                Log.d("duongcv", "disableWindow: remove root view");
                windowManager.removeViewImmediate(rootView);
                rootView = null;
                count--;
            } catch (Exception e) {
                Timber.e("hoangld " + e);
            }
        }
    }

    private void addTouchAndRootView() {
        if (tinyDB.getInt(IS_ENABLE, IS_DISABLE) != DEFAULT_IS_ENABLE) return;
        addViewEdge();
        if (rootView != null && windowManager != null && rootView.getParent() == null) {
            addRootView();
        }
    }

    private void addRootView() {
        try {
            if (rootView.getParent() == null) {
                Log.d("duongcv", "addRootView: ");
                if (paramsManager == null){
                    initManager();
                }
                windowManager.addView(rootView, paramsManager);
                rootView.setVisibility(View.GONE);
                count++;
            }
            isErrorService = false;
        } catch (Exception e) {
            isErrorService = true;
            Timber.e(e);
        }

        setViewDialogOpenSystemAndViewToast();
    }

    private void addViewEdge() {
        addTouchEdgeTop();
        addTouchEdgeLeft();
        addTouchEdgeRight();
        addTouchEdgeBottom();
    }

    public void addTouchEdgeTop() {
        try {
            setUpEdgeTop();
            if (groupTouchTop != null && groupTouchTop.getParent() == null && groupTouchTop.getWindowToken() == null && groupTouchTop.getLayoutParams() != null) {
                Log.d("duongcv", "addTouchEdgeTop: ");
                windowManager.addView(groupTouchTop, groupTouchTop.getLayoutParams());
            }
        } catch (Exception e) {
            Timber.e("hachung e " + e);
        }
    }

    public void addTouchEdgeLeft() {
        try {
            setUpEdgeLeft();
            if (groupTouchLeft != null && groupTouchLeft.getParent() == null && groupTouchLeft.getWindowToken() == null  && groupTouchLeft.getLayoutParams() != null) {
                Log.d("duongcv", "addTouchEdgeLeft: ");
                windowManager.addView(groupTouchLeft, groupTouchLeft.getLayoutParams());
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void addTouchEdgeRight() {
        try {
            setUpEdgeRight();
            if (groupTouchRight != null && groupTouchRight.getParent() == null && groupTouchRight.getWindowToken() == null  && groupTouchRight.getLayoutParams() != null) {
                Log.d("duongcv", "addTouchEdgeRight: ");
                windowManager.addView(groupTouchRight, groupTouchRight.getLayoutParams());
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void addTouchEdgeBottom() {
        try {
            setUpEdgeBottom();
            if (groupTouchBottom != null && groupTouchBottom.getParent() == null && groupTouchBottom.getWindowToken() == null  && groupTouchBottom.getLayoutParams() != null) {
                Log.d("duongcv", "addTouchEdgeBottom: ");
                windowManager.addView(groupTouchBottom, groupTouchBottom.getLayoutParams());
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void setViewDialogOpenSystemAndViewToast() {
        try {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(width, height);
            lp.width = width;
            lp.height = height;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
            } else {
                lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
            }
            lp.format = PixelFormat.TRANSPARENT;
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

            if (viewDialogOpenSystem.getParent() == null) {
                ExtensionsKt.addLayout(windowManager, viewDialogOpenSystem, lp);
                viewDialogOpenSystem.setVisible(false);
            }

            if (viewDialogContent.getParent() == null) {
                ExtensionsKt.addLayout(windowManager, viewDialogContent, lp);
                viewDialogContent.setHide();
            }

            if (viewToast.getParent() == null) {
                ExtensionsKt.addLayout(windowManager, viewToast, lp);
                viewToast.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void setNewLocale(String languageCode) {
        Timber.e("hoangld ");
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        setUpTitleNotySystem();
        loadActionClickControl();
        Timber.e("hachung newConfig 2:");
        newConfig( DensityUtils.getOrientationWindowManager(this));
    }

    private void setWidthHeight() {
        height = App.widthHeightScreenCurrent.h;
        width = App.widthHeightScreenCurrent.w;
        App.widthScreenCurrent = MethodUtils.getScreenWidth();
    }

    private void registerLanguageReceiver() {
        if (languageChangeReceiver != null) {
            return;
        }

        languageChangeReceiver = new LanguageChangeReceiver(this::loadActionClickControl);

        registerReceiver(languageChangeReceiver, filterLanguageChange);

    }

    public void onChangeLanguage() {
        Timber.e("NVQ onChangeLanguage");
        String stringLanguageNew = getString(R.string.low_power_mode);
        if (stringLanguageOld.equals(stringLanguageNew)) {
            isChangeLocale = true;
            Intent i = getPackageManager().getLaunchIntentForPackage(getPackageName());
            if (i != null) {
                startActivity(i);
            }
        } else {
            isChangeLocale = false;
        }
        stringLanguageOld = getString(R.string.low_power_mode);
    }

    private void registerTimeReceiver() {
        if (timeReceiver != null) {
            return;
        }
        timeReceiver = new TimeReceiver(() -> {
            if (controlNotyMiShade != null) {
                controlNotyMiShade.setDataDateTime();
            }
            if (notyCenterViewOS != null) {
                notyCenterViewOS.setTime();
            }
            if (notyCenterView != null) {
                notyCenterView.setTextDateTime();
            }

            updateTextTitle();

        });
        IntentFilter filterTime = new IntentFilter();
        filterTime.addAction("android.intent.action.TIME_TICK");
        filterTime.addAction("android.intent.action.TIMEZONE_CHANGED");
        filterTime.addAction("android.intent.action.TIME_SET");
        try {
            registerReceiver(timeReceiver, filterTime);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void registerBatteryReceiver() {
        try {
            if (batteryBroadCast == null) {
                batteryBroadCast = new BatteryBroadCast(new BatteryBroadCast.IChangeBattery() {
                    @Override
                    public void changeBattery(boolean isCharging, int lv, float pct) {
                        battery = new Battery(lv, isCharging, pct);
                        if (controlNotyMiShade != null) {
                            controlNotyMiShade.changeBattery(isCharging, lv);
                        }
                        if (controlCenterIOSView != null) {
                            controlCenterIOSView.setChangeBattery(isCharging, lv);
                        }
                        if (notyCenterViewOS != null) {
                            notyCenterViewOS.setChangeBattery(isCharging, lv);
                        }
                        if (controlCenterView != null) {
                            controlCenterView.setChangeBattery(isCharging, lv, pct);
                        }
                    }
                });
            }
            registerReceiver(batteryBroadCast, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        } catch (Exception ignored) {
        }
    }

    private void registerActionDoNotDisturb() {

        if (actionDoNotDisturb != null) return;
        actionDoNotDisturb = new ActionDoNotDisturb((valueRegister, b, pos) -> {
            if (controlCenterView != null) {
                controlCenterView.updateActionView(valueRegister, b);
            }
            if (controlNotyMiShade != null) {
                controlNotyMiShade.updateActionView(valueRegister, b);
            }
            if (controlCenterIOSView != null) {
                controlCenterIOSView.updateDoNotDisturb(b);
            }
        }, Constant.STRING_ACTION_DO_NOT_DISTURB, this);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                registerReceiver(actionDoNotDisturb, new IntentFilter(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED));
            }
        } catch (Exception e) {
            Timber.d(e);
        }
    }

    private void registerListenPhoneState() {
        if (waveListener != null) {
            return;
        }
        waveListener = new MyPhoneStateListener(lever -> {
            leverSim = lever;
            if (notyCenterViewOS != null) {
                notyCenterViewOS.setonSignalsChange(lever);
            }
            if (controlNotyMiShade != null) {
                controlNotyMiShade.setonSignalsChange(lever);
            }
            if (controlCenterIOSView != null) {
                controlCenterIOSView.setonSignalsChange(lever);
            }
        });
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(waveListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    private void setListenNonePhoneState() {
        try {
            if (waveListener != null && telephonyManager != null) {
                telephonyManager.listen(waveListener, PhoneStateListener.LISTEN_NONE);
                waveListener = null;
                telephonyManager = null;
            }
        } catch (Exception e) {
            Timber.e("hachung :" + e);
        }

    }

    private void registerBluetooth() {
        if (bluetoothReceiver != null) {
            return;
        }
        bluetoothReceiver = new BluetoothReceiver((valueRegister, b, pos) -> {
            if (controlCenterIOSView != null) {
                controlCenterIOSView.updateViewBluetooth(b);
            }
            if (controlCenterView != null) {
                controlCenterView.updateActionViewExpand(valueRegister, b);
            }
            if (controlNotyMiShade != null) {
                controlNotyMiShade.updateActionView(valueRegister, b);
            }
        }, "Bluetooth");
        try {
            registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void setLevelDisconnect() {
        leverSim = 0;
        if (waveListener != null) {
            waveListener.setLevelDisconnect();
        }

    }

    private void registerLocation() {
        if (actionLocationBroadcastReceiver != null) {
            return;
        }
        actionLocationBroadcastReceiver = new ActionLocationBroadcastReceiver(new CallBackUpdateUi() {


            @Override
            public void stage(String valueRegister, boolean b, int pos) {
                if (controlCenterIOSView != null) {
                    controlCenterIOSView.updateViewLocation(b);
                }
                if (controlCenterView != null) {
                    controlCenterView.updateActionView(valueRegister, b);
                }
                if (controlNotyMiShade != null) {
                    controlNotyMiShade.updateActionView(valueRegister, b);
                }
            }
        }, "Location", this);
        try {
            registerReceiver(actionLocationBroadcastReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void registerLowPowerMode() {
        //power mode
        if (lowPowerModeChange != null) {
            return;
        }
        lowPowerModeChange = new LowPowerModeChangeBroadcast(turnOn -> {
            if (controlCenterIOSView != null) {
                controlCenterIOSView.setStatesLowPower(turnOn);
            }
            if (controlCenterView != null) {
                controlCenterView.updateActionView(Constant.STRING_ACTION_BATTERY, turnOn);
            }
            if (controlNotyMiShade != null) {
                controlNotyMiShade.updateActionView(Constant.STRING_ACTION_BATTERY, turnOn);
            }
        });

        IntentFilter mFilter = new IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
        try {
            registerReceiver(lowPowerModeChange, mFilter);

        } catch (Exception e) {
            Timber.e(e);
        }

    }

    private void registerBrightness() {
        if (contentObBrightness != null) {
            return;
        }
        contentObBrightness = new ContentObBrightness(new Handler(), () -> {
            Timber.e("hachung updateProcessBrightness:");
            if (controlCenterIOSView != null) {
                controlCenterIOSView.updateProcessBrightness();
            } else if (controlNotyMiShade != null) {
                controlNotyMiShade.updateProcessBrightness();
            } else if (controlCenterView != null) {
                controlCenterView.updateProcessBrightness();
            }
        });
        getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, contentObBrightness);

    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public void startWatchHome() {
//        pressedHomeReceiver = new PressedHomeReceiver();
//        IntentFilter mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        try {
//            registerReceiver(pressedHomeReceiver, mFilter);
//        } catch (Exception e) {
//            Timber.e(e);
//        }
    }

    public void stopWatchHome() {
//        if (pressedHomeReceiver != null) {
//            try {
//                unregisterReceiver(pressedHomeReceiver);
//            } catch (Exception e) {
//                Timber.e(e);
//            }
//        }
    }

    private void registerSimStateChange() {

        hasSimCard = SettingUtils.hasSimCard(this);

        if (simChangeBroadcastReceiver != null) {
            return;
        }
        simChangeBroadcastReceiver = new SimChangeBroadcastReceiver();
        IntentFilter mFilter = new IntentFilter("android.intent.action.SIM_STATE_CHANGED");
        try {
            registerReceiver(simChangeBroadcastReceiver, mFilter);

        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void registerAirplane() {
        if (actionAirplaneModeChange != null) {
            return;
        }
        actionAirplaneModeChange = new ActionAirplaneModeChange(new CallBackUpdateUi() {


            @Override
            public void stage(String valueRegister, boolean b, int pos) {
                setAirPlaneToView(b);
                if (controlCenterView != null) {
                    controlCenterView.updateActionView(valueRegister, b);
                    controlCenterView.updateActionDataView(b);
                }
                if (controlNotyMiShade != null) {
                    controlNotyMiShade.updateActionView(valueRegister, b);
                    controlNotyMiShade.updateActionView(Constant.STRING_ACTION_DATA_MOBILE, b);
                }
            }
        }, Constant.STRING_ACTION_AIRPLANE_MODE);
        try {
            registerReceiver(actionAirplaneModeChange, new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAirPlaneToView(boolean airPlaneEnabled) {
        this.airPlaneModeEnabled = airPlaneEnabled;
        if (notyCenterViewOS != null) {
            notyCenterViewOS.updatePlaneMode(airPlaneEnabled);
        }
        if (controlCenterIOSView != null) {
            controlCenterIOSView.updateViewAirplane(airPlaneEnabled);
        }
        if (controlNotyMiShade != null) {
            controlNotyMiShade.updateViewAirplane(airPlaneEnabled);
        }
        handlerDataMobile.removeCallbacks(runnableData);
        setDataMobileToView(!airPlaneEnabled && new DataMobileUtils(this).isDataEnable());
    }

    private void registerWifi() {
        if (wifiBroadcastReceiver != null) {
            return;
        }
        wifiBroadcastReceiver = new WifiBroadcastReceiver(new CallBackUpdateUi() {


            @Override
            public void stage(String valueRegister, boolean b, int pos) {
                setWifiToView(b);
                if (controlCenterView != null) {
                    controlCenterView.updateActionViewExpand(valueRegister, b);
                }
                if (controlNotyMiShade != null) {
                    controlNotyMiShade.updateActionView(valueRegister, b);
                }
            }
        }, Constant.STRING_ACTION_WIFI);

        try {
            registerReceiver(wifiBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerHostPostReceiver() {
        if (actionWifiHostPostReceiver != null) return;
        actionWifiHostPostReceiver = new ActionWifiHostPostReceiver((valueRegister, b, pos) -> {
            if (controlNotyMiShade != null) {
                controlNotyMiShade.updateActionView(valueRegister, b);
            }
            if (controlCenterView != null) {
                controlCenterView.updateActionView(valueRegister, b);
            }

        }, Constant.STRING_ACTION_HOST_POST);
        try {
            registerReceiver(actionWifiHostPostReceiver, new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED"));

        } catch (Exception e) {
            Timber.d(e);
        }
    }

    private void setWifiToView(boolean wifiEnabled) {
        if (notyCenterViewOS != null) {
            notyCenterViewOS.updateWifiMode(wifiEnabled);
        }
        if (controlCenterIOSView != null) {
            controlCenterIOSView.updateViewWifi(wifiEnabled);
        }
        if (controlNotyMiShade != null) {
            controlNotyMiShade.updateWifi(wifiEnabled);
        }

    }

    private void registerDataMobile() {
        if (contentDataMobile != null) {
            return;
        }
        contentDataMobile = new ContentObDataMobile(new Handler(), new CallBackUpdateUi() {

            @Override
            public void stage(String valueRegister, boolean b, int pos) {
                setDataMobileToView(b);
            }
        }, "", this);

        contentDataMobile = new ContentObDataMobile(new Handler(), new CallBackUpdateUi() {


            @Override
            public void stage(String valueRegister, boolean b, int pos) {
                if (controlNotyMiShade != null) {
                    controlNotyMiShade.updateActionView(valueRegister, b);
                }
                if (controlCenterView != null) {
                    controlCenterView.updateActionViewExpand(valueRegister, b);
                }
            }
        }, "", this);

        try {
            getContentResolver().registerContentObserver(Settings.Secure.getUriFor("mobile_data"), true, contentDataMobile);

        } catch (Exception e) {
            Timber.d(e);
        }
    }

    private void setDataMobileToView(boolean enabled) {

        if (notyCenterViewOS != null) {
            notyCenterViewOS.updateDataMobileMode(enabled);
        }
        if (controlCenterIOSView != null) {
            controlCenterIOSView.updateViewDataMobile(enabled);
        }
        if (controlNotyMiShade != null) {
            controlNotyMiShade.updateDataMobile();
        }
    }    private final OnTouchViewListener onTouchViewListener = new OnTouchViewListener() {
        @Override
        public void onDown(boolean isTouchNoty, int typeEdge, MotionEvent event) {
            isSwiping = true;
            checkAllowTouch = true;
            if (typeEdge == Constant.EDGE_TOP || typeEdge == Constant.EDGE_BOT) {
                yDown = event.getRawY();
            } else {
                xDown = event.getRawX();
            }
            changeDispatchTouchEvent();

        }

        @Override
        public void onMove(boolean isTouchNoty, int typeEdge, MotionEvent event) {
            onMoveTouch(!isTouchNoty, event, typeEdge);
        }

        @Override
        public void onUp(boolean isTouchNoty, int typeEdge, MotionEvent event) {
            onUpTouch(!isTouchNoty, typeEdge);
            if (ThemeHelper.itemControl != null && ThemeHelper.itemControl.getTypeBackground().equals(Constant.REAL_TIME) && resultDataMediaProjection == null) {
                closeNotyCenter();
            }
            new Handler(Looper.getMainLooper()).postDelayed(() -> hideKeyboard(rootView), 200);
        }

    };

    public void showToast(String content) {
        if (viewToast == null) {
            return;
        }
        viewToast.setContentToast(content);
    }

    public void showDialogContent(ViewDialogContent.Listener listener) {
        if (viewDialogContent == null) {
            return;
        }
        viewDialogContent.setShow(listener);
    }

    public void openNoty(NotyModel notyModel, CallBackOpenNoty callBackOpenNoty) {
        notyCenterViewOS.clickOpenNoty(notyModel, callBackOpenNoty);
    }

    public void setLastClick(long lastClick) {
        this.lastClick = lastClick;
    }

    public boolean isDoubleClick() {
        long timeClick = System.currentTimeMillis();
        if (timeClick - lastClick >= 500) {
            lastClick = timeClick;
            return false;
        }
        return true;
    }

    public boolean allowClickAction() {
        return allowOpenNoty && !runningOpenNotyToClickAction;
    }

    public int getTransYNotyViewOS() {
        if (notyCenterViewOS == null) {
            return 0;
        }
        return (int) notyCenterViewOS.getTranY();
    }

    public boolean isShowNoty() {
        if (rootView != null) {
            return rootView.isShown();
        }
        return false;
    }

    private void setUpTitleNotySystem() {
        try {
            Resources resourcesForApplication = getPackageManager().getResourcesForApplication(packageSetting);
            Configuration configuration = resourcesForApplication.getConfiguration();
            configuration.locale = getResources().getConfiguration().locale;
            resourcesForApplication.updateConfiguration(configuration, null);
            this.titleNotySystemUi = resourcesForApplication.getString(resourcesForApplication.getIdentifier("accessibility_desc_notification_shade", "string", packageSetting));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.titleNotySystemUi == null) {
            this.titleNotySystemUi = "Notification shade.";
        }
    }

    @Subscribe
    public void CustomEdgeEvent(EventCustomEdge intent) {
        String action = intent.getAction();
        SettingTouchFragment.TabEdge typeEdge = intent.getTabEdge();
        switch (action) {
            case Constant.CHANGE_ENABLED_EDGE:
                switch (typeEdge) {
                    case LEFT:
                        setUpEdgeLeft();
                        break;
                    case RIGHT:
                        setUpEdgeRight();
                        break;
                    case BOTTOM:
                        setUpEdgeBottom();
                        break;
                }
                break;
            case Constant.CHANGE_COLOR_CONTROL:
                int colorTouchControl = intent.getValueInt();
                switch (typeEdge) {
                    case TOP:
                        if (groupTouchTop != null) {
                            groupTouchTop.setColorControl(colorTouchControl);
                        }
                        break;
                    case LEFT:
                        if (groupTouchLeft != null) {
                            groupTouchLeft.setColorControl(colorTouchControl);
                        }
                        break;
                    case RIGHT:
                        if (groupTouchRight != null) {
                            groupTouchRight.setColorControl(colorTouchControl);
                        }
                        break;
                    case BOTTOM:
                        if (groupTouchBottom != null) {
                            groupTouchBottom.setColorControl(colorTouchControl);
                        }
                        break;
                }
                break;
            case Constant.CHANGE_COLOR_NOTY:
                int colorTouchNoty = intent.getValueInt();
                switch (typeEdge) {
                    case TOP:
                        if (groupTouchTop != null) {
                            groupTouchTop.setColorNoty(colorTouchNoty);
                        }
                        break;
                    case LEFT:
                        if (groupTouchLeft != null) {
                            groupTouchLeft.setColorNoty(colorTouchNoty);
                        }
                        break;
                    case RIGHT:
                        if (groupTouchRight != null) {
                            groupTouchRight.setColorNoty(colorTouchNoty);
                        }
                        break;
                    case BOTTOM:
                        if (groupTouchBottom != null) {
                            groupTouchBottom.setColorNoty(colorTouchNoty);
                        }
                        break;
                }
                break;
            case Constant.CHANGE_POSITION_EDGE:
            case Constant.CHANGE_LENGTH_EDGE:
            case Constant.CHANGE_SIZE_EDGE:
                switch (typeEdge) {
                    case TOP:
                        if (groupTouchTop != null) {
                            groupTouchTop.setSizeTouch();
                            updateLpEdgeTop();
                        }
                        break;
                    case LEFT:
                        if (groupTouchLeft != null) {
                            groupTouchLeft.setSizeTouch();
                            updateLpEdgeLeft();
                        }
                        break;
                    case RIGHT:
                        if (groupTouchRight != null) {
                            groupTouchRight.setSizeTouch();
                            updateLpEdgeRight();
                        }
                        break;
                    case BOTTOM:
                        if (groupTouchBottom != null) {
                            groupTouchBottom.setSizeTouch();
                            updateLpEdgeBottom();
                        }
                        break;
                }
                break;
            case Constant.CHANGE_STATUS_EDIT_EDGE:
                isInEditSettingTouch = intent.isValueBoolean();
                if (groupTouchTop != null) {
                    groupTouchTop.setIsEdit(isInEditSettingTouch);
                }
                if (groupTouchLeft != null) {
                    groupTouchLeft.setIsEdit(isInEditSettingTouch);
                }
                if (groupTouchRight != null) {
                    groupTouchRight.setIsEdit(isInEditSettingTouch);
                }
                if (groupTouchBottom != null) {
                    groupTouchBottom.setIsEdit(isInEditSettingTouch);
                }
                break;
            case Constant.EVENT_DATA_SAVER_NOT_SUPPORT:
                showToast(getString(R.string.data_saver_not_support));
                break;
        }
    }

    public void setAutoHideNotyPanelSystemStyleShade(boolean autoHideNotyPanelSystemStyleShade) {
        this.autoHideNotyPanelSystemStyleShade = autoHideNotyPanelSystemStyleShade;
    }

    public void updateStatusNotificationAccess() {
        switch (typeChoose) {
            case VALUE_CONTROL_CENTER_OS:
                if (notyCenterViewOS != null) {
                    notyCenterViewOS.updateStatusNotificationAccess();
                }
                break;
            case VALUE_CONTROL_CENTER:
            case VALUE_PIXEL:
                if (notyCenterView != null) {
                    notyCenterView.updateStatusNotificationAccess();
                }
                break;
            case VALUE_SHADE:
                if (controlNotyMiShade != null) {
                    controlNotyMiShade.updateStatusNotificationAccess();
                }
                break;
        }
    }

    /**
     * when open noty system to click action, set notificationTimeout is 1000ms to {@link NotyControlCenterServicev614#performActionClick} valuable (return true)
     * <p>
     * After done process click action, set notificationTimeout is 0ms to listen close notification panel system. See {@link  NotyControlCenterServicev614#handingWhenShowNotySystem}
     *
     * @param runningOpenNotyToClickAction, true when start open noty system, false when done process
     */
    private void setInfoTimeout(boolean runningOpenNotyToClickAction) {
        try {
            AccessibilityServiceInfo info = getServiceInfo();
            if (info != null) {
                info.notificationTimeout = runningOpenNotyToClickAction ? 1000 : 0;
                setServiceInfo(info);
            }
        } catch (Exception e) {
            Log.e("AccessibilityService", "General Exception: " + e.getMessage());
        }
    }

    private class SnoozedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra(Constant.INTENT_NOTY_GROUP);
            if (controlNotyMiShade != null) {
                controlNotyMiShade.setNotySnoozed(key);
            }
            if (notyCenterView != null) {
                notyCenterView.setNotySnoozed(key);
            }


        }
    }

    private class DeviceUnlockedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                    if (isShowNoty()) {
                        closeNotyCenter();
                    }
                }
                if (intent.getAction().equals(Constant.ACTION_OPEN_APP)) {
                    String pka = intent.getStringExtra(Constant.PACKAGE_NAME_APP_OPEN);
                    if (notyCenterViewOS != null) {
                        notyCenterViewOS.actionOpenApp(pka, intent.getStringExtra(Constant.ID_EVENT_NEXT_UP));
                    }

                }
            }

        }
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            if (mMediaRecorder != null) {
                stopRecord();
            }
        }

    }

    public class OrientationBroadcastReceiver extends BroadcastReceiver {
        //An integer that holds the value of the orientation given by the current configuration

        @Override
        public void onReceive(Context context, Intent intent) {
            adjustFontScale(NotyControlCenterServicev614.this, false);
            //Get a handle to the Window Service
            App.setUpWidthHeightAndBitMapTransparent(context);
            changeStyleDarkLight(context);
            int orientation =  DensityUtils.getOrientationWindowManager(context);
            Timber.e("hachung newConfig 1:");
            newConfig(orientation);
            if (viewDialogOpenSystem != null) {
                viewDialogOpenSystem.setVisible(false);
            }
            if (viewDialogContent != null) {
                viewDialogContent.setHide();
            }
        }
    }

    private class PressedHomeReceiver extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY) || reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        if (isShowNoty()) {
                            closeNotyControl.screenOff();
                        }
                    }
                }
            }
        }
    }

    public class SimChangeBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Get a handle to the Window Service
            if (notyCenterViewOS != null) {
                notyCenterViewOS.updateStateSim();
            }

            if (controlCenterIOSView != null) {
                controlCenterIOSView.updateStateSim();
            }

            if (controlNotyMiShade != null) {
                controlNotyMiShade.updateStateSim();
            }

            hasSimCard = SettingUtils.hasSimCard(context);


            setDataMobileToView(hasSimCard && new DataMobileUtils(context).isDataEnable());

            if (controlCenterView != null) {
                controlCenterView.updateActionViewExpand(Constant.STRING_ACTION_DATA_MOBILE, hasSimCard && new DataMobileUtils(context).isDataEnable());
            }
            if (controlNotyMiShade != null) {
                controlNotyMiShade.updateActionView(Constant.STRING_ACTION_DATA_MOBILE, hasSimCard && new DataMobileUtils(context).isDataEnable());
            }
        }
    }




    private final CloseNotyControl closeNotyControl = new CloseNotyControl() {
        @Override
        public void screenOff() {
            //Timber.e(".");
            onControlCenterListener.onClose();
            onNotyCenterCloseListener.closeEnd();
            if (controlCenterIOSView != null) {
                controlCenterIOSView.setHideViewExpand();
            }
        }
    };


    private final Runnable runnableData = new Runnable() {
        @Override
        public void run() {
            if (dataMobileUtils != null) {
                countRunnableData++;
                setDataMobileToView(dataMobileUtils.isDataEnable());
                handlerDataMobile.postDelayed(runnableData, 1000);
                if (countRunnableData > 5) {
                    handlerDataMobile.removeCallbacks(this);
                }
            }
        }
    };


    private final Runnable runnableClickNoty = new Runnable() {
        @Override
        public void run() {
            switch (typeClickWaiting) {
                case Constant.STRING_ACTION_DATA_MOBILE:
                    setMobileDataModeNoty(iListenActionClickWaiting);
                    break;
                case Constant.DARK_MODE:
                    setDarkModeNoty(iListenActionClickWaiting);
                    break;
                case Constant.STRING_ACTION_BATTERY:
                    setLowPowerModeNoty(iListenActionClickWaiting);
                    break;
                case Constant.STRING_ACTION_AIRPLANE_MODE:
                    setAirPlaneModeNoty(iListenActionClickWaiting);
                    break;
                case Constant.STRING_ACTION_LOCATION:
                    setLocationNoty(iListenActionClickWaiting);
                    break;
                case Constant.STRING_ACTION_HOST_POST:
                    setHotspotNoty(iListenActionClickWaiting);
            }
        }
    };


    private final ControlCenterIOSView.OnControlCenterListener onControlCenterListener = new ControlCenterIOSView.OnControlCenterListener() {
        @Override
        public void onExit() {
            if (isShowNoty()) {
                closeControlCenter();
            }
//            closeControlCenter();

        }

        @Override
        public void onClose() {
            if (isShowNoty()) {
                closeControlCenter();
            }
        }
    };


    private final NotyCenterViewOS.OnNotyCenterCloseListener onNotyCenterCloseListener = () -> {
        if (isShowNoty()) {
            closeNotyCenter();
        }
    };
    BroadcastReceiver locationSwitchStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                Timber.e("NVQ locationSwitchStateReceiver+++");
            }
        }
    };


}
