package com.example.euterpe

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.euterpe.databinding.TrackViewBinding
import com.example.euterpe.model.Track

class TrackListAdapter(val clickListener: TrackListListener): ListAdapter<Track, TrackListAdapter.ViewHolder>(TrackDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: TrackViewBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Track, clickListener: TrackListListener){
            binding.track = item
            binding.clickListener = clickListener
            binding.trackTitleTv.text = item.title
            binding.trackArtistTv.text = item.artist
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TrackViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class TrackDiffCallback : DiffUtil.ItemCallback<Track>(){
    override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem.uri == newItem.uri
    }


    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
        return oldItem == newItem
    }
}

class TrackListListener(val clickListener: (uri: Uri) -> Unit){
    fun onClick(track: Track) = clickListener(track.uri)
}
