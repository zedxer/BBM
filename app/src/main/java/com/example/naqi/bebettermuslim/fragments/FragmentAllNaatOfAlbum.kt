package com.example.naqi.bebettermuslim.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.naqi.bebettermuslim.BeBetterMuslim
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.ALBUM_ID_CONTS
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.FROM_KEY
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.RECITER_ID_CONTS
import com.example.naqi.bebettermuslim.adapter.AdapterAllNaatsAlbum
import com.example.naqi.bebettermuslim.base_classes.CustomFragment
import com.example.naqi.bebettermuslim.callbacks.PlainCallback
import com.example.naqi.bebettermuslim.data.NaatManager
import com.example.naqi.bebettermuslim.data.NaatsResponse
import com.example.naqi.bebettermuslim.data.ReciterManager
import com.example.naqi.bebettermuslim.models.NaatModel
import com.example.naqi.bebettermuslim.models.RecitersModel
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by naqi on 18,May,2019
 */

class FragmentAllNaatOfAlbum : CustomFragment(), PlainCallback {
    override fun plainCallbackCalled(position: Int, listOfNaats: ArrayList<NaatModel>) {
        plainCallbackActivity.plainCallbackCalled(position, listOfNaats)
    }

    private var from: String? = ""
    private lateinit var rootView: View
    private lateinit var reciterImage: CircleImageView
    private lateinit var reciterName: TextView
    private lateinit var albumName: TextView
    private lateinit var albumNameSeparator: View
    private lateinit var recyclerNaats: RecyclerView
    private var reciter: RecitersModel? = null
    private lateinit var naatListAdapter: AdapterAllNaatsAlbum
    private val naatManager = NaatManager.instance
    private val reciterManager = ReciterManager.instance
    private var albumId = ""
    private var reciterId = ""
    private var naatList = ArrayList<NaatModel>()
    lateinit var plainCallbackActivity: PlainCallback
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_all_naat, container, false)
        init(rootView)

        return rootView
    }

    override fun init(rootView: View) {
        val bundle = this.arguments
        if (bundle != null) {
            albumId = bundle.getString(ALBUM_ID_CONTS, "")
            reciterId = bundle.getString(RECITER_ID_CONTS, "")
            from = bundle.getString(FROM_KEY, "")
        }
        findViewsByIds(rootView)
        setListeners()
        prepareRecyclerView()
        getData {
            reciterManager.getObjectFromDatabase(reciterId)?.let {
                reciter = it
                reciterName.text = reciter?.name
                val imageLoader = (context?.applicationContext as BeBetterMuslim).imageLoader
                imageLoader.displayImage(reciter?.pictureUrl, reciterImage)
            }
            naatListAdapter.updateList(naatList)
        }
    }

    override fun findViewsByIds(rootView: View) {
        reciterImage = rootView.findViewById(R.id.reciterImage)
        reciterName = rootView.findViewById(R.id.reciterName)
        albumName = rootView.findViewById(R.id.albumName)
        albumNameSeparator = rootView.findViewById(R.id.albumNameSeparator)
        recyclerNaats = rootView.findViewById(R.id.recyclerNaats)
        when (from) {
            Constants.ALB_KEY -> {
                albumName.visibility = View.VISIBLE
                albumNameSeparator.visibility = View.VISIBLE
            }
            Constants.RECIT_KEY -> {
                albumName.visibility = View.GONE
                albumNameSeparator.visibility = View.GONE
            }
        }
    }

    override fun setListeners() {

    }

    fun getData(cb: () -> Unit) {
        when (from) {
            Constants.RECIT_KEY -> {
                naatManager.getNaatsOfReciter(reciterId, object : Callback<NaatsResponse> {
                    override fun onFailure(call: Call<NaatsResponse>, t: Throwable) {
                        Toast.makeText(context, "Error fetching latest naats", Toast.LENGTH_SHORT).show()
                        naatList = ArrayList(naatManager.getAllNaatOfReciterFromDatabase(reciterId))

                    }

                    override fun onResponse(call: Call<NaatsResponse>, response: Response<NaatsResponse>) {
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        response.body()?.let { naatsResponse ->
                            naatsResponse.naats?.let {
                                Log.v("ALBUM_CB", it.toString())
                                naatManager.updateAllObjectsInDatabase(it)
//                                naatList = ArrayList(it)
                                naatList = ArrayList(naatManager.getAllNaatOfReciterFromDatabase(reciterId))
                                cb()
                            }

                        }
                    }


                })
            }
            Constants.ALB_KEY -> {
                naatManager.getNaatsOfAlbum(albumId, reciterId, object : Callback<NaatsResponse> {
                    override fun onFailure(call: Call<NaatsResponse>, t: Throwable) {
                        Toast.makeText(context?.applicationContext, "Error fetching latest naats", Toast.LENGTH_SHORT)
                            .show()
                        naatList = ArrayList(naatManager.getAllNaatsOfAlbumFromDatabase(albumId))

                    }

                    override fun onResponse(call: Call<NaatsResponse>, response: Response<NaatsResponse>) {
//                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        response.body()?.let { naatsResponse ->
                            naatsResponse.naats?.let {
                                Log.v("ALBUM_CB", it.toString())
                                naatManager.updateAllObjectsInDatabase(it)
//                                naatList = ArrayList(it)
                                naatList = ArrayList(naatManager.getAllNaatsOfAlbumFromDatabase(albumId))
                                cb()
                            }

                        }
                    }

                })
            }
        }

    }

    override fun prepareRecyclerView() {
        when (from) {
            Constants.RECIT_KEY -> {
                naatList = ArrayList(naatManager.getAllNaatOfReciterFromDatabase(reciterId))
            }
            Constants.ALB_KEY -> {
                naatList = ArrayList(naatManager.getAllNaatsOfAlbumFromDatabase(albumId))
            }
        }
        recyclerNaats.layoutManager = LinearLayoutManager(context)
        naatListAdapter = AdapterAllNaatsAlbum(context, naatList, this)
        recyclerNaats.adapter = naatListAdapter

    }

}