package com.example.euterpe

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.app_toolbar))
        requestPermission()
    }

    //override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    //    getMenuInflater().inflate(R.menu.app_menu, menu);
    //    return true
    //}

    private fun requestPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            Log.i("Permission", "Permission hasn't been granted")

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

            return;
        } else {
            Log.i("Permission", "Permission already granted")
        }

    }
}