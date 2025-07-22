package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
    private lateinit var inputEditText: EditText
    private var lastQuery: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        val buttonArrowBack = findViewById<MaterialToolbar>(R.id.search)

        buttonArrowBack.setNavigationOnClickListener {
            finish()
        }

        inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        trackList = findViewById<RecyclerView>(R.id.trackList)
        nothingSearch = findViewById<LinearLayout>(R.id.nothingSearch)
        noInternet = findViewById<LinearLayout>(R.id.noInternet)
        buttonUpdate = findViewById<Button>(R.id.buttonUpdate)

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

        clearButton.setOnClickListener {
            tracks.clear()
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
    private val iTunesBaseUrl = "https://itunes.apple.com/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesAPI::class.java)


    private val tracks = ArrayList<Track>()

    private val adapter = TrackAdapter()

    private var textSearch: String = AMOUNT_DEF
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

}