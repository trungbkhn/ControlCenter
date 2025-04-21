package com.tapbi.spark.controlcenter.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.BaseBundle;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.tapbi.spark.controlcenter.App;
import com.tapbi.spark.controlcenter.common.Constant;
import com.tapbi.spark.controlcenter.common.models.ItemAddedNoty;
import com.tapbi.spark.controlcenter.common.models.ItemRemovedNoty;
import com.tapbi.spark.controlcenter.data.repository.ThemeHelper;
import com.tapbi.spark.controlcenter.feature.NotyManager;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyGroup;
import com.tapbi.spark.controlcenter.feature.controlios14.model.NotyModel;
import com.tapbi.spark.controlcenter.utils.MethodUtils;
import com.tapbi.spark.controlcenter.utils.SettingUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class NotificationListener extends NotificationListenerService {
    private static NotificationListener instance;
    /**
     * This function check System alert "App name is display overlay ..."
     *
     * @param model is NotyModel
     * @return true if System alert overlay
     * @return false if other noty
     */
    private final String packageSystemAndroid = "android";
    public boolean isFirstLoad = true;


    private Runnable runnableEnable = () -> {
        if (NotyControlCenterServicev614.getInstance() != null) {
            Timber.e("hachung enableWindow:");
            NotyControlCenterServicev614.getInstance().enableWindow();
        }

    };

    public static NotificationListener getInstance() {
        return instance;
    }

    private void setUpAccessService() {
        if (NotyControlCenterServicev614.getInstance() != null) {
            NotyControlCenterServicev614.getInstance().disableWindow();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    public void onCreate() {
        super.onCreate();
        instance = this;
        if (NotyControlCenterServicev614.getInstance() != null) {
            NotyControlCenterServicev614.getInstance().updateStatusNotificationAccess();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void loadInFirstUse() {
        new LoadAllNoty().execute();
    }

    private void updateGroupNotyRemoved(int positionGroup, String packageName, int positionModel, String idNoty, boolean isRemovedGroup, boolean isNotyNow) {
        if (NotyControlCenterServicev614.getInstance() != null) {
            NotyControlCenterServicev614.getInstance().updateGroupNotyRemoved(positionGroup, packageName, positionModel, idNoty, isRemovedGroup, isNotyNow);
        }
    }

    public void deleteGroup(String[] keys) {
        try {
            NotificationListener.this.cancelNotifications(keys);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteNoty(String pka, int id, String key) {
        try {
            NotificationListener.this.cancelNotification(key);
        } catch (Exception ex) {
            Timber.e(ex);
        }
    }

    public void deleteAllNoty() {
        try {
            if (!SettingUtils.checkPermissionNotificationListener(getApplicationContext())) {
                SettingUtils.intentToPermissionActivity(getApplicationContext());
            } else {
//                List<String> keys = new ArrayList<>();
//                synchronized (NotyManager.INSTANCE.getNotyGroup()){
//                    for (NotyGroup group : NotyManager.INSTANCE.getNotyGroup()) {
//                        synchronized (group.getNotyModels()){
//                            for (NotyModel noty : group.getNotyModels()) {
//                                //Timber.e("hoangld: " + noty.content);
//                                keys.add(noty.getKeyNoty());
//                            }
//                        }
//                    }
//                }
//
//                for (String key : keys){
//                    cancelNotification(key);
//                }
                cancelAllNotifications();
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public void snoozeNoti(String key, long time) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationListener.this.snoozeNotification(key, time);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private NotyModel getNotiModel(StatusBarNotification sbn) {
        String keyNoty = sbn.getKey();
        NotyModel model = new NotyModel();
        Bundle extras = sbn.getNotification().extras;
        model.setDelete(sbn.isClearable());
//        model.setRemoteView(sbn.getNotification().contentView);
//        model.setRemoteView2(getRemoteView(App.mContext, sbn));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            model.setUid(sbn.getUid());
        }
        model.setPendingIntent(sbn.getNotification().contentIntent);
        if (sbn.getNotification().when == 0L) {
            model.setTime(sbn.getPostTime());
        } else {
            model.setTime(sbn.getNotification().when);
        }
        model.setPakage(sbn.getPackageName());
        model.setIdNoty(sbn.getId());
        model.setKeyNoty(keyNoty);
        model.setGroupKey(sbn.getGroupKey());
        model.setState(NotyGroup.STATE.NONE);
        try {
            if (extras.containsKey(Notification.EXTRA_PICTURE)) {
                Object picture = extras.get(Notification.EXTRA_PICTURE);

                if (picture instanceof Bitmap) {
                    model.setImaBitmap((Bitmap) picture);
                } else {
                    Log.e("TAG", "The data in EXTRA_PICTURE is not a Bitmap.");
                    model.setImaBitmap(null);
                }
            } else {
                model.setImaBitmap(null);
            }
        } catch (OutOfMemoryError e) {
            Log.e("TAG", "OutOfMemoryError: " + e.getMessage());
            model.setImaBitmap(null); // Hoặc xử lý giảm kích thước ảnh
        } catch (IllegalArgumentException e) {
            Log.e("TAG", "getNotiModel: " + e.getMessage());
        }
        try {
            model.setIconApp(MethodUtils.getIconFromPackageName(this, sbn.getPackageName()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try {
            if (sbn.getNotification() != null) {
                if (extras.get(NotificationCompat.EXTRA_TEXT) != null) {
                    model.setContent(extras.get(NotificationCompat.EXTRA_TEXT).toString());
                } else if (extras.get(NotificationCompat.EXTRA_BIG_TEXT) != null) {
                    model.setContent(extras.get(NotificationCompat.EXTRA_BIG_TEXT).toString());
                }

                if (extras.get(NotificationCompat.EXTRA_TITLE) != null) {
                    model.setTitle(extras.get(NotificationCompat.EXTRA_TITLE).toString());
                } else if (extras.get(NotificationCompat.EXTRA_TITLE_BIG) != null) {
                    model.setTitle(extras.get(NotificationCompat.EXTRA_TITLE_BIG).toString());
                }
            }

            //update content messenger
            if (extras.get(NotificationCompat.EXTRA_MESSAGES) != null && extras.getParcelableArray(NotificationCompat.EXTRA_MESSAGES) != null) {
                String message = "";
                Parcelable[] arrayMessage = (Parcelable[]) extras.get(NotificationCompat.EXTRA_MESSAGES);
                for (Parcelable bundle : arrayMessage) {
                    if (bundle instanceof BaseBundle) {
                        String text = ((BaseBundle) bundle).getString("text");
                        if (text != null && !text.equals("")) {
                            message = text + "\n" + message;
                        }
                    }
                }
                if (!message.equals("")) {
                    model.setContent(message.trim());
                }
            } else if (extras.get(NotificationCompat.EXTRA_TEXT_LINES) != null && extras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES) != null) {
                String message = "";
                CharSequence[] arrayMessage = (CharSequence[]) extras.get(NotificationCompat.EXTRA_TEXT_LINES);
                for (CharSequence text : arrayMessage) {
                    if (text instanceof String) {
                        if (text != "") {
                            message = text + "\n" + message;
                        }
                    }
                }
                if (!message.equals("")) {
                    model.setContent(message.trim());
                }
            }

//                    RemoteViews remoteView = sbn.getNotification().contentView;
//                    if (remoteView != null) {
//                        model.setRemoteView(remoteView);
//                    }

            if (model.getTitle().isEmpty() || model.getContent().isEmpty()) {
                List<String> list = getText(sbn.getNotification());
                if (list != null && list.size() > 0) {
                    if (model.getTitle().isEmpty()) {
                        model.setTitle(list.get(0));
                    }
                    if (model.getContent().isEmpty()) {
                        if (list.size() > 1) {
                            model.setContent(list.get(1));
                        } else {
                            model.setContent(list.get(0));
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        model.setCanShow(checkNotyCanShow(model));
        return model;
    }

    private RemoteViews getRemoteView(Context context, StatusBarNotification statusBarNotification) {
        try {
            RemoteViews rmContent = getRemoteContent(context, statusBarNotification);
            if (rmContent != null) {
                Timber.e("Hoangld 1 " + statusBarNotification.getPackageName() + " " + statusBarNotification.getNotification().contentView + " " + statusBarNotification.getNotification().bigContentView + " " + statusBarNotification.getNotification().headsUpContentView);
                return rmContent;
            }
            RemoteViews rmHeadsUp = getRemoteHeadsUp(context, statusBarNotification);
            if (rmHeadsUp != null) {
                Timber.e("Hoangld 2 " + statusBarNotification.getPackageName());
                return rmHeadsUp;
            }
            RemoteViews rmBigContent = getRemoteBigContent(context, statusBarNotification);
            if (rmBigContent != null) {
                Timber.e("Hoangld 3 " + statusBarNotification.getPackageName());
                return rmBigContent;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private RemoteViews getRemoteBigContent(Context context, StatusBarNotification statusBarNotification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Notification.Builder.recoverBuilder(context, statusBarNotification.getNotification()).createBigContentView();
        }
        return null;
    }

    private RemoteViews getRemoteContent(Context context, StatusBarNotification statusBarNotification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Notification.Builder.recoverBuilder(context, statusBarNotification.getNotification()).createContentView();
        }
        return null;
    }

    private RemoteViews getRemoteHeadsUp(Context context, StatusBarNotification statusBarNotification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Notification.Builder.recoverBuilder(context, statusBarNotification.getNotification()).createHeadsUpContentView();
        }
        return null;
    }

    private List<String> getText(Notification notification) {
        // We have to extract the information from the view
        RemoteViews views = notification.bigContentView;
        if (views == null) views = notification.contentView;
        if (views == null) return null;

        // Use reflection to examine the m_actions member of the given RemoteViews object.
        // It's not pretty, but it works.
        ArrayList<String> text = new ArrayList<>();
        try {
            Field field = views.getClass().getDeclaredField("mActions");
            field.setAccessible(true);
            ArrayList<Parcelable> actions = (ArrayList<Parcelable>) field.get(views);

            // Find the setText() and setTime() reflection actions
            for (Parcelable p : actions) {
                Parcel parcel = Parcel.obtain();

                p.writeToParcel(parcel, 0);
                parcel.setDataPosition(0);

                // The tag tells which type of action it is (2 is ReflectionAction, from the source)
                int tag = parcel.readInt();
                if (tag != 2) {
                    continue;
                }
                // View ID
                parcel.readInt();
                String methodName = parcel.readString();
                if (methodName == null) continue;
                else if (methodName.equals("setText")) {
                    // Parameter type (10 = Character Sequence)
                    parcel.readInt();

                    String t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
                    text.add(t);
                } else if (methodName.equals("setTime")) {
                    // Parameter type (5 = Long)
                    parcel.readInt();
                    String t = new SimpleDateFormat("h:mm a").format(new Date(parcel.readLong()));
                    text.add(t);
                }
                parcel.recycle();
            }
        } // It's not usually good style to do this, but then again, neither is the use of reflection...
        catch (Exception e) {
            //Log.e("NotificationClassifier", e.toString())
        }
        return text;
    }

    public void onDestroy() {
        instance = null;
        Timber.e("hoangld ");
        if (NotyControlCenterServicev614.getInstance() != null) {
            NotyControlCenterServicev614.getInstance().updateStatusNotificationAccess();
        }
        super.onDestroy();
    }

    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null) {
            return;
        }
        NotyModel model = getNotiModel(sbn);

        long timeSystem = System.currentTimeMillis();
        if (model.getTime() > timeSystem) {
            model.setTime(timeSystem);
        }
        if (checkSystemAlertAppOverlay(model)) {
            return;
        }
//        if (NotyControlCenterServicev614.getInstance() != null && NotyControlCenterServicev614.getInstance().typeChoose == Constant.VALUE_CONTROL_CENTER_OS) {
////            Timber.e("hachung getFocusIOSStart :" + App.getFocusIOSStart() + "/check: " + sbn.getPackageName().equals(getPackageName()));
//            if (App.focusIOSStart != null && !sbn.getPackageName().equals(getPackageName())) {
//                blockNotification(sbn, model);
//            } else {
//                //deleteNotifi();
//                if (!model.isCanShow()) {
//                    return;
//                }
//                addNotyModel(model);
//            }
//        } else {
//            super.onNotificationPosted(sbn);
//            //deleteNotifi();
//            if (!model.isCanShow()) {
//                return;
//            }
//            addNotyModel(model);
//        }
        addNotyModel(model);
    }

    private void addNotyModel(NotyModel model) {
        if (model.getTitle().isEmpty() && model.getContent().isEmpty()) {
            return;
        }

        if (NotyControlCenterServicev614.getInstance() != null) {

            if (NotyControlCenterServicev614.getInstance().isShowNoty() /*&& NotyControlCenterServicev614.getInstance().typeChoose == Constant.VALUE_CONTROL_CENTER && NotyManager.INSTANCE.getListNotyGroup().size() > 0*/) {
                ItemAddedNoty itemAddedNoty;
                if (NotyControlCenterServicev614.getInstance().typeChoose == Constant.VALUE_CONTROL_CENTER_OS) {
                    itemAddedNoty = NotyManager.INSTANCE.addNotyNow(model);
                } else {
                    itemAddedNoty = NotyManager.INSTANCE.addNoty(model);
                }

                NotyControlCenterServicev614.getInstance().addNoty(itemAddedNoty);
            } else {
                NotyManager.INSTANCE.addNoty(model);
            }
        }
//        if (ToastTextManager.Companion.getCurrentInstance() != null){
//            ToastTextManager.Companion.getCurrentInstance().show(model.getTitle());
//        }
//        App.phoneSms = "";
    }

    private void insertNotification(NotyModel model) {
        Completable.fromRunnable(() -> App.ins.focusPresetRepository.insertNotification(model)).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    private void deleteNotifi() {
        Completable.fromRunnable(() -> {
            App.ins.focusPresetRepository.deleteNoti();
        }).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    private void blockNotification(StatusBarNotification sbn, NotyModel model) {
        Completable.fromRunnable(() -> {
            if (!App.ins.applicationRepository.checkNotificationAppAllow(sbn.getPackageName())) {
//                Timber.e("hachung : checkNotificationAppAllow");
//                String packageSmsDefault = AppUtils.getDefaultSmsAppPackageName(this);
//                if (packageSmsDefault != null && packageSmsDefault.equals(sbn.getPackageName())) {
//                    Timber.e("hachung : getDefaultSmsAppPackageName");
//                    if (!App.ins.applicationRepository.checkNotiPeopleAllow()) {
//                        Timber.e("hachung : checkNotiPeopleAllow");
//                        cancelNotification(sbn.getKey());
//                        App.phoneSms = "";
//                    } else {
//                        addNotyModel(model);
//                    }
//                } else {
//                    cancelNotification(sbn.getKey());
//                    insertNotification(model);
//                    App.phoneSms = "";
//                }
                cancelNotification(sbn.getKey());
                //insertNotification(model);

            } else {
                addNotyModel(model);
            }
        }).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(@NonNull Throwable e) {
                Timber.e("hachung e :" + e);
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getAction() != null && intent.getAction().equals(Constant.ACTION_GET_LAST_NOTY)) {
                Timber.e("hoangld ACTION_GET_LAST_NOTY: ");
                loadInFirstUse();
            }
        }
        return START_STICKY;
    }

    private void unRegisterService() {
//        if (callReceiver != null) {
//            unregisterReceiver(callReceiver);
//        }
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (sbn == null) {
            return;
        }
        NotyModel model = getNotiModel(sbn);
        if (!model.isCanShow() || (model.getTitle().isEmpty() && model.getContent().isEmpty())) {
            return;
        }
        ItemRemovedNoty itemRemovedNoty = NotyManager.INSTANCE.removeNoty(model);
        if (itemRemovedNoty.getPosNotyModel() != -1) {
//            Timber.e("hachung isRemoveGroups:" + itemRemovedNoty.isRemoveGroups());
            updateGroupNotyRemoved(itemRemovedNoty.getPosNotyGroup(), model.getPakage(), itemRemovedNoty.getPosNotyModel(), itemRemovedNoty.getKeyNoty(), itemRemovedNoty.isRemoveGroups(), itemRemovedNoty.isNotyNow());
        }
    }

    @Override
    public void onNotificationChannelGroupModified(String pkg, UserHandle user, NotificationChannelGroup group, int modificationType) {
        super.onNotificationChannelGroupModified(pkg, user, group, modificationType);
    }

    @Override
    public void onNotificationChannelModified(String pkg, UserHandle user, NotificationChannel channel, int modificationType) {
        super.onNotificationChannelModified(pkg, user, channel, modificationType);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        MethodUtils.intentToCheckPermission(this);
        loadInFirstUse();
//        MethodUtils.intentToCheckPermission(this);
//        startReceiver();
//        setUpAccessService();
        if (ThemeHelper.itemControl != null) {
            ThemeHelper.Companion.enableWindow();
        }

    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        MethodUtils.intentToCheckPermission(this);
        unRegisterService();
    }

    private void allNotyLoaded() {
        if (NotyControlCenterServicev614.getInstance() != null) {
            NotyControlCenterServicev614.getInstance().reloadAllNoty();
        }
    }

    private boolean checkSystemAlertAppOverlay(NotyModel model) {
        //check package android
        if (Objects.equals(model.getPakage(), packageSystemAndroid)) {
            //check alert about my app
            if (model.getKeyNoty().contains(getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkNotyCanShow(NotyModel model) {
        if (model.getIdNoty() < 0 || model.getTitle().isEmpty() && model.getContent().isEmpty() && model.getRemoteView() == null) {
            return false;
        }
        return true;
    }

    private class LoadAllNoty extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                NotyManager.INSTANCE.clearAll();
                StatusBarNotification[] statusBarNotifications = getActiveNotifications();

                if (statusBarNotifications != null) {
                    for (StatusBarNotification sbn : statusBarNotifications) {
                        // Kiểm tra và lấy model, bỏ qua nếu null để tránh tạo nhiều đối tượng không cần thiết
                        NotyModel model = getNotiModel(sbn);
                        if (model == null) continue;

                        // Kiểm tra điều kiện để bỏ qua các thông báo không cần thiết
                        if (checkSystemAlertAppOverlay(model) || !model.isCanShow()) {
                            continue;
                        }

                        // Thêm vào danh sách nếu thỏa mãn điều kiện
                        NotyManager.INSTANCE.addFirstTime(model);
                    }
                    // Sắp xếp danh sách sau khi xử lý xong
                    NotyManager.INSTANCE.sortFirstTime();
                }

                isFirstLoad = false;
            } catch (OutOfMemoryError oom) {
                // Bắt lỗi OOM để tránh crash và giải phóng tài nguyên nếu cần
                oom.printStackTrace();
                NotyManager.INSTANCE.clearAll(); // Giải phóng tài nguyên
//                System.gc(); // Gọi garbage collector thủ công (không khuyến khích thường xuyên)
                isFirstLoad = true;
            } catch (Exception ex) {
                ex.printStackTrace();
                isFirstLoad = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            allNotyLoaded(); // Gọi hàm sau khi tải xong thông báo
        }
    }

    class CurrentNoti {
        public boolean isAdding;
        public String packageName;
        public String removePackageName = null;

        public CurrentNoti(boolean isAdding, String packageName) {
            this.isAdding = isAdding;
            this.packageName = packageName;
        }
    }

}
