package com.techullurgy.chatsample.domain.utils

sealed interface DataError: RootError {
    enum class Network: DataError {
        BAD_REQUEST,
        SERVER_ERROR,
        TOO_MANY_REQUESTS,


        NO_INTERNET,
        UNKNOWN
    }

    enum class Local: DataError {
        DATABASE_CONNECTION_NOT_ESTABLISHED,
        DISK_ERROR,

        UNKNOWN
    }
}