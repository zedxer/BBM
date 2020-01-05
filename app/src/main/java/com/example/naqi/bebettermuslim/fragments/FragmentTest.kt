package com.example.naqi.bebettermuslim.fragments

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.media.session.MediaButtonReceiver
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.activities.HomeActivity
import com.example.naqi.bebettermuslim.base_classes.CustomFragment
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.net.URI


/**
 * Created by naqi on 14,May,2019
 */
class FragmentTest() : CustomFragment() {
    private var player: SimpleExoPlayer? = null
    private lateinit var rootView: View
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L
    private lateinit var componentListener: ComponentListener
    private lateinit var media_player: PlayerView
    private lateinit var mStateBuilder: PlaybackStateCompat.Builder
    private lateinit var mNotificationManager: NotificationManager
    val NOTIFICATION_ID = 0
    val CHANNEL_ID = "EXO"
    private lateinit var token: MediaSessionCompat.Token
    private lateinit var playerNotificationManager: PlayerNotificationManager
//
//    companion object {
//        private lateinit var mMediaSession: MediaSessionCompat
//
//        class MediaReceiver : BroadcastReceiver() {
//
//            override fun onReceive(context: Context, intent: Intent) {
//                MediaButtonReceiver.handleIntent(mMediaSession, intent);
//            }
//        }
//
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.naat_player_layout, container, false)
        init(rootView)
        return rootView
    }

    override fun init(rootView: View) {
        findViewsByIds(rootView)
        componentListener = ComponentListener()
//        mMediaSession = MediaSessionCompat(context, "TAG")
//        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
//        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PLAY or
                    PlaybackStateCompat.ACTION_PAUSE or
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )
//        mMediaSession.setPlaybackState(mStateBuilder.build());
//        mMediaSession.setCallback(MySessionCallback());
//        mMediaSession.isActive = true;
        playerNotificationManager = PlayerNotificationManager(
            context,
            CHANNEL_ID,
            NOTIFICATION_ID,
            DescriptionAdapter()
        );
    }

    override fun findViewsByIds(rootView: View) {
        media_player = rootView.findViewById(R.id.media_player)
    }

    override fun setListeners() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(context),
            DefaultTrackSelector(), DefaultLoadControl()
        )
//        if(player ==null){
//            val adaptiveTrackSelectionFactory = AdaptiveTrackSelection.Factory(BANDWIDTH_METER)

//            player = ExoPlayerFactory.newSimpleInstance(
//                DefaultRenderersFactory(context),
//                DefaultTrackSelector(adaptiveTrackSelectionFactory),
//                DefaultLoadControl()
//            )
        media_player.player = player
        player?.addListener(componentListener);
        player?.playWhenReady = playWhenReady
        player?.seekTo(currentWindow, playbackPosition)
//        val uri = Uri.parse("http://www.villopim.com.br/android/Music_01.mp3")
        val uri =
            Uri.parse("https://technollage.com/BBM/BBMNaatData/Dawud_Wharnby_Ali/A_Whisper_of_Peace(1996)/04. Whisper of Peace.mp3")
        val mediaSource = buildMediaSource(uri)
        player?.prepare(mediaSource, true, false)
//        showNotification(mStateBuilder.build());
        playerNotificationManager.setPlayer(player);
    }

//    }

    //    private fun buildMediaSource(uri: Uri): MediaSource {
//        return ExtractorMediaSource.Factory(
//            DefaultHttpDataSourceFactory("exoplayer-codelab")
//        ).createMediaSource(uri)
//    }
    private fun buildMediaSource(uri: Uri): MediaSource {
//        val manifestDataSourceFactory = DefaultHttpDataSourceFactory("ua")
//        val dashChunkSourceFactory = DefaultDashChunkSource.Factory(
//            DefaultHttpDataSourceFactory("ua", BANDWIDTH_METER)
//        )
//        return DashMediaSource.Factory(
//            dashChunkSourceFactory,
//            manifestDataSourceFactory
//        ).createMediaSource(uri)

        return ExtractorMediaSource.Factory(
            DefaultHttpDataSourceFactory("exoplayer-codelab")
        ).createMediaSource(uri)
    }

    private fun buildPlaylist(listOfUri: ArrayList<Uri>): ConcatenatingMediaSource {
        val playlist = ArrayList<MediaSource>()
        val concatenatingMediaSource = ConcatenatingMediaSource()

        for (item in listOfUri.withIndex()) {
//            playlist.add(
//                ExtractorMediaSource.Factory(
//                    DefaultHttpDataSourceFactory("exoplayer-codelab")
//                ).createMediaSource(item)
//            )
            concatenatingMediaSource.addMediaSource(item.index, ExtractorMediaSource.Factory(
                    DefaultHttpDataSourceFactory("exoplayer-codelab")
                ).createMediaSource(item.value))

        }
       return concatenatingMediaSource
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
        }
    }

    private fun releasePlayer() {
        if (player != null) {
//            mNotificationManager.cancelAll()
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            player!!.removeListener(componentListener)
            player!!.release()
            player = null
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        media_player.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun prepareRecyclerView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun showNotification(state: PlaybackStateCompat) {

        // You only need to create the channel on API 26+ devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val builder = NotificationCompat.Builder(context!!, CHANNEL_ID)

        val icon: Int
        val play_pause: String
        if (state.state == PlaybackStateCompat.STATE_PLAYING) {
            icon = R.drawable.exo_controls_pause
            play_pause = "pause"
        } else {
            icon = R.drawable.exo_controls_play
            play_pause = "play"
        }

        val playPauseAction = NotificationCompat.Action(
            icon, play_pause,
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                context,
                PlaybackStateCompat.ACTION_PLAY_PAUSE
            )
        )

        val restartAction = NotificationCompat.Action(
            R.drawable.exo_controls_previous, "restart",
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                context,
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        )

        val contentPendingIntent = PendingIntent.getActivity(context, 0, Intent(context, HomeActivity::class.java), 0)

//        token = mMediaSession.getSessionToken()

        builder.setContentTitle("Guess")
            .setContentText("PLAYER TEXT")
            .setContentIntent(contentPendingIntent)
            .setSmallIcon(R.drawable.add_to_playlist_icon)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(restartAction)
            .addAction(playPauseAction)
            .setStyle(
                android.support.v4.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(token)
                    .setShowActionsInCompactView(0, 1)
            )

        mNotificationManager =
            context?.applicationContext?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(0, builder.build())
    }

    private inner class MySessionCallback : MediaSessionCompat.Callback() {
        override fun onPlay() {
            player?.setPlayWhenReady(true)
        }

        override fun onPause() {
            player?.setPlayWhenReady(false)
        }

        override fun onSkipToPrevious() {
            player?.seekTo(0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        mNotificationManager =
            context?.applicationContext?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // The id of the channel.
        val id = CHANNEL_ID
        // The user-visible name of the channel.
        val name = "Media playback"
        // The user-visible description of the channel.
        val description = "Media playback controls"
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(id, name, importance)
        // Configure the notification channel.
        mChannel.setDescription(description)
        mChannel.setShowBadge(false)
        mChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        mNotificationManager.createNotificationChannel(mChannel)
    }

    private inner class DescriptionAdapter : PlayerNotificationManager.MediaDescriptionAdapter {

        override fun getCurrentContentTitle(player: Player): String {
            val window = player.getCurrentWindowIndex()
            return "reefsd"
        }

        override fun getCurrentContentText(player: Player): String {
            val window = player.getCurrentWindowIndex()
            return "ssfddfs"
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            return getLargeIcon()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent {
            val window = player.getCurrentWindowIndex()
            return createPendingIntent()
        }
    }

    fun getLargeIcon(): Bitmap {
        return BitmapFactory.decodeResource(context!!.getResources(), R.drawable.add_to_playlist_icon)
    }

    fun createPendingIntent(): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            32,
            Intent(context, HomeActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}

private class ComponentListener : Player.DefaultEventListener() {

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        var stateString = ""
        stateString = when (playbackState) {
            ExoPlayer.STATE_IDLE ->
                "ExoPlayer.STATE_IDLE      -"
            ExoPlayer.STATE_BUFFERING ->
                "ExoPlayer.STATE_BUFFERING -"
            ExoPlayer.STATE_READY ->
                "ExoPlayer.STATE_READY     -"
            ExoPlayer.STATE_ENDED ->
                "ExoPlayer.STATE_ENDED     -"
            else ->
                "UNKNOWN_STATE             -"

        }
//   Log.d(TAG, "changed state to " + stateString
//       + " playWhenReady: " + playWhenReady);
    }
}

