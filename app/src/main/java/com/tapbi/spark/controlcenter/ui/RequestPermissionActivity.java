package com.tapbi.spark.controlcenter.ui;

import static com.tapbi.spark.controlcenter.common.Constant.DIALOG_REQUEST_PERMISSION_WRITE_SETTING;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.tapbi.spark.controlcenter.R;
import com.tapbi.spark.controlcenter.eventbus.EventCalendar;
import com.tapbi.spark.controlcenter.feature.controlios14.manager.PermissionManager;
import com.tapbi.spark.controlcenter.service.NotyControlCenterServicev614;
import com.tapbi.spark.controlcenter.ui.base.BaseActivity;
import com.tapbi.spark.controlcenter.ui.dialog.DialogRequestPermissionWriteSetting;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

import timber.log.Timber;

public class RequestPermissionActivity extends BaseActivity {

    public static final int REQUESTCODE_PERMISSION_BG_SCREEN_REAL_TIME = 109;
    public static final int REQUEST_CODE_PERMISSION_CALENDAR = 110;
    public static final int REQUEST_CODE_PERMISSION_BLUETOOTH = 111;
    public static String PERMISSION = "permission";
    public static String TYPE_REALTIME_BG = "TYPE_REALTIME_BG";
    public static String RECORD_VIDEO = "record_video";
    public static String RECORDING = "recording_video";
    private final int REQUESTCODE_PERMISSION_RECORD_SCREEN = 102;
    private final int REQUESTCODE_PERMISSION_AUDIO_RECORD = 103;
    private String[] permission;
    private boolean isTypeRealtime = false;


    private MediaProjectionManager mProjectionManager;
    //private AlertDialog alertDialog;
    private DialogRequestPermissionWriteSetting alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permission);
        permission = getIntent().getStringArrayExtra(PERMISSION);
        if (permission == null) {
            finish();
            return;
        }
        Timber.e("hachung permission:" + permission[0]);
        isTypeRealtime = getIntent().getBooleanExtra(TYPE_REALTIME_BG, false);

        if (isTypeRealtime && permission[0].equals(RECORD_VIDEO)) {
            mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            try {
                startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUESTCODE_PERMISSION_BG_SCREEN_REAL_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (permission[0].equals(Manifest.permission.WRITE_SETTINGS)) {
            try {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage(R.string.mess_permisstion_write_setting);
//                builder.setPositiveButton(R.string.text_allow, (dialog, which) -> {
//                    SettingUtils.intentPermissionWriteSetting(RequestPermissionActivity.this);
//                    dialog.cancel();
//                    finish();
//                });
//                builder.setNegativeButton(R.string.deny, (dialog, which) -> {
//                    dialog.cancel();
//                    finish();
//                });
//                builder.setCancelable(false);
//                alertDialog = builder.show();
                alertDialog = new DialogRequestPermissionWriteSetting();
                alertDialog.setText(getString(R.string.mess_permisstion_write_setting), getString(R.string.text_allow), getString(R.string.deny));
                alertDialog.setDialogListener(new DialogRequestPermissionWriteSetting.ClickListener() {
                    @Override
                    public void onBackPress() {
                        if (alertDialog.isAdded() && !alertDialog.isStateSaved()){
                            alertDialog.dismiss();
                        }
                        RequestPermissionActivity.this.finish();
                    }

                    @Override
                    public void onClickOke() {
                        SettingUtils.intentPermissionWriteSetting(RequestPermissionActivity.this);
                        if (alertDialog.isAdded() && !alertDialog.isStateSaved()){
                            alertDialog.dismiss();
                        }
                        finish();
                    }

                    @Override
                    public void onClickCancel() {
                        if (alertDialog.isAdded() && !alertDialog.isStateSaved()){
                            alertDialog.dismiss();
                        }
                        finish();
                    }
                });
                alertDialog.setCancelable(false);
                if (!isFinishing()){
                    if (!getSupportFragmentManager().isStateSaved()) {
                        alertDialog.show(getSupportFragmentManager(), DIALOG_REQUEST_PERMISSION_WRITE_SETTING);
                    } else {
                        getSupportFragmentManager().beginTransaction()
                                .add(alertDialog, DIALOG_REQUEST_PERMISSION_WRITE_SETTING)
                                .commitAllowingStateLoss();
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (permission[0].equals(RECORD_VIDEO)) {
            mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            try {
                startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUESTCODE_PERMISSION_RECORD_SCREEN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (permission[0].equals(RECORDING)) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage(R.string.mess_stop_recording_video);
//            builder.setPositiveButton(R.string.stop_record, (dialog, which) -> {
//                NotyControlCenterServicev614.getInstance().stopRecord();
//                dialog.cancel();
//                finish();
//            });
//            builder.setNegativeButton(R.string.no, (dialog, which) -> {
//                dialog.cancel();
//                finish();
//            });
//            builder.setCancelable(false);
//            alertDialog = builder.show();

            alertDialog = new DialogRequestPermissionWriteSetting();
            alertDialog.setText(getString(R.string.mess_stop_recording_video), getString(R.string.stop_record), getString(R.string.no));
            alertDialog.setCancelable(false);
            alertDialog.setDialogListener(new DialogRequestPermissionWriteSetting.ClickListener() {
                @Override
                public void onBackPress() {
                    if (alertDialog.isAdded() && !alertDialog.isStateSaved()){
                        alertDialog.dismiss();
                    }
                    finish();
                }

                @Override
                public void onClickOke() {
                    NotyControlCenterServicev614.getInstance().stopRecord();
                    if (alertDialog.isAdded() && !alertDialog.isStateSaved()){
                        alertDialog.dismiss();
                    }
                    finish();
                }

                @Override
                public void onClickCancel() {
                    if (alertDialog.isAdded() && !alertDialog.isStateSaved()){
                        alertDialog.dismiss();
                    }
                    finish();
                }
            });
            if (!isFinishing()) {
                if (!getSupportFragmentManager().isStateSaved()) {
                    alertDialog.show(getSupportFragmentManager(), DIALOG_REQUEST_PERMISSION_WRITE_SETTING);
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .add(alertDialog, DIALOG_REQUEST_PERMISSION_WRITE_SETTING)
                            .commitAllowingStateLoss();
                }
            }


        } else if (permission[0].equals(Manifest.permission.READ_CALENDAR)) {
            ActivityCompat.requestPermissions(this, permission, REQUEST_CODE_PERMISSION_CALENDAR);
        } else if (permission[0].equals(Manifest.permission.BLUETOOTH_CONNECT)) {
            ActivityCompat.requestPermissions(this, permission, REQUEST_CODE_PERMISSION_BLUETOOTH);
        } else if (permission[0].equals(Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(this, permission, REQUESTCODE_PERMISSION_AUDIO_RECORD);
        } else {
            Random r = new Random();
            int x = r.nextInt(100);
            ActivityCompat.requestPermissions(this, permission, x);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionManager.getInstance().checkPermission(this, permission[0])) {
            if (alertDialog.isAdded() && !alertDialog.isStateSaved()){
                alertDialog.dismiss();
            }
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_CODE_PERMISSION_CALENDAR) {
                EventBus.getDefault().post(new EventCalendar());
            }
            finish();
        } else {
            try {
                if (requestCode == REQUEST_CODE_PERMISSION_BLUETOOTH) {
                    showDialog(false, String.format(getString(R.string.You_need_to_enable_permissions), getString(R.string.nearby_device)));
                } else if (requestCode == REQUEST_CODE_PERMISSION_CALENDAR) {
                    showDialog(false, String.format(getString(R.string.You_need_to_enable_permissions), getString(R.string.calendar)));
                } else if (requestCode == REQUESTCODE_PERMISSION_AUDIO_RECORD) {
                    showDialog(false, String.format(getString(R.string.You_need_to_enable_permissions), getString(R.string.micro)));
                } else {
                    showDialog(false, "");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.e("hachung requestCode onActivityResult:" + requestCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_PERMISSION_RECORD_SCREEN) {
                Intent i = new Intent(this, NotyControlCenterServicev614.class).putExtra(NotyControlCenterServicev614.EXTRA_ACTION, NotyControlCenterServicev614.ACTION_RECORD).putExtra(NotyControlCenterServicev614.EXTRA_RESULT_CODE, resultCode).putExtra(NotyControlCenterServicev614.EXTRA_RESULT_INTENT, data);

                startService(i);
                finish();
            } else if (requestCode == REQUESTCODE_PERMISSION_BG_SCREEN_REAL_TIME) {
                Intent i = new Intent(this, NotyControlCenterServicev614.class).putExtra(NotyControlCenterServicev614.EXTRA_ACTION, NotyControlCenterServicev614.ACTION_CAPTURE).putExtra(NotyControlCenterServicev614.EXTRA_RESULT_CODE, resultCode).putExtra(NotyControlCenterServicev614.EXTRA_RESULT_INTENT, data);

                startService(i);
                finish();
            }

        } else {
            finish();
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        hideDialog();
//    }
//
//    private void hideDialog() {
//        try {
//            if (alertDialog != null && alertDialog.isAdded()) {
//                alertDialog.dismissAllowingStateLoss();
//            }
//        } catch (Exception e) {
//        }
//    }


    @Override
    protected void onDestroy() {
        if (alertDialog != null && alertDialog.isShow()) {
            alertDialog.dismiss();
        }
        super.onDestroy();
    }
}
