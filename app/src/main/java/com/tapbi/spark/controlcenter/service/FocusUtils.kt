package com.tapbi.spark.controlcenter.service

import android.Manifest
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.provider.ContactsContract
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.App.Companion.checkGameStart
import com.tapbi.spark.controlcenter.App.Companion.setFocusStart
import com.tapbi.spark.controlcenter.App.Companion.setItemNextTimeAuto
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.MessageEvent
import com.tapbi.spark.controlcenter.receiver.ApplicationReceiver
import com.tapbi.spark.controlcenter.service.observer.ContactObserver
import com.tapbi.spark.controlcenter.ui.splash.SplashActivity
import com.tapbi.spark.controlcenter.utils.AccessUtils.isAccessibilityServiceEnabled
import com.tapbi.spark.controlcenter.utils.AppUtils
import com.tapbi.spark.controlcenter.utils.PermissionUtils.checkPermissionReadPhoneState
import com.tapbi.spark.controlcenter.utils.SettingUtils
import com.tapbi.spark.controlcenter.utils.StringUtils.getIconDefaultApp
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class FocusUtils(private val context: Context) {
    private val ID = 111
    private val handler = Handler(Looper.getMainLooper())
    private var handlerOpenApp: Handler? = null
    private var applicationReceiver: ApplicationReceiver? = null
    private var contactObserver: ContactObserver? = null
    private var permissionGranted = true
    private val runnable: Runnable = object : Runnable {
        override fun run() {
//                boolean readPhone = PermissionUtils.INSTANCE.checkPermissionPhone(FocusUtils.this);
//                boolean notify = SettingUtils.checkPermissionNotificationListener(getApplicationContext());
            val overlay = SettingUtils.checkPermissionOverlay(context)

//                boolean access = SettingUtils.isAccessibilitySettingsOn(FocusUtils.this);
            if ( /*readPhone && notify &&*/overlay /*&& access*/) {
                if (!permissionGranted) {
                    permissionGranted = true
                    updateNoty(
                        notification(
                            context.getString(R.string.some_required_permission_is_disable),
                            focusOn
                        )
                    )
                }
            } else {
                if (permissionGranted) {
                    permissionGranted = false
                    updateNoty(
                        notification(
                            context.getString(R.string.some_required_permission_is_disable),
                            ""
                        )
                    )
                }
            }
            handler.postDelayed(this, 3000)
        }
    }
    private var packageNameCurrent = ""
    private var lastPackageName = ""
    private var activityManager: ActivityManager? = null
    private var tasks: List<RunningAppProcessInfo>? = null
    private var usageStatsManager: UsageStatsManager? = null
    private val runnableOpenApp: Runnable = object : Runnable {
        override fun run() {
            if (packageNameCurrent.isNotEmpty()) {
                lastPackageName = packageNameCurrent
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val namePackage = AppUtils.getForegroundPackage(usageStatsManager)
                if (namePackage != null) {
                    packageNameCurrent = namePackage
                    turnOnOffFocusAutoApp()
                }
            } else {
                tasks = activityManager?.runningAppProcesses
                packageNameCurrent = if (tasks?.isNotEmpty() == true) ({
                    tasks?.get(0)?.processName
                }).toString() else {
                    ""
                }
                turnOnOffFocusAutoApp()
            }
            handlerOpenApp?.postDelayed(this, 1000)
        }
    }
    private val compositeDisposable = CompositeDisposable()

    init {
        createNoty()
        startReceive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        }
        activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val handlerThread = HandlerThread("myHandlerThread")
        handlerThread.start()
        handlerOpenApp = Handler(handlerThread.looper)
    }

    fun sendActionFocus(action: String?, value: String) {
        when (action) {
            Constant.ACTION_CHECK_PERMISSION -> checkPermission()
            Constant.ACTION_CHECK_PERMISSION_CONTACT -> registerContact()
            Constant.TIME_CHANGE -> if (NotyControlCenterServicev614.getInstance() != null && NotyControlCenterServicev614.getInstance().typeChoose == Constant.VALUE_CONTROL_CENTER_OS && !checkGameStart()) {
                updateTimeChange()
            }

            Constant.PACKAGE_REMOVE -> removeApp(value)
        }
    }

    fun onCreate() {
        handlerOpenApp?.removeCallbacks(runnableOpenApp)
        handlerOpenApp?.postDelayed(runnableOpenApp, 1000)
    }

    fun onDestroy() {
        handler.removeCallbacks(runnable)
        handlerOpenApp?.removeCallbacks(runnableOpenApp)
        unRegisterService()
    }

    private fun turnOnOffFocusAutoApp() {
        if (lastPackageName != packageNameCurrent && packageNameCurrent.isNotEmpty()) {
            Completable.fromRunnable {
                var isHasAutoApp = false
                for (focusIOS in App.presetFocusList) {
                    if (focusIOS.startAutoLocation || focusIOS.startAutoTime || focusIOS.startCurrent) {
                        isHasAutoApp = true
                        break
                    }
                }
                if (packageNameCurrent == context.packageName) {
                    App.isPauseApp = false
                    if (!isHasAutoApp) turnOffFocus()
                } else {
                    val timeItem = App.ins.timeRepository.itemTimeNextAppOpenGame
                    if (timeItem != null) {
                        val itemAppGame = App.ins.applicationRepository.getItemAppOpenCurrent(
                            packageNameCurrent,
                            Constant.GAMING
                        )
                        if (itemAppGame != null && itemAppGame.start) {
                            for (item in App.presetFocusList) {
                                if (item.name == itemAppGame.nameFocus) {
                                    if (App.isPauseApp) {
                                        App.isPauseApp = false
                                        if (!isHasAutoApp) turnOffFocus()
                                    } else {
                                        App.ins.focusPresetRepository.startAutoAppOpen(item)
                                        setFocusStart(item)
                                    }
                                } else {
                                    App.ins.focusPresetRepository.turnOffItemFocusIos(item)
                                }
                            }
                        } else {
                            turnOnAutoApp(isHasAutoApp)
                        }
                        EventBus.getDefault().post(MessageEvent(Constant.UPDATE_APP_CHANGE))
                    } else {
                        turnOnAutoApp(isHasAutoApp)
                        EventBus.getDefault().post(MessageEvent(Constant.UPDATE_APP_CHANGE))
                    }
                }
            }.subscribeOn(Schedulers.io()).subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {}
                override fun onComplete() {}
                override fun onError(e: Throwable) {}
            })
        }
    }

    private fun turnOnAutoApp(isHasAutoApp: Boolean) {
        val itemAppGame =
            App.ins.applicationRepository.getItemAppOpenCurrent(packageNameCurrent, Constant.WORK)
        if (itemAppGame != null && itemAppGame.start) {
            for (item in App.presetFocusList) {
                if (item.name == itemAppGame.nameFocus) {
                    if (App.isPauseApp) {
                        App.isPauseApp = false
                        if (!isHasAutoApp) turnOffFocus()
                    } else {
                        App.ins.focusPresetRepository.startAutoAppOpen(item)
                        setFocusStart(item)
                    }
                } else {
                    App.ins.focusPresetRepository.turnOffItemFocusIos(item)
                }
            }
        } else {
            App.isPauseApp = false
            turnOffFocus()
        }
    }

    private fun turnOffFocus() {
        App.ins.focusPresetRepository.turnOffListAppOpen(context)
        //        App.isPauseLocation = false;
        App.ins.setIsResetLocation(true)
        EventBus.getDefault().post(MessageEvent(Constant.UPDATE_APP_CHANGE))
    }

    private fun createNoty() {
        checkPermission()
        checkPermissionOverlay()
    }

    private fun startReceive() {
        applicationReceiver = ApplicationReceiver()
        val intentFilterUninstall = IntentFilter()
        intentFilterUninstall.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilterUninstall.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilterUninstall.addDataScheme("package")
        context.registerReceiver(applicationReceiver, intentFilterUninstall)
        registerContact()
    }

    private fun registerContact() {
        if (ContextCompat.checkSelfPermission(
                App.mContext,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                contactObserver = ContactObserver()
                context.contentResolver.registerContentObserver(
                    ContactsContract.Contacts.CONTENT_URI,
                    true,
                    contactObserver!!
                )
                App.isRegisterServiceContact = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateTimeChange() {
        compositeDisposable.clear()
        Completable.fromRunnable {
            App.timeAutoList = App.ins.timeRepository.listTimeFocus
            App.updateTimeChange()
            setItemNextTimeAuto()
            if (App.itemNextTimeAuto != null) {
                turnOnItemWhenUpdateTimeChange()
            } else {
                App.ins.timeRepository.turnOffListAutoTime()
            }
            EventBus.getDefault().post(MessageEvent(Constant.UPDATE_TIME_CHANGE))
        }.subscribeOn(Schedulers.io()).subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onComplete() {}
            override fun onError(e: Throwable) {}
        })
    }

    private fun turnOnItemWhenUpdateTimeChange() {
        for (item in App.presetFocusList) {
            if (App.itemNextTimeAuto != null) {
                if (item.name == App.itemNextTimeAuto!!.nameFocus) {
//                    if (item.getName().equals(Constant.WORK)) {
////                        Timber.e("hachung update time work");
////                        ItemTurnOn itemTurnOn = App.ins.mapRepository.getLastLocationWorkAuto(App.locationCurrent);
////                        App.ins.mapRepository.updateItemLocation(itemTurnOn.getNameLocation(), System.currentTimeMillis());
//                        App.ins.setIsResetLocation(true);
//                    } else
                    if (item.name == Constant.GAMING) {
//                        Timber.e("hachung updateTimeChange 3");
                        if (App.itemNextTimeAuto!!.startFocus) {
                            App.ins.focusPresetRepository.startAutoTime(item, true)
                            setFocusStart(item)
                        }
                    } else {
                        Timber.e("hachung setFocusStart: ")
                        App.ins.focusPresetRepository.startAutoTime(item, true)
                        setFocusStart(item)
                    }
                } else {
//                    Timber.e("hachung updateTimeChange 4");
                    App.ins.focusPresetRepository.startAutoTime(item, false)
                }
            }
        }
    }

    private fun notification(title: String, content: String): Notification? {
        return try {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "app_active_channel",
                    "App Active",
                    NotificationManager.IMPORTANCE_LOW
                )
                channel.setShowBadge(false)
                notificationManager.createNotificationChannel(channel)
            }
            val intent1 = Intent(context, SplashActivity::class.java)
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    context,
                    0,
                    intent1,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            val channelId = NotificationCompat.Builder(
                context, "app_active_channel"
            )
            channelId.setSmallIcon(R.drawable.ic_noty).setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.ic_noty))
            channelId.setContentTitle(title)
            channelId.setContentText(content)
            channelId.setAutoCancel(false)
            channelId.setContentIntent(pendingIntent)
            channelId.setNotificationSilent()
            channelId.setOngoing(true)
            channelId.build()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun checkPermission() {
        val readPhone = checkPermissionReadPhoneState(context)
        val notify = SettingUtils.checkPermissionNotificationListener(context)
        val overlay = SettingUtils.checkPermissionOverlay(context)
        val access = isAccessibilityServiceEnabled(context)
        //Timber.e("hachung checkPermission readPhone:" + readPhone + "/notify: " + notify + "/overlay: " + overlay + "/access: " + access);
        if (readPhone && notify && overlay && access) {
            removeNoty()
            //            if (NotyControlCenterServicev614.getInstance() == null) {
//                Intent intent = new Intent(context, SplashPermissionActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
//            }
        } else {
            updateNoty(
                notification(
                    context.getString(R.string.some_required_permission_is_disable),
                    ""
                )
            )
        }
    }

    private fun updateNoty(notification: Notification?) {
        if (notification != null) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(ID, notification)
        }
    }

    private fun removeNoty() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ID)
    }

    private fun removeApp(packageName: String) {
        Completable.fromRunnable {
            App.ins.applicationRepository.deleteItemAppFocus(packageName)
            App.ins.applicationRepository.deleteItemAllowApp(packageName)
            for (app in App.listAppDevice) {
                if (app.packageName == packageName) {
                    App.listAppDevice.remove(app)
                    break
                }
            }

//            App.setPresetCurrentFocus();
            if (App.focusIOSStart != null) {
                if (App.focusIOSStart!!.name == Constant.GAMING) {
                    val listAutoGaming = App.ins.applicationRepository.getAllITemAutomationFocus(
                        Constant.GAMING
                    )
                    val isExit = listAutoGaming.size > 0
                    if (!isExit) {
                        App.ins.focusPresetRepository.updateStartItemFocusIos(App.focusIOSStart!!.name)
                        setFocusStart(null)
                        //                        MethodUtils.intentToCheckPermission(this);
                    }
                }
            }
            EventBus.getDefault().post(MessageEvent(Constant.PACKAGE_APP_REMOVE, packageName))
        }.subscribeOn(Schedulers.io()).subscribe(object : CompletableObserver {
            override fun onSubscribe(d: Disposable) {}
            override fun onComplete() {}
            override fun onError(e: Throwable) {}
        })
    }

    private fun checkPermissionOverlay() {
        handler.removeCallbacks(runnable)
        handler.post(runnable)
    }

    private fun unRegisterService() {
//        try {
//            if (timeReceiver != null) {
//                context.unregisterReceiver(timeReceiver)
//            }
//        } catch (ignored: Exception) {
//        }
        try {
            if (applicationReceiver != null) {
                context.unregisterReceiver(applicationReceiver)
            }
        } catch (ignored: Exception) {
        }
        try {
            if (contactObserver != null) {
                context.contentResolver.unregisterContentObserver(contactObserver!!)
                App.isRegisterServiceContact = false
            }
        } catch (ignored: Exception) {
        }
    }

    private val focusOn: String
        get() {
            if (App.tinyDB.getInt(
                    Constant.TYPE_NOTY,
                    Constant.VALUE_CONTROL_CENTER_OS
                ) == Constant.VALUE_CONTROL_CENTER_OS
            ) {
                if (App.tinyDB.getInt(
                        Constant.IS_ENABLE,
                        Constant.IS_DISABLE
                    ) == Constant.DEFAULT_IS_ENABLE
                ) {
                    val focusIOS = App.ins.focusPresetRepository.focusIsOn
                    //                Timber.e("hachung getFocusOn:" + focusIOS);
                    return if (focusIOS == null) {
                        setFocusStart(null)
                        ""
                    } else {
                        setFocusStart(focusIOS)
                        getIconDefaultApp(
                            focusIOS.name,
                            context
                        ) + " " + context.getString(R.string.turn_on)
                    }
                }
            }
            setFocusStart(null)
            return ""
        }
}
