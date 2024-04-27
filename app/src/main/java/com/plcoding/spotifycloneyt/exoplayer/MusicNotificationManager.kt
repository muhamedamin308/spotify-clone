package com.plcoding.spotifycloneyt.exoplayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat.Token
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener
import com.plcoding.spotifycloneyt.R
import com.plcoding.spotifycloneyt.other.Constants.NOTIFICATION_CHANNEL_ID
import com.plcoding.spotifycloneyt.other.Constants.NOTIFICATION_ID

class MusicNotificationManager(
    private val context: Context,
    sessionToken: Token,
    notificationListener: NotificationListener,
    private val newSongCallback: () -> Unit
) {
    private val notificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)
        notificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            NOTIFICATION_CHANNEL_ID,
            R.string.notification_channel_name,
            R.string.notification_channel_description,
            NOTIFICATION_ID,
            DescriptionAdapter(mediaController),
            notificationListener
        ).apply {
            setSmallIcon(R.drawable.ic_music)
            setMediaSessionToken(sessionToken)
        }
    }

    fun showNotification(player: Player) {
        notificationManager.setPlayer(player)
    }

    inner class DescriptionAdapter(
        private val mediaController: MediaControllerCompat
    ) : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(p0: Player): CharSequence =
            mediaController.metadata.description.title.toString()

        override fun createCurrentContentIntent(p0: Player): PendingIntent? =
            mediaController.sessionActivity

        override fun getCurrentContentText(p0: Player): CharSequence =
            mediaController.metadata.description.subtitle.toString()

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            Glide.with(context)
                .asBitmap()
                .load(mediaController.metadata.description.iconUri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        p0: Bitmap,
                        p1: Transition<in Bitmap>?
                    ) {
                        callback.onBitmap(p0)
                    }

                    override fun onLoadCleared(p0: Drawable?) = Unit

                })
            return null
        }

    }
}