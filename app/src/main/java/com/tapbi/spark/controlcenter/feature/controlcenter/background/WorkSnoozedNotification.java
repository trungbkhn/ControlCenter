package com.tapbi.spark.controlcenter.feature.controlcenter.background;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import com.tapbi.spark.controlcenter.common.Constant;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.app.NotificationManager.IMPORTANCE_HIGH;

public class WorkSnoozedNotification extends Worker {
  private Context context;

  public WorkSnoozedNotification(@NonNull Context context, @NonNull WorkerParameters workerParams) {
    super(context, workerParams);
    this.context = context;
  }

  @NonNull
  @Override
  public Result doWork() {
//    String title = getInputData().getString("title");
//    String content = getInputData().getString("content");
//    String pkg = getInputData().getString("pkg");
//    if (getInputData().getByteArray("img") != null) {
//      byte[] img = getInputData().getByteArray("img");
//      Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
//    }

//    int notificationId = new Random().nextInt();
//    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//      createNotificationChannel(notificationManager);
//    }
//    NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "NOTY CENTER");
//    notification.setContentTitle(title);
//    notification.setContentText(content);
//    notification.setSmallIcon(R.mipmap.ic_launcher);
//    notification.setDefaults(Notification.DEFAULT_ALL);
//    notification.setPriority(NotificationCompat.PRIORITY_HIGH);
//    notification.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
//    notificationManager.notify(notificationId, notification.build());
//    Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkg);
//    PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
//        intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    notification.setContentIntent(contentIntent);


    if (getInputData().getString(Constant.KEY_PUT_NOTY_GROUP) !=null){
      String key = getInputData().getString(Constant.KEY_PUT_NOTY_GROUP);
      Intent intent = new Intent();
      intent.setAction(Constant.ACTION_NOTY_SNOOZED);
      intent.putExtra(Constant.INTENT_NOTY_GROUP, key);
      LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }


    return Result.success();
  }

  @RequiresApi(api = Build.VERSION_CODES.O)
  private void createNotificationChannel(NotificationManager notificationManager) {
    String channelName = "NOTY CENTER";
    NotificationChannel channel = new NotificationChannel(channelName, channelName, IMPORTANCE_HIGH);
    channel.setDescription("description");
    channel.enableLights(true);
    channel.setLightColor(Color.GREEN);
    notificationManager.createNotificationChannel(channel);
  }
}
