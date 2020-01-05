package com.example.naqi.bebettermuslim.adapter

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.callbacks.PlainCallback
import com.example.naqi.bebettermuslim.callbacks.RecyclerChildItemClicked
import com.example.naqi.bebettermuslim.callbacks.RecyclerViewItemClicked
import com.example.naqi.bebettermuslim.data.LanguageManager
import com.example.naqi.bebettermuslim.models.AlbumModel
import com.example.naqi.bebettermuslim.models.RecitersModel
import com.thoughtbot.expandablerecyclerview.MultiTypeExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import com.thoughtbot.expandablerecyclerview.models.ExpandableListPosition
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.*
import com.example.naqi.bebettermuslim.BeBetterMuslim


/**
 * Created by naqi on 02,May,2019
 */
// Models
class RecitersExpandModel(var reciter: RecitersModel, title: String, var list: List<AlbumExpandableChildModel>?) :
    ExpandableGroup<AlbumExpandableChildModel>(title, list)

class AlbumExpandableChildModel() : Parcelable {
    var isSeeAll = false
    var album: AlbumModel = AlbumModel()

    constructor(
        id: String,
        reciter: String,
        releaseYear: Int,
        title: String,
        version: Int,
        isSeeAll: Boolean
    ) : this() {
        album._id = id
        album.reciter = reciter
        album.release_year = releaseYear
        album.title = title
        album.__v = version
        this.isSeeAll = isSeeAll
    }

    constructor(parcel: Parcel) : this() {
        album._id = parcel.readString()!!
        album.reciter = parcel.readString()
        album.release_year = parcel.readInt()
        album.title = parcel.readString()
        album.__v = parcel.readInt()
        isSeeAll = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeString(album._id)
        parcel?.writeString(album.reciter)
        parcel?.writeInt(album.release_year!!)
        parcel?.writeString(album.title)
        parcel?.writeInt(album.__v!!)
        parcel?.writeByte(if (isSeeAll) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlbumExpandableChildModel> {
        override fun createFromParcel(parcel: Parcel): AlbumExpandableChildModel {
            return AlbumExpandableChildModel(parcel)
        }

        override fun newArray(size: Int): Array<AlbumExpandableChildModel?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String =
        "AlbumChildModel(_id=${album._id}, releaseYear =${album.release_year}title = ${album.title} , version = ${album.__v} , isSeeAll = ${this.isSeeAll})"
}
//models ends here

const val ALBUM_CHILD_TYPE_1 = 0
const val ALBUM_CHILD_TYPE_2 = 1

class RecitersViewHolder(itemView: View, var context: Context) : GroupViewHolder(itemView) {
    var reciterImage: ImageView
    var titleArtist: TextView
    var langArtist: TextView
    var downArrow: ImageView
    var reciterCell: RelativeLayout
    var rotated = true
    var rotationAngle = 0

    init {
        reciterImage = itemView.findViewById(R.id.reciterImage)
        titleArtist = itemView.findViewById(R.id.titleArtist)
        langArtist = itemView.findViewById(R.id.langArtist)
        downArrow = itemView.findViewById(R.id.downArrow)
        reciterCell = itemView.findViewById(R.id.reciterCell)
    }


    override fun expand() {
//        animateExpand()
        //    reciterCell.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorYellowGrey))
//         rotated = false
//        animateCollapse(downArrow)
    }

    override fun collapse() {
         rotated = false
        animateCollapse(downArrow)
    }

//    private fun animateExpand() {
//        val rotate = RotateAnimation(360f, 180f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
//        rotate.duration = 300
//        rotate.fillAfter = true
//        downArrow.setAnimation(rotate)
//    }
//
//    private fun animateCollapse() {
//        val rotate = RotateAnimation(180f, 360f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
//        rotate.duration = 300
//        rotate.fillAfter = true
//        downArrow.setAnimation(rotate)
//    }
     fun animateCollapse(icon: View?) {

        if(rotated){
            rotated = false
//            reciterCell.setBackgroundColor(ContextCompat.getColor(context, R.color.colorYellowGrey))
            rotationAngle = if (rotationAngle == 0) 90 else 0  //toggle
            icon?.animate()!!.rotation(rotationAngle.toFloat()).setDuration(100).start()

        }else{
            rotated =true
//            reciterCell.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite))
            rotationAngle = if (rotationAngle == 90) 0 else 90  //toggle
            icon?.animate()!!.rotation(rotationAngle.toFloat()).setDuration(100).start()
        }
    }





}

class AlbumChildViewHolder(itemView: View) : ChildViewHolder(itemView) {
    var albumNumber: TextView
    var albumName: TextView
    var albumYearText: TextView
    var albumLayout: LinearLayout

    init {
        albumNumber = itemView.findViewById(R.id.albumNumber)
        albumName = itemView.findViewById(R.id.albumName)
        albumYearText = itemView.findViewById(R.id.albumYearText)
        albumLayout = itemView.findViewById(R.id.albumLayout)
    }

}

class SeeAllChildExpandViewHolder(itemView: View) : ChildViewHolder(itemView) {
    var seeAllNaat: TextView

    init {
        seeAllNaat = itemView.findViewById(R.id.seeAllNaat)
    }
}


class AdapterExpandableRecycler(
    private var context: Context,
    list: List<RecitersExpandModel>?,
    var groupClicked: RecyclerViewItemClicked,
    var childItemClicked: RecyclerChildItemClicked
) :
    MultiTypeExpandableRecyclerViewAdapter<RecitersViewHolder, ChildViewHolder>(list) {
    var rotationAngle = 0

    var view: View? = null
    var cellPosition: Int = 0
    var newholder :RecitersViewHolder? = null
    private fun animateCollapse(icon: View?) {
        rotationAngle = if (rotationAngle == 0) 90 else 0  //toggle
        icon?.animate()!!.rotation(rotationAngle.toFloat()).setDuration(100).start()
    }

    override fun onGroupExpanded(positionStart: Int, itemCount: Int) {
        if (itemCount == 0)
            return
        val groupIndex = expandableList.getUnflattenedPosition(positionStart).groupPos
        if (itemCount > 0) {
            notifyItemRangeInserted(positionStart, itemCount)
            for (grp in groups) {
                if (grp != groups.get(groupIndex)) {
                    if (this.isGroupExpanded(grp)) {
                        this.toggleGroup(grp)
                        newholder?.animateCollapse(newholder?.downArrow)
                    }
                }
            }
        }
    }

    override fun isGroup(viewType: Int): Boolean {
        return viewType == ExpandableListPosition.GROUP
    }
    override fun getItemCount(): Int {
        return super.getItemCount()
    }
//fun getGroupView(positionNow: Int):View? = view

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): RecitersViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.adapter_naat_artist, parent, false)
        return RecitersViewHolder(view, context)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): ChildViewHolder {
        when (viewType) {
            ALBUM_CHILD_TYPE_1 -> {
                val view = LayoutInflater.from(parent?.context).inflate(R.layout.adapter_naat_seeall, parent, false)
                return SeeAllChildExpandViewHolder(view)
            }
            ALBUM_CHILD_TYPE_2 -> {
                val view = LayoutInflater.from(parent?.context).inflate(R.layout.adapter_naats_of_artist, parent, false)
                return AlbumChildViewHolder(view)
            }
        }
        throw IllegalArgumentException("Invalid viewType")
    }


    override fun onBindGroupViewHolder(holder: RecitersViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?) {
        if (group is RecitersExpandModel) {
            val imageLoader = (context.applicationContext as BeBetterMuslim).imageLoader
            imageLoader.displayImage(group.reciter.pictureUrl, holder?.reciterImage)
//            holder?.reciterImage?.setImageResource(R.drawable.reciter)
//
            newholder = holder

            holder?.reciterImage?.tag = flatPosition

            holder?.titleArtist?.text = group.reciter.name
            if (group.reciter.languageModel?.name.isNullOrBlank()) {
                holder?.langArtist?.text =
                    LanguageManager.instance.getObjectFromDatabase(group.reciter.language!!)?.name
            } else {
                holder?.langArtist?.text = group.reciter.languageModel?.name
            }
        }
    }

    override fun onBindChildViewHolder(
        holder: ChildViewHolder?,
        flatPosition: Int,
        group: ExpandableGroup<*>?,
        childIndex: Int
    ) {
        val viewType = getItemViewType(flatPosition)
        when (viewType) {

            ALBUM_CHILD_TYPE_1 -> {
                if (holder is SeeAllChildExpandViewHolder) {
//todo listener here
                    if(group is RecitersExpandModel){
                        holder.seeAllNaat.setOnClickListener {
                            Toast.makeText(context, "${group?.title} ${group.reciter.id}", Toast.LENGTH_SHORT).show()
                            childItemClicked.recyclerChildListClicked(group.reciter.id, "", childIndex, Constants.RECIT_KEY)

                        }
                    }

//                    if ((group?.items?.get(childIndex)) is AlbumExpandableChildModel){
//                        val item = (group.items?.get(childIndex)) as AlbumExpandableChildModel
//                        holder.seeAllNaat.setOnClickListener {
////                            childItemClicked.recyclerChildListClicked(item.album.reciter, null, childIndex)
//                            Toast.makeText(context, "${group?.title} ${item.album._id}", Toast.LENGTH_SHORT).show()
//                        }                    }
                }
            }
            ALBUM_CHILD_TYPE_2 -> {
                if ((group?.items?.get(childIndex)) is AlbumExpandableChildModel && holder is AlbumChildViewHolder) {
                    val item = (group.items?.get(childIndex)) as AlbumExpandableChildModel
                    holder.albumLayout.setOnClickListener {
                        childItemClicked.recyclerChildListClicked(item.album.reciter, item.album._id, childIndex,Constants.ALB_KEY)
                    }
                    holder.albumName.text = item.album.title
                    holder.albumNumber.text = flatPosition.toString()
                    holder.albumYearText.text = item.album.release_year.toString()
                }

            }

        }

    }

//    override fun getChildViewType(position: Int, group: ExpandableGroup<*>?, childIndex: Int): Int {
//        group?.let {
//            if ((group.items.get(childIndex) as AlbumExpandableChildModel).isSeeAll) {
//                return ALBUM_CHILD_TYPE_1
//            } else {
//                return ALBUM_CHILD_TYPE_2
//            }
//        }
//        return -1
//    }

    override fun getChildViewType(position: Int, group: ExpandableGroup<*>?, childIndex: Int): Int {
        group?.let {
            return if ((group.items.get(childIndex) as AlbumExpandableChildModel).isSeeAll) {
                ALBUM_CHILD_TYPE_1
            } else {
                ALBUM_CHILD_TYPE_2
            }
        }
        return -1
    }

    override fun isChild(viewType: Int): Boolean {
        return viewType == ALBUM_CHILD_TYPE_1 || viewType == ALBUM_CHILD_TYPE_2
    }

}