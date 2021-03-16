package com.example.euterpe.model

class TrackList {
    var trackList = listOf<Track>()

    fun addTrack(track: Track){
        trackList += track
    }
}