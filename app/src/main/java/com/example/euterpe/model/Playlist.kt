package com.example.euterpe.model

class Playlist(id: Long, name: String, members: MutableList<Long>){
    var id = id
    var name = name
    var members = members

    fun addMember(memberId: Long){
        members.add(memberId)
    }
}