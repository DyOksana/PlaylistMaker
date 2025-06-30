package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

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
        trackTimeView.text = element.trackTime

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