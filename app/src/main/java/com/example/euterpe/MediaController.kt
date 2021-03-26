package com.example.euterpe

import com.example.euterpe.model.TrackListViewModel

class MediaController{


    var viewModel: TrackListViewModel? = null

    companion object{
        val instance = MediaController()
        var objectViewModel: TrackListViewModel? = null

        fun setObjectVM(viewModel: TrackListViewModel){
            objectViewModel = viewModel
        }
    }
}