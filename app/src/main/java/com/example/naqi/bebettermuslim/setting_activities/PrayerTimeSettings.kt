package com.example.naqi.bebettermuslim.setting_activities

import android.content.Context
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.Toast
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.ASR_JURISTIC
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.CALCULATION_METHOD
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.HIGH_LATITUDE_ADJUSTMENT
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.TIME_FORMAT
import io.github.inflationx.viewpump.ViewPumpContextWrapper

/**
 * Created by naqi on 27,June,2019
 */
class PrayerTimeSettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prayer_time_settings)
        supportActionBar!!.setTitle("Quran Settings")

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        fragmentManager.beginTransaction()
            .replace(R.id.pref_content,
                PrayerSettingsFragment()
            )
            .commit()
    }
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {//do whatever
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    class PrayerSettingsFragment : PreferenceFragment() {
        private var timePreference: ListPreference? = null
        private var arsJuristicPref: ListPreference? = null
        private var highLatAdjPref: ListPreference? = null
        private var prayerTimeConPreference: ListPreference? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val prefMgr = preferenceManager
            prefMgr.sharedPreferencesName = Constants.USER_SETTING
            prefMgr.sharedPreferencesMode = Context.MODE_PRIVATE
            addPreferencesFromResource(R.xml.prayer_time_preference)

            val listener = Preference.OnPreferenceChangeListener { preference, newValue ->
                // newValue is the value you choose
                //                    Toast.makeText(getActivity(), timePreference.getValue().toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, newValue.toString(), Toast.LENGTH_SHORT).show()
                true
            }
            timePreference =  findPreference(TIME_FORMAT) as ListPreference
            prayerTimeConPreference = findPreference(CALCULATION_METHOD) as ListPreference
            arsJuristicPref =  findPreference(ASR_JURISTIC) as ListPreference
            highLatAdjPref =  findPreference(HIGH_LATITUDE_ADJUSTMENT) as ListPreference

            timePreference!!.onPreferenceChangeListener = listener
            prayerTimeConPreference!!.onPreferenceChangeListener = listener
            arsJuristicPref!!.onPreferenceChangeListener = listener
            highLatAdjPref!!.onPreferenceChangeListener = listener
            // Load the preferences from an XML resource
        }
    }

}