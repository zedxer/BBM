package com.example.naqi.bebettermuslim.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.example.naqi.bebettermuslim.R
import io.github.inflationx.viewpump.ViewPumpContextWrapper

class MainActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val lunchTime = Calendar.getInstance()
        val currentCal = Calendar.getInstance()
        lunchTime.set(Calendar.HOUR_OF_DAY, 11) // At the hour you wanna fire
        lunchTime.set(Calendar.MINUTE, 47) // Particular minute
        lunchTime.set(Calendar.SECOND, 0)
        lunchTime.set(Calendar.MILLISECOND, 0)// particular milli second
        var intendedLunchTime = lunchTime.timeInMillis
        val currentTime = currentCal.timeInMillis
        if (intendedLunchTime < currentTime) {
            lunchTime.add(Calendar.DAY_OF_MONTH, 1)
            intendedLunchTime = lunchTime.timeInMillis
        }
        val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val lunchNotificationIntent = Intent(applicationContext, AlarmReceiver::class.java)
        lunchNotificationIntent.putExtra("notification-id", 1)
        val lunchPendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            lunchNotificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.set(AlarmManager.RTC_WAKEUP, intendedLunchTime, lunchPendingIntent)
*/

        Handler().postDelayed(
            {
                //changed here
                startActivity(Intent(this, HomeActivity::class.java))
                this.finish()
            }, (1000).toLong()
        )

    }
}
