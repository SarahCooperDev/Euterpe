package com.example.euterpe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TrackViewModel : ViewModel(){

    lateinit var trackList: MutableList<Track>

    private fun resetList() {
        var track1 = Track("When the End Comes", "Anime")
        var track2 = Track("Zombies", "Bad Wolves")
        var track3 = Track("Storm the Sorrow", "Epica")

        trackList = mutableListOf(
            track1, track2, track3)
        trackList.shuffle()
    }

    init {
        Log.i("TrackViewModel", "TrackViewModel created!")
        resetList()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("TrackViewModel", "TrackViewModel destroyed!")
    }
}