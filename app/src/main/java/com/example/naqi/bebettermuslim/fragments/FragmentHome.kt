package com.example.naqi.bebettermuslim.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.ASR_JURISTIC
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.CALCULATION_METHOD
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.HIGH_LATITUDE_ADJUSTMENT
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.NAMAZ_POSITION
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.PRAYER_TIMING
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.PREF_FILE_NAME
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.TIME_12
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.TIME_24
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.TIME_FORMAT
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.staticFormattedTimings
import com.example.naqi.bebettermuslim.Utils.PrayTimeCalculation
import com.example.naqi.bebettermuslim.Utils.Utils
import com.example.naqi.bebettermuslim.activities.HomeActivity
import com.example.naqi.bebettermuslim.adapter.AdapterImagesViewPager
import com.example.naqi.bebettermuslim.adapter.AdapterPrayerTimeViewPager
import com.example.naqi.bebettermuslim.base_classes.CustomFragment
import com.example.naqi.bebettermuslim.controllers.NotificationPublisher
import com.example.naqi.bebettermuslim.models.CustomeModel
import com.example.naqi.bebettermuslim.setting_activities.Test2Settings
import com.example.naqi.bebettermuslim.views.DepthTransformation
import com.example.naqi.bebettermuslim.views.FadeOutTransformation
import com.example.naqi.bebettermuslim.views.SyncScrollOnTouchListener
import org.joda.time.LocalDate
import org.joda.time.chrono.ISOChronology
import org.joda.time.chrono.IslamicChronology
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FragmentHome : CustomFragment() {

    private lateinit var rootView: View
    private lateinit var prayerTimeCalculation: PrayTimeCalculation
    private var prayerNames = ArrayList<String>()
    private var prayerTimings = ArrayList<String>()
    private var prayers24HrPrayerTimings = ArrayList<String>()
    private lateinit var prayerTimingPager: ViewPager
    private lateinit var timingAdapter: AdapterPrayerTimeViewPager
    private lateinit var imagesAdapter: AdapterImagesViewPager
    private lateinit var backgroundImagePager: ViewPager
    private lateinit var islamicDateView: TextView
    private lateinit var slideMenuBtn: ImageButton
    private lateinit var settingsBtn: ImageButton
    private lateinit var noAlarmButton: ImageButton
    private lateinit var notifyButton: ImageButton
    private lateinit var alarmButton: ImageButton
    private var currentPrayerName = ""
    private var lat = 0.0
    private var long = 0.0
    private var timeZone = 0.0f
    private var timeFormat = 1
    private var calculationMethod = 0
    private var highLatitudeAdj = 0
    private var asrJuristic = 0
    private var namazPosition = 0
    private var cal: Calendar? = null
    private lateinit var dateView: TextView
    private var prayers: PrayTimeCalculation? = null
    private var prayers24Hr: PrayTimeCalculation? = null
    lateinit var prefSettings: SharedPreferences
    private val datedFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US)

    private var prayerTimeTomorrowList = ArrayList<String>()
    private var prayers24HrTomorrowList = ArrayList<String>()
    private var formattedTimings: ArrayList<Date>? = null
    private lateinit var customModel: CustomeModel
    private var sharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { pref, key ->
        timeFormat = Integer.parseInt(pref.getString(Constants.TIME_FORMAT, prayers?.Time12.toString()))
        calculationMethod = Integer.parseInt(pref.getString(Constants.CALCULATION_METHOD, prayers?.Karachi.toString()))
        highLatitudeAdj = Integer.parseInt(pref.getString(Constants.HIGH_LATITUDE_ADJUSTMENT, prayers?.None.toString()))
        asrJuristic = Integer.parseInt(pref.getString(Constants.ASR_JURISTIC, prayers?.Hanafi.toString()))

        println("Here, Key changed")
        providePrayerTiming(lat, long, timeZone.toDouble(), timeFormat, asrJuristic, calculationMethod)
        try {
            Log.v("BBM", "KeyChange")
            prayerTimingPager.invalidate()
            timingAdapter.updateList(prayerTimings)
            prayerTimingPager.post {
                prayerTimingPager.setCurrentItem(namazPosition + 1, true)
//            prayerTimingPager.setCurrentItem(namazPosition, true)
                val handler = Handler()
                handler.postDelayed(
                    {
                        prayerTimingPager.setCurrentItem(namazPosition, true)
                    }, 2000
                )
            }

//            prayerTimingPager
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        when (key) {

            PRAYER_TIMING -> {
            }
            TIME_FORMAT -> {

            }
            CALCULATION_METHOD -> {

            }
            HIGH_LATITUDE_ADJUSTMENT -> {

            }
            ASR_JURISTIC -> {

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_home, container, false)
        arguments?.let {
            lat = it.getDouble(Constants.USER_LATITUDE)
            long = it.getDouble(Constants.USER_LONGITUDE)
            timeZone = it.getFloat(Constants.USER_TIMEZONE)
        }

        init(rootView)
        findViewsByIds(rootView)
        setListeners()

        formattedTimings = formatTimings(cal!!, prayerTimings, timeFormat)
        setUpViewPager(formattedTimings)
        staticFormattedTimings = formattedTimings
        setData()
        return rootView
    }

    private fun setData() {
        val dateFormat = when (prayers!!.timeFormat) {
            TIME_24 -> SimpleDateFormat("HH:mm", Locale.US)
            TIME_12 -> SimpleDateFormat("hh:mm aaa", Locale.US)
            else -> SimpleDateFormat("HH:mm a", Locale.US)

        }
//        var dateString = dateFormat.format(cal!!.time)


//          setting islamic date here
        val iso = ISOChronology.getInstanceUTC()
        val hijri = IslamicChronology.getInstanceUTC()
        val todayIso = LocalDate(cal, iso)
        val todayHijri = LocalDate(
            todayIso.toDateTimeAtStartOfDay(),
            hijri
        )
        val islamicDate =
            Constants.MONTHS[todayHijri.monthOfYear - 1] + " " + todayHijri.dayOfMonth + ", " + todayHijri.year + " AH"
        islamicDateView.text = islamicDate

        var position = 0
        var date: Date = Date()
        var todayTime = dateFormat.format(date)
        val dateFormatiing = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val dateOnly = dateFormatiing.format(date)
//        val parseFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.US)
        val parseFormat = when (prayers!!.timeFormat) {
            TIME_24 -> SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
            TIME_12 -> SimpleDateFormat("dd/MM/yyyy hh:mm aaa", Locale.US)
            else -> SimpleDateFormat("dd/MM/yyyy HH:mm a", Locale.US)
        }
        var date2 = parseFormat.parse(parseFormat.format(cal!!.time))
        date = parseFormat.parse("$dateOnly $todayTime")
        Log.v("Mujtaba", date.toString())
        val isTFour = prefSettings.getBoolean(Constants.TIME_HOURS_SWITCH, false)
        val sharedPreferences = context?.getSharedPreferences(Constants.USER_SETTING, MODE_PRIVATE)
        sharedPreferences?.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
        formattedTimings?.let {
            for (i in 0 until prayerTimings.size) {
                if (prayers!!.timeFormat == TIME_24) {
                    if (date.before(it[i])) {
                        position = i
                        break
                    }
                } else if (prayers!!.timeFormat == TIME_12) {
                    if (date.before(it[i])) {
                        position = i
                        break
                    }
                }
            }
        }

        /**current namaz timing to home screen view*/
        val prefEditor = activity?.getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE)?.edit()
        prefEditor?.putInt(Constants.NAMAZ_POSITION, position)
        prefEditor?.apply()



        prayerTimingPager.currentItem = position
        backgroundImagePager.currentItem = position
        namazPosition = position
//              checkPreferece()
//    dateFormat.format(System.currentTimeMillis())
        dateView.text = datedFormat.format(System.currentTimeMillis())
    }

    private fun formatTimings(cal: Calendar, prayerTimes: ArrayList<String>, timeFormat: Int): ArrayList<Date>? {
        val formattedTimings: ArrayList<Date>
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val dateTimeFormat = when (timeFormat) {
            TIME_24 -> SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
            TIME_12 -> SimpleDateFormat("dd/MM/yyyy hh:mm aaa", Locale.US)
            else -> SimpleDateFormat("dd/MM/yyyy HH:mm a", Locale.US)
        }
        try {
            val dateString = dateFormat.format(cal.time)
            formattedTimings = ArrayList()
            for (i in 0 until prayerTimes.size) {
                formattedTimings.add(
                    dateTimeFormat.parse("$dateString ${prayerTimes[i].replace("am", "AM").replace("pm", "PM")}")
                )
//                formattedTimings.add(dateTimeFormat.parse(dateString + " " + prayerTimes[i].replace(" ", ":00 ")))
            }
            return formattedTimings
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun setUpViewPager(dates: ArrayList<Date>?) {
        val pref = context?.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE)
        val depthTransformation = DepthTransformation()
        val fadeOutTransformation = FadeOutTransformation()
        val drawables = resources.obtainTypedArray(R.array.prayer_time_images)
        prayerTimingPager.setPageTransformer(true, depthTransformation)
        backgroundImagePager.setPageTransformer(true, fadeOutTransformation)

        prayerTimingPager.offscreenPageLimit = 5
        backgroundImagePager.offscreenPageLimit = 5
        var reverse = 0f
        context?.let {
            timingAdapter = AdapterPrayerTimeViewPager(it, dates!!, prayerTimings, prayerNames)
            imagesAdapter = AdapterImagesViewPager(it, drawables)
            prayerTimingPager.adapter = timingAdapter
            backgroundImagePager.adapter = imagesAdapter

            prayerTimingPager.pageMargin = Utils.dpToPxInInt(it, 5)
            prayerTimingPager.setOnTouchListener(SyncScrollOnTouchListener(backgroundImagePager));
            backgroundImagePager.setOnTouchListener { v, event -> true }
            prayerTimingPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                var mScrollState = ViewPager.SCROLL_STATE_IDLE
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                }

                override fun onPageSelected(position: Int) {
                    backgroundImagePager.setCurrentItem(position, true);

                    var dar = position % 6
                    var state = pref?.getInt(dar.toString(), 0);
                    when (state) {
                        0 -> {

                            alarmButton.setImageResource(R.drawable.alarm_sound_fill)
                            notifyButton.setImageResource(R.drawable.noalarm_no_fill)
                            noAlarmButton.setImageResource(R.drawable.no_notify_no_fill)
                        }
                        1 -> {
                            alarmButton.setImageResource(R.drawable.alarm_sound_no_fill)
                            notifyButton.setImageResource(R.drawable.no_alarm_fill)
                            noAlarmButton.setImageResource(R.drawable.no_notify_no_fill)
                        }
                        2 -> {
                            alarmButton.setImageResource(R.drawable.alarm_sound_no_fill)
                            notifyButton.setImageResource(R.drawable.noalarm_no_fill)
                            noAlarmButton.setImageResource(R.drawable.no_notify_fill)
                        }
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        /*providePrayerTiming(lat, long, timeZone.toDouble())
        prayerTimingPager.invalidate()
        timingAdapter.updateList(prayerTimings)*/
//        imagesAdapter.notifyDataSetChanged()

    }

    private fun providePrayerTiming(
        lat: Double,
        long: Double,
        timeZone: Double,
        mTimeFormat: Int,
        jurisMethod: Int,
        calcMethod: Int
    ) {

        prayers = PrayTimeCalculation()
        prayers24Hr = PrayTimeCalculation()
        cal = Calendar.getInstance()
        cal!!.timeInMillis = System.currentTimeMillis()
        val newCal = Calendar.getInstance()
        newCal.add(Calendar.DAY_OF_MONTH, 1)
        //scheduling alarms here  for 2-16 days based on for loop iterations
        val listOfPrayerObj = ArrayList<PrayTimeCalculation>()
        val listOf100Times = ArrayList<ArrayList<String>>()
        for (prayerInt in 0..1) {
            //todo 16 here
            val calObj = Calendar.getInstance()
            calObj.add(Calendar.DAY_OF_MONTH, prayerInt)
            val prayer100TimingObj = PrayTimeCalculation()
            listOfPrayerObj.add(prayer100TimingObj)
            listOfPrayerObj[prayerInt].timeFormat = prayers24Hr!!.Time24
            listOfPrayerObj[prayerInt].asrJuristic = jurisMethod
            listOfPrayerObj[prayerInt].calcMethod = calcMethod
            Log.v("AlarmSetOf PT $prayerInt", "Prayer set of ${Date(calObj.timeInMillis)}")
            val tempPrayerTimeObj = listOfPrayerObj[prayerInt].getPrayerTimes(calObj, lat, long, timeZone)
            tempPrayerTimeObj.removeAt(4)
            listOf100Times.add(tempPrayerTimeObj)
            scheduleNotification(listOf100Times[prayerInt], Date(calObj.timeInMillis))
//            scheduleNotification()
        }


        prayers?.let {
            it.timeFormat = mTimeFormat
            prayers24Hr!!.timeFormat = prayers24Hr!!.Time24
            /*  val isTFour = prefSettings.getBoolean(Constants.TIME_HOURS_SWITCH, false)
              if (isTFour) {
                  it.timeFormat = mTimeFormat
              } else {
                  it.timeFormat = mTimeFormat
              }*/
            it.asrJuristic = jurisMethod
            prayers24Hr!!.asrJuristic = jurisMethod

            it.calcMethod = calcMethod
            prayers24Hr!!.calcMethod = calcMethod

            prayerTimings = it.getPrayerTimes(cal, lat, long, timeZone)
            prayers24HrPrayerTimings = prayers24Hr!!.getPrayerTimes(cal, lat, long, timeZone)

            prayerTimeTomorrowList = it.getPrayerTimes(newCal, lat, long, timeZone)
            prayers24HrTomorrowList = prayers24Hr!!.getPrayerTimes(newCal, lat, long, timeZone)

            prayerNames = it.getTimeNames()
            prayerTimings.removeAt(4)
            prayerTimeTomorrowList.removeAt(4)
            prayerNames.removeAt(4)
        }
        prayerTimings.addAll(prayerTimeTomorrowList)
        prayerNames.addAll(prayerNames)

    }

    override fun init(rootView: View) {
        prefSettings = activity!!.getSharedPreferences(Constants.USER_SETTING, Context.MODE_PRIVATE)
        timeFormat = Integer.parseInt(prefSettings.getString(Constants.TIME_FORMAT, "1"))
        calculationMethod = Integer.parseInt(prefSettings.getString(Constants.CALCULATION_METHOD, "1"))
        highLatitudeAdj = Integer.parseInt(prefSettings.getString(Constants.HIGH_LATITUDE_ADJUSTMENT, "1"))
        asrJuristic = Integer.parseInt(prefSettings.getString(Constants.ASR_JURISTIC, "1"))
        providePrayerTiming(lat, long, timeZone.toDouble(), timeFormat, asrJuristic, calculationMethod)

    }


    override fun findViewsByIds(rootView: View) {
        prayerTimingPager = rootView.findViewById(R.id.prayerTimingPager)
        backgroundImagePager = rootView.findViewById(R.id.backgroundImagePager)
        slideMenuBtn = rootView.findViewById(R.id.slideMenuBtn)
        settingsBtn = rootView.findViewById(R.id.settingsBtn)
        dateView = rootView.findViewById(R.id.dateView)

        islamicDateView = rootView.findViewById(R.id.islamicDateView)

        noAlarmButton = rootView.findViewById(R.id.noAlarmButton)
        notifyButton = rootView.findViewById(R.id.notifyButton)
        alarmButton = rootView.findViewById(R.id.alarmButton)

    }

    override fun setListeners() {
        slideMenuBtn.setOnClickListener {
            (activity as HomeActivity).openNavDrawer()
        }
        settingsBtn.setOnClickListener {
            val intent = Intent(context, Test2Settings::class.java)
            startActivity(intent)


        }

        alarmButton.setOnClickListener {
            val currentPrayerItem = prayerTimingPager.currentItem % 6
            setNamazState(currentPrayerItem.toString(), 0)
            alarmButton.setImageResource(R.drawable.alarm_sound_fill)
            notifyButton.setImageResource(R.drawable.noalarm_no_fill)
            noAlarmButton.setImageResource(R.drawable.no_notify_no_fill)
        }
        notifyButton.setOnClickListener {
            val currentPrayerItem = prayerTimingPager.currentItem % 6

            setNamazState(currentPrayerItem.toString(), 1)
            alarmButton.setImageResource(R.drawable.alarm_sound_no_fill)
            notifyButton.setImageResource(R.drawable.no_alarm_fill)
            noAlarmButton.setImageResource(R.drawable.no_notify_no_fill)
        }

        noAlarmButton.setOnClickListener {
            val currentPrayerItem = prayerTimingPager.currentItem % 6
            setNamazState(currentPrayerItem.toString(), 2)
            alarmButton.setImageResource(R.drawable.alarm_sound_no_fill)
            notifyButton.setImageResource(R.drawable.noalarm_no_fill)
            noAlarmButton.setImageResource(R.drawable.no_notify_fill)

        }
    }


    private fun setNamazState(namazKey: String, state: Int) {
        val prefEditor = context?.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE)?.edit()
        prefEditor?.putInt(namazKey, state)
        prefEditor?.apply()
    }

    override fun prepareRecyclerView() {
    }

    private fun scheduleNotification(listOfTimes: ArrayList<String>, date: Date) {
        val listOfTimesCal = ArrayList<Calendar>()
        val listOfIntendedTime = ArrayList<Long>()
        if (context != null) {
            val alarmManager = context?.applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val cal2 = Calendar.getInstance()
            val lunchNotificationIntent = Intent(activity?.applicationContext, NotificationPublisher::class.java)
            var counterAdd = 0
            for (time in listOfTimes.indices) {
                val separated = listOfTimes[time].split(":")
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

//    private fun scheduleNotification() {
//        val lunchTime = Calendar.getINSTANCE()
//
//        val currentCal = Calendar.getINSTANCE()
//
//        var listOfTimes = ArrayList<String>()
//        val listOfTimesCal = ArrayList<Calendar>()
//        val listOfIntendedTime = ArrayList<Long>()
//
//        val cal = Calendar.getINSTANCE()
//        val date = Date()
//        listOfTimes.add("15:03")
//        listOfTimes.add("15:05")
//        listOfTimes.add("15:06")
//        listOfTimes.add("15:09")
//        listOfTimes.add("15:10")
//
//        lunchTime.set(Calendar.HOUR_OF_DAY, 14) // At the hour you wanna fire
//        lunchTime.set(Calendar.MINUTE, 57) // Particular minute
//        lunchTime.set(Calendar.SECOND, 0)
//        lunchTime.set(Calendar.MILLISECOND, 0) // particular milli second
////        lunchTime.set(2013,2,7,15,42,30)
//
//
//
//        var intendedLunchTime = lunchTime.timeInMillis
//
//        val currentTime = currentCal.timeInMillis
//
//
//        if (intendedLunchTime < currentTime) {
//            lunchTime.add(Calendar.DAY_OF_MONTH, 1)
//            intendedLunchTime = lunchTime.timeInMillis
//        }
//
//        Log.e("Lunch Time", " $intendedLunchTime")
//        Log.e("Current Time", " $currentTime")
//
//
//        val alarmManager = activity?.applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        val lunchNotificationIntent = Intent(activity?.applicationContext, NotificationPublisher::class.java)
//   /*     val lunchPendingIntent = PendingIntent.getBroadcast(
//            context,
//            1,
//            lunchNotificationIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )*/
////        lunchNotificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1)
////        val lunchPendingIntent = PendingIntent.getBroadcast(
////            this,
////            1,
////            lunchNotificationIntent,
////            PendingIntent.FLAG_UPDATE_CURRENT
////        )
//
//
//        for(time in listOfTimes.indices){
//
//            val separated = listOfTimes[time].split(":")
//            // this will contain "Fruit"
//            cal.set(2019,date.month,date.date,separated[0].toInt(),separated[1].toInt())
//            listOfTimesCal.add(cal)
//            listOfIntendedTime.add(cal.timeInMillis)
//            lunchNotificationIntent.putExtra("${time+3}", 1)
//            val lunchPendingIntent = PendingIntent.getBroadcast(
//                context,
//                time+3,
//                lunchNotificationIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT
//            )
//
//            alarmManager.set(AlarmManager.RTC_WAKEUP, listOfIntendedTime[time],lunchPendingIntent)
//        }
////        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, intendedLunchTime, 60000,lunchPendingIntent)
////        alarmManager.set(AlarmManager.RTC_WAKEUP, intendedDinnerTime, dinnerPendingIntent)
//
//    }


}