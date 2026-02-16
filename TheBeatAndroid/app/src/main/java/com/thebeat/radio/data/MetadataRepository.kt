package com.thebeat.radio.data

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

// Data Models
data class NowPlayingResponse(
    @SerializedName("now_playing") val nowPlaying: NowPlayingDetails
)

data class NowPlayingDetails(
    @SerializedName("song") val song: Song
)

data class Song(
    @SerializedName("title") val title: String,
    @SerializedName("artist") val artist: String,
    @SerializedName("art") val art: String
)

data class ShowItem(
    val day: String,
    val time: String,
    val title: String
)

class MetadataRepository {

    private val client = OkHttpClient()
    private val gson = Gson()

    // API URLs
    private val NOW_PLAYING_URL = "https://a9.asurahosting.com/api/nowplaying/the_beat"
    private val SCHEDULE_URL = "https://pub-1fcbf37d73fa4fcdb972c8b1d59a9aec.r2.dev/Android%20Apps/The%20Beat/Show%20EPG/schedule.txt"

    fun getNowPlaying(callback: (Song?) -> Unit) {
        val request = Request.Builder().url(NOW_PLAYING_URL).build()
        
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback(null)
                        return
                    }
                    try {
                        val json = it.body?.string()
                        val data = gson.fromJson(json, NowPlayingResponse::class.java)
                        callback(data.nowPlaying.song)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callback(null)
                    }
                }
            }
        })
    }

    fun getSchedule(callback: (List<ShowItem>) -> Unit) {
        val request = Request.Builder().url(SCHEDULE_URL).build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback(emptyList())
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!it.isSuccessful) {
                        callback(emptyList())
                        return
                    }
                    val text = it.body?.string() ?: ""
                    val parsed = parseSchedule(text)
                    callback(parsed)
                }
            }
        })
    }

    private fun parseSchedule(text: String): List<ShowItem> {
        // Expected format per line: "Day - Time - Show Title" or similar logic
        // Based on user provided URL, we need to adapt to the format. 
        // Example: "MONDAY | 10PM - 12AM | THE LATE SHOW"
        // If specific format is unknown, we split by common delimiters.
        
        val list = mutableListOf<ShowItem>()
        val lines = text.lines()
        
        for (line in lines) {
            if (line.isBlank()) continue
            
            // Simple heuristic parser
            // Assuming format: DAY TIME SHOW or DAY - TIME - SHOW
            // We will just store the raw line if complex, but let's try to split
            
            val parts = line.split("|", "-").map { it.trim() }
            if (parts.size >= 3) {
                 list.add(ShowItem(parts[0], parts[1], parts[2]))
            } else if (parts.size == 2) {
                list.add(ShowItem(parts[0], parts[1], ""))
            } else {
                 list.add(ShowItem("", "", parts[0]))
            }
        }
        return list
    }
}
