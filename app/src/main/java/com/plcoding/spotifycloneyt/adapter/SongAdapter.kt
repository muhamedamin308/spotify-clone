package com.plcoding.spotifycloneyt.adapter

import android.view.LayoutInflater
import androidx.recyclerview.widget.AsyncListDiffer
import com.bumptech.glide.RequestManager
import com.plcoding.spotifycloneyt.R
import com.plcoding.spotifycloneyt.adapter.viewholder.SongViewHolder
import com.plcoding.spotifycloneyt.databinding.ListItemBinding
import javax.inject.Inject

class SongAdapter @Inject constructor(
    private val glide: RequestManager,
    private val inflater: LayoutInflater
) : BaseSongAdapter<ListItemBinding>({ inflater1, parent, _ ->
    ListItemBinding.inflate(inflater1, parent, false)
}) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun createViewHolder(binding: ListItemBinding): BaseViewHolder<ListItemBinding> =
        SongViewHolder(binding)

    override fun onBindViewHolder(holder: BaseViewHolder<ListItemBinding>, pos: Int) {
        val song = differSongList[pos]
        holder.binding.apply {
            tvSongName.text = song.title
            tvSongArtist.text = song.singer
            if (song.imageUrl != null)
                glide.load(song.imageUrl).into(songImage)
            else
                glide.load(R.drawable.unknown_music).into(songImage)
        }
        holder.itemView.apply {
            setOnClickListener {
                onItemClickListener?.let {
                    it(song)
                }
            }
        }
    }
}