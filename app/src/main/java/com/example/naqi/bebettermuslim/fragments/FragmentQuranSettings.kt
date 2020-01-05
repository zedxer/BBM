package com.example.naqi.bebettermuslim.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.base_classes.CustomFragment

/**
 * Created by naqi on 22,January,2019
 */
class FragmentQuranSettings : CustomFragment(){
    private lateinit var rootView: View

    override fun init(rootView: View) {
    }

    override fun findViewsByIds(rootView: View) {
    }

    override fun setListeners() {
    }

    override fun prepareRecyclerView() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_quran_settings, container, false)

        return rootView
    }


}