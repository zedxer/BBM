package com.example.naqi.bebettermuslim.Service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.support.annotation.Nullable
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.BADGE_ICON_NONE
import android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import com.example.naqi.bebettermuslim.BeBetterMuslim
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.activities.AudioActivity
import com.example.naqi.bebettermuslim.activities.NaatActivity
import com.example.naqi.bebettermuslim.data.ReciterManager
import com.example.naqi.bebettermuslim.models.NaatModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener

class AudioService : Service() {
    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
//        (intent?.getStringArrayListExtra("URLs"))?.let {
//            arrayOfURLs = it as ArrayList<String>
//            naatIndex = intent.getIntExtra("NAAT_INDEX", 0)
//            if (arrayOfURLs != null) {
//                player?.prepare(buildPlaylist(arrayOfURLs!!, naatIndex))
//                player?.seekTo(naatIndex, C.TIME_UNSET)
//                player?.setPlayWhenReady(true)
//            }
//        }
        naatIndex = intent?.getIntExtra("NAAT_INDEX", 0)
        if (listOfNaatModels.isNotEmpty()) {
            arrayOfURLs?.clear()
            for (item in listOfNaatModels) {
                if (item.naatUrl != null) {

                    arrayOfURLs?.add(item.naatUrl!!)
                }
            }
            if (arrayOfURLs != null) {
                player?.prepare(buildPlaylist(arrayOfURLs!!, naatIndex!!))
                player?.seekTo(naatIndex!!, C.TIME_UNSET)
                player?.playWhenReady = true
            }
        }
        return VideoServiceBinder()
    }


    var player: SimpleExoPlayer? = null
    val PLAYBACK_NOTIFICATION_ID = 1
    lateinit var context: Context
    private var playerNotificationManager: PlayerNotificationManager? = null
    private var playerNotificationManager2: PlayerNotificationManager? = null
    private var listOfNaatModels = ArrayList<NaatModel>()
    private lateinit var mediaSession: MediaSessionCompat;

    private lateinit var mediaSessionConnector: MediaSessionConnector;
    var arrayOfURLs: ArrayList<String>? = ArrayList()
    var naatIndex: Int? = 0

    inner class VideoServiceBinder : Binder() {
        fun getExoPlayerInstance() = player
        fun getNaatDetails() {

        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        val adaptiveTrackSelection = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())
        player = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(this),
            DefaultTrackSelector(adaptiveTrackSelection),
            DefaultLoadControl()
        )
        playerNotificationManager2 = PlayerNotificationManager(this,
            "playback_channel",
            1,
            object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun createCurrentContentIntent(player: Player?): PendingIntent? {
                    val intent = Intent(context, AudioActivity::class.java)
                    return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                }

                override fun getCurrentContentText(player: Player?): String? {
                    return "Sample Description"
                }

                override fun getCurrentContentTitle(player: Player?): String {
                    return "Sample Title"
                }

                override fun getCurrentLargeIcon(
                    player: Player?,
                    callback: PlayerNotificationManager.BitmapCallback?
                ): Bitmap? {
                    return BitmapFactory.decodeResource(context!!.getResources(), R.drawable.add_to_playlist_icon)
                }
            }, object : PlayerNotificationManager.CustomActionReceiver {
                val actions = arrayListOf<String>(
                    PlayerNotificationManager.ACTION_PREVIOUS,
                    PlayerNotificationManager.ACTION_REWIND,
                    PlayerNotificationManager.ACTION_PLAY,
                    PlayerNotificationManager.ACTION_PAUSE,
                    PlayerNotificationManager.ACTION_FAST_FORWARD,
                    PlayerNotificationManager.ACTION_NEXT,
                    PlayerNotificationManager.ACTION_STOP
                )

                override fun createCustomActions(context: Context?): MutableMap<String, NotificationCompat.Action>? {
                    //                return mutableMapOf()
                    val prevAction = Intent()
                    prevAction.putExtra("action", actions[0])
                    val backwordAction = Intent()
                    backwordAction.putExtra("action", actions[1])
                    val playAction = Intent()
                    playAction.putExtra("action", actions[2])
                    val pauseAction = Intent()

                    pauseAction.putExtra("action", actions[3])
                    val forwardAction = Intent()
                    forwardAction.putExtra("action", actions[4])
                    val nextAction = Intent()
                    nextAction.putExtra("action", actions[5])
                    val stopAction = Intent()
                    nextAction.putExtra("action", actions[6])
                    return mutableMapOf(
                        Pair(
                            actions[0], NotificationCompat.Action(
                                R.drawable.exo_icon_previous, actions[0],
                                PendingIntent.getBroadcast(
                                    context,
                                    1,
                                    Intent(prevAction).setPackage(context?.packageName),
                                    PendingIntent.FLAG_CANCEL_CURRENT
                                )
                            )
                        ),
                        Pair(
                            actions[1], NotificationCompat.Action(
                                R.drawable.exo_icon_rewind, actions[1],
                                PendingIntent.getBroadcast(
                                    context,
                                    1,
                                    Intent(backwordAction).setPackage(context?.packageName),
                                    PendingIntent.FLAG_CANCEL_CURRENT
                                )
                            )
                        ),
                        Pair(
                            actions[2], NotificationCompat.Action(
                                R.drawable.exo_icon_play, actions[2],
                                PendingIntent.getBroadcast(
                                    context,
                                    1,
                                    Intent(playAction).setPackage(context?.packageName),
                                    PendingIntent.FLAG_CANCEL_CURRENT
                                )
                            )
                        ),
                        Pair(
                            actions[3], NotificationCompat.Action(
                                R.drawable.exo_icon_pause, actions[3],
                                PendingIntent.getBroadcast(
                                    context,
                                    1,
                                    Intent(pauseAction).setPackage(context?.packageName),
                                    PendingIntent.FLAG_CANCEL_CURRENT
                                )
                            )
                        ),
                        Pair(
                            actions[4], NotificationCompat.Action(
                                R.drawable.exo_icon_fastforward, actions[4],
                                PendingIntent.getBroadcast(
                                    context,
                                    1,
                                    Intent(forwardAction).setPackage(context?.packageName),
                                    PendingIntent.FLAG_CANCEL_CURRENT
                                )
                            )
                        ),
                        Pair(
                            actions[5], NotificationCompat.Action(
                                R.drawable.exo_icon_next, actions[5],
                                PendingIntent.getBroadcast(
                                    context,
                                    1,
                                    Intent(nextAction).setPackage(context?.packageName),
                                    PendingIntent.FLAG_CANCEL_CURRENT
                                )
                            )
                        ),
                        Pair(
                            actions[6], NotificationCompat.Action(
                                R.drawable.exo_icon_stop, actions[6],
                                PendingIntent.getBroadcast(
                                    context,
                                    1,
                                    Intent(stopAction).setPackage(context?.packageName),
                                    PendingIntent.FLAG_CANCEL_CURRENT
                                )
                            )
                        )
                    )
                }

                override fun getCustomActions(player: Player?): MutableList<String> {
                    return actions
                }

                override fun onCustomAction(player: Player?, action: String?, intent: Intent?) {
                    Log.e("TAG", action ?: "")
                    if (action == PlayerNotificationManager.ACTION_PAUSE) {

                        player?.stop()
                        stopSelf()

                    }
                }
            }
        )
        listOfNaatModels = Constants.listOfSentNaats
        val reciterManager = ReciterManager.instance
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            this,
            "playback_channel",
            R.string.playback_channel_name,
            1,
            object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun createCurrentContentIntent(player: Player?): PendingIntent? {
                    val intent = Intent(context, AudioActivity::class.java)
                    intent.putExtra(Constants.FROM_KEY, Constants.NOTIF_KEY)
                    intent.putExtra(AudioActivity.RECOVER_NAAT_TITLE, listOfNaatModels[player?.currentWindowIndex!!].title )
                    intent.putExtra(AudioActivity.RECOVER_NAAT_ARTIST, reciterManager.getObjectFromDatabase(listOfNaatModels[player.currentWindowIndex].reciter!!)?.name)
                    intent.putExtra(AudioActivity.RECOVER_NAAT_ARTIST_IMAGE, reciterManager.getObjectFromDatabase(listOfNaatModels[player.currentWindowIndex].reciter!!)?.pictureUrl)
                    return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                }

                override fun getCurrentContentText(player: Player?): String? {
                    return reciterManager.getObjectFromDatabase(listOfNaatModels[player?.currentWindowIndex!!].reciter!!)
                        ?.name
                }

                override fun getCurrentContentTitle(player: Player?): String? {
                    return listOfNaatModels[player?.currentWindowIndex!!].title
                }

                override fun getCurrentLargeIcon(
                    player: Player?,
                    callback: PlayerNotificationManager.BitmapCallback?
                ): Bitmap? {
                    var bitmap:Bitmap? = BitmapFactory.decodeResource(context!!.getResources(), R.drawable.add_to_playlist_icon)
                    val imageLoader = (context.applicationContext as BeBetterMuslim).imageLoader

                    imageLoader.loadImage(
                        reciterManager.getObjectFromDatabase(listOfNaatModels[player?.currentWindowIndex!!].reciter!!)
                            ?.pictureUrl,
                        object : SimpleImageLoadingListener() {
                            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                                bitmap = loadedImage
                            }
                        })
                    return bitmap
                }
            }
        )

        playerNotificationManager?.setNotificationListener(object : PlayerNotificationManager.NotificationListener {
            override fun onNotificationCancelled(notificationId: Int) {
                stopSelf()
            }

            override fun onNotificationStarted(notificationId: Int, notification: Notification?) {
                startForeground(notificationId, notification)
            }
        })
//        playerNotificationManager2?.setUseNavigationActions(true)
//        playerNotificationManager2?.setStopAction(null)
        playerNotificationManager?.setVisibility(VISIBILITY_PUBLIC);
        playerNotificationManager?.setUseNavigationActions(true);
        playerNotificationManager?.setFastForwardIncrementMs(0);
        playerNotificationManager?.setRewindIncrementMs(0);
        playerNotificationManager?.setStopAction(PlayerNotificationManager.ACTION_STOP);
        playerNotificationManager?.setSmallIcon(R.mipmap.ic_launcher);
        playerNotificationManager?.setBadgeIconType(BADGE_ICON_NONE);
        playerNotificationManager?.setPriority(NotificationCompat.PRIORITY_LOW);
        player?.addListener(object : Player.DefaultEventListener() {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                playerNotificationManager?.setOngoing(!playWhenReady)
            }

        })
        playerNotificationManager?.setPlayer(player)
        mediaSession = MediaSessionCompat(context, "MEDIA SESSION_TAG")
        mediaSession.isActive = true

        playerNotificationManager?.setMediaSessionToken(mediaSession.sessionToken)

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
            override fun getMediaDescription(player: Player?, windowIndex: Int): MediaDescriptionCompat {
                return MediaDescriptionCompat.Builder().setMediaId("6578123").setTitle("Media title").setIconBitmap(
                    BitmapFactory.decodeResource(context!!.resources, R.drawable.add_to_playlist_icon)
                ).build()
            }
        })
        mediaSessionConnector.setPlayer(player, null)
    }

    override fun onDestroy() {
        mediaSession.release()
        mediaSessionConnector.setPlayer(null, null)
        playerNotificationManager?.setPlayer(null)
        player?.release()
        player = null
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        arrayOfURLs = intent?.getStringArrayListExtra("URLs") as ArrayList<String>
//        (intent?.getStringArrayListExtra("URLs"))?.let {
//            arrayOfURLs = it as ArrayList<String>
//            naatIndex = intent.getIntExtra("NAAT_INDEX", 0)
//            if (arrayOfURLs != null) {
//                player?.prepare(buildPlaylist(arrayOfURLs!!, naatIndex))
//                player?.seekTo(naatIndex, C.TIME_UNSET)
//                player?.setPlayWhenReady(true)
//            }
//        }
        listOfNaatModels = Constants.listOfSentNaats
        naatIndex = intent?.getIntExtra("NAAT_INDEX", 0)
        if (listOfNaatModels.isNotEmpty()) {
            arrayOfURLs?.clear()
            for (item in listOfNaatModels) {
                if (item.naatUrl != null) {

                    arrayOfURLs?.add(item.naatUrl!!)
                }
            }
            if (arrayOfURLs != null) {
                player?.prepare(buildPlaylist(arrayOfURLs!!, naatIndex!!))
                player?.seekTo(naatIndex!!, C.TIME_UNSET)
                player?.playWhenReady = true
            }
        }
        return START_NOT_STICKY
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
}
