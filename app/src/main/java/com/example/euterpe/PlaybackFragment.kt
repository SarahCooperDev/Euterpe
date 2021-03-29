package com.example.euterpe

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.euterpe.controller.AudioController
import com.example.euterpe.databinding.FragmentPlaybackBinding
import com.example.euterpe.model.TrackListViewModel

class PlaybackFragment : Fragment() {

    private val viewModel: TrackListViewModel by activityViewModels()
    lateinit var binding: FragmentPlaybackBinding
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_playback, container, false)
        val view: View = binding.root

        binding?.apply{
            trackListViewModel = viewModel
        }

        binding.lifecycleOwner = viewLifecycleOwner

        binding.playpausePlaybackBtn.setOnClickListener{
            AudioController.playbackTrack(requireContext(), viewModel)
        }

        binding.isFavouritedBtn.setOnClickListener {
            AudioController.toggleFavouriteTrack(requireContext(), viewModel)
        }

        viewModel.currentTrack.observe(viewLifecycleOwner, Observer {
            if(viewModel.currentTrack.value!!.isFavourited){
                binding.isFavouritedBtn.setImageResource(R.mipmap.ic_favourited_filled_btn_dark_foreground)
            } else {
                binding.isFavouritedBtn.setImageResource(R.mipmap.ic_favourited_unfilled_btn_dark_foreground)
            }
        })


        if(viewModel.currentTrack.value!!.isFavourited){
            binding.isFavouritedBtn.setImageResource(R.mipmap.ic_favourited_filled_btn_dark_foreground)
        } else {
            binding.isFavouritedBtn.setImageResource(R.mipmap.ic_favourited_unfilled_btn_dark_foreground)
        }

        viewModel.isPaused.observe(viewLifecycleOwner, Observer {
            if(viewModel.mediaPlayer.value!!.isPlaying){
                binding.playpausePlaybackBtn.setImageResource(R.mipmap.ic_pause_btn_dark_foreground)
            } else {
                binding.playpausePlaybackBtn.setImageResource(R.mipmap.ic_play_btn_dark_foreground)
            }
        })

        binding.previousPlaybackBtn.setOnClickListener{
            AudioController.playPreviousTrack(requireContext(), viewModel)
        }

        binding.nextPlaybackBtn.setOnClickListener{
            AudioController.playNextTrack(requireContext(), viewModel)
        }

        binding.durationSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                AudioController.adjustCurrentPosition(seekBar!!, viewModel)
            }
        })

        handler = Handler()

        runnable = Runnable {
            handler.removeCallbacksAndMessages(null)
            binding.durationSeekbar.progress = viewModel.mediaPlayer.value!!.currentPosition
            handler.postDelayed(runnable, 1000)
        }

        handler.postDelayed(runnable, 1000)

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    companion object {
        @JvmStatic
        fun newInstance() = PlaybackFragment().apply { }
    }
}