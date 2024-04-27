package com.plcoding.spotifycloneyt.util

class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?
) {
    companion object {
        fun <S> success(data: S?): Resource<S> =
            Resource(Status.SUCCESS, data, null)

        fun <E> error(msg: String, data: E?): Resource<E> =
            Resource(Status.ERROR, data, msg)

        fun <L> loading(data: L?): Resource<L> =
            Resource(Status.LOADING, data, null)
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}