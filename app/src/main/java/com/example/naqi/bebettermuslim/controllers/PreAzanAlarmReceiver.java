package com.example.naqi.bebettermuslim.controllers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.example.naqi.bebettermuslim.activities.HomeActivity;
import com.example.naqi.bebettermuslim.R;
import com.example.naqi.bebettermuslim.Utils.Constants;

public class PreAzanAlarmReceiver extends BroadcastReceiver {
    public PreAzanAlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences preferences = context.getSharedPreferences(Constants.USER_SETTING, Context.MODE_PRIVATE);
        String address = preferences.getString(Constants.USER_ADDRESS, null);

        Bundle bundle = intent.getBundleExtra("DATA");

        int alarmID = bundle.getInt("ALARM_ID", -1);
        String remainingTime = bundle.getString("REMAINING_TIME");
        String alarmName = bundle.getString("ALARM_NAME");
        int alarmStatus = bundle.getInt("ALARM_STATUS", 0);
        long alarmTime = bundle.getLong("AlARM_TIME", -1);

        String notificationText = String.format("%s mins to %s", remainingTime, alarmName);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "pre_azan_alarm")
                        .setLargeIcon(icon)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notificationText)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{1000, 0, 1000, 0, 1000})
                        .setLights(Color.BLUE, 3000, 3000);

        Intent resultIntent = new Intent(context, HomeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_INSISTENT;

        if (alarmStatus == 1) {
            String alarmTone = preferences.getString(Constants.ALARM_TONE, null);
            int mp3Resource = context.getResources().getIdentifier(alarmTone, "raw", context.getPackageName());
            Uri soundURI = Uri.parse("android.resource://" + context.getPackageName() + "/" + mp3Resource);
            mBuilder.setSound(soundURI);
        }

        mNotificationManager.notify(alarmID, mBuilder.build());

    }
}
