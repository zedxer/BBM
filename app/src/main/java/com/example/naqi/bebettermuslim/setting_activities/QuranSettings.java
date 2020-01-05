package com.example.naqi.bebettermuslim.setting_activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.example.naqi.bebettermuslim.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.example.naqi.bebettermuslim.Utils.Constants.QURAN_SETTINGS;

/**
 * Created by naqi on 22,January,2019
 */
public class QuranSettings extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setupActionBar();

        setContentView(R.layout.activity_quran_settings);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quran Settings");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(R.id.pref_content, new SettingsFragment())
                .commit();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//do whatever
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PreferenceManager prefMgr = getPreferenceManager();
            prefMgr.setSharedPreferencesName(QURAN_SETTINGS);
            prefMgr.setSharedPreferencesMode(MODE_PRIVATE);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.quran_preferences);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
