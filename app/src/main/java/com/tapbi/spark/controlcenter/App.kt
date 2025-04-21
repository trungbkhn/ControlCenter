package com.tapbi.spark.controlcenter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.ironman.trueads.admob.ControlAds
import com.ironman.trueads.admob.ControlAds.createDebugSetting
import com.ironman.trueads.applovin.ControlAdsMAX
import com.ironman.trueads.common.Common.getListHashDeviceTapbi
import com.ironman.trueads.multiads.InitMultiAdsListener
import com.ironman.trueads.multiads.MultiAdsControl
import com.orhanobut.hawk.Hawk
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.data.db.room.ControlCenterDataBase
import com.tapbi.spark.controlcenter.data.local.SharedPreferenceHelper
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.data.model.ItemApp
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn
import com.tapbi.spark.controlcenter.data.repository.ApplicationRepository
import com.tapbi.spark.controlcenter.data.repository.ContactReposition
import com.tapbi.spark.controlcenter.data.repository.FocusPresetRepository
import com.tapbi.spark.controlcenter.data.repository.LanguageRepository
import com.tapbi.spark.controlcenter.data.repository.MapRepository
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository
import com.tapbi.spark.controlcenter.data.repository.TimeRepository
import com.tapbi.spark.controlcenter.feature.controlios14.manager.SuggestAppManager
import com.tapbi.spark.controlcenter.feature.controlios14.model.ControlCustomize
import com.tapbi.spark.controlcenter.feature.controlios14.model.InfoSystem
import com.tapbi.spark.controlcenter.service.FocusUtils
import com.tapbi.spark.controlcenter.utils.ActivityCallBack
import com.tapbi.spark.controlcenter.utils.LocaleUtils
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.MyDebugTree
import com.tapbi.spark.controlcenter.utils.StringAction
import com.tapbi.spark.controlcenter.utils.TimeUtils
import com.tapbi.spark.controlcenter.utils.TinyDB
import com.tapbi.spark.controlcenter.utils.WidthHeightScreen
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONException
import timber.log.Timber
import timber.log.Timber.Forest.plant
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltAndroidApp
class App : Application() {
    private val stringAction = StringAction()

    @Inject
    lateinit var focusPresetRepository: FocusPresetRepository

    @Inject
    lateinit var applicationRepository: ApplicationRepository

    @Inject
    lateinit var timeRepository: TimeRepository

    @Inject
    lateinit var mapRepository: MapRepository

    @Inject
    lateinit var languageRepository: LanguageRepository

    @Inject
    lateinit var contactReposition: ContactReposition

    @Inject
    lateinit var controlCenterDataBase: ControlCenterDataBase

//    @Inject
//    lateinit var themeHelper: ThemeHelper

//    @Inject
//    lateinit var themesRepository: ThemesRepository

    @Inject
    lateinit var sharedPreferenceHelper: SharedPreferenceHelper

    @JvmField
    var timeRequestPermission: Long = 0

    @JvmField
    var focusUtils: FocusUtils? = null
    override fun onCreate() {
        super.onCreate()
        ins = this
        initLog()
        if (BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        } else {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        }
        statusBarHeight = MethodUtils.getStatusBarHeight(this)
        tinyDB = TinyDB(this)
        mContext = this
        setUpWidthHeightAndBitMapTransparent(this)
        Hawk.init(this).build()
        registerActivityLifecycleCallbacks(ActivityCallBack())
        widthScreenCurrent = MethodUtils.getScreenWidth()
        setActionControl()
        RxJavaPlugins.setErrorHandler { e: Throwable ->
            var e = e
            if (e is UndeliverableException) {
                e = e.cause!!
            }
        }
        focusUtils = FocusUtils(this)
        SuggestAppManager.getInstance().loadSuggestAppAndAlarm(this)
        ThemeHelper.getThemeCurrentApply(mContext)
        ThemesRepository.updateThemesWithCategory6000(mContext)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleUtils.applyLocale(this)
    }

    private fun initLog() {
        if (BuildConfig.DEBUG) {
            plant(MyDebugTree())
        }
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).roundToInt()
    }

    fun initAds(activity: Activity, initMultiAdsListener: InitMultiAdsListener?) {
        val list: List<String> = ArrayList(getListHashDeviceTapbi(this))
        createDebugSetting(activity.applicationContext, BuildConfig.DEBUG, true, list)
        ControlAds.setupUnitIdAdmobWaterFall(
            this,
            resources.getStringArray(R.array.admob_ads_open_id),
            resources.getStringArray(R.array.admob_interstitial_id),
            null
        )
        ControlAdsMAX.setupAds(
            this,
            resources.getStringArray(R.array.applovin_ads_open_id),
            resources.getStringArray(R.array.applovin_interstitial_id),
            null
        )
        MultiAdsControl.initAdsWithConsent(
            activity,
            BuildConfig.DEBUG,
            getString(R.string.ironsrc_app_key),
            false,
            initMultiAdsListener
        )
    }

    private fun setActionControl() {
        if (tinyDB.getString(Constant.ACTION_Mi_SELECT) == "" || tinyDB.getString(Constant.ACTION_SHADE_SELECT) == "" || !tinyDB.getBoolean(
                Constant.UPDATE_ACTION_V6_1_3, false
            )
        ) {
            addAction()
            updateVersionCodeCurrentToDb()
        } else if (tinyDB.getInt(Constant.VERSION_CODE, 1) != BuildConfig.VERSION_CODE) {
            updateIconActionNotyMiAndShade()
        } else {
            updateVersionCodeCurrentToDb()
        }
//        if (tinyDB.getString(Constant.ICON_ACTION_SELECT) != null && tinyDB.getString(
//                Constant.ICON_ACTION_SELECT
//            ) == "ic_apple"
//        ) {
//            tinyDB.putString(Constant.ICON_ACTION_SELECT, Constant.ICON_ACTION_DEFAULT)
//        }
    }

    private fun addAction() {
        val infoSystems = stringAction.addString(resources)

        val infoMi = infoSystems.subList(0, 16)
        val infoShade = infoSystems.subList(0, 17)

//        // Danh sách các hành động cần loại bỏ
//        val excludedActions = listOf(
//            Constant.STRING_ACTION_DATA_MOBILE,
//            Constant.STRING_ACTION_WIFI,
//            Constant.STRING_ACTION_BLUETOOTH,
//            Constant.STRING_ACTION_AIRPLANE_MODE,
//            Constant.STRING_ACTION_DO_NOT_DISTURB,
//            Constant.STRING_ACTION_AUTO_ROTATE
//        )
//
//        // Lọc các hành động không thuộc danh sách loại bỏ
//        val infoOppo = infoSystems.filter { infoSystem ->
//            infoSystem.name !in excludedActions
//        }

        val gson = Gson()
        val jsonMi = gson.toJson(infoMi)
        val jsonShade = gson.toJson(infoShade)
//        val jsonOppo = gson.toJson(infoOppo)

        // Lưu vào TinyDB
        tinyDB.putString(Constant.ACTION_Mi_SELECT, jsonMi)
        tinyDB.putString(Constant.ACTION_SHADE_SELECT, jsonShade)
//        tinyDB.putString(Constant.ACTION_OPPO_SELECT, jsonOppo)
        tinyDB.putBoolean(Constant.UPDATE_ACTION_V6_1_3, true)
    }


    private fun updateIconActionNotyMiAndShade() {
        val listActionShade = getListInfoSystem(tinyDB.getString(Constant.ACTION_SHADE_SELECT))
        val listActionMi = getListInfoSystem(tinyDB.getString(Constant.ACTION_Mi_SELECT))
//        val listActionOPPO = getListInfoSystem(tinyDB.getString(Constant.ACTION_OPPO_SELECT))
        try {
            if (listActionShade.isEmpty() || listActionMi.isEmpty()) {
                addAction()
            } else {
                updateIconForList(listActionShade)
                updateIconForList(listActionMi)
//                updateIconForList(listActionOPPO)
                val gson = Gson()
                tinyDB.putString(Constant.ACTION_Mi_SELECT, gson.toJson(listActionMi))
                tinyDB.putString(Constant.ACTION_SHADE_SELECT, gson.toJson(listActionShade))
//                tinyDB.putString(Constant.ACTION_OPPO_SELECT, gson.toJson(listActionOPPO))
            }
        } catch (e: Exception) {
            addAction()
        }

        updateVersionCodeCurrentToDb()
    }


    private fun updateIconForList(list: MutableList<InfoSystem>) {
        for (i in list.indices) {
            list[i].icon = stringAction.getIconAction(list[i].name)
        }
    }

    private fun getListInfoSystem(values: String): MutableList<InfoSystem> {
        val listAction: MutableList<InfoSystem> = ArrayList()
        try {
            val jsonArray = JSONArray(values)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val names = jsonObject.names() ?: continue
                if (names.length() < 3) continue
                val nameKey = names.getString(0)
                val uriKey = names.getString(1)
                val iconKey = names.getString(2)

                val actionInfo = InfoSystem(
                    jsonObject.getString(nameKey),
                    "",
                    jsonObject.getString(uriKey),
                    jsonObject.getInt(iconKey)
                )
                listAction.add(actionInfo)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return listAction
    }

    private fun updateVersionCodeCurrentToDb() {
        tinyDB.putInt(Constant.VERSION_CODE, BuildConfig.VERSION_CODE)
    }

    fun setIsResetLocation(isResetLocation: Boolean) {
        Companion.isResetLocation = isResetLocation
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var ins: App

        @JvmField
        public var myScope = CoroutineScope(Dispatchers.IO)

        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context

        //  public OrientationManagerApp broadcast;
        lateinit var tinyDB: TinyDB

        @JvmField
        var widthScreenCurrent = 0


        lateinit var widthHeightScreenCurrent: WidthHeightScreen

        //devphase4 model focus
        @JvmField
        var presetFocusList = mutableListOf<FocusIOS>()

        @JvmField
        var listAppDevice: MutableList<ItemApp> = mutableListOf()

        @JvmField
        var timeAutoList: List<ItemTurnOn> = ArrayList()
        var locationAutoList: List<ItemTurnOn> = ArrayList()

        @JvmField
        var itemNextTimeAuto: ItemTurnOn? = null

        @JvmField
        var focusIOSStart: FocusIOS? = null
        var isResetLocation = true

        //    public static boolean isPauseLocation = false;
        //    public static ItemTurnOn itemNextLocationAuto;
        //    public static Location locationCurrent;
        @JvmField
        var isStartActivity = false

        @JvmField
        var isPauseApp = false

        @JvmField
        var isRegisterServiceContact = false

        //    public static boolean PauseLocation = false;
        var notyDropDownInHuawei = false

        @JvmField
        var colorFocus = ""

        //    public static String phoneSms = "";
        @JvmField
        var statusBarHeight = 0

        var included: MutableList<ControlCustomize> = mutableListOf()
        var all: MutableList<ControlCustomize> = mutableListOf()

        @JvmStatic
        fun setUpWidthHeightAndBitMapTransparent(mContext: Context) {
            widthHeightScreenCurrent = WidthHeightScreen(mContext)
        }


        //    public static void setPresetCurrentFocus() {
        //        Timber.e("hachung :");
        //        boolean isExist = false;
        //        for (FocusIOS focusIOS : presetFocusList) {
        //            if (focusIOS.getStartCurrent() || focusIOS.getStartAutoTime() || focusIOS.getStartAutoAppOpen() || focusIOS.getStartAutoLocation()) {
        //                focusIOSStart = focusIOS;
        //                isExist = true;
        //                break;
        //            }
        //        }
        //        if (!isExist) focusIOSStart = null;
        //    }
        @JvmStatic
        fun updateTimeChange() {
            if (timeAutoList.isNotEmpty()) {
                for (it in timeAutoList) {
                    if (TimeUtils.checkDayOfWeek(
                            it.monDay, it.tueDay, it.wedDay, it.thuDay, it.friDay,
                            it.satDay, it.sunDay
                        )
                    ) {
                        if (System.currentTimeMillis() > it.timeEnd) {
                            ins.focusPresetRepository.updateTimeRepeat(it)
                        }
                    }
                }
            }
        }

        @JvmStatic
        fun setItemNextTimeAuto() {
            itemNextTimeAuto = null
            val listAutoNext: MutableList<ItemTurnOn> = ArrayList()
            for (item in timeAutoList) {
                if (item.nameFocus != Constant.GAMING || item.startFocus) {
                    val currentTimeMillis = System.currentTimeMillis();
                    if (currentTimeMillis >= item.timeStart && currentTimeMillis < item.timeEnd) {
                        listAutoNext.add(item)
                    }

                }
            }
            if (listAutoNext.size > 0) {
                var tempTime = listAutoNext[0].timeStart
                itemNextTimeAuto = listAutoNext[0]
                for (item in listAutoNext) {
                    val time = listAutoNext[0].timeStart
                    if (tempTime < time) {
                        itemNextTimeAuto = item
                        tempTime = time
                    } else if (tempTime == time) {
                        if (itemNextTimeAuto!!.lastModify != null) {
                            if (item.lastModify > itemNextTimeAuto!!.lastModify) {
                                itemNextTimeAuto = item
                            }
                        }
                    }
                }
            }
        }

        @JvmStatic
        fun checkGameStart(): Boolean {
            var isExist = false
            for (item in presetFocusList) {
                if (item.startAutoAppOpen) {
                    isExist = true
                    break
                }
            }
            return isExist
        }

        @JvmStatic
        val nameFocusRunning: String
            get() = if (focusIOSStart != null) {
                focusIOSStart!!.name
            } else {
                ""
            }

        @JvmStatic
        fun setFocusStart(focusIOS: FocusIOS?) {
            focusIOSStart = focusIOS
        }
    }
}
