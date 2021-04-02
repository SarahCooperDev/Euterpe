package com.example.euterpe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.euterpe.adapter.TrackListAdapter
import com.example.euterpe.adapter.TrackListListener
import com.example.euterpe.controller.AudioController
import com.example.euterpe.databinding.FragmentFavouritesBinding
import com.example.euterpe.model.TrackListViewModel

class FavouritesFragment : Fragment() {

    private val TAG = "Favourites Fragment"
    private val viewModel: TrackListViewModel by activityViewModels()
    lateinit var binding: FragmentFavouritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourites, container, false);
        val view: View = binding.root

        binding?.apply{
            trackListViewModel = viewModel
        }

        val adapter = TrackListAdapter(TrackListListener { uri ->
                viewModel.setCurrentPlaylist("Favourites")
                AudioController.setPlayingTracklistToFav(viewModel)
                AudioController.playOnClick(requireContext(), viewModel, uri, true)
            })

        binding.favouriteRcv.adapter = adapter

        viewModel.playlists.observe(viewLifecycleOwner, Observer{
            it?.let{
                var favouritePlaylist = viewModel.playlists.value!!.find{it.name == "Favourites"}
                var favouriteList = viewModel.trackList.value!!.trackList.filter {favouritePlaylist!!.members!!.contains(it.id)}

                adapter.submitList(favouriteList)
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.viewTrackList.observe(viewLifecycleOwner, Observer {
            it?.let{
                var favouritePlaylist = viewModel.playlists.value!!.find{it.name == "Favourites"}
                var favouriteList = viewModel.viewTrackList.value!!.trackList.filter {favouritePlaylist!!.members!!.contains(it.id)}

                adapter.submitList(favouriteList)
                adapter.notifyDataSetChanged()
            }
        })

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = FavouritesFragment().apply {}
    }
}