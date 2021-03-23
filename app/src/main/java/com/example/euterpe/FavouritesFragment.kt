package com.example.euterpe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.euterpe.adapter.TrackListAdapter
import com.example.euterpe.adapter.TrackListListener
import com.example.euterpe.databinding.FragmentFavouritesBinding
import com.example.euterpe.model.TrackListViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [FavouritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouritesFragment : Fragment() {

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
                viewModel.setPlayingTracklistToFav()
                viewModel.playPlaylistOnClick(requireContext(), uri)
            })

        binding.favouriteRcv.adapter = adapter

        viewModel.playlists.observe(viewLifecycleOwner, Observer{
            it?.let{
                Log.i("Favourites Fragment", "Retrieving favourites playlist")

                var favouritePlaylist = viewModel.playlists.value!!.find{it.name == "Favourites"}
                Log.i("Favourites Fragment", favouritePlaylist.toString())

                var favouriteList = viewModel.trackList.value!!.trackList.filter {favouritePlaylist!!.members.contains(it.id)}
                Log.i("Favourites Fragment", favouriteList.toString())

                adapter.submitList(favouriteList)
            }
        })

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouritesFragment().apply {

            }
    }
}