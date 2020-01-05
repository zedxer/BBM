package com.example.naqi.bebettermuslim.fragments

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.staticFormattedTimings
import com.example.naqi.bebettermuslim.Utils.PrayTimeCalculation
import com.example.naqi.bebettermuslim.base_classes.CustomFragment
import com.example.naqi.bebettermuslim.callbacks.RecyclerViewHolderCallback
import com.example.naqi.bebettermuslim.controllers.NotificationPublisher
import com.example.naqi.bebettermuslim.data.PreNamazAlarmManager
import com.example.naqi.bebettermuslim.fragments.FragmentPreNamazAlarm.Companion.ALARM_TIME
import com.example.naqi.bebettermuslim.fragments.FragmentPreNamazAlarm.Companion.CELL_ITEM
import com.example.naqi.bebettermuslim.models.Holder
import com.example.naqi.bebettermuslim.models.PreAzanAlarmModel
import com.example.naqi.bebettermuslim.views.TimePickerPopWin
import io.realm.RealmList
import org.joda.time.DateTime
import java.text.ParseException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.random.Random


/**
 * Created by naqi on 02,July,2019
 */
class FragmentPreNamazAlarm : CustomFragment(), RecyclerViewHolderCallback {


    private lateinit var rootView: View
    private lateinit var preAzanRecycler: RecyclerView
    private var prayers: PrayTimeCalculation? = null
    private var latitude = 0.0
    private var longitude = 0.0
    private var timezone = 0.0f
    var cal: Calendar = Calendar.getInstance()
    private var prefs: SharedPreferences? = null
    private var prayerTimes = ArrayList<String>()
    private var prayerNames = ArrayList<String>()
    private var holderArrayList = ArrayList<Holder>()
    private var alarmSet = mutableSetOf<String>()
    private val preAzanManager = PreNamazAlarmManager.INSTANCE
    private var databaseList: List<PreAzanAlarmModel>? = null
    private var timeFormat: Int = 0
    private var calcMethod: Int = 0
    private var asrJuristic: Int = 0
    private var adjustHighLats: Int = 0

    companion object {
        val ALARM_TIME = 1
        val CELL_ITEM = 2
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_pre_azan, container, false)
        init(rootView)
        findViewsByIds(rootView)
        prepareRecyclerView()
        return rootView
    }

    override fun init(rootView: View) {
        arguments?.let {
            latitude = it.getDouble(Constants.USER_LATITUDE)
            longitude = it.getDouble(Constants.USER_LONGITUDE)
            timezone = it.getFloat(Constants.USER_TIMEZONE)
        }
        val sharedPreferences = context?.getSharedPreferences(Constants.USER_SETTING, Context.MODE_PRIVATE)
        prayers = PrayTimeCalculation()

        prayers?.let {
            timeFormat = Integer.parseInt(sharedPreferences?.getString(Constants.TIME_FORMAT, (it.Time12).toString())!!)
            calcMethod = Integer.parseInt(
                sharedPreferences.getString(Constants.CALCULATION_METHOD, it.Jafari.toString())!!
            )
            asrJuristic = Integer.parseInt(
                sharedPreferences.getString(Constants.ASR_JURISTIC, it.Shafii.toString())!!
            )
            adjustHighLats = Integer.parseInt(
                sharedPreferences.getString(Constants.HIGH_LATITUDE_ADJUSTMENT, it.AngleBased.toString())!!
            )
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
        alarmSet = prefs?.getStringSet("ALARMSET", alarmSet)!!

    }

    override fun findViewsByIds(rootView: View) {
        preAzanRecycler = rootView.findViewById(R.id.preAzanRecycler)
    }

    override fun setListeners() {

    }

    fun scheduleSingleTimeNotifications(prayerId: Int, timez: String) {
        val listOfPrayerObj = ArrayList<PrayTimeCalculation>()
        val separated = timez.split(":")
        for (prayerInt in 0..2) {
            //todo 16 here
            val calObj = Calendar.getInstance()
            calObj.add(Calendar.DAY_OF_MONTH, prayerInt)
            val prayer100TimingObj = PrayTimeCalculation()
            listOfPrayerObj.add(prayer100TimingObj)
            listOfPrayerObj[prayerInt].timeFormat = prayers!!.Time24
            listOfPrayerObj[prayerInt].asrJuristic = asrJuristic
            listOfPrayerObj[prayerInt].calcMethod = calcMethod
            Log.v("AlarmSetOf PT $prayerInt", "Prayer set of ${Date(calObj.timeInMillis)}")
            val tempPrayerTimeObj =
                listOfPrayerObj[prayerInt].getPrayerTimes(calObj, latitude, longitude, timezone.toDouble())
            tempPrayerTimeObj.removeAt(4)
//            listOf100Times.add(tempPrayerTimeObj)
//            listOfSinglePrayerTime.add(tempPrayerTimeObj[prayerId])

            scheduleNotification(
                tempPrayerTimeObj[prayerId],
                Date(calObj.timeInMillis),
                separated[0].toInt(),
                separated[1].toInt()
            )
        }
    }

//    fun scheduleAllNotifications(prayerId: Int, timez: String) {
//        val listOfPrayerObj = ArrayList<PrayTimeCalculation>()
//        val listOf100Times = ArrayList<ArrayList<String>>()
//        val separated = timez.split(":")
//
//        for (prayerInt in 0..1) {
//            //todo 16 here
//            val calObj = Calendar.getInstance()
//            calObj.add(Calendar.DAY_OF_MONTH, prayerInt)
//            val prayer100TimingObj = PrayTimeCalculation()
//            listOfPrayerObj.add(prayer100TimingObj)
//            listOfPrayerObj[prayerInt].timeFormat = prayers!!.Time24
//            listOfPrayerObj[prayerInt].asrJuristic = asrJuristic
//            listOfPrayerObj[prayerInt].calcMethod = calcMethod
//            Log.v("AlarmSetOf PT $prayerInt", "Prayer set of ${Date(calObj.timeInMillis)}")
//            val tempPrayerTimeObj =
//                listOfPrayerObj[prayerInt].getPrayerTimes(calObj, latitude, longitude, timezone.toDouble())
//            tempPrayerTimeObj.removeAt(4)
//            listOf100Times.add(tempPrayerTimeObj)
//            scheduleNotification(
//                listOf100Times[prayerInt],
//                Date(calObj.timeInMillis),
//                separated[0].toInt(),
//                separated[1].toInt()
//            )
////            scheduleNotification()
//        }
//    }

    private fun scheduleNotification(time: String, date: Date, hourAdded: Int, minuteAdded: Int) {
        val listOfTimesCal = ArrayList<Calendar>()
        val listOfIntendedTime = ArrayList<Long>()
        if (context != null) {
            val alarmManager = context?.applicationContext?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val cal2 = Calendar.getInstance()
            val lunchNotificationIntent = Intent(activity?.applicationContext, NotificationPublisher::class.java)
            var counterAdd = 0
            val separated = time.split(":")
            // this will contain "Fruit"
            cal2.set(cal2.get(Calendar.YEAR), date.month, date.date, separated[0].toInt(), separated[1].toInt())
            cal2.add(Calendar.HOUR_OF_DAY, -hourAdded)
            cal2.add(Calendar.MINUTE, -minuteAdded)
            listOfTimesCal.add(cal2)
                if (cal2.after(Calendar.getInstance())) {
            listOfIntendedTime.add(cal2.timeInMillis)
            lunchNotificationIntent.action = "com.example.naqi.bebettermuslim.controllers.NotificationPublisher"
            lunchNotificationIntent.putExtra(NotificationPublisher.PRAYER_NOTIFICATION_ID, 5)
            val lunchPendingIntent = PendingIntent.getBroadcast(
                context,
                Random.nextInt()+8,
                lunchNotificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            Log.v("AlarmSetOf CH$counterAdd", "Alarm Set Of ${Date(listOfIntendedTime[counterAdd])}")

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, listOfIntendedTime[counterAdd], lunchPendingIntent)

                }
//            }
        }

    }


    override fun prepareRecyclerView() {
        setPrayerTimes(cal)
        databaseList = preAzanManager.getAllObjectsFromDatabase()
        preAzanRecycler.layoutManager = LinearLayoutManager(context) as RecyclerView.LayoutManager?
        preAzanRecycler.adapter = AdapterPreAzan(context!!, holderArrayList, ArrayList(databaseList), this)
    }

    private fun setPrayerTimes(cal: Calendar) {
        prayerTimes = prayers!!.getPrayerTimes(cal, latitude, longitude, timezone.toDouble())
        //todo get Past list here grom getallobject from database
        val pastPrayerDBlist = preAzanManager.getAllObjectsFromDatabase()
//        val preNamazAlarm = PreAzanAlarmModel()
        val preNamazAlarmList = RealmList<PreAzanAlarmModel>()
        prayerTimes.removeAt(Constants.SUNSET_CONSTANT)
        prayerNames.removeAt(Constants.SUNSET_CONSTANT)
//        prayerTimes.removeAt(SUNRISE_CONSTANT)
//        prayerNames.removeAt(SUNRISE_CONSTANT)
        holderArrayList.clear()
        for (i in prayerNames.indices) {
            val temp = Holder(prayerNames[i], prayerTimes[i].toUpperCase())
            if (pastPrayerDBlist.isNullOrEmpty()) {
                preNamazAlarmList.add(
                    PreAzanAlarmModel(
                        i.toString(),
                        prayerNames[i],
                        prayerTimes[i],
                        isActivated = false,
                        isMuted = false,
                        alarmTime = "00:00"
                    )
                )
            }
            holderArrayList.add(temp)
        }
        preAzanManager.updateAllObjectsInDatabase(preNamazAlarmList)
    }


    private fun showAlarmSetDialog() {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_set_new_alarm)
        val dialogAlarmTime = dialog.findViewById<View>(R.id.dialogAlarmTime) as TextView
        dialogAlarmTime.setOnClickListener {
            dialogAlarmTime.visibility = View.VISIBLE

        }

        dialog.setCancelable(true)
        dialog.show()
    }

    override fun recyclerViewHolderCB(holder: RecyclerView.ViewHolder, position: Int, itemClicked: Int) {
        when (itemClicked) {
            CELL_ITEM -> {
                (holder as AdapterPreAzan.PreAzanViewHolder)
                val editor = context?.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE)?.edit()
                if (!alarmSet.add(position.toString())) {
                    alarmSet.remove(position.toString())
                    holder.outterLayout.background =
                        rootView.resources.getDrawable(R.drawable.rectangle_sharp_corner_gray)
                    editor?.putStringSet("ALARMSET", alarmSet)
                    editor?.apply()
                } else {

                    holder.outterLayout.background =
                        rootView.resources.getDrawable(R.drawable.rectangle_sharp_corner_green)
                    editor?.putStringSet("ALARMSET", alarmSet)
                    editor?.apply()
                }

            }
            ALARM_TIME -> {
//                showAlarmSetDialog()
                if (databaseList != null) {
                    showTimeLayout(
                        holderArrayList[position].prayNames,
                        holderArrayList[position].prayTimes,
                        databaseList!![position].isMuted,
                        databaseList!![position].isActivated
                    ) { isMuted, isActivated, time ->
                        (holder as AdapterPreAzan.PreAzanViewHolder).alarmTime.setText(time)
                        preAzanManager.updateSingleObjectInDatabase(position.toString(), isActivated, isMuted, time)
                        scheduleSingleTimeNotifications(position, time)
                        if(isActivated){
                            holder.outterLayout.background = rootView.resources.getDrawable(R.drawable.rectangle_sharp_corner_green)
                        }else{
                            holder.outterLayout.background =  rootView.resources.getDrawable(R.drawable.rectangle_sharp_corner_gray)
                        }
                    }
                }


            }
        }

    }

    fun showTimeLayout(
        namazName: String,
        namazTime: String,
        isMuted: Boolean,
        isActivated: Boolean,
        cb: (Boolean, Boolean, String) -> Unit
    ) {
        val timePickerPopWin = TimePickerPopWin.Builder(context,
            TimePickerPopWin.OnTimePickListener { hour, minute, isMuted, isActivated, time ->
                Toast.makeText(context, time + isActivated, Toast.LENGTH_SHORT).show()
                cb(isMuted, isActivated, time)
            })
            .textConfirm("CONFIRM")
            .textCancel("CANCEL")
            .btnTextSize(16)
            .viewTextSize(25)
            .colorCancel(Color.parseColor("#999999"))
            .colorConfirm(Color.parseColor("#009900"))
            .setNamazTime(namazTime)
            .setNamazName(namazName)
            .setIsMuted(isMuted)
            .setIsActivated(isActivated)
            .build()
        timePickerPopWin.showPopWin(activity)
    }

}

class AdapterPreAzan(
   var context: Context,
    var holderList: ArrayList<Holder>,
    var listOfNamaz: ArrayList<PreAzanAlarmModel>,
    var listener: RecyclerViewHolderCallback
) : RecyclerView.Adapter<AdapterPreAzan.PreAzanViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreAzanViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_pre_azan, parent, false
        )
        return PreAzanViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return listOfNamaz.size
    }

    override fun onBindViewHolder(holder: PreAzanViewHolder, position: Int) {

        holder.outterLayout.setOnClickListener {
            listener.recyclerViewHolderCB(holder, position, ALARM_TIME)
        }
        holder.namazName.text = holderList[position].prayNames
        holder.namazTime.text = holderList[position].prayTimes
        holder.alarmTime.text = listOfNamaz[position].alarmTime
        if(listOfNamaz[position].isActivated){
            holder.outterLayout.background =context.applicationContext.getDrawable(R.drawable.rectangle_sharp_corner_green)
        }else{
            holder.outterLayout.background =context.applicationContext.getDrawable(R.drawable.rectangle_sharp_corner_gray)
        }
        val mHandler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                var millisUntilFinished = if (position > 5) {
//                        dates[position].time - cal!!.time.time
                    val dtOrgic = DateTime(staticFormattedTimings!![position])
                    val dtPlusOneLoc = dtOrgic.plusDays(1)
                    ((dtPlusOneLoc.millis)) - System.currentTimeMillis()

                } else {
                    staticFormattedTimings!![position].time - System.currentTimeMillis()
                }
                mHandler.postDelayed(this, 1000)
                val Hours = (millisUntilFinished / (1000 * 60 * 60)).toInt()
                val Mins = (millisUntilFinished / (1000 * 60)).toInt() % 60
                val Secs = ((millisUntilFinished / 1000).toInt() % 60).toLong()
                val formattedMin = String.format("%02d", abs(Mins))
                val formattedHour = String.format("%02d", -Hours)
                val formattedSec = String.format("%02d", abs(Secs))
                val diff = "${formattedHour}:$formattedMin:$formattedSec" // updated value every1 second
                holder.namazRemainingTime.text = "($diff)"
                if (Hours == 0 && Mins == 0 && Secs == 0L) {
                    println("Times up")
                    try {
                        //todo call callback from here
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
            }
        }
        mHandler.postDelayed(runnable, 1000)
        holder.alarmTime.setOnClickListener {
            listener.recyclerViewHolderCB(holder, position, ALARM_TIME)
        }

    }

    class PreAzanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var namazName: TextView
        var namazTime: TextView
        var namazRemainingTime: TextView
        var alarmTime: TextView
        var textView3: TextView
        var outterLayout: ConstraintLayout

        init {
            outterLayout = itemView.findViewById(R.id.outterLayout)
            namazName = itemView.findViewById(R.id.namazNameTV)
            namazTime = itemView.findViewById(R.id.namazTimeTV)
            namazRemainingTime = itemView.findViewById(R.id.namazRemainingTime)
            alarmTime = itemView.findViewById(R.id.alarmTime)
            textView3 = itemView.findViewById(R.id.textView3)
        }
    }
}