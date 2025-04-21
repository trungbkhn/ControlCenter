package com.tapbi.spark.controlcenter.service;

import android.os.Build;
import android.telecom.Call;
import android.telecom.CallScreeningService;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.common.Constant;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class CallScreeningListener extends CallScreeningService {
    private Call.Details callDetailsCurrent = null;

    @Override
    public void onScreenCall(@NonNull Call.Details details) {
        int callDirection = details.getCallDirection();
        if (callDirection == Call.Details.DIRECTION_INCOMING) {
            callDetailsCurrent = details;
            CheckRejectCall();
        }
    }

    private void CheckRejectCall() {
        Single.fromCallable(() -> {
//            App.setPresetCurrentFocus();
            if (App.focusIOSStart != null) {
                if (App.focusIOSStart.getModeAllowPeople() != Constant.EVERY_ONE) {
                    String phoneNumber = callDetailsCurrent.getHandle().getSchemeSpecificPart();
//                    if (!App.ins.contactReposition.checkAllowPeople(App.getFocusIOSStart().getName(), phoneNumber)) {
//                        return !App.tinyDB.getBoolean(Constant.IS_PAUSE_FOCUS, false);
//                    }
                }
            }
            return false;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Boolean>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {}

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull Boolean rejectCall) {
                try {
                    if (rejectCall) {
                        rejectCall();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Timber.e(e);
            }
        });

    }

    public void rejectCall() {
        CallResponse.Builder response = new CallResponse.Builder();
        response.setDisallowCall(true);
        response.setRejectCall(true);
        respondToCall(callDetailsCurrent, response.build());
    }
}
