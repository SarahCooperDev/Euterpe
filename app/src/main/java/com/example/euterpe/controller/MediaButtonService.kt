package com.example.euterpe.controller

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MediaButtonService: Service(){
    override fun onBind(intent: Intent?): IBinder? {
        Log.i("MediaButton", "On bind")
        return null
    }

}