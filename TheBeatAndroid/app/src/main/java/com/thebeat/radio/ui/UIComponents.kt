package com.thebeat.radio.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.thebeat.radio.R
import com.thebeat.radio.data.ShowItem
import com.thebeat.radio.data.Song
import com.thebeat.radio.ui.theme.NeonBlue
import com.thebeat.radio.ui.theme.NeonPurple
import kotlin.random.Random

@Composable
fun PlayerScreen(
    currentSong: Song?,
    isPlaying: Boolean,
    onPlayPause: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        // Album Art
        AsyncImage(
            model = currentSong?.art ?: R.drawable.ic_launcher, // Fallback
            contentDescription = "Album Art",
            modifier = Modifier
                .size(300.dp)
                .background(Color.DarkGray)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Metadata
        Text(
            text = currentSong?.title ?: "The Beat",
            style = MaterialTheme.typography.displayLarge,
            color = NeonBlue
        )
        Text(
            text = currentSong?.artist ?: "Hip Hop Radio",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Visualizer
        Visualizer(isPlaying = isPlaying)

        Spacer(modifier = Modifier.height(32.dp))

        // Controls
        IconButton(
            onClick = onPlayPause,
            modifier = Modifier.size(80.dp)
        ) {
            val icon = if (isPlaying) R.drawable.ic_pause_neon else R.drawable.ic_play_neon
            // Fallback to system icon if drawable missing handled by try/catch in parent or resource checks
            // For now assuming resources exist.
            Icon(
                painter = painterResource(id = icon),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.Unspecified // Keep original neon colors
            )
        }
    }
}

@Composable
fun Visualizer(isPlaying: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "visualizer")
    // Animate bars only if playing
    val amplitude by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isPlaying) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "amplitude"
    )

    Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
        val barWidth = size.width / 20
        var x = 0f
        for (i in 0..19) {
            val height = if (isPlaying) Random.nextFloat() * size.height * amplitude else 5f
            drawLine(
                brush = Brush.verticalGradient(listOf(NeonBlue, NeonPurple)),
                start = Offset(x, size.height),
                end = Offset(x, size.height - height),
                strokeWidth = barWidth - 4f
            )
            x += barWidth
        }
    }
}

@Composable
fun ScheduleScreen(schedule: List<ShowItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        item {
            Text("Show Schedule", style = MaterialTheme.typography.headlineLarge, color = NeonBlue)
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(schedule) { show ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = show.day, color = NeonPurple, fontWeight = FontWeight.Bold)
                    Text(text = show.time, color = Color.White)
                    Text(text = show.title, style = MaterialTheme.typography.bodyLarge, color = NeonBlue)
                }
            }
        }
    }
}
