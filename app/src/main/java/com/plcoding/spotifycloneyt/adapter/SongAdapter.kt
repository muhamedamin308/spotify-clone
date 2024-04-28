package com.plcoding.spotifycloneyt.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.plcoding.spotifycloneyt.R
import com.plcoding.spotifycloneyt.data.entities.Song
import com.plcoding.spotifycloneyt.databinding.ListItemBinding
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
    class SongViewHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song, glide: RequestManager) {
            binding.apply {
                tvSongName.text = song.title
                tvSongArtist.text = song.singer
                if (song.imageUrl != null) {
                    glide.load(song.imageUrl).into(songImage)
                } else {
                    glide.load(R.drawable.unknown_music).into(songImage)
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean =
            oldItem.mediaId == newItem.mediaId

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var differSongList: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): SongViewHolder =
        SongViewHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(p0.context),
                p0,
                false
            )
        )

    override fun getItemCount(): Int = differSongList.size

    override fun onBindViewHolder(holder: SongViewHolder, pos: Int) {
        val song = differSongList[pos]
        holder.bind(song, glide)
        holder.itemView.apply {
            setOnClickListener {
                onItemClickListener?.let {
                    it(song)
                }
            }
        }
    }

    private var onItemClickListener: ((Song) -> Unit)? = null
    fun setOnItemClickListener(listener: (Song) -> Unit) {
        onItemClickListener = listener
    }
}