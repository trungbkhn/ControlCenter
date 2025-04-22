package com.tapbi.spark.controlcenter.ui.main

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tapbi.spark.controlcenter.App
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.Constant.LAST_TIME_EDIT_THEME
import com.tapbi.spark.controlcenter.common.LiveEvent
import com.tapbi.spark.controlcenter.common.models.CustomizeControlApp
import com.tapbi.spark.controlcenter.common.models.IconNotyEvent
import com.tapbi.spark.controlcenter.common.models.MessageEvent
import com.tapbi.spark.controlcenter.common.models.ScaleViewMainEvent
import com.tapbi.spark.controlcenter.common.models.TitleMiControlChange
import com.tapbi.spark.controlcenter.common.models.Wallpaper
import com.tapbi.spark.controlcenter.data.local.SharedPreferenceHelper
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.data.model.ItemApp
import com.tapbi.spark.controlcenter.data.model.ItemControl
import com.tapbi.spark.controlcenter.data.model.ItemPeople
import com.tapbi.spark.controlcenter.data.model.ItemTurnOn
import com.tapbi.spark.controlcenter.data.model.ThemeControl
import com.tapbi.spark.controlcenter.data.repository.BackgroundRepository
import com.tapbi.spark.controlcenter.data.repository.ColorRepository
import com.tapbi.spark.controlcenter.data.repository.PackageRepository
import com.tapbi.spark.controlcenter.data.repository.ThemesRepository
import com.tapbi.spark.controlcenter.eventbus.EventUpdateFocus
import com.tapbi.spark.controlcenter.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class MainViewModel @Inject constructor(
    private val colorRepository: ColorRepository,
    private var backgroundRepository: BackgroundRepository,
) : BaseViewModel() {
    @JvmField
    var scaleViewMainLiveEvent = LiveEvent<ScaleViewMainEvent>()

    var eventSetSelect = MutableLiveData<Boolean>()

    @JvmField
    var iconNotyLiveEvent = LiveEvent<IconNotyEvent>()

    @JvmField
    var titleMiControlChangeLiveEvent = LiveEvent<TitleMiControlChange>()

    @JvmField
    var itemFocusDetail = MutableLiveData<FocusIOS>()

    @JvmField
    var itemFocusCurrentApp = MutableLiveData<FocusIOS>()

    @JvmField
    var itemFocusCurrentPeople = MutableLiveData<FocusIOS>()

    @JvmField
    var itemFocusNewAutomation = MutableLiveData<FocusIOS>()

    @JvmField
    var itemFocusNewAutomationTime = MutableLiveData<FocusIOS>()

    @JvmField
    var itemFocusNewAutomationApps = MutableLiveData<FocusIOS>()
    var itemFocusNewAutomationLocation = MutableLiveData<FocusIOS>()

    @JvmField
    var itemCreateAppFocusCurrent = MutableLiveData<FocusIOS>()

    @JvmField
    var itemCreateFocusCurrent = MutableLiveData<FocusIOS>()

    @JvmField
    var itemEditFocusCurrent = MutableLiveData<FocusIOS>()

    @JvmField
    var listItemAllowedPeople = MutableLiveData<List<ItemPeople>>()

    @JvmField
    var listItemPeopleStart = MutableLiveData<MutableList<ItemPeople>>()

    @JvmField
    var listItemAppStart = MutableLiveData<List<String>>()

    @JvmField
    var listItemAllowedApps = MutableLiveData<List<ItemApp>>()

    @JvmField
    var listFocusAdded = MutableLiveData<List<FocusIOS>>()

    @JvmField
    var listColorMutableLiveData = MutableLiveData<List<String>>()

    @JvmField
    var listIconMutableLiveData = MutableLiveData<List<String>>()

    @JvmField
    var deleteFocusMutableLiveData = MutableLiveData<Boolean>()

    @JvmField
    var listAddFocusMutableLiveData = MutableLiveData<List<FocusIOS>>()

    @JvmField
    var listAutomationMutableLiveData = MutableLiveData<List<ItemTurnOn>>()

    @JvmField
    var editItemAutomationFocus = MutableLiveData<FocusIOS>()

    @JvmField
    var itemAutomationFocus = MutableLiveData<ItemTurnOn>()
    var internetConnectedSearch = MutableLiveData<Boolean>()
    var internetConnected = MutableLiveData<Boolean>()
    var insertThemeControlDone = MutableLiveData<Boolean>()
    var liveDataSetCurrentViewPager = MutableLiveData<Boolean>()

    @JvmField
    var openNewAutomationApp = MutableLiveData<Boolean>()

    @JvmField
    var openEditAutomationApp = MutableLiveData<Boolean>()
    var messageEventLiveEvent = LiveEvent<MessageEvent>()

    var liveDataShowDialogPermissionNoty = MutableLiveData<Boolean>()

    @JvmField
    var messageEventAccessibility = LiveEvent<MessageEvent>()

    var allThemeMyCustomizationControlLiveData = MutableLiveData<MutableList<ThemeControl>>()

    var liveDataDeleteThemeCustomization = MutableLiveData<ThemeControl>()

    /*private FocusPresetRepository focusPresetRepository;*/
    @JvmField
    var eventDismissLoadingAds = LiveEvent<Boolean>()

    var currentPositionHome: MutableLiveData<Int> = MutableLiveData()

    var chooseBackGroundInStoreLiveData: MutableLiveData<Int> = MutableLiveData(null)

    fun getListFocusAdded() {
        App.ins.focusPresetRepository.listFocus.subscribe(object : SingleObserver<List<FocusIOS>> {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onSuccess(list: List<FocusIOS>) {
                App.presetFocusList = list.toMutableList()
                listFocusAdded.postValue(list)
            }

            override fun onError(e: Throwable) {}
        })
    }

    fun insertFocus(focusIOS: FocusIOS?) {
        Completable.fromRunnable { App.ins.focusPresetRepository.insertFocus(focusIOS) }
            .subscribeOn(
                Schedulers.io()
            ).subscribe(object : CompletableObserver {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onComplete() {
                    getListFocusAdded()
                    EventBus.getDefault().post(EventUpdateFocus())
                }

                override fun onError(e: Throwable) {}
            })
    }

    fun deleteFocus(focusIOS: FocusIOS?) {
        App.ins.focusPresetRepository.deleteFocus(focusIOS)
            .subscribe(object : SingleObserver<Boolean> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(aBoolean: Boolean) {
                    deleteFocusMutableLiveData.postValue(aBoolean)
                    EventBus.getDefault().post(EventUpdateFocus())
                }

                override fun onError(e: Throwable) {}
            })
    }

    fun getAllowedPeopleByName(name: String?) {
        App.ins.contactReposition.getAllowedPeople(name)
            .subscribe(object : SingleObserver<List<ItemPeople>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(itemPeople: List<ItemPeople>) {
                    listItemAllowedPeople.postValue(itemPeople)
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    fun getAllowedAppsByName(name: String?) {
        App.ins.applicationRepository.getAllowedApps(name)
            .subscribe(object : SingleObserver<List<ItemApp>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(appList: List<ItemApp>) {
                    listItemAllowedApps.postValue(appList)
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    val listColor: Unit
        get() {
            colorRepository.listColor.subscribe(object : SingleObserver<List<String>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(strings: List<String>) {
                    listColorMutableLiveData.postValue(strings)
                }

                override fun onError(e: Throwable) {}
            })
        }

    fun getListIcon(context: Context?) {
        colorRepository.getListIcon(context).subscribe(object : SingleObserver<List<String>> {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onSuccess(strings: List<String>) {
                listIconMutableLiveData.postValue(strings)
            }

            override fun onError(e: Throwable) {
            }
        })
    }

    fun getListFocusAdd(list: List<FocusIOS?>?) {
        App.ins.focusPresetRepository.getListFocusAdd(list)
            .subscribe(object : SingleObserver<List<FocusIOS>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(list: List<FocusIOS>) {
                    listAddFocusMutableLiveData.postValue(list)
                }

                override fun onError(e: Throwable) {}
            })
    }

    fun getListAutomationByFocus(name: String?) {
        App.ins.focusPresetRepository.getListTimeDefault(name)
            .subscribe(object : SingleObserver<List<ItemTurnOn>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(turnOnList: List<ItemTurnOn>) {
                    listAutomationMutableLiveData.postValue(turnOnList)
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    fun checkInternetSearch(context: Context?) {
        App.ins.mapRepository.isNetwork(context).subscribe(object : SingleObserver<Boolean> {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onSuccess(aBoolean: Boolean) {
                internetConnectedSearch.postValue(aBoolean)
            }

            override fun onError(e: Throwable) {
            }
        })
    }

    fun checkInternet(context: Context?) {
        App.ins.mapRepository.isNetwork(context).subscribe(object : SingleObserver<Boolean> {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onSuccess(aBoolean: Boolean) {
                internetConnected.postValue(aBoolean)
            }

            override fun onError(e: Throwable) {
            }
        })
    }


    var customizeControlAppLiveData = MutableLiveData<CustomizeControlApp>()
    fun getListAppForCustomize(
        context: Context?,
        customControlCurrent: Array<String?>
    ) {

        PackageRepository().getListAppCustomizeRx(context, customControlCurrent)
            .subscribe(object : SingleObserver<CustomizeControlApp> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(customizeControlApp: CustomizeControlApp) {
                    customizeControlAppLiveData.postValue(customizeControlApp)
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    var listThemes = MutableLiveData<MutableList<ThemeControl>>()


    fun getListThemes(context: Context) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler(fun(
            _: CoroutineContext,
            throwable: Throwable
        ) {
            run {
                Timber.e(throwable)
                listThemes.postValue(mutableListOf())
            }
        })) {
            listThemes.postValue(ThemesRepository.getAllListThemes(context))
        }
    }


    fun getPathBackground(idWallpaper: Int): String {
        return if (App.tinyDB.getInt(Constant.STYLE_SELECTED, Constant.LIGHT) == Constant.LIGHT) {
            "file:///android_asset/" + Constant.FOLDER_BACKGROUND_ASSETS + "/" + idWallpaper + "/" + Constant.FILE_NAME_BACKGROUND_LIGHT
        } else {
            "file:///android_asset/" + Constant.FOLDER_BACKGROUND_ASSETS + "/" + idWallpaper + "/" + Constant.FILE_NAME_BACKGROUND_DARK
        }
    }


    var wallpaperLiveData: MutableLiveData<ArrayList<Wallpaper>> =
        MutableLiveData<ArrayList<Wallpaper>>()

    fun getListWallPaperAssets(context: Context) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            run {
                Timber.e(throwable)
            }
        }) {
            wallpaperLiveData.postValue(backgroundRepository.getListBackground(context))
        }
    }


    private val _themeControlsStateFlow = MutableStateFlow<List<ThemeControl>>(emptyList())
    val themeControlsStateFlow: StateFlow<List<ThemeControl>> = _themeControlsStateFlow

    fun getThemesDatabase() {
        viewModelScope.launch {
            ThemesRepository.getAllThemeControlFlow()
                .collect { controls ->
                    _themeControlsStateFlow.value = controls
                }
        }
    }

    fun deleteThemeControl(context: Context, theme: ThemeControl) {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Timber.e(throwable)
        }

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            try {
                ThemesRepository.deleteThemeControlById(theme.id)
                ThemesRepository.deleteThemeData(context, theme.idCategory, theme.id)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun insertThemeControl(
        context: Context,
        control: ItemControl,
        preview: ConstraintLayout,
        isEditMode: Boolean = false
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (!isEditMode){
                    val idTheme = ThemesRepository.saveTheme(
                        context,
                        control,
                        preview,
                        false
                    )

                    val theme = ThemeControl(idTheme, control.idCategory, "thumb.webp")

                    ThemesRepository.insertThemeControl(theme)
                    insertThemeControlDone.postValue(true)
                } else {
                    SharedPreferenceHelper.storeLong(LAST_TIME_EDIT_THEME, System.currentTimeMillis())
                    ThemesRepository.saveTheme(
                        context,
                        control,
                        preview,
                        true
                    )

                    val theme = ThemeControl(control.id, control.idCategory, "thumb.webp")

                    ThemesRepository.insertThemeControl(theme)
                    insertThemeControlDone.postValue(true)
                }
            } catch (e: Exception) { }
        }
    }

}
