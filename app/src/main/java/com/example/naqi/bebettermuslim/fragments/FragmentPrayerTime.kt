package com.example.naqi.bebettermuslim.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.ASR_CONSTANT_KEY
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.DHUHR_CONSTANT_KEY
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.FAJAR_CONSTANT_KEY
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.ISHA_CONSTANT_KEY
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.MAGHRIB_CONSTANT_KEY
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.MONTHS
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.MUTED_AZAN
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.PREF_FILE_NAME
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.SUNSET_CONSTANT
import com.example.naqi.bebettermuslim.Utils.PrayTimeCalculation
import com.example.naqi.bebettermuslim.base_classes.CustomFragment
import com.example.naqi.bebettermuslim.callbacks.RecyclerViewItemClicked
import com.example.naqi.bebettermuslim.controllers.NotificationPublisher
import com.example.naqi.bebettermuslim.models.Holder
import org.joda.time.LocalDate
import org.joda.time.chrono.ISOChronology
import org.joda.time.chrono.IslamicChronology
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by naqi on 26,June,2019
 */

class FragmentPrayerTime : CustomFragment(), RecyclerViewItemClicked {

    private lateinit var rootView: View
    private lateinit var islamicDateText: TextView
    private lateinit var gregorianDateText: TextView
    private lateinit var prayerTimingRV: RecyclerView
    private lateinit var islamicDatePrev: ImageView
    private lateinit var islamicDateNext: ImageView
    private lateinit var prayerTimingAdapter: PrayTimeAdapter
    private var listOfTimes = ArrayList<Holder>()
    private var latitude = 0.0
    private var longitude = 0.0
    private var timezone = 0.0f
    private var prayers: PrayTimeCalculation? = null
    private var prayerTimes = ArrayList<String>()
    private var prayerNames = ArrayList<String>()
    private var holderArrayList = ArrayList<Holder>()
    private var newDate = Date()
    var cal: Calendar = Calendar.getInstance()
    private var namazMuteSet = mutableSetOf<String>()
    private var prefs: SharedPreferences? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_prayer_time, container, false)
        init(rootView)
        findViewsByIds(rootView)
        prepareRecyclerView()
        setListeners()
        return rootView
    }

    override fun init(rootView: View) {
        arguments?.let {
            latitude = it.getDouble(Constants.USER_LATITUDE)
            longitude = it.getDouble(Constants.USER_LONGITUDE)
            timezone = it.getFloat(Constants.USER_TIMEZONE)
        }
        val sharedPreferences = context?.getSharedPreferences(Constants.USER_SETTING, MODE_PRIVATE)
        prayers = PrayTimeCalculation()
        prayers?.let {

            it.timeFormat = Integer.parseInt(
                sharedPreferences?.getString(Constants.TIME_FORMAT, (it.Time12).toString())!!
            )
            it.calcMethod = Integer.parseInt(
                sharedPreferences.getString(Constants.CALCULATION_METHOD, it.Jafari.toString())!!
            )
            it.asrJuristic = Integer.parseInt(
                sharedPreferences.getString(Constants.ASR_JURISTIC, it.Shafii.toString())!!
            )
            it.adjustHighLats = Integer.parseInt(
                sharedPreferences.getString(Constants.HIGH_LATITUDE_ADJUSTMENT, it.AngleBased.toString())!!
            )
            prayerNames = it.getTimeNames()

        }
        cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        prefs = context?.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE)
        namazMuteSet = prefs?.getStringSet(Constants.MUTED_AZAN, namazMuteSet)!!

    }

    fun setPrayerTimes(cal: Calendar, shouldRemove: Boolean): ArrayList<Holder> {

        val englishDate = cal.getDisplayName(
            Calendar.MONTH,
            Calendar.SHORT,
            Locale.US
        ) + " " + cal.get(Calendar.DATE) + ", " + cal.get(
            Calendar.YEAR
        )
        gregorianDateText.setText(englishDate)

        val iso = ISOChronology.getInstanceUTC()
        val hijri = IslamicChronology.getInstanceUTC()

        val todayIso = LocalDate(cal, iso)
        val todayHijri = LocalDate(
            todayIso.toDateTimeAtStartOfDay(),
            hijri
        )

        val islamicDate =
            MONTHS[todayHijri.monthOfYear - 1] + " " + todayHijri.dayOfMonth + ", " + todayHijri.year + " AH"

        islamicDateText.text = islamicDate

        prayerTimes = prayers!!.getPrayerTimes(cal, latitude, longitude, timezone.toDouble())

        //removing sunrise and sunset from the list
        if (shouldRemove) {
            prayerTimes.removeAt(SUNSET_CONSTANT)
            prayerNames.removeAt(SUNSET_CONSTANT)
        }
//        prayerTimes.removeAt(SUNRISE_CONSTANT)
//        prayerNames.removeAt(SUNRISE_CONSTANT)

        holderArrayList.clear()
        for (i in prayerNames.indices) {
            val temp = Holder(prayerNames[i], prayerTimes[i].toUpperCase())
            holderArrayList.add(temp)
        }
        return holderArrayList
    }

    override fun recyclerViewListClicked(v: View, img: ImageView, position: Int) {
//        scheduleNotification(listOfTimes[position].prayTimes, newDate)
//        val editor = context?.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE)?.edit()
//        if (!namazMuteSet.add(position.toString())) {
//            namazMuteSet.remove(position.toString())
//            img.setImageResource(R.drawable.volume_high_fill)
//            v.alpha = 1.0f
//            editor?.putStringSet(MUTED_AZAN, namazMuteSet)
//            editor?.apply()
//        } else {
//            v.alpha = 0.5f
//            img.setImageResource(R.drawable.volume_low)
//            editor?.putStringSet(MUTED_AZAN, namazMuteSet)
//            editor?.apply()
//        }
        when (position) {
            0 -> {
                setNamazState(img, FAJAR_CONSTANT_KEY)
            }
            1 -> {
            }
            2 -> {
                setNamazState(img, DHUHR_CONSTANT_KEY)
            }
            3 -> {
                setNamazState(img, ASR_CONSTANT_KEY)
            }
            4 -> {
                setNamazState(img, MAGHRIB_CONSTANT_KEY)
            }
            5 -> {
                setNamazState(img, ISHA_CONSTANT_KEY)
            }
        }

    }

    private fun setNamazState(img: ImageView, namazKey: String) {
        val prefEditor = context?.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE)?.edit()
        var namazState = prefs?.getInt(namazKey, 0)
        when (namazState) {
            0 -> {
                namazState = 1
                prefEditor?.putInt(namazKey, namazState)
                prefEditor?.apply()
                img.setImageResource(R.drawable.notification_only)

            }
            1 -> {
                namazState = 2
                prefEditor?.putInt(namazKey, namazState)
                prefEditor?.apply()
                img.setImageResource(R.drawable.no_alarm_no_notification)
            }
            2 -> {
                namazState = 0
                prefEditor?.putInt(namazKey, namazState)
                prefEditor?.apply()
                img.setImageResource(R.drawable.alarm_with_notification)
            }
        }

    }

    override fun findViewsByIds(rootView: View) {
        islamicDateText = rootView.findViewById(R.id.islamicDateText)
        gregorianDateText = rootView.findViewById(R.id.gregorianDateText)
        prayerTimingRV = rootView.findViewById(R.id.prayerTimingRV)
        islamicDatePrev = rootView.findViewById(R.id.islamicDatePrev)
        islamicDateNext = rootView.findViewById(R.id.islamicDateNext)


    }

    override fun setListeners() {

        islamicDatePrev.setOnClickListener {
            gregorianDateText.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
            islamicDateText.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
            cal.add(Calendar.DAY_OF_MONTH, -1)
            newDate = Date(cal.timeInMillis)
            prayerTimingAdapter.updateList(setPrayerTimes(cal, false))
        }

        islamicDateNext.setOnClickListener {
            gregorianDateText.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
            islamicDateText.startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
            cal.add(Calendar.DAY_OF_MONTH, 1)
            newDate = Date(cal.timeInMillis)
            prayerTimingAdapter.updateList(setPrayerTimes(cal, false))
        }


    }

    override fun prepareRecyclerView() {
        listOfTimes = setPrayerTimes(cal, true)
        prayerTimingAdapter = PrayTimeAdapter(context!!, listOfTimes, this, namazMuteSet)
        prayerTimingRV.layoutManager = LinearLayoutManager(context!!)
        prayerTimingRV.adapter = prayerTimingAdapter
        prayerTimingRV.itemAnimator = DefaultItemAnimator()
        prayerTimingRV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

    }

    private fun scheduleNotification(times: String, date: Date) {
        val listOfTimesCal = ArrayList<Calendar>()
        val listOfIntendedTime = ArrayList<Long>()
        var formatedime = times
        if (context != null) {
            val alarmManager = context?.applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val cal2 = Calendar.getInstance()
            val lunchNotificationIntent = Intent(activity?.applicationContext, NotificationPublisher::class.java)
            var counterAdd = 0
            if (times.contains("AM")) {
                formatedime = times.replace(" AM", "")
            } else if (times.contains("PM")) {
                formatedime = times.replace(" PM", "")
            }
            val separated = formatedime.split(":")
            // this will contain "Fruit"

            cal2.set(2019, date.month, date.date, separated[0].toInt(), separated[1].toInt())
            listOfTimesCal.add(cal2)
            if (cal2.after(Calendar.getInstance())) {
                listOfIntendedTime.add(cal2.timeInMillis)
                lunchNotificationIntent.putExtra(NotificationPublisher.PRAYER_NOTIFICATION_ID, 0)
                val lunchPendingIntent = PendingIntent.getBroadcast(
                    context,
                    counterAdd + 3,
                    lunchNotificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                Log.v("AlarmSetOf CH$counterAdd", "Alarm Set Of ${Date(listOfIntendedTime[counterAdd])}")

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, listOfIntendedTime[counterAdd], lunchPendingIntent)
                counterAdd += 1


            }
        }

    }

}

class PrayTimeAdapter(
    var context: Context,
    var list: ArrayList<Holder>,
    var itemClicked: RecyclerViewItemClicked,
    val namazMuteSet: MutableSet<String>
) :
    RecyclerView.Adapter<PrayTimeAdapter.PrayTimeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayTimeViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_prayer_timings, parent, false
        )
        return PrayTimeViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(newList: ArrayList<Holder>) {
//        list.clear()
//        list.addAll(newList)
        list = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: PrayTimeViewHolder, position: Int) {
        holder.prayerName.setText(list[position].prayNames)
        holder.prayerTimings.setText(list[position].prayTimes)
//        var lastPosition = 2
        holder.alarmButtonListview.setOnClickListener {
            //            holder.alarmButtonListview.setImageResource(R.drawable.volume_low)
        }


        holder.prayerTimeLayout.setOnClickListener {
            itemClicked.recyclerViewListClicked(holder.prayerTimeLayout, holder.alarmButtonListview, position)
        }

        val pref = context?.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE)
        var namzState = pref.getInt(position.toString(), 0)
        when (namzState) {
            0 -> {
                holder.alarmButtonListview.setImageResource(R.drawable.alarm_with_notification)
            }
            1 -> {
                holder.alarmButtonListview.setImageResource(R.drawable.notification_only)

            }
            2 -> {
                holder.alarmButtonListview.setImageResource(R.drawable.no_alarm_no_notification)

            }
        }
/*        if (namazMuteSet.contains(position.toString())) {
//            holder.prayerTimeLayout.alpha = 0.5f
            holder.alarmButtonListview.setImageResource(R.drawable.volume_low)

        } else {
//            holder.prayerTimeLayout.alpha = 1.0f
            holder.alarmButtonListview.setImageResource(R.drawable.volume_high_fill)
        }*/


        val animation = AnimationUtils.loadAnimation(
            context,
//            if (position > lastPosition)
            R.anim.up_from_bottom
//            else
//                R.anim.down_from_top
        )
        holder.itemView.startAnimation(animation)
//        lastPosition = position
    }

    override fun onViewDetachedFromWindow(holder: PrayTimeViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    class PrayTimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var alarmButtonListview: ImageView
        var prayerName: TextView
        var prayerTimings: TextView
        var prayerTimeLayout: LinearLayout

        init {
            alarmButtonListview = itemView.findViewById<View>(R.id.alarm_button_listview) as ImageView
            prayerName = itemView.findViewById<View>(R.id.prayer_name) as TextView
            prayerTimings = itemView.findViewById<View>(R.id.prayer_timings) as TextView
            prayerTimeLayout = itemView.findViewById<View>(R.id.prayerTimeLayout) as LinearLayout

        }
    }
}