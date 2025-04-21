/***
 Copyright (c) 2015 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.tapbi.spark.controlcenter.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.view.Surface;

import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import timber.log.Timber;

public class ImageTransmogrifier {
    private final int width;
    private final int height;
    private final ImageReader imageReader;
    private final NotyControlCenterServicev614 svc;
    private Bitmap latestBitmap = null;

    private Runnable runnable = this::screenShot;


    public ImageTransmogrifier(NotyControlCenterServicev614 svc, Handler handler, int x, int y) {
        this.svc = svc;
        this.width = x;
        this.height = y;

        imageReader = ImageReader.newInstance(x, y,
                PixelFormat.RGBA_8888, 2);
        imageReader.setOnImageAvailableListener(reader -> {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 100);
        }, null);
    }


    private void screenShot() {
        Image image = null;
        try {
            image = imageReader.acquireLatestImage();
            if (image != null) {
                Image.Plane[] planes = image.getPlanes();
                ByteBuffer buffer = planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;
                int bitmapWidth = width + rowPadding / pixelStride;
                if (latestBitmap != null && !latestBitmap.isRecycled()) {
                    latestBitmap.recycle();
                    latestBitmap = null;
                }

                latestBitmap = Bitmap.createBitmap(bitmapWidth, height, Bitmap.Config.ARGB_8888);
                latestBitmap.copyPixelsFromBuffer(buffer);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap cropped = Bitmap.createBitmap(latestBitmap, 0, 0, width, height);
                cropped.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                byte[] newPng = baos.toByteArray();

                cropped.recycle();

                svc.processImage(newPng);
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (image != null) {
                image.close();
            }
        }
    }

    public Bitmap createOptimizedBitmap(byte[] imageData, int width, int height) {
        Bitmap bitmap = null;
        try {
            // Giảm kích thước bitmap nếu cần thiết
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // Đọc kích thước ảnh mà không tạo bitmap
            BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

            // Tính toán inSampleSize để giảm kích thước ảnh
            options.inSampleSize = calculateInSampleSize(options, width, height);

            // Tạo bitmap với các tùy chọn đã giảm kích thước
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

            if (bitmap != null) {
                // Kiểm tra xem có cần cắt ảnh không và thực hiện
                if (bitmap.getWidth() > width || bitmap.getHeight() > height) {
                    Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle(); // Giải phóng bộ nhớ của bitmap cũ
                    }
                    bitmap = croppedBitmap; // Gán bitmap mới
                }

                // Nén bitmap nếu cần (Chất lượng 50% thay vì 100% để tiết kiệm bộ nhớ)
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] compressedBytes = baos.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.length); // Chuyển đổi lại thành bitmap từ byte[]
            }
        } catch (OutOfMemoryError e) {
            // Xử lý OutOfMemoryError
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null; // Set lại bitmap khi xảy ra lỗi
        } catch (Exception e) {
        }

        return bitmap;
    }

    // Hàm tính toán inSampleSize để giảm kích thước ảnh
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Kích thước gốc của ảnh
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Tiếp tục giảm kích thước ảnh cho đến khi chiều cao và chiều rộng nhỏ hơn yêu cầu
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public Surface getSurface() {
        return (imageReader.getSurface());
    }

    public int getWidth() {
        return (width);
    }

    public int getHeight() {
        return (height);
    }

    void close() {
        imageReader.close();
    }
}
