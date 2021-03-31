package com.example.euterpe.adapter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.euterpe.controller.NotificationService

class MediaReceiver: BroadcastReceiver(){
    private val TAG = "Media Receiver"
    val ACTION_PLAY = "action_play"
    val ACTION_PAUSE = "action_pause"
    val ACTION_NEXT = "action_next"
    val ACTION_PREVIOUS = "action_previous"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, intent!!.action.toString())
        val newIntent = Intent(context, MediaReceiver::class.java)

        when {
            ACTION_PAUSE == intent!!.action -> {
                newIntent.action = NotificationService.ACTION_PAUSE
            } ACTION_NEXT == intent!!.action -> {
                newIntent.action = NotificationService.ACTION_NEXT
            } ACTION_PREVIOUS == intent!!.action -> {
                newIntent.action = NotificationService.ACTION_PREVIOUS
            } ACTION_PLAY == intent!!.action -> {
                newIntent.action = NotificationService.ACTION_PLAY
            } else -> {
                Log.i(TAG, "Couldn't categorise intent")
            }
        }

        LocalBroadcastManager.getInstance(context!!).sendBroadcast(newIntent)
    }
}