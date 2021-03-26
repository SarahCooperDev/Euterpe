package com.example.euterpe.adapter

import android.bluetooth.BluetoothDevice
import android.util.Log

class BluetoothService{

    companion object{
        fun getBondedBluetooth(): Set<BluetoothDevice>?{
            val pairedDevices: Set<BluetoothDevice>? = BluetoothController.bluetoothAdapter?.bondedDevices
            pairedDevices?.forEach { device ->
                val deviceName = device.name
                val deviceHardwareAddress = device.address

                Log.i("Bluetooth", "Found a bluetooth device")
                Log.i("Bluetooth", deviceName.toString() + " - " + deviceHardwareAddress.toString())
            }

            return pairedDevices
        }
    }
}