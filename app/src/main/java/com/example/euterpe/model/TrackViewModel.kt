package com.example.euterpe.model

import android.util.Log
import androidx.lifecycle.ViewModel

class TrackViewModel : ViewModel(){

    lateinit var trackList: MutableList<TempTrack>

    private fun resetList() {
        var track1 = TempTrack("When the End Comes", "Anime")
        var track2 = TempTrack("Zombies", "Bad Wolves")
        var track3 = TempTrack("Storm the Sorrow", "Epica")

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