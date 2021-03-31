package com.example.euterpe

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private val TAG = "Main Activity"
    private val PERMISSION_TAG = "Permissions"

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWritePermission()
        requestReadPermission()
        requestBluetoothPermissions()
        requestBluetoothAdminPermissions()
        requestLocationPermissions()
        requestBackLocationPermissions()
        requestMediaPermissions()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.app_toolbar))
    }

    private fun requestMediaPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.MEDIA_CONTENT_CONTROL)
            != PackageManager.PERMISSION_GRANTED) {

            Log.i(PERMISSION_TAG, "Bluetooth Permissions haven't been granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.MEDIA_CONTENT_CONTROL), 2)
            return
        } else {
            Log.i(PERMISSION_TAG, "Bluetooth Permissions already granted")
        }
    }

    private fun requestBluetoothPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
            != PackageManager.PERMISSION_GRANTED) {

            Log.i(PERMISSION_TAG, "Bluetooth Permissions haven't been granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN), 2)
            return
        } else {
            Log.i(PERMISSION_TAG, "Bluetooth Permissions already granted")
        }
    }

    private fun requestBluetoothAdminPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
            != PackageManager.PERMISSION_GRANTED) {

            Log.i(PERMISSION_TAG, "Bluetooth Admin Permissions haven't been granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_ADMIN), 2)
            return
        } else {
            Log.i(PERMISSION_TAG, "Bluetooth Admin Permissions already granted")
        }
    }

    private fun requestLocationPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            Log.i(PERMISSION_TAG, "Location Permissions havn't been granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION), 2)
            return
        } else {
            Log.i(PERMISSION_TAG, "Location Permissions already granted")
        }
    }

    private fun requestBackLocationPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            Log.i(PERMISSION_TAG, "Background Location Permissions havn't been granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 2)
            return
        } else {
            Log.i(PERMISSION_TAG, "Background Location Permissions already granted")
        }
    }

    private fun requestWritePermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            Log.i(PERMISSION_TAG, "Write Permission hasn't been granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
            return
        } else {
            Log.i(PERMISSION_TAG, "Write Permission already granted")
        }
    }

    private fun requestReadPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            Log.i(PERMISSION_TAG, "Read Permission hasn't been granted")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            return
        } else {
            Log.i(PERMISSION_TAG, "Read Permission already granted")
        }
    }
}