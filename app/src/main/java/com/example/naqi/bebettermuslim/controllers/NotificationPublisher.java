package com.example.naqi.bebettermuslim.controllers;

import android.app.*;
import android.content.*;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.provider.AlarmClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.example.naqi.bebettermuslim.R;
import com.example.naqi.bebettermuslim.Utils.Constants;
import com.example.naqi.bebettermuslim.activities.HomeActivity;
import com.example.naqi.bebettermuslim.data.PreNamazAlarmManager;
import com.example.naqi.bebettermuslim.models.PreAzanAlarmModel;

import java.util.*;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by naqi on 09,March,2019
 */
public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static final String PRAYER_NOTIFICATION_ID = "prayer-notification-id";
    public static final String PRE_NAMAZ_ALARM_NOTIFICATION_ID = "pre-prayer-notification-id";
    public static String NOTIFICATION = "notification";
    private Set<String> stringSet = new HashSet<String>();
    NotificationManager notificationManager;
    String CHANNEL_ID = "101";
    String CHANNEL_ID_MUTED = "102";
    String PRE_NAMAZ_ALARM_CHANNEL = "105";
    int notificationId = 0;

    //notification
    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        int id = intent.getIntExtra(PRAYER_NOTIFICATION_ID, 0);
        Log.v("Receiver Called", "Something called : " + id);
        if (id == 0) {
            boolean shouldDismiss = false;
            int someId = intent.getIntExtra("SOMETHING", 8);
//        Log.e("Receiver Called", id == 1 ? "Lunch Time" : "Dinner Time");
            Log.v("Receiver Called", "Something called : " + id);
            if (Objects.equals(intent.getAction(), AlarmClock.ACTION_DISMISS_ALARM)) {
                notificationManager.cancel(id);
                shouldDismiss = true;
            }
            if (!shouldDismiss) {
                SharedPreferences pref = context.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE).edit();
                int prayerInt = pref.getInt(Constants.NAMAZ_POSITION, 0);
                stringSet = pref.getStringSet(Constants.MUTED_AZAN, stringSet);

                switch (prayerInt) {
                    case Constants.FAJAR_CONSTANT: {
//                    if (stringSet.contains(String.valueOf(prayerInt))) {
//                        Log.v("NOTIF", "contains" + prayerInt);
//                        createNotification(context, "Fajar" + context.getString(R.string.namaz_timing_title) + id, context.getString(R.string.namaz_timing_body), id, false);
//                    } else {
//                        createNotification(context, "Fajar" + context.getString(R.string.namaz_timing_title) + id, context.getString(R.string.namaz_timing_body), id, true);
//
//                    }
                        checkNamazMuteState(context, "Fajar", id, pref);
                        editor.putInt(Constants.NAMAZ_POSITION, Constants.DHUHR_CONSTANT);
                        editor.apply();
                        break;
                    }
                    case 1: {
//                    checkNamazMuteState(context,"Fajar",id, pref);
//                    editor.putInt(Constants.NAMAZ_POSITION, Constants.ASR_CONSTANT);
//                    editor.apply();
                    }
                    case Constants.DHUHR_CONSTANT: {
                        checkNamazMuteState(context, "Dhuhr", id, pref);
                        editor.putInt(Constants.NAMAZ_POSITION, Constants.ASR_CONSTANT);
                        editor.apply();
                        break;
                    }
                    case Constants.ASR_CONSTANT: {
             /*       if (stringSet.contains(String.valueOf(prayerInt))) {
                        Log.v("NOTIF", "contains" + prayerInt);
                        createNotification(context, "Asr " + context.getString(R.string.namaz_timing_title) + id, context.getString(R.string.namaz_timing_body), id, false);
                    } else {
                        createNotification(context, "Namaz Time is Asr " + id, "Its Dinner Time, checkout nearest Halal Restaurants from you.", id, true);

                    }*/
                        checkNamazMuteState(context, "Asr", id, pref);
                        editor.putInt(Constants.NAMAZ_POSITION, Constants.MAGHRIB_CONSTANT);
                        editor.apply();
                        break;
                    }
                    case Constants.MAGHRIB_CONSTANT: {
//                    if (stringSet.contains(String.valueOf(prayerInt))) {
//                        Log.v("NOTIF", "contains" + prayerInt);
//                        createNotification(context, "Namaz Time is Maghrib" + id, "Its Dinner Time, checkout nearest Halal Restaurants from you.", id, false);
//                    } else {
//                        createNotification(context, "Namaz Time is Maghrib" + id, "Its Dinner Time, checkout nearest Halal Restaurants from you.", id, true);
//
//                    }
                        checkNamazMuteState(context, "Maghrib", id, pref);
                        editor.putInt(Constants.NAMAZ_POSITION, Constants.ISHA_CONSTANT);
                        editor.apply();
                        break;
                    }
                    case Constants.ISHA_CONSTANT: {
//                    if (stringSet.contains(String.valueOf(prayerInt))) {
//                        Log.v("NOTIF", "contains" + prayerInt);
//                        createNotification(context, "Namaz Time is Isha" + id, "Its Dinner Time, checkout nearest Halal Restaurants from you.", id, false);
//                    } else {
//                        createNotification(context, "Namaz Time is Isha" + id, "Its Dinner Time, checkout nearest Halal Restaurants from you.", id, true);
//                    }
                        checkNamazMuteState(context, "Isha", id, pref);
                        editor.putInt(Constants.NAMAZ_POSITION, Constants.FAJAR_CONSTANT);
                        editor.apply();
                        break;
                    }
                }

            } else {
                notificationManager.cancel(id);
            }
        } else if (id == 5) {
            if (Objects.equals(intent.getAction(), AlarmClock.ACTION_DISMISS_ALARM)) {
                notificationManager.cancel(id);
            }
            PreNamazAlarmManager preNamazAlarmManager = PreNamazAlarmManager.Singleton.getINSTANCE();
            SharedPreferences pref = context.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);
            int prayerInt = pref.getInt(Constants.NAMAZ_POSITION, 0);

            switch (prayerInt) {
                case Constants.FAJAR_CONSTANT: {
                    PreAzanAlarmModel preAzanAlarmModel = preNamazAlarmManager.getObjectFromDatabase(Constants.FAJAR_CONSTANT_KEY);
                    if (preAzanAlarmModel != null && preAzanAlarmModel.isActivated()) {
                        if (preAzanAlarmModel.isMuted()) {
                            createNotificationPreNamaz(context, "Fajar", "PRE Azan Alram", id, false);
                        } else {
                            createNotificationPreNamaz(context, "Fajar", "PRE Azan Alram", id, true);

                        }
                    }
                    break;
                }
                case 1: {
                }
                case Constants.DHUHR_CONSTANT: {
                    PreAzanAlarmModel preAzanAlarmModel = preNamazAlarmManager.getObjectFromDatabase(Constants.DHUHR_CONSTANT_KEY);
                    if (preAzanAlarmModel != null && preAzanAlarmModel.isActivated()) {
                        if (preAzanAlarmModel.isMuted()) {
                            createNotificationPreNamaz(context, "Dhuhr", "PRE Azan Alram", id, false);
                        } else {
                            createNotificationPreNamaz(context, "Dhuhr", "PRE Azan Alram", id, true);
                        }
                    }

                    break;
                }
                case Constants.ASR_CONSTANT: {
//                    checkNamazMuteState(context, "Asr", id, pref);
                    PreAzanAlarmModel preAzanAlarmModel = preNamazAlarmManager.getObjectFromDatabase(Constants.ASR_CONSTANT_KEY);
                    if (preAzanAlarmModel != null && preAzanAlarmModel.isActivated()) {
                        if (preAzanAlarmModel.isMuted()) {
                            createNotificationPreNamaz(context, "Asr", "PRE Azan Alram", id, false);
                        } else {
                            createNotificationPreNamaz(context, "Asr", "PRE Azan Alram", id, true);
                        }
                    }

                    break;
                }
                case Constants.MAGHRIB_CONSTANT: {
//                    checkNamazMuteState(context, "Maghrib", id, pref);
                    PreAzanAlarmModel preAzanAlarmModel = preNamazAlarmManager.getObjectFromDatabase(Constants.MAGHRIB_CONSTANT_KEY);
                    if (preAzanAlarmModel != null && preAzanAlarmModel.isActivated()) {
                        if (preAzanAlarmModel.isMuted()) {
                            createNotificationPreNamaz(context, "Maghrib", "PRE Azan Alram", id, false);

                        } else {

                            createNotificationPreNamaz(context, "Maghrib", "PRE Azan Alram", id, true);
                        }
                    }
                    break;
                }
                case Constants.ISHA_CONSTANT: {
                    PreAzanAlarmModel preAzanAlarmModel = preNamazAlarmManager.getObjectFromDatabase(Constants.ISHA_CONSTANT_KEY);
                    if (preAzanAlarmModel != null && preAzanAlarmModel.isActivated()) {
                        if (preAzanAlarmModel.isMuted()) {
                            createNotificationPreNamaz(context, "Isha", "PRE Azan Alram", id, false);
                        } else {
                            createNotificationPreNamaz(context, "Isha", "PRE Azan Alram", id, true);

                        }
                    }
                    break;
                }
            }
        }


    }

    private void checkNamazMuteState(Context context, String namazName, int id, SharedPreferences pref) {
        int namazState = pref.getInt(Constants.FAJAR_CONSTANT_KEY, 0);
        switch (namazState) {
            case 0: {
                createNotification(context, namazName + context.getString(R.string.namaz_timing_title), context.getString(R.string.namaz_timing_body) + " " + namazName, id, true);
                break;
            }
            case 1: {
                createNotification(context, namazName + context.getString(R.string.namaz_timing_title), context.getString(R.string.namaz_timing_body) + " " + namazName, id, false);
                break;
            }
            case 2: {
                break;
            }
        }
    }

    private void createNotification(Context context, String title, String body, int notificationId, boolean makeSound) {

        Intent intent = new Intent(context, HomeActivity.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent snoozeIntent = new Intent(context, NotificationPublisher.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            snoozeIntent.setAction(AlarmClock.ACTION_DISMISS_ALARM);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            snoozeIntent.putExtra("SOMETHING", 89);
        }
        snoozeIntent.putExtra("SOMETHING", 69);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);
        if (makeSound) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(Notification.PRIORITY_HIGH)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
//                .setSound(alarmSound)
                    .setAutoCancel(true)
                    .addAction(R.drawable.close_sound_options, "Dismiss", snoozePendingIntent)
//                    .addAction(0, context.getString(R.string.namaz_timing_title), PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
//                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.azan))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(body));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
                mBuilder.setColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
            }
            createNotificationChannel(context, makeSound);

            if (notificationManager != null) {
                Uri soundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + context.getPackageName() + "/raw/azan");
                mBuilder.setSound(soundURI);
                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

//            notificationManager.notify(new Random().nextInt(), mBuilder.build());
                notificationManager.notify(notificationId, mBuilder.build());
//            MediaPlayer mp= MediaPlayer.create(context, R.raw.azan);
//            mp.start();
            }


        } else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID_MUTED)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(Notification.PRIORITY_HIGH)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
//                .setSound(alarmSound)
                    .setAutoCancel(true)
                    .addAction(R.drawable.close_sound_options, "Snooze", snoozePendingIntent)
//                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.azan))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(body));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
                mBuilder.setColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
            }
            createNotificationChannel(context, makeSound);

            if (notificationManager != null) {
                Uri soundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + context.getPackageName() + "/raw/azan");
//                mBuilder.setSound(soundURI);
                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

//            notificationManager.notify(new Random().nextInt(), mBuilder.build());
                notificationManager.notify(notificationId, mBuilder.build());
//            MediaPlayer mp= MediaPlayer.create(context, R.raw.azan);
//            mp.start();
            }
            mBuilder.setSound(null);
        }

    }


    private void createNotificationPreNamaz(Context context, String title, String body, int notificationId, boolean makeSound) {

        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent snoozeIntent = new Intent(context, NotificationPublisher.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            snoozeIntent.setAction(AlarmClock.ACTION_DISMISS_ALARM);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            snoozeIntent.putExtra("SOMETHING", 58);
        }
        snoozeIntent.putExtra("SOMETHING", 33);
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);
        if (makeSound) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), PRE_NAMAZ_ALARM_CHANNEL)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(Notification.PRIORITY_HIGH)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
//                .setSound(alarmSound)
                    .setAutoCancel(true)
                    .addAction(R.drawable.disclosure_left, "Dismiss", snoozePendingIntent)
//                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.azan))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(body));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
                mBuilder.setColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
            }
            createNotificationChannelPreNamaz(context, makeSound);
            if (notificationManager != null) {
                Uri soundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + context.getPackageName() + "/raw/pre_azan_alarm_tone");
                mBuilder.setSound(soundURI);
                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
                notificationManager.notify(notificationId, mBuilder.build());

            }
        } else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID_MUTED)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(Notification.PRIORITY_HIGH)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .addAction(R.drawable.close_sound_options, "Snooze", snoozePendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(body));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground);
                mBuilder.setColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
            }
            createNotificationChannel(context, makeSound);

            if (notificationManager != null) {
                Uri soundURI = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + context.getPackageName() + "/raw/azan");
//                mBuilder.setSound(soundURI);
                mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
                notificationManager.notify(notificationId, mBuilder.build());
            }
            mBuilder.setSound(null);

        }

    }


    private void createNotificationChannel(Context context, boolean makeSound) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (makeSound) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Prayer Time", importance);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                channel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.azan), audioAttributes);
                channel.setDescription("dsa");
                notificationManager.createNotificationChannel(channel);

            } else {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID_MUTED, "Prayer Time Muted", importance);
                channel.setSound(null, null);
                channel.setDescription("dsa");
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void createNotificationChannelPreNamaz(Context context, boolean makeSound) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (makeSound) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(PRE_NAMAZ_ALARM_CHANNEL, "Prayer Time", importance);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                channel.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.pre_azan_alarm_tone), audioAttributes);
                channel.setDescription("dsa");
                notificationManager.createNotificationChannel(channel);
            } else {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(PRE_NAMAZ_ALARM_CHANNEL, "Prayer Time", importance);
                channel.setSound(null, null);
                channel.setDescription("dsa");
                notificationManager.createNotificationChannel(channel);
            }

        }

    }

    private boolean applicationInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> services = activityManager.getRunningAppProcesses();
        boolean isActivityFound = false;

        if (services.get(0).processName.equalsIgnoreCase(context.getPackageName())) {
            isActivityFound = true;
        }

        return isActivityFound;
    }
}