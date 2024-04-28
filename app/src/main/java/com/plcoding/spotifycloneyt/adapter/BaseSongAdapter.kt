package com.plcoding.spotifycloneyt.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.plcoding.spotifycloneyt.data.entities.Song

abstract class BaseSongAdapter<VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : RecyclerView.Adapter<BaseSongAdapter.BaseViewHolder<VB>>() {
    open class BaseViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder<VB> {
        val binding = inflate(LayoutInflater.from(p0.context), p0, false)
        return createViewHolder(binding)
    }
    abstract fun createViewHolder(binding: VB): BaseViewHolder<VB>

    protected val diffCallback = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean =
            oldItem.mediaId == newItem.mediaId

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    protected abstract val differ: AsyncListDiffer<Song>
    var differSongList: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    override fun getItemCount(): Int = differSongList.size

    protected var onItemClickListener: ((Song) -> Unit)? = null
    fun setItemClickListener(listener: (Song) -> Unit) {
        onItemClickListener = listener
    }
}