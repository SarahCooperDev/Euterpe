package com.example.euterpe.adapter

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.euterpe.MainActivity
import com.example.euterpe.R

class MediaService{

    companion object{
        val ACTION_PLAY = "action_play"
        val ACTION_PAUSE = "action_pause"
        val ACTION_REWIND = "action_rewind"
        val ACTION_FAST_FORWARD = "action_fast_foward"
        val ACTION_NEXT = "action_next"
        val ACTION_PREVIOUS = "action_previous"
        val ACTION_STOP = "action_stop"

        private val mMediaPlayer: MediaPlayer? = null
        private val mManager: MediaSessionManager? = null
        private val mSession: MediaSession? = null

        private val NOTIFICATION_ID = 0
        private val REQUEST_CODE = 0
        private val FLAGS = 0
        private val notificationChannel: String = "EuterpeChannel"


        @SuppressLint("RestrictedApi")
        fun NotificationManager.sendNotification(messageTitle: String, messageBody: String, context: Context, mediaSession: MediaSessionCompat, audioReceiver: BroadcastReceiver, builder: NotificationCompat.Builder?, isPaused: Boolean){
            Log.i("Media Service", "Send notification")
            val contentIntent = Intent(context, MainActivity::class.java)
            val contentPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            builder!!.setContentTitle(messageTitle)
            builder!!.setContentText(messageBody)
            builder!!.setContentIntent(contentPendingIntent)

            builder!!.mActions.clear()
            builder!!.addAction(R.mipmap.ic_previous_btn_dark_foreground, "Previous", getActionIntent(context, ACTION_PREVIOUS))

            if(isPaused){
                builder!!.addAction(R.mipmap.ic_play_btn_dark_foreground, "Play", getActionIntent(context, ACTION_PLAY))
            } else {
                builder!!.addAction(R.mipmap.ic_pause_btn_dark_foreground, "Pause", getActionIntent(context, ACTION_PAUSE))
            }

            builder!!.addAction(R.mipmap.ic_next_btn_dark_foreground, "Next", getActionIntent(context, ACTION_NEXT))

            Log.i("Media Service", "Notify")
            notify(NOTIFICATION_ID, builder!!.build())
        }

        fun getActionIntent(context: Context, action: String): PendingIntent{
            val intent = Intent(context, MediaReceiver::class.java)
            intent.action = action
            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        fun generateBaseBuilder(context: Context, mediaSession: MediaSessionCompat): NotificationCompat.Builder{
            val eggImage = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
            val bigPicStyle = NotificationCompat.BigPictureStyle().bigPicture(eggImage).bigLargeIcon(null)

            var builder = NotificationCompat.Builder(context, notificationChannel)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(bigPicStyle)
                .setLargeIcon(eggImage)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setStyle(
                    androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0,1,2)
                        .setMediaSession(mediaSession.sessionToken))

            return builder
        }

        fun NotificationManager.cancelNotifications(){
            cancelAll()
        }
    }

}