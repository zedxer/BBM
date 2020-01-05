package com.example.naqi.bebettermuslim.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.PREF_FILE_NAME
import com.example.naqi.bebettermuslim.activities.HomeActivity
import com.example.naqi.bebettermuslim.base_classes.CustomFragment
import com.example.naqi.bebettermuslim.controllers.Compass
import com.example.naqi.bebettermuslim.controllers.HomeApp

class FragmentQiblaDirection : CustomFragment() {
    private lateinit var rootView: View
    private lateinit var compass: Compass
    private lateinit var naatPageIcon: ImageView
    private lateinit var pageTitle: TextView
    private lateinit var navbarIcon: ImageView
    private var lat = 0.0
    private var long = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_qibla_direction, container, false)
        init(rootView)
        findViewsByIds(rootView)
        setListeners()
        prepareRecyclerView()
        return rootView
    }

    override fun init(rootView: View) {

        val pref = activity?.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
        lat = pref?.getString(Constants.USER_LATITUDE, lat.toString())!!.toDouble()
        long = pref.getString(Constants.USER_LONGITUDE, long.toString())!!.toDouble()
        context?.let {
            compass = Compass(it)

        }
    }

    override fun findViewsByIds(rootView: View) {
        compass.arrowView = rootView.findViewById(R.id.main_image_hands) as ImageView
        compass.dialView = rootView.findViewById(R.id.dialView) as ImageView
        compass.kabaImageView = rootView.findViewById(R.id.kabaImageView) as ImageView
        compass.headingTextView = rootView.findViewById(R.id.headingTextView) as TextView
        compass.heading = rootView.findViewById(R.id.heading) as TextView
        compass.displacementTextView = rootView.findViewById(R.id.displacementTextView) as TextView
        naatPageIcon = rootView.findViewById(R.id.naatPageIcon)
        pageTitle = rootView.findViewById(R.id.pageTitle)
        navbarIcon = rootView.findViewById(R.id.navbarIcon)


    }

    override fun setListeners() {
        compass.setLatitude(lat)
        compass.setLongitude(long)
        compass.start()
        navbarIcon.setOnClickListener{
            activity?.let {
                (it as HomeActivity).openNavDrawer()
            }
        }
    }

    override fun prepareRecyclerView() {
        naatPageIcon.setImageDrawable(ContextCompat.getDrawable(context!!,R.drawable.qibla_fill))
        pageTitle.setText("Qibla Direction")

    }

    override fun onPause() {
        super.onPause()
        compass.stop()
    }

    override fun onResume() {
        super.onResume()
        compass.start()

    }
}