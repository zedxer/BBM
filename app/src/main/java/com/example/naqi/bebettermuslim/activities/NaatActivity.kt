package com.example.naqi.bebettermuslim.activities

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.base_classes.CustomActivity
import com.example.naqi.bebettermuslim.callbacks.ChildRecyclerCallback
import com.example.naqi.bebettermuslim.fragments.FragmentAllNaatOfAlbum
import com.example.naqi.bebettermuslim.fragments.FragmentNaat
import com.example.naqi.bebettermuslim.fragments.FragmentPlaylist
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_naat.*
import kotlinx.android.synthetic.main.toolbar_naat_frag.*


class NaatActivity : CustomActivity(), ChildRecyclerCallback {
    private lateinit var fManager: FragmentManager
    private val fragmentManager: FragmentManager = supportFragmentManager

    private var fragmentNaat = FragmentNaat()
    private var  fragmentPlaylist = FragmentPlaylist()
    var active = Fragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_naat)
        init()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun init() {
        this.setSupportActionBar(toolbarNaat)
        active = fragmentNaat
        fManager = this.supportFragmentManager
        fManager.beginTransaction().add(R.id.frameView, fragmentPlaylist, "FragmentPlaylist").hide(fragmentPlaylist)
            .commit()
        fManager.beginTransaction().add(R.id.frameView, fragmentNaat, "FragmentNaat").commit()
        fragmentNaat.childRecyclerCallback = this
        setListeners()
    }

    override fun setListeners() {
        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.naat_artist -> {
                        //0

                        fManager.beginTransaction().hide(active).show(fragmentNaat).commit()
                        active = fragmentNaat
                        toolbarNaat.findViewById<View>(R.id.addNaat).visibility = View.GONE

                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.naat_playlist -> {
                        //1
                        fManager.beginTransaction().hide(active).show(fragmentPlaylist).commit()
                        active = fragmentPlaylist
                        toolbarNaat.findViewById<View>(R.id.addNaat).visibility = View.VISIBLE

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
//            fragmentPlaylist.showAddPlaylistDialog(this)
        }

    }

    override fun childRecyclerCallback(reciterId: String?, albumId: String?, from:String?) {

        when(from){
            Constants.RECIT_KEY->{
                val fragmentAllNaat = FragmentAllNaatOfAlbum()
                val bundle = Bundle()
                bundle.putString(Constants.RECITER_ID_CONTS, reciterId)
                bundle.putString(Constants.FROM_KEY, Constants.RECIT_KEY)
                fragmentAllNaat.arguments = bundle
                active = fragmentAllNaat
                changeFragment(fragmentAllNaat)

            }

            Constants.ALB_KEY->{
                val fragmentAllNaat = FragmentAllNaatOfAlbum()
                val bundle = Bundle()
                bundle.putString(Constants.RECITER_ID_CONTS, reciterId)
                bundle.putString(Constants.ALBUM_ID_CONTS, albumId)
                bundle.putString(Constants.FROM_KEY,  Constants.ALB_KEY)
                fragmentAllNaat.arguments = bundle
                active = fragmentAllNaat
                changeFragment(fragmentAllNaat)
            }
        }


    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        changeFragmentToArtist()
//        bottomNav.selectedItemId = R.id.naat_artist
//    }
    private fun changeFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .add(R.id.frameView, fragment)
            .addToBackStack(null)
            .commit()
    }
    private fun changeFragmentToArtist() {
        fragmentManager.beginTransaction()
            .add(R.id.frameView, fragmentNaat)
            .addToBackStack(null)
            .commit()
    }
    private fun replaceFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .replace(R.id.frameView, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onClick(v: View?) {
    }


}
