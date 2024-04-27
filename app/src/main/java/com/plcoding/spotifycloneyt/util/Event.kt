package com.plcoding.spotifycloneyt.util

open class Event<out T>(
    private val data: T
) {
    var hasBeenHandled: Boolean = false
        private set

    fun getContentIfNotHandled(): T? =
        if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            data
        }

    fun peekContent(): T = data
}