package com.example.euterpe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.euterpe.model.TempTrack

class TrackListAdapter: RecyclerView.Adapter<TextItemViewHolder>() {

    var data =  listOf<TempTrack>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        val item = data[position]
        holder.title.text = item.title.toString()
        holder.artist.text = item.artist.toString()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.track_view, parent, false)
        return TextItemViewHolder(view)
    }
}

class TextItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    val title: TextView = itemView.findViewById(R.id.track_title_tv)
    val artist: TextView = itemView.findViewById(R.id.track_artist_tv)
}
