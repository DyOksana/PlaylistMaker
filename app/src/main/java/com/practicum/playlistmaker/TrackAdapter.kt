package com.practicum.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter (private val searchHistory: SearchHistory) : RecyclerView.Adapter<TrackViewHolder> () { //

    var tracks = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder = TrackViewHolder(parent)

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { searchHistory.addTrack(tracks[position]) }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}
