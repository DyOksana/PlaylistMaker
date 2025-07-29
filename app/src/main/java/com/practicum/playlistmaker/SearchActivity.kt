package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private lateinit var buttonUpdate: Button
    private lateinit var noInternet: LinearLayout
    private lateinit var nothingSearch: LinearLayout
    private lateinit var trackList: RecyclerView
    private lateinit var trackListHistory: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var youSearch: LinearLayout
    private lateinit var buttonClearHistory: Button
    private lateinit var clearButton: ImageView
    private lateinit var buttonArrowBack: MaterialToolbar
    private var lastQuery: String = ""
    private val iTunesBaseUrl = "https://itunes.apple.com/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesAPI::class.java)

    private lateinit var prefs: SharedPreferences
    private lateinit var searchHistory: SearchHistory
    private lateinit var adapter: TrackAdapter
    private lateinit var adapterHistory: TrackAdapter
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
    private val tracks = ArrayList<Track>()

    private var textSearch: String = AMOUNT_DEF
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(prefs)
        adapter = TrackAdapter(searchHistory) { track ->
            val intent = Intent(this, AudioPlayerActivity::class.java).apply {
                putExtra(Constants.KEY_ARTWORK_URL, track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
                putExtra(Constants.KEY_TRACK_NAME, track.trackName)
                putExtra(Constants.KEY_ARTIST_NAME, track.artistName)
                putExtra(Constants.KEY_TRACK_TIME, track.trackTime)
                putExtra(Constants.KEY_COLLECTION_NAME, track.collectionName)
                putExtra(Constants.KEY_RELEASE_DATE, track.releaseDate)
                putExtra(Constants.KEY_PRIMARY_GENRE, track.primaryGenreName)
                putExtra(Constants.KEY_COUNTRY, track.country)
            }
            startActivity(intent)
        }
        adapterHistory = TrackAdapter(searchHistory){ track ->
            val intent = Intent(this, AudioPlayerActivity::class.java).apply {
                putExtra(Constants.KEY_ARTWORK_URL, track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
                putExtra(Constants.KEY_TRACK_NAME, track.trackName)
                putExtra(Constants.KEY_ARTIST_NAME, track.artistName)
                putExtra(Constants.KEY_TRACK_TIME, track.trackTime)
                putExtra(Constants.KEY_COLLECTION_NAME, track.collectionName)
                putExtra(Constants.KEY_RELEASE_DATE, track.releaseDate)
                putExtra(Constants.KEY_PRIMARY_GENRE, track.primaryGenreName)
                putExtra(Constants.KEY_COUNTRY, track.country)
            }
            startActivity(intent)
        }

        buttonArrowBack = findViewById(R.id.search)
        buttonArrowBack.setNavigationOnClickListener {
            finish()
        }

        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)
        trackList = findViewById(R.id.trackList)
        trackListHistory = findViewById(R.id.trackListHistory)
        nothingSearch = findViewById(R.id.nothingSearch)
        noInternet = findViewById(R.id.noInternet)
        buttonUpdate = findViewById(R.id.buttonUpdate)

        youSearch = findViewById(R.id.youSearch)
        buttonClearHistory = findViewById(R.id.buttonClearHistory)

        var historyTracks = searchHistory.readPreferences()
        adapterHistory.tracks = historyTracks.toMutableList()
        trackListHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackListHistory.adapter = adapterHistory

        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == HISTORY_KEY) {
                historyTracks = searchHistory.readPreferences()
                adapterHistory.tracks = historyTracks.toMutableList()
                adapterHistory.notifyDataSetChanged()
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            youSearch.visibility = if (hasFocus && inputEditText.text.isEmpty() && historyTracks.isNotEmpty()) View.VISIBLE else View.GONE
        }

        inputEditText.doOnTextChanged{ text, _, _, _ ->
            youSearch.isVisible = inputEditText.hasFocus() && text?.isEmpty() == true && historyTracks.isNotEmpty()
        }

        adapter.tracks = tracks
        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackList.adapter = adapter

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks(inputEditText.text.toString())
                true
            }
            false
        }

        buttonClearHistory.setOnClickListener{
            searchHistory.removeHistory()
            adapterHistory.notifyDataSetChanged()
            youSearch.isVisible = false
        }

        clearButton.setOnClickListener {
            tracks.clear()
            trackList.visibility = View.GONE
            adapter.notifyDataSetChanged()
            inputEditText.setText("")
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        buttonUpdate.setOnClickListener {
            searchTracks(lastQuery)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                textSearch = inputEditText.text.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
        if (savedInstanceState != null) {
            textSearch = savedInstanceState.getString(PRODUCT_AMOUNT, AMOUNT_DEF)
            inputEditText.setText(textSearch)
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun searchTracks(query: String) {
        if (query.isNotEmpty()) {
            iTunesService.search(text = query).enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    if (response.isSuccessful) {
                        tracks.clear()
                        val results = response.body()?.results
                        if (results != null) {
                            trackList.visibility = View.VISIBLE
                            nothingSearch.visibility = View.GONE
                            noInternet.visibility = View.GONE
                            tracks.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                        }
                        if (tracks.isEmpty()) {
                            trackList.visibility = View.GONE
                            nothingSearch.visibility = View.VISIBLE
                            noInternet.visibility = View.GONE
                        }
                    } else {
                        trackList.visibility = View.GONE
                        noInternet.visibility = View.VISIBLE
                        nothingSearch.visibility = View.GONE
                        lastQuery = query
                    }
                }
                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    trackList.visibility = View.GONE
                    noInternet.visibility = View.VISIBLE
                    nothingSearch.visibility = View.GONE
                    lastQuery = query
                }
            })
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PRODUCT_AMOUNT, textSearch)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        textSearch = savedInstanceState.getString(PRODUCT_AMOUNT, AMOUNT_DEF)
    }

    companion object {
        private const val PRODUCT_AMOUNT = "PRODUCT_AMOUNT"
        private const val AMOUNT_DEF = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }

}