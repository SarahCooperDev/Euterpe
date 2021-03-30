package com.example.euterpe.controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import android.view.KeyEvent


class MediaButtonIntentReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        Log.i("MediaButtonIntent", "In Media Button")
        val intentAction = intent.action
        if (Intent.ACTION_MEDIA_BUTTON != intentAction) {
            return
        }
        val event: KeyEvent =
            intent.getParcelableExtra<Parcelable>(Intent.EXTRA_KEY_EVENT) as KeyEvent
                ?: return
        val action: Int = event.getAction()
        if (action == KeyEvent.ACTION_DOWN) {
            // do something

        }
        abortBroadcast()
    }
}