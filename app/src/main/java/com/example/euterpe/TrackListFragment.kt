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
import com.example.euterpe.controller.AudioController
import com.example.euterpe.databinding.FragmentTrackListBinding
import com.example.euterpe.model.TrackListViewModel

class TrackListFragment : Fragment() {

    private val viewModel: TrackListViewModel by activityViewModels()
    lateinit var binding: FragmentTrackListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track_list, container, false);
        val view: View = binding.root

        binding?.apply{
            trackListViewModel = viewModel
        }

        val adapter =
            TrackListAdapter(TrackListListener { uri ->
                viewModel.setCurrentPlaylist("Tracks")
                AudioController.playOnClick(requireContext(), viewModel, uri, false)
            })

        binding.trackListRcv.adapter = adapter
        viewModel.viewTrackList.observe(viewLifecycleOwner, Observer{
            it?.let{
                Log.i("Track List Fragment", "Tracklist observer triggered")
                adapter.submitList(it.trackList)
                adapter.notifyDataSetChanged()
            }
        })

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = TrackListFragment().apply {}
    }
}