package com.tapbi.spark.controlcenter.feature.controlios14.helper;

import static com.tapbi.spark.controlcenter.common.Constant.CURRENT_BACKGROUND;
import static com.tapbi.spark.controlcenter.common.Constant.DEFAULT;
import static com.tapbi.spark.controlcenter.common.Constant.LIGHT;
import static com.tapbi.spark.controlcenter.common.Constant.STORE_WALLPAPER;
import static com.tapbi.spark.controlcenter.common.Constant.STYLE_SELECTED;
import static com.tapbi.spark.controlcenter.common.Constant.TRANSPARENT;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.core.content.ContextCompat;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.TinyDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class BlurBackground {
    private static BlurBackground instance;
    private TinyDB tinyDB;

    private Context context;
    private Bitmap bitmapBgBlur;
    private Bitmap bitmapBgBlurScale;
    private Bitmap bitmapBg;

    private boolean isLoaded = false;
    //    private Bitmap bmTrans;
    private int ratio = Constant.SCALE_BITMAP_BLUR;
    private String typeBackground = DEFAULT;

    private BlurBackground() {
        context = App.mContext;
        tinyDB = App.tinyDB;
//        bmTrans = Bitmap.createBitmap(App.widthHeightScreenCurrent.w, App.widthHeightScreenCurrent.h, Bitmap.Config.ARGB_8888);
//        bmTrans.eraseColor(ContextCompat.getColor(context, R.color.color_background_trans));

    }


    public static BlurBackground getInstance() {
        if (instance == null) {
            instance = new BlurBackground();
        }
        return instance;
    }

    public void setBitmapTrans() {
        //Timber.e(".");
//        if (bitmapBgBlur == null || bitmapBgBlurScale == null || bitmapBg == null) {
//            bitmapBgBlur = bmTrans;
//            setBitmapBgBlurScale();
//            bitmapBg = bmTrans;
//        }
    }

//    public Bitmap getBitmapTransparent() {
//        return bmTrans;
//    }

    public boolean backgroundLoaded() {
        return isLoaded;
    }

    private void setBackgroundLoad(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public void loadBackground(ILoadBackground iLoadBackground) {
        setBackgroundLoad(false);
        Completable.fromAction(this::loadBitmapBackgroundRx).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onComplete() {
                setBackgroundLoad(true);
                iLoadBackground.onLoadBackgroundDone();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                setBackgroundLoad(true);
                iLoadBackground.onLoadBackgroundDone();
            }
        });
    }

    private void loadBitmapBackgroundRx() {
        bitmapBg = switch (ThemeHelper.itemControl.getTypeBackground()) {
            case TRANSPARENT -> createTransparentBackground();
            case CURRENT_BACKGROUND -> getCurrentBackgroundBitmap();
            case STORE_WALLPAPER -> getBitmapBgTypeStoreWallpaper();
            default -> getBackground();
        };

        if (bitmapBg != null) {
            Bitmap bitmapBlur = MethodUtils.blurImage(context, bitmapBg, 20, Constant.SCALE_BITMAP_BLUR);
            if (bitmapBlur != null) {
                bitmapBgBlur = Bitmap.createScaledBitmap(bitmapBlur, App.widthHeightScreenCurrent.w, App.widthHeightScreenCurrent.h, true);
                // Đảm bảo bitmapBgBlur hoạt động đúng trước khi tái chế bitmapBlur
                if (!bitmapBgBlur.isRecycled()) {
                    bitmapBlur.recycle();
                }
            }
            setBitmapBgBlurScale();
        }
    }


    private Bitmap createTransparentBackground() {
        Bitmap bitmap = Bitmap.createBitmap(App.widthHeightScreenCurrent.w, App.widthHeightScreenCurrent.h, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(ContextCompat.getColor(context, R.color.color_background_item_dark));
        return bitmap;
    }

    private Bitmap getCurrentBackgroundBitmap() {
        Bitmap bitmap = MethodUtils.getWallPaper(context);
        if (bitmap == null) {
            bitmap = getBackground();
        }
        return bitmap;
    }


    public void setBitmapBgRealTime(Bitmap bitmapBg) {
        this.bitmapBg = MethodUtils.blurImage(context, bitmapBg, 20, ratio);
    }

    public void setBitmapBgBlurRealTime(Bitmap bitmapBg) {
        Timber.e("hachung setBitmapBgBlurRealTime:"+bitmapBg);
        this.bitmapBgBlur = MethodUtils.blurImage(context, bitmapBg, 20, ratio);
        setBitmapBgBlurScale();
    }

    public void setBitmapBgBlurScale() {
        this.bitmapBgBlurScale = Bitmap.createScaledBitmap(BlurBackground.getInstance().getBitmapBgBlur(), App.widthHeightScreenCurrent.w / Constant.SCALE_BITMAP_BLUR, App.widthHeightScreenCurrent.h / Constant.SCALE_BITMAP_BLUR, true);
    }

    public Bitmap getBitmapBgBlur() {
        if (bitmapBgBlur != null && !bitmapBgBlur.isRecycled()) {
            return bitmapBgBlur;
        }
        return null;
    }

    public Bitmap createBitmapBlur(int x, int y, int width, int height) {
        Bitmap bitmap = null;
        try {
            Bitmap source = getBitmapBgBlur();

            // Kiểm tra nếu kích thước vùng cắt vượt quá kích thước gốc
            int reqWidth = Math.min(width, source.getWidth());
            int reqHeight = Math.min(height, source.getHeight());

            // Scale nhỏ lại nếu kích thước lớn
            int inSampleSize = calculateInSampleSize(source.getWidth(), source.getHeight(), reqWidth, reqHeight);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = inSampleSize;

            bitmap = Bitmap.createBitmap(source, x, y, reqWidth, reqHeight);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc(); // Giải phóng bộ nhớ trước khi thử lại

            try {
                Bitmap source = getBitmapBgBlur();
                bitmap = Bitmap.createBitmap(source, x, y, width / 2, height / 2);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * Tính toán inSampleSize để giảm kích thước Bitmap tránh OutOfMemoryError
     */
    private int calculateInSampleSize(int srcWidth, int srcHeight, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (srcHeight > reqHeight || srcWidth > reqWidth) {
            final int halfHeight = srcHeight / 2;
            final int halfWidth = srcWidth / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public Bitmap getBitmapBgNotBlur() {
        if (bitmapBg != null && !bitmapBg.isRecycled()) {
            return bitmapBg;
        }
        return null;
    }


    private Bitmap getBackground() {
        try {
            return getBitmapBackground(Constant.FOLDER_THEMES_ASSETS + "/" + ThemeHelper.itemControl.getIdCategory() + "/" + ThemeHelper.itemControl.getId() + "/" + ThemeHelper.itemControl.getBackground());
        } catch (Exception e) {
            e.printStackTrace();
            Timber.e("hachung Exception:" + e);
            return null;
        }
    }

    private Bitmap getBgTypeStoreWallpaper(boolean setBlur) {
        String path = buildWallpaperPath();
        if (path == null || path.isEmpty()) {
            return null; // Trả về null nếu đường dẫn không hợp lệ
        }

        try {
            Bitmap bitmap = getBitmap(path); // Tải bitmap từ đường dẫn
            if (bitmap != null && setBlur) {
                return MethodUtils.blurImage(context, bitmap, 20, Constant.SCALE_BITMAP_BLUR); // Áp dụng hiệu ứng blur
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Trả về null nếu xảy ra lỗi
        }
    }

    private Bitmap getBitmapBgTypeStoreWallpaper() {
        String path = buildWallpaperPath();
        if (path.isEmpty()) {
            return null;
        }

        try {
            return getBitmap(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Trả về null nếu xảy ra lỗi
        }
    }

    private String buildWallpaperPath() {
        int idWallpaper = ThemeHelper.itemControl.getIdStoreWallpaper();
        String folder = Constant.FOLDER_BACKGROUND_ASSETS;
        String fileName = tinyDB.getInt(STYLE_SELECTED, LIGHT) == LIGHT
                ? Constant.FILE_NAME_BACKGROUND_LIGHT
                : Constant.FILE_NAME_BACKGROUND_DARK;
        return folder + "/" + idWallpaper + "/" + fileName;
    }


    public Bitmap getBitmapBackground(String pathInAssets) {
        File file = new File(context.getFilesDir(), pathInAssets);

        if (file.exists()) {
            return getBitmapFromFile(file);
        } else {
            return getBitmapFromAssets(pathInAssets);
        }
    }

    // Hàm đọc hình ảnh từ cache hoặc file
    private Bitmap getBitmapFromFile(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;  // Đọc thông tin kích thước trước

        // Tính toán scale factor
        int targetWidth = App.widthHeightScreenCurrent.w;
        int targetHeight = App.widthHeightScreenCurrent.h;

        try (FileInputStream fis = new FileInputStream(file)) {
            BitmapFactory.decodeStream(fis, null, options);  // Đọc kích thước ảnh
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Tính toán scale factor
        int scaleFactor = Math.min(options.outWidth / targetWidth, options.outHeight / targetHeight);
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        try (FileInputStream fis = new FileInputStream(file)) {
            Bitmap bitmap = BitmapFactory.decodeStream(fis, null, options);
            if (bitmap != null) {
                return centerCropBitmap(bitmap, targetWidth, targetHeight);  // Center crop ảnh
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm đọc hình ảnh từ assets
    private Bitmap getBitmapFromAssets(String pathInAssets) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;  // Đọc thông tin kích thước trước

        // Tính toán scale factor
        int targetWidth = App.widthHeightScreenCurrent.w;
        int targetHeight = App.widthHeightScreenCurrent.h;

        try (InputStream inputStream = context.getAssets().open(pathInAssets)) {
            BitmapFactory.decodeStream(inputStream, null, options);  // Đọc kích thước ảnh
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Tính toán scale factor
        int scaleFactor = Math.min(options.outWidth / targetWidth, options.outHeight / targetHeight);
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        try (InputStream inputStream = context.getAssets().open(pathInAssets)) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            if (bitmap != null) {
                return centerCropBitmap(bitmap, targetWidth, targetHeight);  // Center crop ảnh
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getBitmap(String path) {
        // Đường dẫn đến tệp hình ảnh trong thư mục assets
        // Đọc hình ảnh từ assets
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // Chỉ đọc thông tin kích thước, không tải hình ảnh vào bộ nhớ
        try {
            InputStream inputStream = context.getAssets().open(path);
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int targetWidth = App.widthHeightScreenCurrent.w;
        int targetHeight = App.widthHeightScreenCurrent.h;
        int scaleFactor = Math.min(options.outWidth / targetWidth, options.outHeight / targetHeight);

        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor; // Đặt scale factor
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Định dạng bitmap

        try {
            InputStream inputStream = context.getAssets().open(path);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            if (bitmap != null) {
                bitmap = centerCropBitmap(bitmap, targetWidth, targetHeight);
            }

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Hàm center crop một Bitmap
    public Bitmap centerCropBitmap(Bitmap srcBitmap, int newWidth, int newHeight) {
        float scaleWidth = ((float) newWidth) / srcBitmap.getWidth();
        float scaleHeight = ((float) newHeight) / srcBitmap.getHeight();

        float scale = Math.max(scaleWidth, scaleHeight);

        float scaledWidth = scale * srcBitmap.getWidth();
        float scaledHeight = scale * srcBitmap.getHeight();

        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;
        float right = (newWidth + scaledWidth) / 2;
        float bottom = (newHeight + scaledHeight) / 2;

        Bitmap resultBitmap = Bitmap.createBitmap(newWidth, newHeight, srcBitmap.getConfig());
        Canvas canvas = new Canvas(resultBitmap);
        RectF rectF = new RectF(left, top, right, bottom);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        canvas.drawBitmap(srcBitmap, null, rectF, paint);

        return resultBitmap;
    }

    public Bitmap getBitmapBlurScale() {
        if (bitmapBgBlurScale != null && !bitmapBgBlurScale.isRecycled()) {
            return bitmapBgBlurScale;
        }
        return null;

    }

    public String getTypeBackground() {
        return typeBackground;
    }

    public interface ILoadBackground {
        void onLoadBackgroundDone();
    }


}
