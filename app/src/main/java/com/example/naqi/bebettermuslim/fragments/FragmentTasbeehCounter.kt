package com.example.naqi.bebettermuslim.fragments

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.naqi.bebettermuslim.BeBetterMuslim
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.base_classes.CustomFragment
import com.google.android.gms.analytics.Tracker

/**
 * Created by naqi on 11,March,2019
 */
class FragmentTasbeehCounter : CustomFragment() {
    private lateinit var rootView: View
    private var counter = 0
    private var counterView: TextView? = null
    private var editText: EditText? = null
    private var checkBox: CheckBox? = null
    private var vibrator: Vibrator? = null
    private lateinit var resetBtn: Button
    private lateinit var minusOneBtn: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_tasbeeh_counter, container, false)
        init(rootView)
        findViewsByIds(rootView)
        setListeners()
        return rootView
    }

    override fun init(rootView: View) {

        vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        counter = 0

    }

    override fun findViewsByIds(rootView: View) {
        counterView = rootView.findViewById(R.id.counterTextView)
        editText = rootView.findViewById(R.id.editText)
        checkBox = rootView.findViewById(R.id.beepAfterCheck)
        resetBtn = rootView.findViewById(R.id.resetBtn)
        minusOneBtn = rootView.findViewById(R.id.minusOneBtn)
    }

    override fun setListeners() {
        minusOneBtn.setOnClickListener {

            decreaseCounter(it)

        }

        resetBtn.setOnClickListener {
            resetCounter(it)

        }
        counterView?.setOnClickListener {
            counter++
            counterView?.text = counter.toString()
            checkBox?.let {
                if (checkBox!!.isChecked) {

                    val text = editText?.text.toString()

                    val limit = Integer.parseInt(if (text == "") "0" else text)

                    if (limit == counter) {
                        Toast.makeText(context, "Limit Reached", Toast.LENGTH_SHORT).show()

                        vibrator?.vibrate(longArrayOf(0, 300, 200, 300), -1)
                    }
                }
            }

        }
        checkBox?.setOnClickListener {
            checkBox?.let {
                if (it.isChecked) {
                    editText?.setEnabled(true)
                } else {
                    editText?.setEnabled(false)
                }
            }
        }

    }

    fun resetCounter(view: View) {
        counter = 0
        counterView?.text = counter.toString()
    }

    fun decreaseCounter(view: View) {
        if (counter != 0) {
            counter--
            counterView?.text = counter.toString()
        }
    }

    override fun prepareRecyclerView() {
    }

}