package com.example.naqi.bebettermuslim.adapter

import android.content.Context
import android.content.res.TypedArray
import android.os.Handler
import android.support.constraint.ConstraintLayout
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.naqi.bebettermuslim.R
import java.nio.file.Files.size
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.naqi.bebettermuslim.BeBetterMuslim
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.TIME_12
import com.squareup.picasso.Picasso
import org.joda.time.LocalDate
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.os.CountDownTimer
import android.util.Log
import kotlin.collections.ArrayList
import org.joda.time.DateTime




class AdapterPrayerTimeViewPager(
    mContext: Context,
    private var dates:ArrayList<Date>,
    private var prayerTiming: ArrayList<String>,
    private var prayerNames: ArrayList<String>
) : PagerAdapter() {

    private val mLayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return prayerTiming.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as LinearLayout
    }

    fun updateList(prayerTiming2: ArrayList<String>) {
        prayerTiming.clear()
        prayerTiming.addAll(prayerTiming2)
        notifyDataSetChanged()

    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.layout_prayer_timing, container, false)
        val prayerTime = itemView.findViewById<View>(R.id.prayerTime) as TextView
        val remainingTime = itemView.findViewById<View>(R.id.remainingTime) as TextView
        val prayerName = itemView.findViewById<View>(R.id.prayerName) as TextView
        val cal = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val dt = Date()
        val dtOrg = DateTime(dt)
        val dtPlusOne = dtOrg.plusDays(1)


        val  cal2= Calendar.getInstance()
        cal2.timeInMillis = System.currentTimeMillis()
        val mStopHandler = false
        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.US)
        val timeFormat = SimpleDateFormat("hh:mm:ss", Locale.US)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val dateString=dateFormat.format(cal2.time)
        prayerTiming[position].contains("am",true)
        val prayerTimeLong = dateTimeFormat.parse(
            dateFormat.format(Date(System.currentTimeMillis()))
                    + " " + prayerTiming[position].replace(" ", ":00 ")
        ).time
//        Log.v("TICKCURRENT3",""+        dates[position])

        dateTimeFormat.parse("$dateString ${prayerTiming[position].replace("am", "AM").replace("pm", "PM")}")

//        Log.v("TICKCURRENT4","$position  : "+dateTimeFormat.parse("$dateString ${prayerTiming[position].replace("am", "AM").replace("pm", "PM")}"))
        prayerTime.text = prayerTiming[position]
        remainingTime.text
        prayerName.text = prayerNames[position]

//        Log.v("TICKCURRENT1",""+timeFormat.format(prayerTimeLong))
//        Log.v("TICKCURRENT2",""+timeFormat.format(System.currentTimeMillis()))

        val mHandler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (!mStopHandler) {
                    var millisUntilFinished = if (position > 5) {
//                        dates[position].time - cal!!.time.time
                        val dtOrgic = DateTime(dates[position])
                        val dtPlusOneLoc = dtOrgic.plusDays(1)
                        ((dtPlusOneLoc.millis))- System.currentTimeMillis()

                    } else {
                        dates[position].time - System.currentTimeMillis()
                    }
                    mHandler.postDelayed(this, 1000)
                    val Hours = (millisUntilFinished / (1000 * 60 * 60)).toInt()
                    val Mins = (millisUntilFinished / (1000 * 60)).toInt() % 60
                    val Secs = ((millisUntilFinished / 1000).toInt() % 60).toLong()
                    val formattedMin = String.format("%02d", Math.abs(Mins))
                    val formattedHour = String.format("%02d", -Hours)
                    val formattedSec = String.format("%02d", Math.abs(Secs))
                    val diff = "${formattedHour}:$formattedMin:$formattedSec" // updated value every1 second
                    remainingTime.text = diff
                    millisUntilFinished = prayerTimeLong - System.currentTimeMillis()
//                    Log.v("TICKCURRENT",""+timeFormat.format(millisUntilFinished))
                    if (Hours == 0 && Mins == 0 && Secs == 0L) {
                        println("Times up")
                        try {
                            //todo call callback from here
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }

                        println("Prayer Time in Long, Run: " + Date(prayerTimeLong).toString())
                        millisUntilFinished = prayerTimeLong - System.currentTimeMillis()
//                        Log.v("TICKCURRENT",""+timeFormat.format(millisUntilFinished))

                    }
                }
            }
        }
        mHandler.postDelayed(runnable, 1000)
/*
var someTime = System.currentTimeMillis()- prayerTimeLong

        Log.v("TICKCURRENT",""+timeFormat.format(someTime))
        Log.v("TICKSomeTime",""+timeFormat.format(System.currentTimeMillis()))
*/

/*object : CountDownTimer(someTime , -1000) {

            override fun onTick(millisUntilFinished: Long) {
//                remainingTime.text = timeFormat.format(millisUntilFinished / 1000)
                Log.v("TICK",""+millisUntilFinished)
                remainingTime.post{
                    remainingTime.text = timeFormat.format(millisUntilFinished)
                    //here you can have your logic to set text to edittext
                }
            }

            override fun onFinish() {
                remainingTime.text = "done!"
            }

        }.start()*/

        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
//        super.destroyItem(container, position, `object`)
    }


}

class AdapterImagesViewPager(
    private val mContext: Context,
    private val drawables: TypedArray
) : PagerAdapter() {

    private val mLayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return drawables.length()
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.layout_images, container, false)
        val backgroundView = itemView.findViewById<View>(R.id.backgroundImage) as ImageView
        val imageLoader = (mContext.applicationContext as BeBetterMuslim).imageLoader
        imageLoader.displayImage("drawable://" + drawables.getResourceId(position, -1), backgroundView)
//        Picasso.get().load(drawables.getResourceId(position, -1)).into(backgroundView)
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }


}

