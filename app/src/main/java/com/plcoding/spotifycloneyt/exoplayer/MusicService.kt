package com.plcoding.spotifycloneyt.exoplayer

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.plcoding.spotifycloneyt.exoplayer.callbacks.MusicPlaybackPrepare
import com.plcoding.spotifycloneyt.exoplayer.callbacks.MusicPlayerEventListener
import com.plcoding.spotifycloneyt.exoplayer.callbacks.MusicPlayerNotificationListener
import com.plcoding.spotifycloneyt.other.Constants.MEDIA_ROOT_ID
import com.plcoding.spotifycloneyt.other.Constants.SERVICE_TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {
    @Inject
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    @Inject
    lateinit var exoPlayer: SimpleExoPlayer

    @Inject
    lateinit var firebaseMusicSource: FirebaseMusicSource

    private lateinit var musicNotificationManager: MusicNotificationManager
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector
    var isForegroundService = false
    private var currentPlayingSong: MediaMetadataCompat? = null
    private var isPlayerInitialized = false
    private lateinit var musicPlayerEventListener: MusicPlayerEventListener

    companion object {
        var currentSongDuration = 0L
            private set
    }

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            firebaseMusicSource.fetchMediaData()
        }
        // TODO: getActivity(_, _, _, flags) -> if there's any wrong with tha app remember to change
        //  the flags value to 0.
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        mediaSessionCompat = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }
        sessionToken = mediaSessionCompat.sessionToken
        musicNotificationManager = MusicNotificationManager(
            this,
            mediaSessionCompat.sessionToken,
            MusicPlayerNotificationListener(this)
        ) {
            currentSongDuration = exoPlayer.duration
        }
        val musicPlaybackPrepare = MusicPlaybackPrepare(firebaseMusicSource) {
            currentPlayingSong = it
            preparePlayer(
                firebaseMusicSource.songs,
                it,
                true
            )

        }
        mediaSessionConnector = MediaSessionConnector(mediaSessionCompat).also {
            it.setPlaybackPreparer(musicPlaybackPrepare)
            it.setQueueNavigator(MusicQueueNavigator())
            it.setPlayer(exoPlayer)
        }
        musicPlayerEventListener = MusicPlayerEventListener(this)
        exoPlayer.addListener(musicPlayerEventListener)
        musicNotificationManager.showNotification(exoPlayer)
    }

    private fun preparePlayer(
        songs: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playNow: Boolean
    ) {
        val currentSongIndex = if (currentPlayingSong == null) 0 else songs.indexOf(itemToPlay)
        exoPlayer.prepare(firebaseMusicSource.asMediaSource(dataSourceFactory))
        exoPlayer.seekTo(currentSongIndex, 0L)
        exoPlayer.playWhenReady = playNow
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        exoPlayer.removeListener(musicPlayerEventListener)
        exoPlayer.release()
    }

    override fun onGetRoot(p0: String, p1: Int, p2: Bundle?): BrowserRoot =
        BrowserRoot(MEDIA_ROOT_ID, null)

    override fun onLoadChildren(
        p0: String, p1: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when (p0) {
            MEDIA_ROOT_ID -> {
                val resultSent = firebaseMusicSource.whenReady { isInitialized ->
                    if (isInitialized) {
                        p1.sendResult(firebaseMusicSource.asMediaItems())
                        if (!isPlayerInitialized && firebaseMusicSource.songs.isNotEmpty()) {
                            preparePlayer(
                                firebaseMusicSource.songs,
                                firebaseMusicSource.songs[0],
                                false
                            )
                            isPlayerInitialized = true
                        }
                    } else {
                        p1.sendResult(null)
                    }
                }
                if (!resultSent)
                    p1.detach()
            }
        }
    }

    private inner class MusicQueueNavigator : TimelineQueueNavigator(mediaSessionCompat) {
        override fun getMediaDescription(p0: Player, p1: Int): MediaDescriptionCompat =
            firebaseMusicSource.songs[p1].description
    }

}