package com.example.naqi.bebettermuslim.fragments

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.base_classes.CustomFragment


/**
 * Created by naqi on 10,May,2019
 */
class FragmentNaatBase : CustomFragment() {
    lateinit var fManager: FragmentManager
    private lateinit var rootView: View
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var toolbarNaat: Toolbar
    var fragmentNaat = FragmentNaat()
    var fragmentPlaylist = FragmentPlaylist()
    var active = Fragment()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_naat_base, container, false)
        init(rootView)
        return rootView
    }

    override fun init(rootView: View) {
        findViewsByIds(rootView)
        (activity as AppCompatActivity).setSupportActionBar(toolbarNaat)
        active = fragmentNaat
        activity?.let {
            fManager = it.supportFragmentManager
            fManager?.beginTransaction()?.add(R.id.frameView, fragmentPlaylist, "FragmentPlaylist")
                ?.hide(fragmentPlaylist)
                ?.commit()
            fManager?.beginTransaction()?.add(R.id.frameView, fragmentNaat, "FragmentNaat")?.commit()

        }
        setListeners()
    }

    override fun findViewsByIds(rootView: View) {
        bottomNav = rootView.findViewById(R.id.bottomNav)
        toolbarNaat = rootView.findViewById(R.id.toolbarNaat)

    }

    override fun setListeners() {
        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.naat_artist -> {
                        //0
                        activity?.let {
                            fManager.beginTransaction().hide(active).show(fragmentNaat).commit()
                            active = fragmentNaat
                            toolbarNaat.findViewById<View>(R.id.addNaat).visibility = View.GONE

                        }
                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.naat_playlist -> {
                        //1
                        activity?.let {
                            fManager.beginTransaction().hide(active).show(fragmentPlaylist).commit()
                            active = fragmentPlaylist
                            toolbarNaat.findViewById<View>(R.id.addNaat).visibility = View.VISIBLE
                        }
                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.home_action -> {
                        //2

                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        toolbarNaat.findViewById<View>(R.id.addNaat).setOnClickListener {
//            fragmentPlaylist.showAddPlaylistDialog(context!!,null)
        }
    }



    override fun prepareRecyclerView() {

    }

}