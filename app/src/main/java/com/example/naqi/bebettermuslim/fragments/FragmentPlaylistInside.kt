package com.example.naqi.bebettermuslim.fragments

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.naqi.bebettermuslim.BeBetterMuslim
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Utils
import com.example.naqi.bebettermuslim.activities.AudioActivity
import com.example.naqi.bebettermuslim.base_classes.CustomFragment
import com.example.naqi.bebettermuslim.callbacks.RecyclerViewPositionCallback
import com.example.naqi.bebettermuslim.data.PlayListManager
import com.example.naqi.bebettermuslim.data.ReciterManager
import com.example.naqi.bebettermuslim.models.NaatModel
import com.example.naqi.bebettermuslim.models.PlaylistModel

/**
 * Created by naqi on 19,June,2019
 */
class FragmentPlaylistInside : CustomFragment(), RecyclerViewPositionCallback {

    private lateinit var rootView: View
    private lateinit var recyclerPlaylistInside: RecyclerView
    private var playlist: PlaylistModel? = null
    private val playlistManager = PlayListManager.INSTANCE
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_playlist_inside, container, false)
        init(rootView)
        setListeners()
        prepareRecyclerView()
        return rootView
    }

    override fun recylerViewItemPosition(position: Int) {
        if (activity != null && playlist?.naatList != null) {
            (activity as AudioActivity).startPlayer(ArrayList(playlist?.naatList!!), position, "PLAYLIST")
        }
    }

    override fun init(rootView: View) {
        val bundle = arguments
        val playlistId = bundle?.getString("playlist_id", "")

        if (playlistId != null) {
            playlist = playlistManager.getObjectFromDatabase(playlistId)
            if (activity != null && playlist != null) {
                (activity as AudioActivity).changPageTitle(playlist?.name!!)
            }
        }
        findViewsByIds(rootView)
    }

    override fun onStop() {
        super.onStop()
        if (activity != null) {
            (activity as AudioActivity).changPageTitle("Naats")
        }
    }

    override fun findViewsByIds(rootView: View) {
        recyclerPlaylistInside = rootView.findViewById(R.id.recyclerPlaylistInside)
    }

    override fun setListeners() {


    }

    override fun prepareRecyclerView() {
        playlist?.let {
            recyclerPlaylistInside.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerPlaylistInside.adapter = AdapterPlaylistInside(context!!, ArrayList(it.naatList!!), this)
        }

    }

}

class AdapterPlaylistInside(
    var context: Context,
    var listOfNaat: ArrayList<NaatModel>,
    val rvPositionCB: RecyclerViewPositionCallback
) :
    RecyclerView.Adapter<AdapterPlaylistInside.PlaylistInsideVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): PlaylistInsideVH {
        val layoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_playlist_inside, parent, false
        )
        return PlaylistInsideVH(layoutView)
    }

    override fun getItemCount(): Int {
        return listOfNaat.size
    }

    override fun onBindViewHolder(holder: PlaylistInsideVH, position: Int) {
        val imageLoader = (context.applicationContext as BeBetterMuslim).imageLoader
        imageLoader.displayImage(
            ReciterManager.instance.getObjectFromDatabase(listOfNaat[position].reciter!!)!!.pictureUrl,
            holder.reciterImage)

        holder.naatName.text = listOfNaat[position].title
        holder.naatDuration.text = Utils.formatPlayTime(listOfNaat[position].playTime!!.toLong())
        holder.artistName.text = ReciterManager.instance.getObjectFromDatabase(listOfNaat[position].reciter!!)!!.name
        holder.playlistItemCell.setOnClickListener {
            rvPositionCB.recylerViewItemPosition(position)
        }
    }

    class PlaylistInsideVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var reciterImage: ImageView
        var naatName: TextView
        var artistName: TextView
        var naatDuration: TextView
        var playlistItemCell: RelativeLayout

        init {
            reciterImage = itemView.findViewById(R.id.reciterImage)
            naatName = itemView.findViewById(R.id.naatName)
            artistName = itemView.findViewById(R.id.artistName)
            naatDuration = itemView.findViewById(R.id.naatDuration)
            playlistItemCell = itemView.findViewById(R.id.playlistItemCell)
        }
    }
}
