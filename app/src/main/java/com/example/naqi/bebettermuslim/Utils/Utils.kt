package com.example.naqi.bebettermuslim.Utils

import android.app.PendingIntent.getActivity
import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Utils {
    companion object {
        fun dpToPxInInt(context: Context, valueInDp: Int): Int {
            val d = context.resources.displayMetrics.density;
            return (valueInDp * d).toInt()

        }

        fun dpToPx(context: Context, valueInDp: Float): Float {
            val metrics = context.resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
        }

        fun spToPx(context: Context, valueInSp: Float): Float {
            val metrics = context.resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, valueInSp, metrics)
        }

        fun getCurrentTime(): Long = System.currentTimeMillis() / 1000L

        fun formatPlayTime(time: Long): String {
            val cal = Calendar.getInstance()
            cal.timeInMillis = time
            val sf = SimpleDateFormat("mm:ss")
            sf.timeZone = cal.timeZone
            val date = Date((time - Utils.getSecondsFromGMT()) * 1000)
            return sf.format(date)
        }

        fun getSecondsFromGMT(): Long {
            val tz = TimeZone.getDefault()
            val date = Date()
            return tz.getOffset(date.time) / 1000L
        }
        fun formatDefaultIslamicDate(adj: Int): String {
            val sf = SimpleDateFormat("EEEE")
            val date = Date()
            return DateHijri.writeIslamicDateHomeFeed(adj)
        }

        fun formatIslamicDate(adj: Int): String {
            val sf = SimpleDateFormat("EEEE")
            val date = Date()
            return sf.format(date) + DateHijri.writeIslamicDateHomeFeed(adj)
        }




    }


}