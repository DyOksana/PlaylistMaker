package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

const val HISTORY_KEY = "key_for_history"
class SearchHistory(private val sharedPreferences: SharedPreferences) {

    fun addTrack (track: Track){
        val trackArray = readPreferences().toMutableList()
        trackArray.removeAll{it.trackId == track.trackId}
        trackArray.add(0, track)
        if (trackArray.size > 10) {
            trackArray.removeLast()
        }
        val jsonAdd = Gson().toJson(trackArray)
        sharedPreferences.edit()
            .putString(HISTORY_KEY, jsonAdd)
            .apply()
    }

    fun readPreferences(): Array<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return emptyArray<Track>()
        return Gson().fromJson(json, Array<Track>::class.java)?: return emptyArray<Track>()
    }

    fun removeHistory(){
        sharedPreferences.edit()
            .remove(HISTORY_KEY)
            .apply()
    }

}