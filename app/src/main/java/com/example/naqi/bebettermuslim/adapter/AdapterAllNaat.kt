package com.example.naqi.bebettermuslim.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.COLOR_TINT_DARKBLUE
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.COLOR_TINT_GREEN
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.COLOR_TINT_LIGHTBLUE
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.COLOR_TINT_ORANGE
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.COLOR_TINT_RED
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.COLOR_TINT_YELLOW
import com.example.naqi.bebettermuslim.Utils.Utils
import com.example.naqi.bebettermuslim.callbacks.PlainCallback
import com.example.naqi.bebettermuslim.callbacks.RecyclerViewPositionCallback
import com.example.naqi.bebettermuslim.models.NaatModel
import com.example.naqi.bebettermuslim.models.PlaylistModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by naqi on 10,May,2019
 */

class AdapterPlaylist(
    var context: Context?,
    private val list: ArrayList<PlaylistModel>,
    val rvPlaylistClicked: RecyclerViewPositionCallback
) :
    RecyclerView.Adapter<AdapterPlaylist.PlaylistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_playlist, parent, false
        )
        return PlaylistViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.playlistTitle.text = list[position].name
        holder.numberOfNaat.text = "Total Naats: ${list[position].naatList!!.size}"
        DrawableCompat.setTint(
            DrawableCompat.wrap(holder.playlistColorDot.drawable),
            ContextCompat.getColor(
                context!!, when (list[position].colorInt) {
                    COLOR_TINT_RED -> {
                        R.color.colorRed
                    }
                    COLOR_TINT_DARKBLUE -> {
                        R.color.colorAccent
                    }
                    COLOR_TINT_GREEN -> {
                        R.color.colorGreen
                    }
                    COLOR_TINT_LIGHTBLUE -> {
                        R.color.colorLightBlue
                    }
                    COLOR_TINT_ORANGE -> {
                        R.color.colorOrange
                    }
                    COLOR_TINT_YELLOW -> {
                        R.color.colorYellow
                    }
                    else -> {
                        R.color.colorAccent
                    }
                }
            )
        )
        holder.viewForeground.setOnClickListener {
            rvPlaylistClicked.recylerViewItemPosition(position)

        }

    }


    fun updateList(newList: ArrayList<PlaylistModel>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun getObjectFromPosition(position: Int): PlaylistModel {
        return list[position]
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)
    }

    fun restoreItem(item: PlaylistModel, index: Int) {
        list.add(index, item)
        notifyItemInserted(index)
    }
    //todo change color like this
    /*** DrawableCompat.setTint(DrawableCompat.wrap(holder.circleImageView.drawable), ContextCompat.getColor(context, R.color.meat_brown))**/
    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var numberOfNaat: TextView
        var playlistTitle: TextView
        var playlistColorDot: ImageView
        var viewBackground: RelativeLayout
        var viewForeground: RelativeLayout


        init {
            numberOfNaat = itemView.findViewById(R.id.numberOfNaat)
            playlistTitle = itemView.findViewById(R.id.playlistTitle)
            playlistColorDot = itemView.findViewById(R.id.playlistColorDot)
            viewBackground = itemView.findViewById(R.id.view_background)
            viewForeground = itemView.findViewById(R.id.view_foreground)

        }
    }

}

class AdapterAllNaatsAlbum(
    private var context: Context?,
    private var naatList: ArrayList<NaatModel>,
    val plainCallback: PlainCallback
) :
    RecyclerView.Adapter<AdapterAllNaatsAlbum.AllNaatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllNaatViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_all_naat_album, parent, false
        )
        return AllNaatViewHolder(layoutView)
    }

    override fun getItemCount(): Int {
        return naatList.size
    }

    override fun onBindViewHolder(holder: AllNaatViewHolder, position: Int) {
        holder.naatName.text = naatList[position].title
        holder.naatDuration.text =  Utils.formatPlayTime(naatList[position].playTime!!.toLong())
        holder.naatItemView.setOnClickListener {
            plainCallback.plainCallbackCalled(position, naatList)
        }
    }

    fun updateList(list: ArrayList<NaatModel>) {
        naatList.clear()
        naatList.addAll(list)
        notifyDataSetChanged()
    }

    class AllNaatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var naatName: TextView
        var naatDuration: TextView
        var naatItemView: LinearLayout

        init {
            naatName = itemView.findViewById(R.id.albumName)
            naatDuration = itemView.findViewById(R.id.naatDuration)
            naatItemView = itemView.findViewById(R.id.naatItemView)

        }
    }
}