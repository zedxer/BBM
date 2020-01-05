package com.example.naqi.bebettermuslim.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.GOOGLE_PLACES_KEY
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.USER_LATITUDE
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.USER_LONGITUDE
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.USER_TIMEZONE
import com.example.naqi.bebettermuslim.adapter.NavAdapter
import com.example.naqi.bebettermuslim.base_classes.CustomActivity
import com.example.naqi.bebettermuslim.data.GetJSONListener
import com.example.naqi.bebettermuslim.data.JSONClient
import com.example.naqi.bebettermuslim.data.LanguageManager
import com.example.naqi.bebettermuslim.data.LanguageResponse
import com.example.naqi.bebettermuslim.fragments.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_home.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.TimeUnit


class HomeActivity : CustomActivity(), NavAdapter.OnItemClickListener {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val fragmentManager = supportFragmentManager
    val fragmentTransaction = fragmentManager.beginTransaction()
    private var lat = 0.0
    private var long = 0.0
    private var fragmentLoaded = false
    private var firstTime = true
    private var isRunned = false
//    private lateinit var sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var address: String
    private var timezone: Float = 0.0f
    private var cal: Calendar? = null
    private lateinit var fragmentHome: FragmentHome
    private lateinit var bundle: Bundle
    private lateinit var progressDialog: ProgressDialog
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent
    var alarmType = AlarmManager.ELAPSED_REALTIME;
    private val FIFTEEN_SEC_MILLIS = 15000
    private lateinit var sharedPrefsEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
//        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_home)
        init()

    }

    fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    override fun init() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getAllPrayerData()
        setListeners()
        getLanguageCallback()
        val drawerItems = resources.getStringArray(R.array.drawer_items)
        val drawables = resources.obtainTypedArray(R.array.drawer_drawables);
        navList.setHasFixedSize(true)
        navList.adapter = NavAdapter(drawerItems, drawables, this)
    }

    private fun getAllPrayerData() {
        cal = Calendar.getInstance()
        cal!!.timeInMillis = System.currentTimeMillis()
        val timeZone = cal!!.timeZone
        timezone =
            TimeUnit.HOURS.convert(timeZone.getOffset(System.currentTimeMillis()).toLong(), TimeUnit.MILLISECONDS)
                .toFloat()


    }

    private fun getPermission(permissionListener: PermissionListener) {
        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setDeniedMessage("If you reject permission,you can not use this service\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .check()
    }

    @SuppressLint("MissingPermission")
    override fun setListeners() {
        val permissionListener = object : PermissionListener {
            @SuppressLint("MissingPermission")
            override fun onPermissionGranted() {
                Toast.makeText(this@HomeActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
                displayLocationSettingsRequest(this@HomeActivity)
                if (!isRunned) {
                    getCoordinates {
                        if (it)
                            loadFragment()
                    }
                }
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {

                if (!Places.isInitialized()) {
                    Places.initialize(this@HomeActivity, GOOGLE_PLACES_KEY)
                }
                val fields =
                    listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS)
                // Start the autocomplete intent.
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields
                )
                    .build(this@HomeActivity.applicationContext)
                startActivityForResult(intent, 101)
                Toast.makeText(
                    this@HomeActivity,
                    "Please Provide your location manually",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        getPermission(permissionListener)
        displayLocationSettingsRequest(this)
    }

    private fun checkGps(): Boolean {
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkAndOpenGPS(): Boolean {
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Location service not available")  // GPS not found
            builder.setMessage("GPS service not available, Please go to -> Settings/Location to turn on the location service") // Want to enable?
            builder.setPositiveButton(
                "Yes"
            ) { r, v ->
                applicationContext.startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            builder.setNegativeButton("Select your location") { _, _ ->
                if (!Places.isInitialized()) {
                    Places.initialize(this, GOOGLE_PLACES_KEY)
                }
                val fields = Arrays.asList(
                    Place.Field.LAT_LNG,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.ADDRESS_COMPONENTS
                )
                // Start the autocomplete intent.
                val intent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields
                )
                    .build(this.getApplicationContext())
                startActivityForResult(intent, 101)

            }
            builder.create().show()
            return false
        } else {
            return true
        }
    }

    override fun onResume() {
        super.onResume()
    }


    private fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
            } catch (ex: ApiException) {
                when (ex.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = ex as ResolvableApiException
                        resolvableApiException
                            .startResolutionForResult(
                                this@HomeActivity,
                                Constants.RESULT_ACTIVITY_LOGIN
                            )
                    } catch (e: IntentSender.SendIntentException) {

                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.RESULT_ACTIVITY_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                if (!isRunned) {
                    getCoordinates {
                        loadFragment()
                    }
                }
            } else {
                checkAndOpenGPS()
            }
        } else if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                if (resultCode == Activity.RESULT_OK) {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.i("BBM", "Place: " + place.name!!)
                    Log.i("BBM", "Place: " + place.latLng!!.latitude)
                    Log.i("BBM", "Place: " + place.latLng!!.longitude)
                    Log.i("BBM", "Address: " + place.address!!)


                    latitude = place.latLng!!.latitude
                    longitude = place.latLng!!.longitude
                    address = place.address!!


                    //todo optimize this
                    lat = latitude
                    long = longitude


                    //                addressTextView.setText(address);

                    val tsLong = System.currentTimeMillis() / 1000
                    val ts = tsLong.toString()

                    createDialog()

                    val url = "https://maps.googleapis.com/maps/api/timezone/json" +
                            "?location=" + latitude + "," + longitude +
                            "&timestamp=" + ts +
                            "&key=" + GOOGLE_PLACES_KEY

                    JSONClient(this, l).execute(url)
                    loadFragment()
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Log.i("BBM", status.statusMessage)
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
        }
    }

    private fun createDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Getting Location..Please wait..")
        progressDialog.setCancelable(false)
        progressDialog.setIndeterminate(true)
        progressDialog.show()
    }

    internal var l: GetJSONListener = GetJSONListener { jsonFromNet ->
        // add code to act on the JSON object that is returned

        if (jsonFromNet != null) {

            try {
                println(jsonFromNet.toString())
                val timeZoneID = jsonFromNet.getString("timeZoneId")
                val timeZone = TimeZone.getTimeZone(timeZoneID)
                timezone = TimeUnit.HOURS.convert(
                    timeZone.getOffset(System.currentTimeMillis()).toLong(),
                    TimeUnit.MILLISECONDS
                ).toFloat()
                sharedPrefsEditor =
                    application.getSharedPreferences(Constants.USER_SETTING, Context.MODE_PRIVATE).edit()
                sharedPrefsEditor.putFloat(Constants.USER_LATITUDE, latitude.toFloat())
                sharedPrefsEditor.putFloat(Constants.USER_LONGITUDE, longitude.toFloat())
                sharedPrefsEditor.putString(Constants.USER_ADDRESS, address)
                sharedPrefsEditor.putFloat(Constants.USER_TIMEZONE, timezone)
                sharedPrefsEditor.apply()


                progressDialog.dismiss()


            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun getCoordinates(cb: (Boolean) -> Unit) {
        isRunned = true
        fusedLocationClient.lastLocation.addOnSuccessListener {
            lat = it.latitude
            long = it.longitude
            Toast.makeText(
                this@HomeActivity,
                "location is $lat,$long",
                Toast.LENGTH_SHORT
            ).show()
            val locationEditor = application.getSharedPreferences(Constants.USER_SETTING, Context.MODE_PRIVATE).edit()
            locationEditor.putFloat(USER_LATITUDE, lat.toFloat())
            locationEditor.putFloat(USER_LONGITUDE, long.toFloat())
            locationEditor.apply()
            Log.d("LOCATIONTAG", "$lat,$long")
            cb(true)
        }
        fusedLocationClient.lastLocation.addOnFailureListener {
            cb(false)
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadFragment() {

        bundle = Bundle()
        bundle.putDouble(USER_LATITUDE, lat)
        bundle.putDouble(USER_LONGITUDE, long)
        bundle.putFloat(USER_TIMEZONE, timezone)
        fragmentHome = FragmentHome()
        fragmentHome.arguments = bundle
        fragmentManager
            .beginTransaction()
            .add(R.id.container, fragmentHome)
//                .commit()
            .commitAllowingStateLoss()
        fragmentLoaded = true
        //TODO remove commit from here to somewhere else
    }


    override fun onClick(v: View?) {

    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onNavItemClick(view: View, position: Int) {
        selectItem(position)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigation_view)) {
            drawerLayout.closeDrawer(navigation_view)
        } else {
            super.onBackPressed()
        }
    }

    fun openNavDrawer() {
        drawerLayout.openDrawer(navigation_view)
    }

    @SuppressLint("CommitTransaction") // commit() is called
    private fun selectItem(position: Int) {
        when (position) {
            QURAN_SCREEN -> {
                startActivity(Intent(this, QuranActivity::class.java))
            }
            NAAT_SCREEN -> {
                Toast.makeText(this, "This is a NAAT SCREEN", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, AudioActivity::class.java))

            }
            ALARM_SCREEN -> {
                Toast.makeText(this, "This is a triumph", Toast.LENGTH_SHORT).show()
//TODO ALARM SCREEN
                val fragPreAzn = FragmentPreNamazAlarm()
                var bundle = Bundle()
                bundle.putDouble(USER_LATITUDE, lat)
                bundle.putDouble(USER_LONGITUDE, long)
                bundle.putFloat(USER_TIMEZONE, timezone)
                fragPreAzn.arguments = bundle
                changeFragment(fragPreAzn, true)

            }
            PRAYER_TIME_SCREEN -> {
                val fragmentPrayerTime = FragmentPrayerTime()
                fragmentPrayerTime.arguments = bundle
                changeFragment(fragmentPrayerTime, true)

            }
            QIBLA_DIRECTION_SCREEN -> {
//                changeFragment(FragmentTest(), true)
                changeFragment(FragmentQiblaDirection(), true)

            }
            TASBEEH_COUNTER_SCREEN -> {
                changeFragment(FragmentTasbeehCounter(), true)


            }
            else -> {

                Toast.makeText(this, "I am making a note here", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun changeFragment(fragment: Fragment, handleNav: Boolean) {
        fragmentManager.beginTransaction().run {
            setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.abc_fade_in,
                R.anim.exit_to_right
            )
            add(R.id.container, fragment)
            addToBackStack(null).commit()
        }
        if (handleNav)
            drawerLayout.closeDrawer(navigation_view)

    }

    fun getLanguageCallback() {
        val languageManager = LanguageManager.instance
        languageManager.getAllLangsFromServer(object : Callback<LanguageResponse> {
            override fun onFailure(call: Call<LanguageResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, t.toString(), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<LanguageResponse>, response: Response<LanguageResponse>) {
                if (!response.body()?.error!!) {
                    response.body()?.let {
                        languageManager.updateAllObjectsInDatabase(it.languages!!.toList())
                        Toast.makeText(applicationContext, "FAILED", Toast.LENGTH_SHORT).show()
//                                    Toast.makeText(applicationContext, languageManager.getAllObjectsFromDatabase().toString(), Toast.LENGTH_SHORT).show()
                        Log.v("TAG_ME", languageManager.getAllObjectsFromDatabase().toString())
                    }
                }
            }

        })
    }

    companion object {
        const val QURAN_SCREEN = 0
        const val NAAT_SCREEN = 1
        const val ALARM_SCREEN = 2
        const val PRAYER_TIME_SCREEN = 3
        const val QIBLA_DIRECTION_SCREEN = 4
        const val TASBEEH_COUNTER_SCREEN = 5
    }


}
