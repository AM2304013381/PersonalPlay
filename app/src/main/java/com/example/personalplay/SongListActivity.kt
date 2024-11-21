package com.example.personalplay

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SongListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var songAdapter: SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song_list)

        recyclerView = findViewById(R.id.songListRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        songAdapter = SongAdapter(songs) { position ->
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("songPosition", position)
            startActivity(intent)
        }

        recyclerView.adapter = songAdapter
    }

    companion object {
        val songs = listOf(
            Song("Tore Up", "Don Toliver", R.raw.toreup),
            Song("Song 2", "Unknown", R.raw.song2),
            Song("Song 3", "Unknown", R.raw.song3),
            Song("Bunyi Gitar", "P Ramlee", R.raw.bunyigitar),
            Song("Alasanmu", "Exists", R.raw.alasanmu),
            Song("Ku Relakan Jiwa", "Hazama", R.raw.kurelakanjiwa),
            Song("Titian Perjalanan", "XPDC", R.raw.titianperjalan),
            Song("Arcade", "Duncan Laurance", R.raw.arcade),
            Song("Shut Up and Dance", "Walk The Moon", R.raw.shutupanddance)
        )
    }
}

class SongAdapter(
    private val songs: List<Song>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.songTitleText)
        val artistText: TextView = view.findViewById(R.id.songArtistText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.song_list_item, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.titleText.text = song.title
        holder.artistText.text = song.artist
        holder.itemView.setOnClickListener { onItemClick(position) }
    }

    override fun getItemCount() = songs.size
}

data class Song(val title: String, val artist: String, val resourceId: Int)