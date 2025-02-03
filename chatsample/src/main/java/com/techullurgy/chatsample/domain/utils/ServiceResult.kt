package com.techullurgy.chatsample.domain.utils

sealed interface RootError

sealed interface ServiceResult<out D, out E: RootError> {
    data class Success<out D, out E: RootError>(val data: D): ServiceResult<D, E>
    data class Error<out D, out E: RootError>(val error: E): ServiceResult<D, E>
}