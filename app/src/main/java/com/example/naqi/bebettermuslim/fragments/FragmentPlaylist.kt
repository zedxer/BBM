package com.example.naqi.bebettermuslim.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.COLOR_TINT_DARKBLUE
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.COLOR_TINT_GREEN
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.COLOR_TINT_LIGHTBLUE
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.COLOR_TINT_ORANGE
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.COLOR_TINT_RED
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.COLOR_TINT_YELLOW
import com.example.naqi.bebettermuslim.activities.AudioActivity
import com.example.naqi.bebettermuslim.adapter.AdapterPlaylist
import com.example.naqi.bebettermuslim.base_classes.CustomFragment
import com.example.naqi.bebettermuslim.callbacks.AddPlayList
import com.example.naqi.bebettermuslim.callbacks.ChildRecyclerCallback
import com.example.naqi.bebettermuslim.callbacks.RecyclerViewPositionCallback
import com.example.naqi.bebettermuslim.data.PlayListManager
import com.example.naqi.bebettermuslim.models.NaatModel
import com.example.naqi.bebettermuslim.models.PlaylistModel
import com.example.naqi.bebettermuslim.views.RecyclerItemTouchHelper
import com.example.naqi.bebettermuslim.views.SwipeToDeleteCallback2
import io.realm.RealmList
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by naqi on 10,May,2019
 */
class FragmentPlaylist : CustomFragment(), AddPlayList, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,
    RecyclerViewPositionCallback {


    var listOfNaat = RealmList<NaatModel>()
    val manager: PlayListManager = PlayListManager.INSTANCE
    lateinit var adapterPlaylist: AdapterPlaylist
    lateinit var listOfPlaylist: ArrayList<PlaylistModel>
    lateinit var addPlayList: AddPlayList

    private lateinit var rootView: View
    private lateinit var playlistRecycler: RecyclerView
    private lateinit var parentLayout: ConstraintLayout
    private var selectedPlaylistId: String = ""
    private var fragmentPlaylistInside = FragmentPlaylistInside()
    lateinit var childRecyclerCallback: ChildRecyclerCallback
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_playlist, container, false)
        init(rootView)
        return rootView
    }

    private fun getData() {
        listOfPlaylist = ArrayList(manager.getAllObjectsFromDatabase()!!)
    }

    override fun recylerViewItemPosition(position: Int) {
        selectedPlaylistId = listOfPlaylist[position].id
        val bundle = Bundle()
        bundle.putString("playlist_id", selectedPlaylistId)
        fragmentPlaylistInside.arguments = bundle
//        (activity as AudioActivity).changeFragment(fragmentPlaylistInside)
        /**here selected playlist id is send in album id*/
        childRecyclerCallback.childRecyclerCallback(null, listOfPlaylist[position].id, "PLAYLIST_INSIDE")
    }

    override fun init(rootView: View) {
        findViewsByIds(rootView)
        setListeners()
        getData()
        prepareRecyclerView()
        addPlayList = this


    }

    override fun findViewsByIds(rootView: View) {
        playlistRecycler = rootView.findViewById(R.id.playlistRecycler)
        parentLayout = rootView.findViewById(R.id.parentLayout)

    }

    override fun setListeners() {

    }

    override fun prepareRecyclerView() {
        adapterPlaylist = AdapterPlaylist(context, listOfPlaylist, this)
        playlistRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        playlistRecycler.adapter = AdapterPlaylist(context, listOfPlaylist, this)
        playlistRecycler.itemAnimator = DefaultItemAnimator()
        playlistRecycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        val swipeHandler = object : SwipeToDeleteCallback2(context!!) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = playlistRecycler.adapter as AdapterPlaylist
                val name = listOfPlaylist[viewHolder.adapterPosition].name

                // backup of removed item for undo purpose
                val deletedItem = listOfPlaylist[viewHolder.adapterPosition]
                val deletedIndex = viewHolder.adapterPosition

                // remove the item from recycler view
                manager.removeItemFromDatabase(adapterPlaylist.getObjectFromPosition(viewHolder.adapterPosition).id)
                adapter.removeItem(viewHolder.adapterPosition)
                adapter.notifyDataSetChanged()
                // showing snack bar with Undo option
                val snackbar = Snackbar
                    .make(parentLayout, "$name removed from list!", Snackbar.LENGTH_LONG)
                snackbar.setAction("UNDO") {
                    // undo is selected, restore the deleted item
                    adapter.restoreItem(deletedItem, deletedIndex)
                }
                snackbar.setActionTextColor(Color.YELLOW)
                snackbar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(playlistRecycler)
    }

    private fun addNewPlaylist(playListName: String, selectedColorInt: Int) {
        PlayListManager.INSTANCE.addSingleObjectInDatabase(
            UUID.randomUUID().toString(),
            playListName,
            selectedColorInt,
            listOfNaat
        )
    }

    private fun addNewPlaylist(playListName: String, selectedColorInt: Int, playlistNaat: RealmList<NaatModel>) {
        PlayListManager.INSTANCE.addSingleObjectInDatabase(
            UUID.randomUUID().toString(),
            playListName,
            selectedColorInt,
            playlistNaat
        )
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is AdapterPlaylist.PlaylistViewHolder) {
            // get the removed item name to display it in snack bar
            val name = listOfPlaylist[viewHolder.adapterPosition].name

            // backup of removed item for undo purpose
            val deletedItem = listOfPlaylist[viewHolder.adapterPosition]
            val deletedIndex = viewHolder.adapterPosition

            // remove the item from recycler view
            adapterPlaylist.removeItem(viewHolder.adapterPosition)
//            manager.removeItemFromDatabase(adapterPlaylist.getObjectFromPosition(viewHolder.adapterPosition).id)
            adapterPlaylist.notifyDataSetChanged()
            // showing snack bar with Undo option
            val snackbar = Snackbar
                .make(parentLayout, "$name removed from list!", Snackbar.LENGTH_LONG)
            snackbar.setAction("UNDO") {
                // undo is selected, restore the deleted item
                adapterPlaylist.restoreItem(deletedItem, deletedIndex)
            }
            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show()
        }
    }

    override fun addNewPlaylistCB() {
        adapterPlaylist.updateList(ArrayList(manager.getAllObjectsFromDatabase()))
    }

    fun showAddPlaylistDialog(context: Context, openedFrom: String, naat: NaatModel?) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_add_new_playlist)
        var selectedColorInt = 0
        var playListName = ""
        var selectedImageView: View? = null
        val playlistNaat = RealmList<NaatModel>()
        val imageViewColorRed = dialog.findViewById<View>(R.id.ivColorRed) as ImageView
        val imageViewColorDarkBlue = dialog.findViewById<View>(R.id.ivColorDarkBlue) as ImageView
        val imageViewColorGreen = dialog.findViewById<View>(R.id.ivColorGreen) as ImageView
        val imageViewColorLightBlue = dialog.findViewById<View>(R.id.ivColorLightBlue) as ImageView
        val imageViewColorOrange = dialog.findViewById<View>(R.id.ivColorOrange) as ImageView
        val imageViewColorYellow = dialog.findViewById<View>(R.id.ivColorYellow) as ImageView

        DrawableCompat.setTint(
            DrawableCompat.wrap(imageViewColorRed.drawable),
            ContextCompat.getColor(context, R.color.colorRed)
        )
        DrawableCompat.setTint(
            DrawableCompat.wrap(imageViewColorDarkBlue.drawable),
            ContextCompat.getColor(context, R.color.colorAccent)
        )
        DrawableCompat.setTint(
            DrawableCompat.wrap(imageViewColorGreen.drawable),
            ContextCompat.getColor(context, R.color.colorGreen)
        )
        DrawableCompat.setTint(
            DrawableCompat.wrap(imageViewColorLightBlue.drawable),
            ContextCompat.getColor(context, R.color.colorLightBlue)
        )
        DrawableCompat.setTint(
            DrawableCompat.wrap(imageViewColorOrange.drawable),
            ContextCompat.getColor(context, R.color.colorOrange)
        )
        DrawableCompat.setTint(
            DrawableCompat.wrap(imageViewColorYellow.drawable),
            ContextCompat.getColor(context, R.color.colorYellow)
        )

        val playListNameEditText = dialog.findViewById<View>(R.id.playlistName) as EditText
        val buttonConfirm = dialog.findViewById<View>(R.id.btnAddPlaylist) as TextView


        imageViewColorRed.setOnClickListener {
            if (selectedImageView != null) {
                selectedImageView!!.background = null
            }
            it.background = context.getDrawable(R.drawable.cirlce_small_nofill)
            selectedImageView = it
            selectedColorInt = COLOR_TINT_RED
        }
        imageViewColorDarkBlue.setOnClickListener {
            if (selectedImageView != null) {
                selectedImageView!!.background = null
            }
            it.background = context.getDrawable(R.drawable.cirlce_small_nofill)
            selectedImageView = it
            selectedColorInt = COLOR_TINT_DARKBLUE
        }
        imageViewColorGreen.setOnClickListener {
            if (selectedImageView != null) {
                selectedImageView!!.background = null
            }
            it.background = context.getDrawable(R.drawable.cirlce_small_nofill)
            selectedImageView = it
            selectedColorInt = COLOR_TINT_GREEN
        }
        imageViewColorLightBlue.setOnClickListener {
            if (selectedImageView != null) {
                selectedImageView!!.background = null
            }
            it.background = context.getDrawable(R.drawable.cirlce_small_nofill)
            selectedImageView = it
            selectedColorInt = COLOR_TINT_LIGHTBLUE
        }
        imageViewColorOrange.setOnClickListener {
            if (selectedImageView != null) {
                selectedImageView!!.background = null
            }
            it.background = context.getDrawable(R.drawable.cirlce_small_nofill)
            selectedImageView = it
            selectedColorInt = COLOR_TINT_ORANGE
        }
        imageViewColorYellow.setOnClickListener {
            if (selectedImageView != null) {
                selectedImageView!!.background = null
            }
            it.background = context.getDrawable(R.drawable.cirlce_small_nofill)
            selectedImageView = it
            selectedColorInt = COLOR_TINT_YELLOW
        }
        buttonConfirm.setOnClickListener {
            when (openedFrom) {
                "PLAYER" -> {
                    playlistNaat.add(naat!!)
                    playListName = playListNameEditText.text.toString()
                    addNewPlaylist(playListName, selectedColorInt, playlistNaat)
                    Toast.makeText(
                        context.applicationContext,
                        "${naat.title} added to $playListName",
                        Toast.LENGTH_SHORT
                    ).show()
                    addPlayList.addNewPlaylistCB()
                }
                else -> {
                    playListName = playListNameEditText.text.toString()
                    addNewPlaylist(playListName, selectedColorInt)
                    addPlayList.addNewPlaylistCB()
                }
            }
            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.show()
    }


}