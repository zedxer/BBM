//package com.example.naqi.bebettermuslim.Utils
////
////import android.R
////import android.app.Dialog
////import android.content.Context
////import android.graphics.Color
////import android.graphics.drawable.ColorDrawable
////import android.support.v4.content.ContextCompat
////import android.support.v4.graphics.drawable.DrawableCompat
////import android.view.View
////import android.widget.EditText
////import android.widget.ImageView
////import android.widget.TextView
////
/////**
//// * Created by naqi on 13,May,2019
//// */
////class TempCode{
////    fun showAddPlaylistDialog(context: Context) {
////        val dialog = Dialog(context, R.style.ThemeOverlay_Material_Light)
//////        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
////        dialog.setContentView(com.example.naqi.bebettermuslim.R.layout.dialog_add_new_playlist)
//////        dialog.setCancelable(false)
////        val window = dialog.window
////        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
////        window?.setBackgroundDrawableResource(com.example.naqi.bebettermuslim.R.color.dialogTranslucent)
////        val imageViewColorRed = dialog.findViewById<View>(com.example.naqi.bebettermuslim.R.id.ivColorRed) as ImageView
////        val imageViewColorDarkBlue = dialog.findViewById<View>(com.example.naqi.bebettermuslim.R.id.ivColorDarkBlue) as ImageView
////        val imageViewColorGreen = dialog.findViewById<View>(com.example.naqi.bebettermuslim.R.id.ivColorGreen) as ImageView
////        val imageViewColorLightBlue = dialog.findViewById<View>(com.example.naqi.bebettermuslim.R.id.ivColorLightBlue) as ImageView
////        val imageViewColorOrange = dialog.findViewById<View>(com.example.naqi.bebettermuslim.R.id.ivColorOrange) as ImageView
////        val imageViewColorYellow = dialog.findViewById<View>(com.example.naqi.bebettermuslim.R.id.ivColorYellow) as ImageView
////
////        DrawableCompat.setTint(
////            DrawableCompat.wrap(imageViewColorRed.drawable),
////            ContextCompat.getColor(context, com.example.naqi.bebettermuslim.R.color.colorRed)
////        )
////        DrawableCompat.setTint(
////            DrawableCompat.wrap(imageViewColorDarkBlue.drawable),
////            ContextCompat.getColor(context, com.example.naqi.bebettermuslim.R.color.colorAccent)
////        )
////        DrawableCompat.setTint(
////            DrawableCompat.wrap(imageViewColorGreen.drawable),
////            ContextCompat.getColor(context, com.example.naqi.bebettermuslim.R.color.colorGreen)
////        )
////        DrawableCompat.setTint(
////            DrawableCompat.wrap(imageViewColorLightBlue.drawable),
////            ContextCompat.getColor(context, com.example.naqi.bebettermuslim.R.color.colorLightBlue)
////        )
////        DrawableCompat.setTint(
////            DrawableCompat.wrap(imageViewColorOrange.drawable),
////            ContextCompat.getColor(context, com.example.naqi.bebettermuslim.R.color.colorOrange)
////        )
////        DrawableCompat.setTint(
////            DrawableCompat.wrap(imageViewColorYellow.drawable),
////            ContextCompat.getColor(context, com.example.naqi.bebettermuslim.R.color.colorYellow)
////        )
////
////
////        val playListNameEditText = dialog.findViewById<View>(com.example.naqi.bebettermuslim.R.id.playlistName) as EditText
////        val buttonConfirm = dialog.findViewById<View>(com.example.naqi.bebettermuslim.R.id.btnAddPlaylist) as TextView
////        buttonConfirm.setOnClickListener {
////            playListName = playListNameEditText.text.toString()
////            addNewPlaylist(playListName, selectedColorInt)
////            addPlayList.addNewPlaylistCB()
////            dialog.dismiss()
////        }
////        imageViewColorRed.setOnClickListener { selectedColorInt = com.example.naqi.bebettermuslim.R.color.colorRed }
////        imageViewColorDarkBlue.setOnClickListener { selectedColorInt = com.example.naqi.bebettermuslim.R.color.colorAccent }
////        imageViewColorGreen.setOnClickListener { selectedColorInt = com.example.naqi.bebettermuslim.R.color.colorGreen }
////        imageViewColorLightBlue.setOnClickListener { selectedColorInt = com.example.naqi.bebettermuslim.R.color.colorLightBlue }
////        imageViewColorOrange.setOnClickListener { selectedColorInt = com.example.naqi.bebettermuslim.R.color.colorOrange }
////        imageViewColorYellow.setOnClickListener { selectedColorInt = com.example.naqi.bebettermuslim.R.color.colorYellow }
////        dialog.show()
////    }
////
////
////}
//

/**new*/
//package com.example.naqi.bebettermuslim.views
//
///**
// * Created by naqi on 13,May,2019
// */
//import android.graphics.Canvas
//import android.support.v7.widget.RecyclerView
//import android.support.v7.widget.helper.ItemTouchHelper
//import android.view.View
//import com.example.naqi.bebettermuslim.adapter.AdapterPlaylist
//
///**
// * Created by ravi on 29/09/17.
// */
//
//class RecyclerItemTouchHelper(dragDirs: Int, swipeDirs: Int, private val listener: RecyclerItemTouchHelperListener) :
//    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
//
//    override fun onMove(
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        target: RecyclerView.ViewHolder
//    ): Boolean {
//        return true
//    }
//
//    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
//        if (viewHolder != null) {
//            val foregroundView = (viewHolder as AdapterPlaylist.PlaylistViewHolder).viewForeground
//
//            ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(foregroundView)
//        }
//    }
//
//    override fun onChildDrawOver(
//        c: Canvas, recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
//        actionState: Int, isCurrentlyActive: Boolean
//    ) {
//        val foregroundView = (viewHolder as AdapterPlaylist.PlaylistViewHolder).viewForeground
//        ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(
//            c, recyclerView, foregroundView, dX, dY,
//            actionState, isCurrentlyActive
//        )
//    }
//
//    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
//        val foregroundView = (viewHolder as AdapterPlaylist.PlaylistViewHolder).viewForeground
//        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(foregroundView)
//    }
//
//    override fun onChildDraw(
//        c: Canvas, recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
//        actionState: Int, isCurrentlyActive: Boolean
//    ) {
//        val foregroundView = (viewHolder as AdapterPlaylist.PlaylistViewHolder).viewForeground
//
//        ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(
//            c, recyclerView, foregroundView, dX, dY,
//            actionState, isCurrentlyActive
//        )
//    }
//
//    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//        listener.onSwiped(viewHolder, direction, viewHolder.adapterPosition)
//    }
//
//    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
//        return super.convertToAbsoluteDirection(flags, layoutDirection)
//    }
//
//    interface RecyclerItemTouchHelperListener {
//        fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int)
//    }
//}