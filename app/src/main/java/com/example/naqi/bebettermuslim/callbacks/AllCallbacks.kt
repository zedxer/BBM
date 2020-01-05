package com.example.naqi.bebettermuslim.callbacks

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.example.naqi.bebettermuslim.models.NaatModel

/**
 * Created by naqi on 28,February,2019
 */

interface FragPrefChangeCallback {
    fun onTimeFormatChange(formatCode: Int)
}

interface AddPlayList {
    fun addNewPlaylistCB()
}

interface ChildRecyclerCallback {
    fun childRecyclerCallback(reciterId: String?, albumId: String?, from: String?)
}

interface RecyclerViewItemClicked {
    fun recyclerViewListClicked(v: View, img: ImageView, position: Int)
}

interface RecyclerChildItemClicked {
    fun recyclerChildListClicked(reciterId: String?, albumId: String?, position: Int, from: String?)
}

interface PlainCallback {
    fun plainCallbackCalled(position: Int, listOfNaats: ArrayList<NaatModel>)
}
interface RecyclerViewPositionCallback{
    fun recylerViewItemPosition(position: Int)
}
interface RecyclerViewHolderCallback{
    fun recyclerViewHolderCB(holder: RecyclerView.ViewHolder, position:Int, itemClicked:Int)
}