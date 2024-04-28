package com.plcoding.spotifycloneyt.data.entities

data class Song(
    val mediaId: String = "",
    val title: String = "",
    val singer: String = "",
    val songUrl: String = "",
    val imageUrl: String? = null
)