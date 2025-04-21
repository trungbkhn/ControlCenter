package com.tapbi.spark.controlcenter.utils;

import static android.app.ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.Surface;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyGroup;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class DensityUtils {
    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static void startOpenOtherApp(Context context, String packageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent == null) {
                return;
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static void sendPendingIntent(Context context, PendingIntent pendingIntent, String packageName) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ActivityOptions activityOptions = ActivityOptions.makeBasic();
                activityOptions.setPendingIntentBackgroundActivityStartMode(MODE_BACKGROUND_ACTIVITY_START_ALLOWED);
                pendingIntent.send(context, 0, null, null, null, null, activityOptions.toBundle());
            } else {
                pendingIntent.send();
            }
        } catch (PendingIntent.CanceledException e) {
            startOpenOtherApp(App.mContext, packageName);
            e.printStackTrace();
        }
    }

    public static boolean isAtLeastSdkVersion(int versionCode) {
        return Build.VERSION.SDK_INT >= versionCode;
    }

    public static long timeUnitToDuration(TimeUnit timeUnit, long duration) {
        switch (timeUnit) {
            case HOURS:
                return duration * 60 * 1000 * 60;

            case DAYS:
                return duration * 60 * 1000 * 60 * 24;

            case MINUTES:
            default:
                return duration * 60 * 1000;
        }
    }

    public static Bitmap scaleBitmap(Bitmap bitmap) {
        Bitmap bm = bitmap;
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            boolean isResize = false;
            if (width > height && width > getScreenWidth()) {
                height = getScreenWidth() * height / width;
                width = getScreenWidth();
                isResize = true;
            } else if (height > width && height > getScreenHeight()) {
                width = getScreenHeight() * width / height;
                height = getScreenHeight();
                isResize = true;
            }
            if (width % 2 != 0) {
                width += 1;
                isResize = true;
            }
            if (height % 2 != 0) {
                height += 1;
                isResize = true;
            }
            if (isResize) {
                bm = Bitmap.createScaledBitmap(bitmap, width, height, false);
            }
        }

        return bm;
    }

    @SuppressLint("PrivateApi")
    public static String getSystemProperty(String key) {
        try {
            @SuppressLint("PrivateApi") Class<?> props = Class.forName("android.os.SystemProperties");
            return (String) props.getMethod("get", String.class).invoke(null, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    // Serialize a single object.
    public String serializeToJson(NotyGroup myClass) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(myClass);
    }

    // Deserialize to single object.
    public NotyGroup deserializeFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, NotyGroup.class);
    }

    public static int getOrientationWindowManager(Context context) {
        try {
            //A WindowManager object that will act as a handle to the window service
            WindowManager wm = (WindowManager) App.mContext.getSystemService(Context.WINDOW_SERVICE);
            //Query the current orientation made available by the Window Service
            //The getOrientation() method is deprecated. Instead, use getRotation() when targeting Android API 8 (Android 2.2 - Froyo) or above.
            //An integer that holds the value of the orientation (in degrees) given by the window service
            int windowServOrientation = wm.getDefaultDisplay().getRotation();
            //Display the current orientation using a Toast notification
            int orientation;
            if (windowServOrientation == Surface.ROTATION_0 || windowServOrientation == Surface.ROTATION_180) {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            } else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
            return orientation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    public int getRotationWindowManager(Context context) {
        //A WindowManager object that will act as a handle to the window service
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //Query the current orientation made available by the Window Service
        //The getOrientation() method is deprecated. Instead, use getRotation() when targeting Android API 8 (Android 2.2 - Froyo) or above.
        //An integer that holds the value of the orientation (in degrees) given by the window service
        int windowServOrientation = wm.getDefaultDisplay().getRotation();

        //Display the current orientation using a Toast notification
        int rotation = 0;
        if (windowServOrientation == Surface.ROTATION_0) {
            rotation = 0;
        } else if (windowServOrientation == Surface.ROTATION_90) {
            rotation = 90;
        } else if (windowServOrientation == Surface.ROTATION_180) {
            rotation = 180;
        } else if (windowServOrientation == Surface.ROTATION_270) {
            rotation = 270;
        }
        return rotation;
    }


}
