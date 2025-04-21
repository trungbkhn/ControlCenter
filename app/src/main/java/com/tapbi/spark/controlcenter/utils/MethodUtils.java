package com.tapbi.spark.controlcenter.utils;

import static android.content.Context.LOCATION_SERVICE;
import static com.tapbi.spark.controlcenter.common.Constant.CURRENT_BACKGROUND;
import static com.tapbi.spark.controlcenter.common.Constant.REAL_TIME;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.StringSearch;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.BuildConfig;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper;
import com.tapbi.spark.controlcenter.feature.controlios14.helper.BlurBackground;
import com.tapbi.spark.controlcenter.interfaces.IListenerBackPressed;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.ui.dialog.DialogRequestPermissionWriteSetting;
import com.tapbi.spark.controlcenter.ui.policy.PolicyActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;


public class MethodUtils {
    public static final Map<String, Drawable> ICON_CACHE = new HashMap<>();

    public static Bitmap getWallPaper(Context context) {
        try {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            Drawable wallpaperDrawable;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                wallpaperDrawable = wallpaperManager.getBuiltInDrawable(App.widthHeightScreenCurrent.w, App.widthHeightScreenCurrent.h, true, 0.5f, 0.5f, WallpaperManager.FLAG_SYSTEM);
            } else {
                wallpaperDrawable = wallpaperManager.getDrawable();
            }
            if (wallpaperDrawable != null) {
                return ((BitmapDrawable) wallpaperDrawable).getBitmap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkSetBackgroundToDefault(Context context, String typeBackground) {
        return typeBackground == CURRENT_BACKGROUND && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
    }

    public static Bitmap blurImage(Context mContext, Bitmap bitmap, float radius, int ratio) {
        try {
            Bitmap mInBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / ratio, bitmap.getHeight() / ratio, true);
            Bitmap mOutBitmap = Bitmap.createBitmap(mInBitmap.getWidth(), mInBitmap.getHeight(), mInBitmap.getConfig());
            RenderScript mRenderScript = RenderScript.create(mContext);
            final Allocation input = Allocation.createFromBitmap(mRenderScript, mInBitmap);
            final Allocation output = Allocation.createTyped(mRenderScript, input.getType());

            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(mOutBitmap);
            if (mInBitmap != null && !mInBitmap.isRecycled()) {
                mInBitmap.recycle();
            }
            return mOutBitmap;
        } catch (OutOfMemoryError e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static int getStatusBarHeight(Context context) {
        try {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return context.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getNavigationBarHeight(Context context) {
        boolean isNavigationBar = context.getResources().getBoolean(context.getResources().getIdentifier("config_showNavigationBar", "bool", "android"));
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && isNavigationBar) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    @Nullable
    public static Drawable getIconFromPackageName(Context context, String pka) {
        if (ICON_CACHE.containsKey(pka)) {
            return ICON_CACHE.get(pka);
        }
        Drawable drawable;
        try {
            drawable = context.getPackageManager().getApplicationIcon(pka);
            ICON_CACHE.put(pka, drawable);
        } catch (Exception e) {
            drawable = new ColorDrawable(Color.TRANSPARENT);
        }

        return drawable;
    }

    @NonNull
    public static BitmapDrawable getIconFromPackageNameMusic(Context context, String pka) {
        Drawable drawable;
        try {
            drawable = context.getPackageManager().getApplicationIcon(pka);
            if (drawable instanceof BitmapDrawable) {
                return (BitmapDrawable) drawable;
            } else {
                Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return new BitmapDrawable(context.getResources(), bitmap);
            }
        } catch (Exception e) {
            Bitmap emptyBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            return new BitmapDrawable(context.getResources(), emptyBitmap);
        }
    }


    public static Drawable roundDrawableFromBitmap(Context context, Bitmap bitmap) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        final float roundPx = (float) bitmap.getWidth() * 0.1f;
        roundedBitmapDrawable.setCornerRadius(roundPx);
        return roundedBitmapDrawable;
    }

    public static String getTimeAgo(Context context, long timeResult) {
        final long ONE_MINUTE = 60 * 1000; // 1 phút
        final long ONE_HOUR = 60 * ONE_MINUTE; // 1 giờ
        final long ONE_DAY = 24 * ONE_HOUR; // 1 ngày

        long currentTime = System.currentTimeMillis();
        if (timeResult > currentTime || timeResult <= 0) {
            return null;
        }
        long diff = currentTime - timeResult;

        if (diff < ONE_MINUTE) {
            // Bây giờ
            return context.getString(R.string.just_now);
        } else if (diff < 2 * ONE_MINUTE) {
            // 1 phút trước
            return context.getString(R.string.a_minute_ago);
        } else if (diff < ONE_HOUR) {
            // N phút trước
            long minutes = diff / ONE_MINUTE;
            return minutes + " " + context.getString(R.string.minutes_ago);
        } else if (diff < 2 * ONE_HOUR) {
            // 1 giờ trước
            return context.getString(R.string.an_hour_ago);
        } else if (diff < ONE_DAY) {
            // N giờ trước
            long hours = diff / ONE_HOUR;
            return hours + " " + context.getString(R.string.hours_ago);
        } else if (diff < 2 * ONE_DAY) {
            // 1 ngày trước
            return context.getString(R.string.yesterday);
        } else {
            // N ngày trước
            long days = diff / ONE_DAY;
            return days + " " + context.getString(R.string.days_ago);
        }
    }

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    public static int dpToPx(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static String getAppNameFromPackageName(Context context, String packageName) {
        try {
            final PackageManager pm = context.getApplicationContext().getPackageManager();
            ApplicationInfo ai;
            try {
                ai = pm.getApplicationInfo(packageName, 0);
            } catch (Exception e) {
                ai = null;
                e.printStackTrace();
            }
            return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "(unknown)";
    }

    public static String getDateEvent(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        String formattedDate = formatter.format(calendar.getTime());
        if (Locale.getDefault().getLanguage().equals("vi")) {
            formattedDate = formattedDate.replace("AM", "SA").replace("PM", "CH");
        }
        return formattedDate;
    }

    public static void showKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_SHOWN);
    }

    public static void closeKeyboard(Context context, EditText edtSearch) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, InputMethodManager.RESULT_HIDDEN);
        inputMethodManager.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
    }

    public static boolean checkServiceNoty(Context context) {
        String enabledAppList = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        if (enabledAppList == null || context.getPackageName() == null) {
            return false;
        }
        return enabledAppList.contains(context.getPackageName());
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        /*
         * If no view is focused, an NPE will be thrown
         *
         * Maxim Dmitriev
         */
        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        ;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        view.clearFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //NVQ dialog
//    public static AlertDialog showDialogPermission(Context context, boolean setGoBack, boolean finishWhenIntentSetting, IListenerBackPressed iListenerBackPressed) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle).setMessage(context.getResources().getString(R.string.You_need_to_enable_permissions_to_use_this_feature)).setPositiveButton(context.getResources().getString(R.string.go_to_setting), (dialog, which) -> {
//            // navigate to settings
//            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
//            intent.setData(uri);
//            context.startActivity(intent);
//
//            if (finishWhenIntentSetting && context instanceof Activity) {
//                ((Activity) context).finish();
//            }
//        });
//
//        if (setGoBack) {
//            builder.setNegativeButton(context.getResources().getString(R.string.go_back), (dialog, which) -> {
//                // leave?
//                dialog.dismiss();
//            });
//            builder.setCancelable(true);
//        } else {
//            builder.setCancelable(false);
//            builder.setOnKeyListener((dialogInterface, i, keyEvent) -> {
//                if (i == KeyEvent.KEYCODE_BACK) {
//                    Timber.e("hachung KEYCODE_BACK:");
//                    // move other fragment
//                    if (iListenerBackPressed != null) {
//                        iListenerBackPressed.onBackPressed();
//                    }
//                    return true;
//                }
//                return false;
//            });
//        }
//
//        return builder.create();
//    }
    //NVQ dialog
    public static DialogRequestPermissionWriteSetting showDialogPermission(Context context, boolean setGoBack, String content, boolean finishWhenIntentSetting, IListenerBackPressed iListenerBackPressed) {
        Timber.e("NVQ DialogRequestPermissionWriteSetting :" + context);
        DialogRequestPermissionWriteSetting dialogRequestPermissionWriteSetting = new DialogRequestPermissionWriteSetting();
        if (setGoBack) {
            dialogRequestPermissionWriteSetting.setText(context.getString(R.string.You_need_to_enable_permissions_to_use_this_feature), context.getString(R.string.go_to_setting), context.getResources().getString(R.string.go_back));
            dialogRequestPermissionWriteSetting.setCancelable(true);
        } else {
            dialogRequestPermissionWriteSetting.setText(content.isEmpty() ? context.getString(R.string.You_need_to_enable_permissions_to_use_this_feature) : content, context.getString(R.string.go_to_setting), "");
            dialogRequestPermissionWriteSetting.setCancelable(false);
        }
        dialogRequestPermissionWriteSetting.setDialogListener(new DialogRequestPermissionWriteSetting.ClickListener() {
            @Override
            public void onClickOke() {
                // navigate to settings
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);

                if (finishWhenIntentSetting && context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }

            @Override
            public void onClickCancel() {
                dialogRequestPermissionWriteSetting.dismissAllowingStateLoss();
            }

            @Override
            public void onBackPress() {
                if (!setGoBack) {
                    if (iListenerBackPressed != null) {
                        iListenerBackPressed.onBackPressed();
                    }
                }
            }
        });
        return dialogRequestPermissionWriteSetting;
    }


    public static String getNameActionShowTextView(Context context, String action) {
        if (action == null) {
            return null;
        }
        String name = action;
        Resources res = context.getResources();
        switch (action) {
            case Constant.STRING_ACTION_DATA_MOBILE:
                name = res.getString(R.string.data_mobile);
                break;
            case Constant.STRING_ACTION_WIFI:
                name = res.getString(R.string.wifi);
                break;
            case Constant.STRING_ACTION_BLUETOOTH:
                name = res.getString(R.string.bluetooth);
                break;
            case Constant.STRING_ACTION_FLASH_LIGHT:
                name = res.getString(R.string.flash_light);
                break;
            case Constant.STRING_ACTION_SOUND:
                name = res.getString(R.string.sound);
                break;
            case Constant.STRING_ACTION_AIRPLANE_MODE:
                name = res.getString(R.string.airplane);
                break;
            case Constant.STRING_ACTION_DO_NOT_DISTURB:
                name = res.getString(R.string.do_not_disturb);
                break;
            case Constant.STRING_ACTION_LOCATION:
                name = res.getString(R.string.location);
                break;
            case Constant.STRING_ACTION_AUTO_ROTATE:
                name = res.getString(R.string.auto_rotate);
                break;
            case Constant.STRING_ACTION_HOST_POST:
                name = res.getString(R.string.host_post);
                break;
            case Constant.STRING_ACTION_SCREEN_CAST:
                name = res.getString(R.string.screen_cast);
                break;
            case Constant.STRING_ACTION_OPEN_SYSTEM:
                name = res.getString(R.string.open_system);
                break;
            case Constant.STRING_ACTION_SYNC:
                name = res.getString(R.string.sync);
                break;
            case Constant.STRING_ACTION_CLOCK:
                name = res.getString(R.string.clock);
                break;
            case Constant.STRING_ACTION_CAMERA:
                name = res.getString(R.string.camera);
                break;
            case Constant.STRING_ACTION_KEYBOARD_PICKER:
                name = res.getString(R.string.keyBroad_picker);
                break;
            case Constant.STRING_ACTION_BATTERY:
                name = res.getString(R.string.battery);
                break;
            case Constant.DARK_MODE:
                name = res.getString(R.string.dark_mode);
                break;
        }
        return name;
    }

    public static String getAction(Context context, String textShow) {
        String name = textShow;
        Resources res = context.getResources();

        if (textShow.equals(res.getString(R.string.data_mobile))) {
            name = Constant.STRING_ACTION_DATA_MOBILE;
        } else if (textShow.equals(res.getString(R.string.wifi))) {
            name = Constant.STRING_ACTION_WIFI;
        } else if (textShow.equals(res.getString(R.string.bluetooth))) {
            name = Constant.STRING_ACTION_BLUETOOTH;
        } else if (textShow.equals(res.getString(R.string.flash_light))) {
            name = Constant.STRING_ACTION_FLASH_LIGHT;
        } else if (textShow.equals(res.getString(R.string.sound))) {
            name = Constant.STRING_ACTION_SOUND;
        } else if (textShow.equals(res.getString(R.string.airplane))) {
            name = Constant.STRING_ACTION_AIRPLANE_MODE;
        } else if (textShow.equals(res.getString(R.string.do_not_disturb))) {
            name = Constant.STRING_ACTION_DO_NOT_DISTURB;
        } else if (textShow.equals(res.getString(R.string.location))) {
            name = Constant.STRING_ACTION_LOCATION;
        } else if (textShow.equals(res.getString(R.string.auto_rotate))) {
            name = Constant.STRING_ACTION_AUTO_ROTATE;
        } else if (textShow.equals(res.getString(R.string.host_post))) {
            name = Constant.STRING_ACTION_HOST_POST;
        } else if (textShow.equals(res.getString(R.string.screen_cast))) {
            name = Constant.STRING_ACTION_SCREEN_CAST;
        } else if (textShow.equals(res.getString(R.string.open_system))) {
            name = Constant.STRING_ACTION_OPEN_SYSTEM;
        } else if (textShow.equals(res.getString(R.string.sync))) {
            name = Constant.STRING_ACTION_SYNC;
        } else if (textShow.equals(res.getString(R.string.clock))) {
            name = Constant.STRING_ACTION_CLOCK;
        } else if (textShow.equals(res.getString(R.string.camera))) {
            name = Constant.STRING_ACTION_CAMERA;
        } else if (textShow.equals(res.getString(R.string.keyBroad_picker))) {
            name = Constant.STRING_ACTION_KEYBOARD_PICKER;
        } else if (textShow.equals(res.getString(R.string.battery))) {
            name = Constant.STRING_ACTION_BATTERY;
        } else if (textShow.equals(res.getString(R.string.dark_mode))) {
            name = Constant.DARK_MODE;
        }
        return name;
    }

    public static void changeBackgroundNotiCenter(BlurBackground.ILoadBackground iLoadBackground) {
        BlurBackground.getInstance().loadBackground(iLoadBackground);

    }

    public static boolean isTypeRealTimeBg(TinyDB tinyDB) {
        if (NotyControlCenterServicev614.getInstance() == null) {
            return false;
        }
//        int typeBg = tinyDB.getInt(Constant.BACKGROUND_SELECTED, DEFAULT);
        return ThemeHelper.itemControl.getTypeBackground().equals(REAL_TIME) && NotyControlCenterServicev614.getInstance().getResultDataMediaProjection() != null;
    }

    public static boolean containsIgnoreCase(String haystack, String needle) {
        return indexOfIgnoreCase(haystack, needle) >= 0;
    }

    public static int indexOfIgnoreCase(String haystack, String needle) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                StringSearch stringSearch = new StringSearch(needle, haystack);
                stringSearch.getCollator().setStrength(Collator.PRIMARY);
                return stringSearch.first();
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    public static boolean isEdgeToEdgeEnabled(Context context) {
        try {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("config_navBarInteractionMode", "integer", "android");
            if (resourceId > 0) {
//                0 : Navigation is displaying with 3 buttons
//
//                1 : Navigation is displaying with 2 button(Android P navigation mode)
//
//                2 : Full screen gesture(Gesture on android Q)
                return resources.getInteger(resourceId) == 2;
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    public static boolean isGPSEnabled(@javax.annotation.Nullable Context context) {
        if (context == null) {
            return false;
        }
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean packageIsGame(Context context, String packageName) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return info.category == ApplicationInfo.CATEGORY_GAME;
            } else {
                // We are suppressing deprecation since there are no other options in this API Level
                //noinspection deprecation
                return (info.flags & ApplicationInfo.FLAG_IS_GAME) == ApplicationInfo.FLAG_IS_GAME;
            }
        } catch (PackageManager.NameNotFoundException e) {
//            Log.e("Util", "Package info not found for name: " + packageName, e);
            // Or throw an exception if you want
            return false;
        }
    }

    public static boolean isHuawei() {
        return "huawei".equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isOppo() {
        return "OPPO".equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isMiUi() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            java.lang.Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

//    public static boolean checkPermissionCallListener(Context context) {
//        List<String> requiredPermissions = new ArrayList<>();
//        requiredPermissions.add(Manifest.permission.CALL_PHONE);
//        requiredPermissions.add(Manifest.permission.READ_PHONE_STATE);
//        requiredPermissions.add(Manifest.permission.READ_CALL_LOG);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            requiredPermissions.add(Manifest.permission.ANSWER_PHONE_CALLS);
//        }
//        List<String> missingPermissions = new ArrayList<>();
//        for (String permission : requiredPermissions) {
//            if (ContextCompat.checkSelfPermission(context, permission)
//                    != PackageManager.PERMISSION_GRANTED
//            ) {
//                missingPermissions.add(permission);
//            }
//        }
//        return missingPermissions.isEmpty();
//    }

    public static Rect locateView(View view) {
        Rect loc = new Rect();
        int[] location = new int[2];
        if (view == null) {
            return loc;
        }
        view.getLocationOnScreen(location);

        loc.left = location[0];
        loc.top = location[1];
        loc.right = loc.left + view.getWidth();
        loc.bottom = loc.top + view.getHeight();
        return loc;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.5;
    }

    public static void intentToCheckPermission(Context context) {
        if (App.ins.focusUtils != null) {
            App.ins.focusUtils.sendActionFocus(Constant.ACTION_CHECK_PERMISSION, "");
        }
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean focusIsEnable(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int type = mNotificationManager.getCurrentInterruptionFilter();
            return type != NotificationManager.INTERRUPTION_FILTER_ALL;
        }
        return false;
    }

    public static boolean isAccessGranted(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode;
            mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String loadJsonFromAsset(Context context, String strFileName) {

        String json = "";
        try {
            InputStream inputStream = context.getAssets().open(strFileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }

    public static void openPlayStore(Context context, String packageName) {
        if (context == null) {
            return;
        }
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException exception) {
                Toast.makeText(context, R.string.could_not_find_play_store_on_this_device, Toast.LENGTH_SHORT).show();
            }

        }
    }


    public void shareApp(Context context) {
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.link_app_share) + context.getPackageName());
            context.startActivity(shareIntent);
        } catch (Exception e) {
            Timber.d(e);
        }

    }

    public void sendCommentEmail(Context context) {
        try {
            if (context != null) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/email");
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.acc_email_feedback)});
                intent.putExtra(Intent.EXTRA_SUBJECT, "App Report " + context.getPackageName() + " - version " + BuildConfig.VERSION_NAME + " - " + android.os.Build.MANUFACTURER + " - " + android.os.Build.MODEL + " - Android " + android.os.Build.VERSION.SDK_INT);
                context.startActivity(intent);
            }
        } catch (Exception ignored) {
        }

    }

    public void goToGpLedKeyboard(Context context) {
        final String appPackageName = context.getString(R.string.package_led_keyboard);
        openPlayStore(context, appPackageName);
    }

    public void openWebApp(Context context) {
        try {
            Intent i = new Intent(context, PolicyActivity.class);
            context.startActivity(i);
        } catch (Exception e) {
            Timber.d(e);
        }
    }

    public boolean areNotificationsEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            return manager.areNotificationsEnabled();
        } else {
            return true;
        }
    }


}
