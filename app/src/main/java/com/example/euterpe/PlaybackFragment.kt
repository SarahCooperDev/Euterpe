package com.example.euterpe

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.euterpe.databinding.FragmentPlaybackBinding
import com.example.euterpe.model.TrackListViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [PlaybackFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlaybackFragment : Fragment() {

    private val viewModel: TrackListViewModel by activityViewModels()
    lateinit var binding: FragmentPlaybackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playback, container, false);
        val view: View = binding.root

        binding?.apply{
            trackListViewModel = viewModel
        }

        binding.lifecycleOwner = viewLifecycleOwner

        binding.playpausePlaybackBtn.setOnClickListener{
            viewModel.playbackTrack(requireContext())

            if(viewModel.mediaPlayer.value!!.isPlaying){
                binding.playpausePlaybackBtn.text = "Pause"
            } else {
                binding.playpausePlaybackBtn.text = "Play"
            }
        }

        binding.nextPlaybackBtn.setOnClickListener{
            viewModel.playNextTrack(requireContext())
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlaybackFragment.
         */
        @JvmStatic
        fun newInstance() = PlaybackFragment().apply { }
    }
}