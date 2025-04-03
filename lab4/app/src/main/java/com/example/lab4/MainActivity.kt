package com.example.lab4

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var videoView: VideoView
    private lateinit var tabLayout: TabLayout
    private lateinit var buttonPlay: Button
    private lateinit var buttonPause: Button
    private lateinit var buttonStop: Button
    private lateinit var buttonSelectFile: Button

    private var currentPlayerType = PlayerType.AUDIO

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openFilePicker()
        }
    }

    private val getContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            when (currentPlayerType) {
                PlayerType.AUDIO -> playAudio(it)
                PlayerType.VIDEO -> playVideo(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)
        videoView = findViewById(R.id.video_view)
        tabLayout = findViewById(R.id.tab_layout)
        buttonPlay = findViewById(R.id.button_play)
        buttonPause = findViewById(R.id.button_pause)
        buttonStop = findViewById(R.id.button_stop)
        buttonSelectFile = findViewById(R.id.button_select_file)

        exoPlayer = ExoPlayer.Builder(this).build()
        playerView.player = exoPlayer

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentPlayerType = when (tab.position) {
                    0 -> PlayerType.AUDIO
                    1 -> PlayerType.VIDEO
                    else -> PlayerType.AUDIO
                }
                updatePlayerVisibility()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        buttonPlay.setOnClickListener {
            when (currentPlayerType) {
                PlayerType.AUDIO -> exoPlayer.play()
                PlayerType.VIDEO -> {
                    if (!videoView.isPlaying) {
                        videoView.start()
                    }
                }
            }
        }

        buttonPause.setOnClickListener {
            when (currentPlayerType) {
                PlayerType.AUDIO -> exoPlayer.pause()
                PlayerType.VIDEO -> {
                    if (videoView.isPlaying) {
                        videoView.pause()
                    }
                }
            }
        }

        buttonStop.setOnClickListener {
            when (currentPlayerType) {
                PlayerType.AUDIO -> {
                    exoPlayer.stop()
                    exoPlayer.seekTo(0)
                    exoPlayer.clearMediaItems()
                    Toast.makeText(this, "Аудіо зупинено і закрито", Toast.LENGTH_SHORT).show()
                }

                PlayerType.VIDEO -> {
                    videoView.stopPlayback()
                    videoView.setVideoURI(null)
                    Toast.makeText(this, "Відео зупинено і закрито", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonSelectFile.setOnClickListener {
            checkStoragePermission()
        }

        updatePlayerVisibility()
    }

    private fun updatePlayerVisibility() {
        when (currentPlayerType) {
            PlayerType.AUDIO -> {
                playerView.visibility = android.view.View.VISIBLE
                videoView.visibility = android.view.View.GONE
            }
            PlayerType.VIDEO -> {
                playerView.visibility = android.view.View.GONE
                videoView.visibility = android.view.View.VISIBLE
            }
        }
    }

    private fun checkStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (currentPlayerType) {
                PlayerType.AUDIO -> Manifest.permission.READ_MEDIA_AUDIO
                PlayerType.VIDEO -> Manifest.permission.READ_MEDIA_VIDEO
            }
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openFilePicker()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun openFilePicker() {
        when (currentPlayerType) {
            PlayerType.AUDIO -> getContent.launch("audio/*")
            PlayerType.VIDEO -> getContent.launch("video/*")
        }
    }

    private fun playAudio(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    private fun playVideo(uri: Uri) {
        videoView.setVideoURI(uri)
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = false
            videoView.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

    enum class PlayerType {
        AUDIO, VIDEO
    }
}