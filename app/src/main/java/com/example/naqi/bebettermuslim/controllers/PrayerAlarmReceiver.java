package com.example.naqi.bebettermuslim.controllers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class PrayerAlarmReceiver extends BroadcastReceiver {
    public PrayerAlarmReceiver() {
    }

    String alarmName;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.USER_SETTING, Context.MODE_PRIVATE);
        String address = sharedPreferences.getString(Constants.USER_ADDRESS, null);
        Bundle bundle = intent.getExtras();

        int alarmID = bundle.getInt("ALARM_ID", -1);

//        AlarmDatabase alarmDatabase = new AlarmDatabase(context);
//        SQLiteDatabase db = alarmDatabase.getWritableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.PRAYER_ALARM_TABLE + " WHERE " + Constants.COLUMN_ALARM_ID + " = " + alarmID, null);
//        if (cursor.moveToFirst()) {
//            do {
//                alarmName = cursor.getString(2);
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        db.close();

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "prayer_alarm")
                        .setLargeIcon(icon)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(alarmName + " in " + (address != null ? address : "Hometown"))
                        .setAutoCancel(true)
                        .setVibrate(new long[]{1000, 0, 1000, 0, 1000})
                        .setLights(Color.BLUE, 3000, 3000);

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_INSISTENT;

        Intent resultIntent = new Intent(context, HomeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int alarmState = sharedPreferences.getInt(String.valueOf(alarmID - 1), 0);
        if (alarmState == 0) {
            Uri soundURI = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.azan);
            mBuilder.setSound(soundURI);
        }
        mNotificationManager.notify(alarmID, notification);
    }
}
