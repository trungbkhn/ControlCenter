package com.tapbi.spark.controlcenter.feature.controlcenter.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Build;

import androidx.annotation.NonNull;

import com.tapbi.spark.controlcenter.feature.controlcenter.cb.CallBackUpdateUi;

import timber.log.Timber;

public class FlashUtils {

    public static boolean enabled = false;
    private final String valueRegister;
    private CameraManager camManager;
    private String cameraId;
    private CallBackUpdateUi stageFlash;
    private boolean isFlashSupported = false;
    private Camera cam;
    private CameraManager.TorchCallback listenerChangeFlash;
    private int pos;

    private boolean isReceiverRegistered;

    public FlashUtils(Context context, CallBackUpdateUi stageFlash, String valueRegister, int pos) {
        this.stageFlash = stageFlash;
        this.valueRegister = valueRegister;
        this.pos = pos;
        initListenerChangeFlash();
        camManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIdList = camManager.getCameraIdList();
            if (cameraIdList.length > 0) {
                cameraId = cameraIdList[0];
            }
            //check plash is support
            isFlashSupported = checkFlashShip(context);
        } catch (Exception e) {
            Timber.e(e);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (!isReceiverRegistered) {
                    camManager.registerTorchCallback(listenerChangeFlash, null);
                    isReceiverRegistered = true;

                }
            } catch (Exception ignored) {
            }
        }
    }

    public static Boolean checkFlashShip(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    private void initListenerChangeFlash() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            listenerChangeFlash = new CameraManager.TorchCallback() {
                @Override
                public void onTorchModeUnavailable(@NonNull String cameraId) {
                    super.onTorchModeUnavailable(cameraId);
                    if (stageFlash != null) {
                        Timber.e("hachung isEnabledApi22:" + isEnabledApi22());
                        stageFlash.stage(valueRegister, isEnabledApi22(), pos);
                    }
                }

                @Override
                public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                    super.onTorchModeChanged(cameraId, enabled);
                    if (stageFlash != null) {
                        FlashUtils.enabled = enabled;
                        stageFlash.stage(valueRegister, enabled, pos);
                    }
                }
            };
        }
    }

    public void unRegisterListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && camManager != null && listenerChangeFlash != null && isReceiverRegistered) {
            try {
                camManager.unregisterTorchCallback(listenerChangeFlash);
                camManager = null;
                listenerChangeFlash = null;
                stageFlash = null;
                isReceiverRegistered = false;
            } catch (IllegalArgumentException  ignored) {
            }
        }
    }

    public void flashOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isFlashSupported) {
                try {
                    camManager.setTorchMode(cameraId, true);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        } else {
            try {
                cam = Camera.open();
                Camera.Parameters params = cam.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(params);
                if (Build.MODEL.contains("Nexus")) {
                    cam.setPreviewTexture(new SurfaceTexture(0));
                }
                cam.startPreview();
                stageFlash.stage(valueRegister, true, pos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void flashOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isFlashSupported) {
                try {
                    camManager.setTorchMode(cameraId, false);
                } catch (Exception e) {
                    Timber.d(e);
                }
            }
        } else {
            try {
                cam.stopPreview();
                cam.release();
                cam = null;
                stageFlash.stage(valueRegister, false, pos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isEnabledApi22() {
        if (cam == null) {
            return false;
        } else {
            try {
                Camera.Parameters p = cam.getParameters();
                String flashMode = p.getFlashMode();
                //DO something, if it has no value
                return flashMode != null && !flashMode.equals(Camera.Parameters.FLASH_MODE_OFF);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

}
