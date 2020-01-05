package com.example.naqi.bebettermuslim.activities

import android.content.Context
import android.os.Bundle
import android.view.View
import com.example.naqi.bebettermuslim.base_classes.CustomActivity
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_settings.*
import android.content.DialogInterface
import com.example.naqi.bebettermuslim.R
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.TIME_HOURS_SWITCH
import com.example.naqi.bebettermuslim.fragments.FragmentQuranSettings


class SettingsActivity : CustomActivity(), DialogInterface.OnClickListener {
    private var isTFour = false
    private val fragmentManager = this.supportFragmentManager

    override fun init() {
        getPreference()
        setListeners()
    }

    private fun getPreference() {
        val prefs = getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE)
        isTFour = prefs.getBoolean(Constants.TIME_HOURS_SWITCH, false)
        setUpLayout()
    }

    private fun setUpLayout() {
        tfourHourSwitch.isChecked = isTFour
    }

    override fun setListeners() {
        setLocationView.setOnClickListener(this)
        preNamazAlarmView.setOnClickListener(this)
        quranSettingView.setOnClickListener(this)
        calMethodView.setOnClickListener(this)
        JurMethodView.setOnClickListener(this)
        highLatView.setOnClickListener(this)
        feedBackView.setOnClickListener(this)
        rateAppView.setOnClickListener(this)
        faqView.setOnClickListener(this)
        policyView.setOnClickListener(this)
        tfourHourSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val editor =
                    this.application.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE).edit()
                editor.putBoolean(TIME_HOURS_SWITCH, true)
                editor.apply()

            } else {
                val editor =
                    this.application.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE).edit()
                editor.putBoolean(TIME_HOURS_SWITCH, false)
                editor.apply()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.setLocationView -> {




            }

            R.id.preNamazAlarmView -> {
                val colors = arrayOf("red", "green", "blue", "black")

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Pick a color")
                builder.setItems(colors, this)
                builder.create()
                builder.show()
            }

            R.id.quranSettingView -> {
                fragmentManager
                    .beginTransaction()
                    .replace(R.id.container, FragmentQuranSettings())
                .commit()
//                    .commitAllowingStateLoss()
            }

            R.id.calMethodView -> {

            }

            R.id.JurMethodView -> {

            }

            R.id.highLatView -> {

            }

            R.id.feedBackView -> {

            }

            R.id.rateAppView -> {

            }

            R.id.faqView -> {

            }

            R.id.policyView -> {

            }

        }
    }

    //dialog click
    override fun onClick(dialog: DialogInterface?, which: Int) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        init()
    }


    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

}
