package com.example.euterpe

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val notificationChannel: String = "EuterpeChannel"
    val channelDescription = "Music player"
    val importance = NotificationManager.IMPORTANCE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWritePermission()
        requestReadPermission()
        requestBluetoothPermissions()
        requestLocationPermissions()
        requestMediaPermissions()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.app_toolbar))

        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel = NotificationChannel(notificationChannel,"Euterpe", importance)
            channel.apply { description = channelDescription }

            var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    private fun requestMediaPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MEDIA_CONTENT_CONTROL)
            != PackageManager.PERMISSION_GRANTED) {

            Log.i("Permission", "Bluetooth Permissions haven't been granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.MEDIA_CONTENT_CONTROL), 2)
            return;
        } else {
            Log.i("Permission", "Bluetooth Permissions already granted")
        }
    }

    private fun requestBluetoothPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
            != PackageManager.PERMISSION_GRANTED) {

            Log.i("Permission", "Bluetooth Permissions haven't been granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN), 2)
            return;
        } else {
            Log.i("Permission", "Bluetooth Permissions already granted")
        }
    }

    private fun requestLocationPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            Log.i("Permission", "Location Permissions havn't been granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION), 2)
            return;
        } else {
            Log.i("Permission", "Location Permissions already granted")
        }
    }

    private fun requestWritePermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            Log.i("Permission", "Write Permission hasn't been granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
            return;
        } else {
            Log.i("Permission", "Write Permission already granted")
        }
    }

    private fun requestReadPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            Log.i("Permission", "Read Permission hasn't been granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            return
        } else {
            Log.i("Permission", "Read Permission already granted")
        }
    }
}