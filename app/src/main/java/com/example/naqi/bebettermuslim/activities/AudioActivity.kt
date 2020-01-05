package com.example.naqi.bebettermuslim.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import com.example.naqi.bebettermuslim.BeBetterMuslim
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Service.AudioService
import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.adapter.NavAdapter
import com.example.naqi.bebettermuslim.base_classes.CustomActivity
import com.example.naqi.bebettermuslim.callbacks.ChildRecyclerCallback
import com.example.naqi.bebettermuslim.callbacks.PlainCallback
import com.example.naqi.bebettermuslim.callbacks.RecyclerViewPositionCallback
import com.example.naqi.bebettermuslim.data.PlayListManager
import com.example.naqi.bebettermuslim.data.ReciterManager
import com.example.naqi.bebettermuslim.fragments.*
import com.example.naqi.bebettermuslim.models.NaatModel
import com.example.naqi.bebettermuslim.models.PlaylistModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.naat_player_layout.*
import kotlinx.android.synthetic.main.naat_player_layout.navList
import kotlinx.android.synthetic.main.toolbar_naat_frag.*


/**
 * Created by naqi on 16,May,2019
 */
class AudioActivity : CustomActivity(), ChildRecyclerCallback, PlainCallback, RecyclerViewPositionCallback, NavAdapter.OnItemClickListener {


    lateinit var playerView: PlayerView
    private lateinit var playerControls: PlayerControlView
    private lateinit var playerControls2: PlayerControlView
    private lateinit var volumeSeekbar: SeekBar
    private lateinit var audioManager: AudioManager
    private lateinit var pageTitle: TextView
    private val fragmentPlaylist = FragmentPlaylist()
    private val fragmentNaat = FragmentNaat()
    private val fragmentAllNaat = FragmentAllNaatOfAlbum()
    private val fragmentPlaylistInside = FragmentPlaylistInside()
    private var notifReciterId = ""
    private var notifAlbumId = ""
    private var notifFrom = ""
    private var boundToService = true

    private var recoverstringNaattitle = ""
    private var recoverstringNaatartist = ""
    private var recoverstringNaatartistimage = ""

    val adapter = ViewPagerAdapter(supportFragmentManager)
    private var player: SimpleExoPlayer? = null
    private val playlistManager = PlayListManager.INSTANCE
    var CURRENT_PLAYING_NAAT: NaatModel? = null
    var CURRENT_PLAYING_NAAT_ID: String? = null
    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            boundToService = false
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is AudioService.VideoServiceBinder) {
                print("service audio service player set")
                player = service.getExoPlayerInstance()!!
                Constants.exoplayerInstace = player
                playerView.player = player
                playerControls.player = playerView.player
                playerControls2.player = playerView.player
                playerControls.showShuffleButton = true
                /*        playerControls?.player.addListener(object : Player.DefaultEventListener() {
                            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

                            }
                        })*/
                playerControls?.player?.addListener(object : Player.EventListener {
                    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                    }

                    override fun onSeekProcessed() {
                    }

                    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
//                        tempListOfNaat[playerControls?.player?.currentWindowIndex!!]
                        Toast.makeText(
                            this@AudioActivity,
                            "${Constants.listOfSentNaats[playerControls?.player?.currentWindowIndex!!].title}" + "slide panel",
                            Toast.LENGTH_SHORT
                        ).show()
                        setNaatVisuals(Constants.listOfSentNaats[playerControls?.player?.currentWindowIndex!!])
                        Log.v(
                            "CURRENTNAAT",
                            "Current Naat Playing ${Constants.listOfSentNaats[playerControls?.player?.currentWindowIndex!!].title}"
                        )

                    }

                    override fun onPlayerError(error: ExoPlaybackException?) {
                    }

                    override fun onLoadingChanged(isLoading: Boolean) {
                    }

                    override fun onPositionDiscontinuity(reason: Int) {
                    }

                    override fun onRepeatModeChanged(repeatMode: Int) {
                    }

                    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    }

                    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
                    }

                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        Toast.makeText(this@AudioActivity, "$playWhenReady" + "$playbackState", Toast.LENGTH_SHORT)
                            .show()
                        if (playWhenReady) {
                            if (playbackState == Player.STATE_IDLE) {
//                                playerView.player.playWhenReady = true
//                                playerView.player.playbackState = Player.STATE_READY

                            }
                        }
                    }

                })
                playerControls?.player.currentWindowIndex
                playerView.setRepeatToggleModes(Player.REPEAT_MODE_ONE)



                initControls()

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.naat_player_layout)
        init()
        bindServiceIfAlive()
    }


    fun bindServiceIfAlive() {
        if (Constants.exoplayerInstace != null) {
            if (Constants.exoplayerInstace?.playWhenReady!!) {
                playerControls.player = Constants.exoplayerInstace
                playerControls2.player = Constants.exoplayerInstace
                sliding_layout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

                if (Constants.artistNameNaat != "") {
                    setNaatVisuals(
                        Constants.artistNameNaat,
                        Constants.titleNaat,
                        ReciterManager.instance.getObjectFromDatabase(Constants.imageUrlNaat)?.pictureUrl!!
                    )
                }


            }

        }
    }

    fun setNaatVisuals(naat: NaatModel?) {
        naatTitle.text = naat?.title
        recoverstringNaattitle = naat?.title!!
        recoverstringNaatartist = naat.reciter!!
        recoverstringNaatartistimage = naat.reciter!!

        Constants.artistNameNaat = naat.title!!
        Constants.titleNaat = naat.reciter!!
        Constants.imageUrlNaat = naat.reciter!!
        artistName.text =
            ReciterManager.instance.getObjectFromDatabase(naat?.reciter!!)?.name.toString()

        val imageLoader = (this.applicationContext as BeBetterMuslim).imageLoader

        imageLoader.displayImage(
            ReciterManager.instance.getObjectFromDatabase(naat?.reciter!!)?.pictureUrl,
            artistImage
        )
        imageLoader.loadImage(
            ReciterManager.instance.getObjectFromDatabase(naat?.reciter!!)?.pictureUrl,
            object : SimpleImageLoadingListener() {
                override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                    Blurry.with(this@AudioActivity).async().radius(40)
                        .from(loadedImage).into(backImage)
                }
            })
    }

    fun setNaatVisuals(naatTitletext: String, artistNameID: String, artisitImageUrl: String) {
        naatTitle.text = naatTitletext
        artistName.text =
            ReciterManager.instance.getObjectFromDatabase(artistNameID)?.name.toString()

        val imageLoader = (this.applicationContext as BeBetterMuslim).imageLoader

        imageLoader.displayImage(
            artisitImageUrl,
            artistImage
        )
        backImage.post {
            imageLoader.loadImage(
                artisitImageUrl,
                object : SimpleImageLoadingListener() {
                    override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                        Blurry.with(this@AudioActivity).async().radius(40)
                            .from(loadedImage).into(backImage)
                    }
                })
        }

    }

    override fun plainCallbackCalled(position: Int, listOfNaats: ArrayList<NaatModel>) {
        startPlayer(listOfNaats, position, "ALLNAAT")
        setNaatVisuals(listOfNaats[position])
//        playerControls.player?.playbackState
        CURRENT_PLAYING_NAAT = listOfNaats[position]
        CURRENT_PLAYING_NAAT_ID = listOfNaats[position].id
        val naatURLlist = ArrayList<String>()
        for (item in listOfNaats) {

            naatURLlist.add(item.naatUrl!!)
        }

        player?.prepare(buildPlaylist(naatURLlist!!, position))
        player?.seekTo(position, C.TIME_UNSET)
        player?.playWhenReady = true

        sliding_layout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED

    }

    override fun recylerViewItemPosition(position: Int) {
        /***playlist dialog Clicked**/
        playlistManager.getAllObjectsFromDatabase()?.let {
            val list = it
            if (playlistManager.isNaatAlreadyExist(
                    list[position].id,
                    Constants.listOfSentNaats[playerControls?.player?.currentWindowIndex!!].id
                )
            ) {
                Toast.makeText(
                    this@AudioActivity,
                    "${Constants.listOfSentNaats[playerControls?.player?.currentWindowIndex!!].title} already exist in ${list[position]}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                addToPlaylist(
                    list[position].id,
                    Constants.listOfSentNaats[playerControls?.player?.currentWindowIndex!!]
                )
                Toast.makeText(this@AudioActivity, "Added in ${list[position].name}", Toast.LENGTH_SHORT).show()

            }

//            playlistManager.addNaatToPlaylist(list[position].id, naat)
        }


    }

    public fun changPageTitle(titleText: String) {
        pageTitle.text = titleText
    }

    override fun init() {
        if (intent.getStringExtra(Constants.FROM_KEY) == Constants.NOTIF_KEY) {
            recoverstringNaattitle = intent.getStringExtra(RECOVER_NAAT_TITLE)
            recoverstringNaatartist = intent.getStringExtra(RECOVER_NAAT_ARTIST)
            recoverstringNaatartistimage = intent.getStringExtra(RECOVER_NAAT_ARTIST_IMAGE)
            onNotificationClickedFunction()
        }
        playerView = findViewById(R.id.media_player)
        playerControls = findViewById(R.id.playerControl)
        playerControls2 = findViewById(R.id.playerControl2)
        pageTitle = findViewById(R.id.pageTitle)

        val drawerItems = resources.getStringArray(R.array.drawer_items)
        val drawables = resources.obtainTypedArray(R.array.drawer_drawables);
        navList.setHasFixedSize(true)
        navList.adapter = NavAdapter(drawerItems, drawables, this)

        Constants.audioConnection = connection
        setupViewPager(audioPager)
        tabLayout.setupWithViewPager(audioPager)
        tabLayout.getTabAt(0)?.icon = getDrawable(R.drawable.naat_artist)
        tabLayout.getTabAt(1)?.icon = getDrawable(R.drawable.naat_playlist)
        tabLayout.getTabAt(2)?.icon = getDrawable(R.drawable.naat_fav)
        fragmentNaat.childRecyclerCallback = this
        fragmentPlaylist.childRecyclerCallback = this
        fragmentAllNaat.plainCallbackActivity = this
        setListeners()
    }

    override fun onResume() {
        fragmentNaat.childRecyclerCallback = this
        fragmentPlaylist.childRecyclerCallback = this
        super.onResume()
    }

    private fun addToPlaylist(playlistId: String, naat: NaatModel) {
        playlistManager.addNaatToPlaylist(playlistId, naat)
    }

    private fun startPlayer() {
        val intent = Intent(this, AudioService::class.java)
//        intent.putStringArrayListExtra("URLs", arrayofURLs)
        startForegroundService(this, intent)
    }

    fun startPlayer(list: ArrayList<NaatModel>, naatIndex: Int, from: String) {
        val naatURLlist = ArrayList<String>()
        Constants.listOfSentNaats = list
        for (item in list) {

            naatURLlist.add(item.naatUrl!!)
        }
//        tempListOfNaat = list
        val intent = Intent(this, AudioService::class.java)
        intent.putStringArrayListExtra("URLs", naatURLlist)
        intent.putExtra("NAAT_INDEX", naatIndex)
        startForegroundService(this, intent)
//        playerControls.player?.seekTo(naatIndex, C.TIME_UNSET)
    }

    override fun setListeners() {
        backButton.setOnClickListener(this)
        navbarIcon.setOnClickListener(this)
        sliding_layout.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
//                Toast.makeText(this@AudioActivity, "$slideOffset" + "slide panel", Toast.LENGTH_SHORT).show()
                sce_toolbar.alpha = 1 - slideOffset * 2
                expanded_toolbar.alpha = slideOffset * 2
            }

            override fun onPanelStateChanged(
                panel: View?, previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                when (newState) {
                    SlidingUpPanelLayout.PanelState.EXPANDED -> {
//                        Toast.makeText(this@AudioActivity, "$newState" + "state changed", Toast.LENGTH_SHORT).show()
                        sce_toolbar.visibility = View.GONE
                        expanded_toolbar.visibility = View.VISIBLE
                    }
                    SlidingUpPanelLayout.PanelState.COLLAPSED -> {
                        sce_toolbar.visibility = View.VISIBLE
                        expanded_toolbar.visibility = View.GONE

                    }
                    SlidingUpPanelLayout.PanelState.DRAGGING -> {
                        sce_toolbar.visibility = View.VISIBLE
                        expanded_toolbar.visibility = View.VISIBLE


                    }
                    else -> {

                    }
                }
            }

        })
        addNaat.setOnClickListener(this)
        addToPlaylistIV.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.backButton ->{
                onBackPressed()
            }
            R.id.navbarIcon->{

            }
            R.id.addNaat -> {
                fragmentPlaylist.showAddPlaylistDialog(this, "PLAYLIST", null)

            }
            R.id.addToPlaylistIV -> {
                showPlaylistDialog()
            }
        }
    }

    private fun showPlaylistDialog() {
        val dialog = Dialog(this@AudioActivity)
        dialog.setContentView(R.layout.dialog_playlist)
        dialog.setCancelable(true)
        val listOfPlaylist = dialog.findViewById<View>(R.id.listOfPlaylist) as? RecyclerView
        val newPlaylistTV = dialog.findViewById<View>(R.id.newPlaylistTV) as? TextView
        val cancelTV = dialog.findViewById<View>(R.id.cancelTV) as? TextView
        cancelTV?.setOnClickListener {
            dialog.dismiss()
        }
        newPlaylistTV?.setOnClickListener {
            dialog.dismiss()
            fragmentPlaylist.showAddPlaylistDialog(
                this,
                "PLAYER",
                Constants.listOfSentNaats[playerControls?.player?.currentWindowIndex!!]
            )//todo add method callback here to add naat to this playlist

            dialog.dismiss()
        }
        listOfPlaylist?.layoutManager = LinearLayoutManager(this@AudioActivity, LinearLayoutManager.VERTICAL, false)
        listOfPlaylist?.adapter =
            AdapterNaatPlaylistOfDialog(this, ArrayList(playlistManager.getAllObjectsFromDatabase()), this, dialog)
        dialog.show()
    }

    override fun onBackPressed() {
        when (sliding_layout.panelState) {
            SlidingUpPanelLayout.PanelState.EXPANDED -> {
                sliding_layout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
            else -> {
                super.onBackPressed()
            }
        }

    }
    override fun onNavItemClick(view: View, position: Int) {
        selectItem(position)
    }

    override fun childRecyclerCallback(reciterId: String?, albumId: String?, from: String?) {
        allNaatView.visibility = View.VISIBLE
        when (from) {
            Constants.RECIT_KEY -> {
                if (fragmentAllNaat.isAdded) {

                } else {
                    val bundle = Bundle()
                    bundle.putString(Constants.RECITER_ID_CONTS, reciterId)
                    bundle.putString(Constants.FROM_KEY, Constants.RECIT_KEY)
                    fragmentAllNaat.arguments = bundle
                    changeFragment(fragmentAllNaat,false)
                }


            }

            Constants.ALB_KEY -> {
                if (fragmentAllNaat.isAdded) {

                } else {
                    val bundle = Bundle()
                    bundle.putString(Constants.RECITER_ID_CONTS, reciterId)
                    bundle.putString(Constants.ALBUM_ID_CONTS, albumId)
                    bundle.putString(Constants.FROM_KEY, Constants.ALB_KEY)
                    fragmentAllNaat.arguments = bundle
                    changeFragment(fragmentAllNaat,false)
                }

            }
            Constants.NOTIF_KEY -> {
                if (player != null) {
                    sliding_layout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
                }
            }
            else -> {
                if (fragmentPlaylistInside.isAdded) {

                } else {
                    val bundle = Bundle()
                    /**here album id is selected playlist id*/
                    bundle.putString("playlist_id", albumId)
                    fragmentPlaylistInside.arguments = bundle
                    changeFragment(fragmentPlaylistInside,false)
                }
            }
        }
    }

    fun changeFragment(fragment: Fragment , handleNav:Boolean) {
        supportFragmentManager.beginTransaction().run {
            setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.abc_fade_in,
                R.anim.exit_to_right
            )
            add(R.id.allNaatView, fragment)
            addToBackStack(null).commit()
        }
        if (handleNav)
            navDrawerLayout.closeDrawer(nav_view)
    }

    private fun startForegroundService(context: Context, intent: Intent): ComponentName? {
        return if (Util.SDK_INT >= 26) {
//            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            doBindService(intent)
            context.startForegroundService(intent)
        } else {
//            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            doBindService(intent)
            context.startService(intent)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    private fun buildPlaylist(listOfUri: ArrayList<String>, index: Int): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()

        for (item in listOfUri.withIndex()) {
            val myUri = Uri.parse(item.value)
            concatenatingMediaSource.addMediaSource(
                item.index, ExtractorMediaSource.Factory(
                    DefaultHttpDataSourceFactory("exoplayer-codelab")
                ).createMediaSource(myUri)
            )

        }
        return concatenatingMediaSource
    }

    private fun initControls() {
        try {
            volumeSeekbar = findViewById(R.id.volumeSeekBar) as SeekBar
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            volumeSeekbar.max = audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            volumeSeekbar.progress = audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC)


            volumeSeekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
                override fun onStopTrackingTouch(arg0: SeekBar) {}

                override fun onStartTrackingTouch(arg0: SeekBar) {}

                override fun onProgressChanged(arg0: SeekBar, progress: Int, arg2: Boolean) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

//    override fun onDestroy() {
//        if (boundToService && connection != null) {
//            doUnbindService()
//        }
//        unbindService(connection)
//
//        super.onDestroy()
//
//    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("RECOVER_NAAT_TITLE", recoverstringNaattitle)
        outState?.putString("RECOVER_NAAT_ARTIST", recoverstringNaatartist)
        outState?.putString("RECOVER_NAAT_ARTIST_IMAGE", recoverstringNaatartistimage)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        recoverstringNaattitle = savedInstanceState?.getString("RECOVER_NAAT_TITLE", "")!!
        recoverstringNaatartist = savedInstanceState?.getString("RECOVER_NAAT_ARTIST", "")!!
        recoverstringNaatartistimage = savedInstanceState?.getString("RECOVER_NAAT_ARTIST_IMAGE", "")!!
    }

    private fun doBindService(intent: Intent) {
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        boundToService = true
    }

    private fun doUnbindService() {
        unbindService(connection)
        boundToService = false
    }

    private fun onNotificationClickedFunction() {
        val intent = Intent(this, AudioService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        childRecyclerCallback(notifReciterId, notifAlbumId, notifFrom)
        sce_toolbar.visibility = View.GONE
        expanded_toolbar.visibility = View.VISIBLE
        sliding_layout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        setNaatVisuals(recoverstringNaattitle, recoverstringNaatartist, recoverstringNaatartistimage)
    }

    @SuppressLint("CommitTransaction") // commit() is called
    private fun selectItem(position: Int) {
        when (position) {
            0 -> {
                changeFragment(FragmentQiblaDirection(), true)
            }
            1 -> {
                Toast.makeText(this, "This is a triumph", Toast.LENGTH_SHORT).show()

                changeFragment(FragmentTasbeehCounter(), true)

            }
            2 -> {
                Toast.makeText(this, "This is a triumph", Toast.LENGTH_SHORT).show()

//                changeFragment(FragmentNaatBase(), true)

            }
            3 -> {
                startActivity(Intent(this, QuranActivity::class.java))
            }
            4 -> {
//                changeFragment(FragmentTest(), true)
                startActivity(Intent(this, AudioActivity::class.java))
            }
            5->{
                startActivity(Intent(this, NaatActivity::class.java))

            }
            else -> {

                Toast.makeText(this, "I am making a note here", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        adapter.addFrag(fragmentNaat, "Artist")
        adapter.addFrag(fragmentPlaylist, "Playlist")
        viewPager.offscreenPageLimit = 2
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

//                Toast.makeText(this@ActivityTripDetail, viewPager.adapter?.getPageTitle(position).toString(),Toast.LENGTH_SHORT).show()
                if (viewPager.adapter?.getPageTitle(position) == "Playlist") {
                    addNaat.visibility = View.VISIBLE
                } else {
                    addNaat.visibility = View.GONE

                }

            }

        })


    }

    companion object {
        const val PLAYER_FROM_ALLNAAT = "ALLNAAT"
        const val PLAYER_FROM_PLAYLIST = "PLAYLIST"
        const val PLAYER_FROM_FAVORITE = "FAVORITE"
        const val RECOVER_NAAT_TITLE = "RECOVER_NAAT_TITLE       "
        const val RECOVER_NAAT_ARTIST = "RECOVER_NAAT_ARTIST      "
        const val RECOVER_NAAT_ARTIST_IMAGE = "RECOVER_NAAT_ARTIST_IMAGE"

    }
}

class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
    private val mFragmentList = java.util.ArrayList<Fragment>()
    private val mFragmentTitleList = java.util.ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFrag(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    fun addNewFrag(fragment: Fragment, title: String, index: Int?) {
        if (index != null) {
            mFragmentList.add(index, fragment)
            mFragmentTitleList.add(index, title)
        } else {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }
        notifyDataSetChanged()
    }

    fun removeFrag(pos: Int) {
        mFragmentList.removeAt(pos)
        mFragmentTitleList.removeAt(pos)
        notifyDataSetChanged()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}

class AdapterNaatPlaylistOfDialog(
    var context: Context,
    private val listOfNaats: ArrayList<PlaylistModel>,
    var recyclerViewClicked: RecyclerViewPositionCallback, var dialog: Dialog
) :
    RecyclerView.Adapter<AdapterNaatPlaylistOfDialog.PlaylistOfDialogVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistOfDialogVH {
        val layoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_simple_naat_list, parent, false
        )
        return PlaylistOfDialogVH(layoutView)
    }

    override fun getItemCount(): Int {
        return listOfNaats.size

    }

    override fun onBindViewHolder(holder: PlaylistOfDialogVH, position: Int) {
        holder.naatNameOflist.text = listOfNaats[position].name
        holder.naatNameOflist.setOnClickListener {
            recyclerViewClicked.recylerViewItemPosition(position)
            dialog.dismiss()
        }
    }

    class PlaylistOfDialogVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var naatNameOflist: TextView

        init {
            naatNameOflist = itemView.findViewById(R.id.naatNameOflist)
        }
    }
}