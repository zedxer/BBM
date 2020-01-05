package com.example.naqi.bebettermuslim.controllers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.activities.HomeActivity
import com.example.naqi.bebettermuslim.activities.MainActivity
import java.util.*

/**
 * Created by naqi on 08,March,2019
 */
class AlarmReceiver: BroadcastReceiver() {
    private val CHANNEL_ID = "101"
    var NOTIFICATION_ID = "notification-id"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            // Set the alarm here.
            Toast.makeText(context,"MARJAO!!!",Toast.LENGTH_SHORT).show()
        }

        val id = intent.getIntExtra(NOTIFICATION_ID, 0)
        Log.e("Receiver Called", if (id == 1) "Lunch Time" else "Dinner Time")

        if (id == 1) {
            createNotification(context, "Lunch Time", "Its Lunch Time, checkout nearest Halal Restaurants from you.")
        }

        if (id == 0) {
            createNotification(context, "Dinner Time", "Its Dinner Time, checkout nearest Halal Restaurants from you.")
        }


    }


    private fun createNotification(context: Context, title: String, body: String) {

        val intent = Intent(context, HomeActivity::class.java)


        val pendingIntent =
            PendingIntent.getActivity(context.applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val mBuilder = NotificationCompat.Builder(context.applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.bookmark_icon)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(Notification.PRIORITY_HIGH)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setSound(alarmSound)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.bookmark_icon)
            mBuilder.color = context.resources.getColor(R.color.colorGray)
        } else {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
        }

        createNotificationChannel(context)
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager?.notify(Random().nextInt(), mBuilder.build())
    }


    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, "Halal Ways", importance)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }


}