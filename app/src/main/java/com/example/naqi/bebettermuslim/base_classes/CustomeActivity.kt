package com.example.naqi.bebettermuslim.base_classes

import android.support.v7.app.AppCompatActivity
import android.view.View

abstract class CustomActivity : AppCompatActivity(), View.OnClickListener {
    abstract fun init()
    abstract fun setListeners()
}