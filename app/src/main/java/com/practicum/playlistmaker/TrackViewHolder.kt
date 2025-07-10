package com.practicum.playlistmaker

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Date
import java.util.Locale


class TrackViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
        .inflate(R.layout.track_view, parent, false)) {

    private val trackNameView: TextView = itemView.findViewById(R.id.textTrackName)
    private val artistNameView: TextView = itemView.findViewById(R.id.textArtistName)
    private val imageTrackURLView: ImageView = itemView.findViewById(R.id.imageTrack)
    private val trackTimeView: TextView = itemView.findViewById(R.id.textTrackTime)

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    fun bind(element: Track) {

        trackNameView.text = element.trackName
        artistNameView.text = element.artistName
        trackTimeView.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(element.trackTime.toLong())

        Glide.with(imageTrackURLView.context)
            .load(element.artworkUrl100)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f, imageTrackURLView.context)))
            .into(imageTrackURLView)

    }

}