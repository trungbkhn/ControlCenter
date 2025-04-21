package com.tapbi.spark.controlcenter.service;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import timber.log.Timber;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Timber.e("onMessageReceived: "+ remoteMessage.getFrom());
        super.onMessageReceived(remoteMessage);
    }
    @Override
    public void onNewToken(@NonNull String s) {
        Timber.e("onNewToken: "+ s);
        super.onNewToken(s);
    }
}
