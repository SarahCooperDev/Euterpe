package com.example.euterpe.model

import android.net.Uri

class Track(uri: Uri, title: String, artist: String, album: String, duration: Int){
    var uri = uri
    var title = title
    var artist = artist
    var duration = duration
    var album = album

    var detailSummaryString = artist + " - " + album
}