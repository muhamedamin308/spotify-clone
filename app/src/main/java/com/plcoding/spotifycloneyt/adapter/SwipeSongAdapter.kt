package com.plcoding.spotifycloneyt.adapter

import android.view.LayoutInflater
import androidx.recyclerview.widget.AsyncListDiffer
import com.plcoding.spotifycloneyt.adapter.viewholder.SwipeSongViewHolder
import com.plcoding.spotifycloneyt.databinding.SwipeItemBinding
import javax.inject.Inject

class SwipeSongAdapter @Inject constructor(
    private val inflater: LayoutInflater
) : BaseSongAdapter<SwipeItemBinding>({ inflater1, parent, _ ->
    SwipeItemBinding.inflate(inflater1, parent, false)
}) {

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun createViewHolder(binding: SwipeItemBinding): BaseViewHolder<SwipeItemBinding> =
        SwipeSongViewHolder(binding)

    override fun onBindViewHolder(holder: BaseViewHolder<SwipeItemBinding>, pos: Int) {
        val song = differSongList[pos]
        holder.binding.apply {
            val displayedText = "${song.title} - ${song.singer}"
            tvPrimary.text = displayedText
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