package com.example.euterpe.adapter

import android.bluetooth.*
import android.content.Context
import android.util.Log
import java.util.*

class BluetoothController {


    companion object{

        var bluetoothAdapter: BluetoothAdapter? = null
        var bluetoothA2dp: BluetoothA2dp? = null
        var socket: BluetoothSocket? = null
        var euterpeUUID = UUID.fromString("00001108-0000-1000-8000-00805f9b34fb")
        // var euterpeUUID = UUID.randomUUID()

        private class ConnectThread(device: BluetoothDevice) : Thread(){
            private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
                device.createRfcommSocketToServiceRecord(euterpeUUID)
            }

            override fun run(){
                bluetoothAdapter?.cancelDiscovery()
                Log.i("Bluetooth", "Post cancel discovery")
                socket?.use{
                    Log.i("Bluetooth", "Attempting socket usage")
                    socket?.connect()
                    Log.i("Bluetooth", "Successfully connected")
                    manageConnectedSocket(socket!!)
                }
            }
        }

        fun manageConnectedSocket(socket: BluetoothSocket){
            Log.i("Bluetooth", "Managing connected socket")
        }

        fun createSocket(btSpeaker: BluetoothDevice) {
            Log.i("Bluetooth", "Trying to create socket")
            var uuids = btSpeaker.uuids
            Log.i("Bluetooth", uuids.size.toString() + " - " + uuids.toString())

            //for (uuid in uuids) {
                //try {
                    socket = btSpeaker.createRfcommSocketToServiceRecord(euterpeUUID)
                    bluetoothAdapter?.cancelDiscovery()

                    socket.use { socket ->
                        Log.i("Bluetooth", "Attempting socket usage")
                        socket?.connect()
                        Log.i("Bluetooth", "Successfully connected")
                        manageConnectedSocket(socket!!)
                    }
                //} catch (io: IOException){
                //    Log.i("Bluetooth", io.message)
                //}
            //}
        }

        private val profileListener = object : BluetoothProfile.ServiceListener {
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile){
                if(profile == BluetoothProfile.A2DP){
                    Log.i("Bluetooth", "On service connected")
                    bluetoothA2dp = proxy as BluetoothA2dp

                    var connectedDevices = bluetoothA2dp!!.connectedDevices

                    if(connectedDevices.size > 0){
                        var btSpeaker = connectedDevices.get(0)
                        Log.i("Bluetooth", "Connected devices - " + btSpeaker.name.toString())
                        var thread = ConnectThread(btSpeaker)
                        thread.run()
                        //createSocket(btSpeaker)
                    } else {
                        var statesToCheck = IntArray(BluetoothA2dp.STATE_DISCONNECTED)
                        var disconnectedDevices = bluetoothA2dp!!.getDevicesMatchingConnectionStates(statesToCheck)

                        if(disconnectedDevices.size > 0){
                            Log.i("Bluetooth", "Disconnected devices")
                            var btSpeaker = disconnectedDevices.get(0)
                            createSocket(btSpeaker)
                        }
                    }
                }
            }

            override fun onServiceDisconnected(profile: Int) {
                if(profile == BluetoothProfile.A2DP){
                    bluetoothA2dp = null
                }
            }
        }

        fun getBluetoothAdapter(){
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                Log.i("Bluetooth","Can't get bluetooth adapter")
            }
        }

        fun getProxy(context: Context){
            bluetoothAdapter?.getProfileProxy(context, profileListener, BluetoothProfile.A2DP)
        }

        fun closeA2dpProxy(bluetoothAdapter: BluetoothAdapter, bluetoothA2dp: BluetoothA2dp?){
            if(bluetoothA2dp != null){
                bluetoothAdapter?.closeProfileProxy(BluetoothProfile.A2DP, bluetoothA2dp)
            }
        }
    }
}