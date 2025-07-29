package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import java.util.Locale


class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var track: ImageView
    private lateinit var textTrackName: TextView
    private lateinit var textArtistName: TextView
    private lateinit var backButton: ImageButton
    private lateinit var valueTrackTime: TextView
    private lateinit var valueCollectionName: TextView
    private lateinit var valueReleaseDate: TextView
    private lateinit var valuePrimaryGenreName: TextView
    private lateinit var valueCountry: TextView
    private lateinit var collectionGroup: Group
    private lateinit var releaseDateGroup: Group
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)
        val artworkUrl100 = intent.getStringExtra(Constants.KEY_ARTWORK_URL)
        val trackName = intent.getStringExtra(Constants.KEY_TRACK_NAME)
        val artistName = intent.getStringExtra(Constants.KEY_ARTIST_NAME)
        val trackTime = intent.getStringExtra(Constants.KEY_TRACK_TIME)
        val collectionName = intent.getStringExtra(Constants.KEY_COLLECTION_NAME)
        val releaseDate = intent.getStringExtra(Constants.KEY_RELEASE_DATE)
        val primaryGenreName = intent.getStringExtra(Constants.KEY_PRIMARY_GENRE)
        val country = intent.getStringExtra(Constants.KEY_COUNTRY)
        track = findViewById(R.id.trackImage)
        textTrackName = findViewById(R.id.textTrackName)
        textArtistName = findViewById(R.id.textArtistName)
        backButton = findViewById(R.id.back_button)
        valueTrackTime = findViewById(R.id.valueTrackTime)
        valueCollectionName = findViewById(R.id.valueCollectionName)
        valueReleaseDate = findViewById(R.id.valueReleaseDate)
        valuePrimaryGenreName = findViewById(R.id.valuePrimaryGenreName)
        valueCountry = findViewById(R.id.valueCountry)
        collectionGroup = findViewById(R.id.collectionGroup)
        releaseDateGroup = findViewById(R.id.releaseDateGroup)

        valueTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime?.toLongOrNull() ?: 0)

        if (collectionName.isNullOrEmpty()) {
            collectionGroup.visibility = View.GONE
        } else {
            collectionGroup.visibility = View.VISIBLE
            valueCollectionName.text = collectionName
        }

        if (releaseDate.isNullOrEmpty()) {
            releaseDateGroup.visibility = View.GONE
        } else {
            releaseDateGroup.visibility = View.VISIBLE
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            valueReleaseDate.text = SimpleDateFormat("yyyy", Locale.getDefault()).format(dateFormat.parse(releaseDate))
        }

        valuePrimaryGenreName.text = primaryGenreName
        valueCountry.text = country

        textTrackName.text = trackName
        textArtistName.text = artistName
        Glide.with(track.context)
            .load(artworkUrl100)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .into(track)

        backButton.setOnClickListener {
            finish()
        }
    }
}