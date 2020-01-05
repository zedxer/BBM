//package com.example.naqi.bebettermuslim.activities
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.support.v4.media.session.MediaSessionCompat
//import android.support.v4.media.session.PlaybackStateCompat
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.media.session.MediaButtonReceiver
//import com.example.naqi.bebettermuslim.R
//import com.example.naqi.bebettermuslim.Utils.Constants
//import com.example.naqi.bebettermuslim.callbacks.NotificationHelper
//import com.example.naqi.bebettermuslim.callbacks.PlayerCallbacks
//import com.google.android.exoplayer2.*
//import com.google.android.exoplayer2.metadata.id3.TextInformationFrame
//import com.google.android.exoplayer2.source.ExtractorMediaSource
//import com.google.android.exoplayer2.source.TrackGroupArray
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
//import com.google.android.exoplayer2.trackselection.TrackSelectionArray
//import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
//import com.google.android.exoplayer2.util.Util
//import kotlinx.android.synthetic.main.naat_player_layout.*
//
///**
// * Created by naqi on 15,May,2019
// */
//
//class ActivityTest2 : AppCompatActivity(), ExoPlayer.EventListener {
//    override fun onSeekProcessed() {
//        Toast.makeText(this, "onSeekProcessed", Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onPositionDiscontinuity(reason: Int) {
//        Toast.makeText(this, "onPositionDiscontinuity", Toast.LENGTH_SHORT).show()
//
//    }
//
//    override fun onRepeatModeChanged(repeatMode: Int) {
//        Toast.makeText(this, "onRepeatModeChanged", Toast.LENGTH_SHORT).show()
//
//    }
//
//    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
//        Toast.makeText(this, "onShuffleModeEnabledChanged", Toast.LENGTH_SHORT).show()
//
//    }
//
//    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
//        Toast.makeText(this, "onTimelineChanged", Toast.LENGTH_SHORT).show()
//
//    }
//
//    companion object {
//        @JvmStatic
//        lateinit var mMediaSession: MediaSessionCompat
//    }
//
//    private var mExoPlayer: SimpleExoPlayer? = null
//    private var mPlaybackState: PlaybackStateCompat.Builder? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.naat_player_layout)
//        NotificationHelper.setupNotificationChannel(this)
//        initializeMediaSession()
//        setupPlayer()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mExoPlayer!!.stop()
//        mExoPlayer!!.release()
//        mExoPlayer = null
//        mMediaSession.isActive = false
//    }
//
//    private fun setupPlayer() {
//        if (mExoPlayer == null) {
//            val trackSelector = DefaultTrackSelector()
//            val loadControl = DefaultLoadControl()
//            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl)
//            media_player.player = mExoPlayer
//            mExoPlayer!!.addListener(this)
//            val userAgent = Util.getUserAgent(this, "AndroidAPP")
//            val uri =
//                Uri.parse("https://technollage.com/BBM/BBMNaatData/Dawud_Wharnby_Ali/A_Whisper_of_Peace(1996)/04. Whisper of Peace.mp3")
//            val mediaSource = ExtractorMediaSource.Factory(
//                DefaultHttpDataSourceFactory("exoplayer-codelab")
//            ).createMediaSource(uri)
//            mMediaSession.setCallback(PlayerCallbacks(mExoPlayer!!))
//            mExoPlayer!!.prepare(mediaSource)
//            mExoPlayer!!.playWhenReady = true
//
//            mExoPlayer!!.setMetadataOutput {
//                NotificationHelper.showPlaybackNotification(
//                    mPlaybackState!!.build(),
//                    this,
//                    mMediaSession,
//                    (it.get(0) as TextInformationFrame).value,
//                    (it.get(2) as TextInformationFrame).value
//                )
//            }
//        }
//    }
//
//    private fun initializeMediaSession() {
//        mMediaSession = MediaSessionCompat(this, Constants.MEDIA_SESSION_TAG)
//        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
//        mMediaSession.setMediaButtonReceiver(null)
//
//        mPlaybackState = PlaybackStateCompat.Builder().setActions(
//            PlaybackStateCompat.ACTION_PLAY or
//                    PlaybackStateCompat.ACTION_PAUSE or
//                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
//                    PlaybackStateCompat.ACTION_FAST_FORWARD or
//                    PlaybackStateCompat.ACTION_REWIND or
//                    PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
//                    PlaybackStateCompat.ACTION_SKIP_TO_NEXT
//        )
//
//        mMediaSession.setPlaybackState(mPlaybackState!!.build())
//        mMediaSession.isActive = true
//    }
//
//    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//        if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
//            mPlaybackState!!.setState(
//                PlaybackStateCompat.STATE_PLAYING,
//                mExoPlayer!!.currentPosition, 1f
//            )
//        } else if (playbackState == ExoPlayer.STATE_READY) {
//            mPlaybackState!!.setState(
//                PlaybackStateCompat.STATE_PAUSED,
//                mExoPlayer!!.currentPosition, 1f
//            )
//        }
//        ActivityTest2.mMediaSession.setPlaybackState(mPlaybackState!!.build())
//        NotificationHelper.showPlaybackNotification(mPlaybackState!!.build(), this, mMediaSession, null, null)
//    }
//
//    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
//
//    }
//
//    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
//
//    }
//
//    override fun onPlayerError(error: ExoPlaybackException?) {
//
//    }
//
//    override fun onLoadingChanged(isLoading: Boolean) {
//
//    }
//
////    override fun onPositionDiscontinuity() {
////
////    }
////
////    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {
////
////    }
//
//    class MediaReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            MediaButtonReceiver.handleIntent(mMediaSession, intent)
//        }
//    }
//}
