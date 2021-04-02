package com.example.euterpe

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.euterpe.controller.AudioController
import com.example.euterpe.databinding.FragmentCurrentTrackBinding
import com.example.euterpe.model.TrackListViewModel

class CurrentTrackFragment : Fragment() {

    private val TAG = "Current Track Fragment"
    private val viewModel: TrackListViewModel by activityViewModels()
    lateinit var binding: FragmentCurrentTrackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_track, container, false);
        val view: View = binding.root

        binding?.apply{ trackListViewModel = viewModel }
        binding.lifecycleOwner = viewLifecycleOwner

        binding.playbackBtn.setOnClickListener{
            AudioController.playbackTrack(requireContext(), viewModel)
            var mSession = AudioController.getMSession()
            Log.i(TAG, "In Current Fragment, msession is: " + mSession.toString())
        }

        viewModel.isPaused.observe(viewLifecycleOwner, Observer {
            if(viewModel.mediaPlayer.value!!.isPlaying){
                binding.playbackBtn.setBackgroundResource(R.mipmap.ic_pause_btn_dark_foreground)
            } else {
                binding.playbackBtn.setBackgroundResource(R.mipmap.ic_play_btn_dark_foreground)
            }
        })

        binding.previousBtn.setOnClickListener{
            AudioController.playPreviousTrack(requireContext(), viewModel)
        }

        binding.nextBtn.setOnClickListener{
            AudioController.playNextTrack(requireContext(), viewModel)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = CurrentTrackFragment().apply {}
    }
}