package com.example.personalplay

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.ProgressBar

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var progressCircle: ProgressBar
    private lateinit var playPauseButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var shuffleButton: ImageButton
    private lateinit var repeatButton: ImageButton
    private lateinit var songTitleText: TextView
    private lateinit var artistNameText: TextView
    private val handler = Handler(Looper.getMainLooper())

    private var currentSongIndex = 0
    private var isShuffleOn = false
    private var repeatMode = RepeatMode.OFF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        progressCircle = findViewById(R.id.progressCircle)
        playPauseButton = findViewById(R.id.playPauseButton)
        previousButton = findViewById(R.id.previousButton)
        nextButton = findViewById(R.id.nextButton)
        shuffleButton = findViewById(R.id.shuffleButton)
        repeatButton = findViewById(R.id.repeatButton)
        songTitleText = findViewById(R.id.songTitleText)
        artistNameText = findViewById(R.id.artistNameText)

        // Get the song position from the intent
        currentSongIndex = intent.getIntExtra("songPosition", 0)

        // Initialize MediaPlayer with the selected song
        mediaPlayer = MediaPlayer.create(this, SongListActivity.songs[currentSongIndex].resourceId)
        playSong(currentSongIndex)

        // Set up button click listeners
        playPauseButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
                updateProgress()
            }
            updatePlayPauseButton()
        }

        previousButton.setOnClickListener {
            playPreviousSong()
        }

        nextButton.setOnClickListener {
            playNextSong()
        }

        shuffleButton.setOnClickListener {
            toggleShuffle()
        }

        repeatButton.setOnClickListener {
            toggleRepeat()
        }

    }

    private fun playNextSong() {
        currentSongIndex = if (isShuffleOn) {
            (0 until SongListActivity.songs.size).random()
        } else {
            (currentSongIndex + 1) % SongListActivity.songs.size
        }
        playSong(currentSongIndex)
    }

    private fun playPreviousSong() {
        currentSongIndex = if (isShuffleOn) {
            (0 until SongListActivity.songs.size).random()
        } else {
            (currentSongIndex - 1 + SongListActivity.songs.size) % SongListActivity.songs.size
        }
        playSong(currentSongIndex)
    }

    private fun toggleShuffle() {
        isShuffleOn = !isShuffleOn
        shuffleButton.setImageResource(if (isShuffleOn) R.drawable.ic_shuffle_on else R.drawable.ic_shuffle)
    }

    private fun toggleRepeat() {
        repeatMode = when (repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        updateRepeatButtonIcon()
    }

    private fun updateRepeatButtonIcon() {
        val iconResource = when (repeatMode) {
            RepeatMode.OFF -> R.drawable.ic_repeat
            RepeatMode.ALL -> R.drawable.ic_repeat_all
            RepeatMode.ONE -> R.drawable.ic_repeat_one
        }
        repeatButton.setImageResource(iconResource)
    }

    private fun playSong(index: Int) {
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer.create(this, SongListActivity.songs[index].resourceId)
        mediaPlayer.start()
        updateSongInfo()
        updatePlayPauseButton()
        progressCircle.max = mediaPlayer.duration
        updateProgress()
    }

    private fun updateSongInfo() {
        val currentSong = SongListActivity.songs[currentSongIndex]
        songTitleText.text = currentSong.title
        artistNameText.text = currentSong.artist
    }

    private fun updatePlayPauseButton() {
        playPauseButton.setImageResource(if (mediaPlayer.isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
    }

    private fun updateProgress() {
        progressCircle.progress = mediaPlayer.currentPosition
        if (mediaPlayer.isPlaying) {
            handler.postDelayed(::updateProgress, 1000)
        }
        if (!mediaPlayer.isPlaying && mediaPlayer.currentPosition >= mediaPlayer.duration - 100) {
            when (repeatMode) {
                RepeatMode.OFF -> if (currentSongIndex < SongListActivity.songs.size - 1) playNextSong()
                RepeatMode.ALL -> playNextSong()
                RepeatMode.ONE -> playSong(currentSongIndex)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }
}

enum class RepeatMode {
    OFF, ALL, ONE
}