package com.example.naqi.bebettermuslim.base_classes

import android.support.v4.app.Fragment
import android.view.View

abstract class CustomFragment : Fragment() {

    abstract fun init(rootView: View)
    abstract fun findViewsByIds(rootView: View)
    abstract fun setListeners()
    abstract fun prepareRecyclerView()
}