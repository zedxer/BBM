package com.example.naqi.bebettermuslim.setting_activities;

import android.app.ProgressDialog;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.naqi.bebettermuslim.R;
import com.example.naqi.bebettermuslim.Utils.Constants;
import com.example.naqi.bebettermuslim.data.GetJSONListener;
import com.example.naqi.bebettermuslim.data.JSONClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.example.naqi.bebettermuslim.Utils.Constants.GOOGLE_PLACES_KEY;
import static com.example.naqi.bebettermuslim.Utils.Constants.PRAYER_TIMING;

/**
 * Created by naqi on 22,January,2019
 */
public class Test2Settings extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "User Settings";
//    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2_settings);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
//        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getFragmentManager().beginTransaction()
                .replace(R.id.pref_content_view, new SettingsFragment())
                .commit();

        Places.initialize(getApplicationContext(), GOOGLE_PLACES_KEY);

//        PlacesClient placesClient = Places.createClient(this);
//
//        placesClient.
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//do whatever
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStart() {
        super.onStart();
//        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
//        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public static class SettingsFragment extends PreferenceFragment {

        private String address;
        private String timeFormat;
        private double latitude;
        private double longitude;
        private double timezone;

        private ListPreference timePreference;
        private ListPreference arsJuristicPref;
        private ListPreference highLatAdjPref ;
        private ListPreference prayerTimeConPreference;
        private Preference prayerScreen;
        private Preference locationPreference;
        private Preference feedbackPreference;
        private Preference aboutPreference;
        private Preference ratePreference;
        private Preference quranPreference;

        private ProgressDialog progressDialog;
        SharedPreferences.Editor sharedPrefsEditor;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PreferenceManager prefMgr = getPreferenceManager();
            prefMgr.setSharedPreferencesName(Constants.USER_SETTING);
            prefMgr.setSharedPreferencesMode(MODE_PRIVATE);
//            ((AppCompatPreferenceActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            ((AppCompatPreferenceActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.app_preference);
//            timePreference = (ListPreference) findPreference(TIME_FORMAT);
//            timeFormat = locationPreference.getSharedPreferences().getString(Constants.TIME_FORMAT, null);
//            timePreference.setSummary(timeFormat);
//            timePreference.getValue();


//            prayerTimeConPreference = (ListPreference) findPreference(CALCULATION_METHOD);
//            arsJuristicPref = (ListPreference) findPreference(ASR_JURISTIC);
//            highLatAdjPref = (ListPreference) findPreference(HIGH_LATITUDE_ADJUSTMENT);
            prayerScreen = findPreference(PRAYER_TIMING);
//            timePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
////                    Toast.makeText(getActivity(), timePreference.getValue().toString(), Toast.LENGTH_SHORT).show();
//
//
//                    return false;
//                }
//            });


            Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // newValue is the value you choose
//                    Toast.makeText(getActivity(), timePreference.getValue().toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), newValue.toString(), Toast.LENGTH_SHORT).show();
                    return true;
                }
            };

//            timePreference.setOnPreferenceChangeListener(listener);
//            prayerTimeConPreference.setOnPreferenceChangeListener(listener);
//            arsJuristicPref.setOnPreferenceChangeListener(listener);
//            highLatAdjPref.setOnPreferenceChangeListener(listener);
//            prayerScreen.setOnPreferenceChangeListener(listener);
            locationPreference = findPreference("ADDRESS");

            address = locationPreference.getSharedPreferences().getString(Constants.USER_ADDRESS, null);
            locationPreference.setSummary(address);
            locationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Location")
                            .setMessage("Current Location is " + address)
                            .setCancelable(true)
                            .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        if (!Places.isInitialized()) {
                                            Places.initialize(getActivity(), GOOGLE_PLACES_KEY);
                                        }
//
//                                        Autocomplete typeFilter = new AutocompleteFilter.Builder()
//                                                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
//                                                .build();
//
//                                        Intent intent =
//                                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(typeFilter)
//                                                        .build(getActivity());
//                                        startActivityForResult(intent, 1);

                                        // Set the fields to specify which types of place data to return.
                                        List<Place.Field> fields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS);

                                        // Start the autocomplete intent.
                                        Intent intent = new Autocomplete.IntentBuilder(
                                                AutocompleteActivityMode.OVERLAY, fields)
                                                .build(getActivity().getApplicationContext());
                                        startActivityForResult(intent, 1);



                                    } catch (Exception e) {
                                        // TODO: Handle the error.
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(R.drawable.location_icon)
                            .create()
                            .show();
                    return false;
                }
            });


            feedbackPreference = findPreference("FEEDBACK");

            feedbackPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@bebettermuslim.com"});
                    i.putExtra(Intent.EXTRA_SUBJECT, "Feedback for BeBetterMuslim");
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }

                    return false;
                }
            });

            aboutPreference = findPreference("ABOUT");

            aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bebettermuslim.com/"));
                    startActivity(browserIntent);

                    return false;
                }
            });

            ratePreference = findPreference("RATE");

            ratePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    // To count with Play market backstack, After pressing back button,
                    // to taken back to our application, we need to add following flags to intent.
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                    }
                    return false;
                }
            });

            quranPreference = findPreference("QURAN_SETTINGS");

            quranPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    startActivity(new Intent(getActivity(), QuranSettings.class));

                    return false;
                }
            });

            prayerScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    startActivity(new Intent(getActivity(), PrayerTimeSettings.class));
                    return false;
                }
            });

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    Log.i(TAG, "Place: " + place.getName());
                    Log.i(TAG, "Place: " + place.getLatLng().latitude);
                    Log.i(TAG, "Place: " + place.getLatLng().longitude);
                    Log.i(TAG, "Address: " + place.getAddress());


                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;
                    address = (String) place.getAddress();

                    sharedPrefsEditor = locationPreference.getSharedPreferences().edit();


//                addressTextView.setText(address);

                    Long tsLong = System.currentTimeMillis() / 1000;
                    String ts = tsLong.toString();

                    createDialog();

                    String url = "https://maps.googleapis.com/maps/api/timezone/json" +
                            "?location=" + latitude + "," + longitude +
                            "&timestamp=" + ts +
                            "&key="+GOOGLE_PLACES_KEY;

                    new JSONClient(getActivity(), l).execute(url);

                }else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.i(TAG, status.getStatusMessage());
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }

            }
        }

        public void createDialog() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Getting Location..Please wait..");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        GetJSONListener l = new GetJSONListener() {

            @Override
            public void onRemoteCallComplete(JSONObject jsonFromNet) {
                // add code to act on the JSON object that is returned

                if (jsonFromNet != null) {

                    try {
                        System.out.println(jsonFromNet.toString());
                        String timeZoneID = jsonFromNet.getString("timeZoneId");
                        TimeZone timeZone = TimeZone.getTimeZone(timeZoneID);
                        timezone = TimeUnit.HOURS.convert(timeZone.getOffset(System.currentTimeMillis()), TimeUnit.MILLISECONDS);

                        sharedPrefsEditor.putFloat(Constants.USER_LATITUDE, (float) latitude);
                        sharedPrefsEditor.putFloat(Constants.USER_LONGITUDE, (float) longitude);
                        sharedPrefsEditor.putString(Constants.USER_ADDRESS, address);
                        sharedPrefsEditor.putFloat(Constants.USER_TIMEZONE, (float) timezone);
                        sharedPrefsEditor.apply();

                        locationPreference.setSummary(address);

                        progressDialog.dismiss();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        };

    }


}
