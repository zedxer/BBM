package com.example.naqi.bebettermuslim.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.adapter.AdapterExpandableRecycler
import com.example.naqi.bebettermuslim.adapter.RecitersExpandModel
import com.example.naqi.bebettermuslim.base_classes.CustomFragment
import com.example.naqi.bebettermuslim.callbacks.ChildRecyclerCallback
import com.example.naqi.bebettermuslim.callbacks.RecyclerChildItemClicked
import com.example.naqi.bebettermuslim.callbacks.RecyclerViewItemClicked
import com.example.naqi.bebettermuslim.data.AlbumManager
import com.example.naqi.bebettermuslim.data.AlbumResponse
import com.example.naqi.bebettermuslim.data.ReciterManager
import com.example.naqi.bebettermuslim.data.RecitersResponse
import com.example.naqi.bebettermuslim.models.AlbumModel
import com.example.naqi.bebettermuslim.models.RecitersModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by naqi on 17,January,2019
 */
class FragmentNaat : CustomFragment(), RecyclerViewItemClicked, RecyclerChildItemClicked {


    private lateinit var rootView: View
    private var albumArray = ArrayList<AlbumModel>()
    private var reciterArray = ArrayList<RecitersModel>()
    private var reciterExapdableList = ArrayList<RecitersExpandModel>()
    private lateinit var artistRecycler: RecyclerView
    private lateinit var albumManager: AlbumManager
    private lateinit var reciterManager: ReciterManager
    private lateinit var allNaatView: FrameLayout
    var rotationAngle = 0
    lateinit var childRecyclerCallback: ChildRecyclerCallback
    private lateinit var adapterExpandableList: AdapterExpandableRecycler
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_naat, null)

        init(rootView)
        findViewsByIds(rootView)
        setListeners()
        getData()


        return rootView
    }


    override fun onAttach(context: Context?) {
        Log.v("FRAG_BASE", "fragment naat attached called")
        Log.v("FRAG_BASE", "${UUID.randomUUID()} UUID")
        super.onAttach(context)
    }

    override fun init(rootView: View) {
        reciterManager = ReciterManager.instance
        albumManager = AlbumManager.instance

    }

    override fun findViewsByIds(rootView: View) {
        artistRecycler = rootView.findViewById(R.id.artistRecycler)
//        allNaatView = rootView.findViewById(R.id.allNaatView)
    }

    override fun setListeners() {

    }

    override fun prepareRecyclerView() {
        reciterExapdableList = reciterManager.getAlbumExpandableList(reciterArray, albumArray)
//        Log.v("EXPAND", reciterExapdableList.toString())
//        Log.v("ALBUM_RECY", albumArray.toString())
        Log.v("FRAG_BASE", "fragment naat called")
        artistRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapterExpandableList = AdapterExpandableRecycler(context!!, reciterExapdableList, this, this)
        adapterExpandableList.setOnGroupClickListener { flatPos ->
            true
        }
        artistRecycler.adapter = adapterExpandableList
    }

    override fun recyclerViewListClicked(v: View, img: ImageView, position: Int) {
//        if (adapterExpandableList.isGroupExpanded(position)) {
//            v.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorYellowGrey))
//            animateCollapse(img)
//        } else {
//            animateCollapse(img)
//            v.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorWhite))
//
//        }
    }

    override fun recyclerChildListClicked(reciterId: String?, albumId: String?, position: Int, from: String?) {
        childRecyclerCallback.childRecyclerCallback(reciterId, albumId, from)
    }

    private fun animateCollapse(icon: View?) {
        rotationAngle = if (rotationAngle == 0) 90 else 0  //toggle
        icon?.animate()!!.rotation(rotationAngle.toFloat()).setDuration(100).start()
    }

    private fun getData() {
//        getAllAlbumCallback()
//        getAllReciterCallback()
        getReciterListData {
            getAlbumListData {
                prepareRecyclerView()
            }
        }

    }

    private fun getAlbumListData(cb: () -> Unit) {
        if (albumArray.isEmpty()) {
            val dbAlbumList = albumManager.getAllObjectsFromDatabase()
            if (dbAlbumList.isNullOrEmpty()) {
                getAllAlbumCallback() {
                    cb()
                }
            } else {
                albumArray.addAll(ArrayList(dbAlbumList))
                cb()
            }
        }
//        getAllAlbumCallback(){
//            cb()
//        }
    }

    private fun getReciterListData(cb: () -> Unit) {
        if (reciterArray.isEmpty()) {
            val dbReciterList = reciterManager.getAllObjectsFromDatabase()
            if (dbReciterList.isNullOrEmpty()) {
                getAllReciterCallback {
                    cb()
                }
            } else {
                reciterArray.addAll(ArrayList(dbReciterList))
                cb()
            }
        }

//        getAllReciterCallback{
//            cb()
//        }
    }

    private fun getAllAlbumCallback(cb: () -> Unit) {
        var albumList: ArrayList<AlbumModel>
        AlbumManager.instance.getAllAlbumsFromServer(object : Callback<AlbumResponse> {
            override fun onFailure(call: Call<AlbumResponse>, t: Throwable) {
                Toast.makeText(context, "Failed $t", Toast.LENGTH_LONG).show()
                Log.v("ALBUM_CB", t.toString())
            }

            override fun onResponse(call: Call<AlbumResponse>, response: Response<AlbumResponse>) {
                Toast.makeText(context, "Successfull $response", Toast.LENGTH_LONG).show()
//                    Log.v("ALBUM", response.toString())
//                    Log.v("ALBUM", response.body().toString())

                response.body()?.let { albumResponse ->
                    albumResponse.albums?.let {
                        Log.v("ALBUM_CB", it.toString())
                        albumManager.updateAllObjectsInDatabase(it)
//                        albumList = ArrayList(it)
                        albumList = ArrayList(albumManager.getAllObjectsFromDatabase())
                        albumArray.addAll(albumList)
                        cb()
                    }

                }
//                adapterExpandableList.notifyDataSetChanged()
//                prepareRecyclerView()

            }
        })
    }

    private fun getAllReciterCallback(cb: () -> Unit) {
        var reciterList: ArrayList<RecitersModel>
        reciterManager.getAllReciterFromServer(object : Callback<RecitersResponse> {
            override fun onFailure(call: Call<RecitersResponse>, t: Throwable) {
                Toast.makeText(context, "Failed $t", Toast.LENGTH_LONG).show()
                Log.v("RECITER", t.toString())
            }

            override fun onResponse(call: Call<RecitersResponse>, response: Response<RecitersResponse>) {
                Toast.makeText(context, "Successfull $response", Toast.LENGTH_LONG).show()
//                Log.v("ALBUM", response.toString())
//                Log.v("ALBUM", response.body().toString())
                response.body()?.let { rec ->
                    rec.reciters?.let {
                        Log.v("RECITER_CB", it.toString())

                        reciterManager.updateAllObjectsInDatabase(it)
//                        reciterList = ArrayList(it)
                        reciterList = ArrayList(reciterManager.getAllObjectsFromDatabase())
                        reciterArray.addAll(reciterList)
                        cb()
                    }
//                    Log.v("ALBUM", rec.reciters.toString())
                }
            }

        })


    }

}