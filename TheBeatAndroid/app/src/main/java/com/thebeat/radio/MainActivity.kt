package com.thebeat.radio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.dn.thebeat.ui.theme.TheBeatTheme // Assuming package correction or move
import com.thebeat.radio.data.MetadataRepository
import com.thebeat.radio.data.ShowItem
import com.thebeat.radio.data.Song
import com.thebeat.radio.service.RadioService
import com.thebeat.radio.ui.PlayerScreen
import com.thebeat.radio.ui.ScheduleScreen
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import androidx.media3.common.Player

class MainActivity : ComponentActivity() {

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val repository = MetadataRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Start Service
        val intent = Intent(this, RadioService::class.java)
        startForegroundService(intent)

        setContent {
            TheBeatTheme {
                val currentSong = remember { mutableStateOf<Song?>(null) }
                val isPlaying = remember { mutableStateOf(false) }
                val showSchedule = remember { mutableStateOf<List<ShowItem>>(emptyList()) }
                // Simple state for screen navigation
                var currentScreen by remember { mutableStateOf("player") } 

                // Polling for metadata
                LaunchedEffect(Unit) {
                    while(true) {
                        repository.getNowPlaying { song ->
                            currentSong.value = song
                        }
                        repository.getSchedule { schedule ->
                            // Update schedule
                            if (schedule.isNotEmpty()) showSchedule.value = schedule
                        }
                        kotlinx.coroutines.delay(10000) 
                    }
                }

                Scaffold(
                    bottomBar = {
                        // Simple Bottom Nav
                        androidx.compose.material3.NavigationBar {
                            androidx.compose.material3.NavigationBarItem(
                                icon = { androidx.compose.material3.Icon(androidx.compose.ui.res.painterResource(R.drawable.ic_launcher), "Player") }, // Placeholder icon
                                label = { androidx.compose.material3.Text("Player") },
                                selected = currentScreen == "player",
                                onClick = { currentScreen = "player" }
                            )
                            androidx.compose.material3.NavigationBarItem(
                                icon = { androidx.compose.material3.Icon(androidx.compose.ui.res.painterResource(R.drawable.ic_schedule_neon), "Schedule") },
                                label = { androidx.compose.material3.Text("Schedule") },
                                selected = currentScreen == "schedule",
                                onClick = { currentScreen = "schedule" }
                            )
                        }
                    }
                ) { innerPadding ->
                    androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
                        if (currentScreen == "player") {
                            PlayerScreen(
                                currentSong = currentSong.value,
                                isPlaying = isPlaying.value,
                                onPlayPause = {
                                    if (isPlaying.value) mediaController?.pause() else mediaController?.play()
                                    isPlaying.value = !isPlaying.value
                                }
                            )
                        } else {
                            ScheduleScreen(schedule = showSchedule.value) 
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val sessionToken = SessionToken(this, ComponentName(this, RadioService::class.java))
        controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
            // Sync initial state
            // isPlaying.value = mediaController?.isPlaying == true
        }, MoreExecutors.directExecutor())
    }

    override fun onStop() {
        super.onStop()
        controllerFuture?.let { MediaController.releaseFuture(it) }
    }
}
