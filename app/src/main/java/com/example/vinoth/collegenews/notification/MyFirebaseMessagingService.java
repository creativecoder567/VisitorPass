package com.example.vinoth.collegenews.notification;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.vinoth.collegenews.NewsDetailActivity;
import com.example.vinoth.collegenews.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.List;

import static android.media.RingtoneManager.getDefaultUri;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private Context mContext;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = "";
        String body = "";
        String id = "";
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }
        Log.i(TAG, "onMessageReceived: " + remoteMessage.getData().size());
        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                if (json.has("title"))
                    title = json.getString("title");
                if (json.has("body"))
                    body = json.getString("body");
                if (json.has("news_id"))
                    id = json.getString("news_id");

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
        if (!isAppIsInBackground())
            setNotification(title, body, id);
    }

    private void setNotification(String title, String notificationMessage, String id) {
        int requestID = (int) System.currentTimeMillis();
        Uri alarmSound = getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String CHANNEL_ID = "my_channel_01";
        Intent notificationIntent = new Intent(getApplicationContext(), NewsDetailActivity.class);
        notificationIntent.putExtra(NewsDetailActivity.NEWS_ID, id);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.male_reporter)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationMessage))
                .setContentText(notificationMessage).setAutoCancel(true);
        mBuilder.setSound(alarmSound);
        mBuilder.setColor(ContextCompat.getColor(this, R.color.colorAccent));
        mBuilder.setContentIntent(contentIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(19921, mBuilder.build());

    }

    public boolean isAppIsInBackground() {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}