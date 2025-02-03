package com.techullurgy.chatsample.domain.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class DynamicString(val text: String): UiText
    data class StringResource(
        @StringRes val id: Int,
        val formatArgs: List<Any>
    ): UiText

    @Composable
    fun asUiString(): String =
        when(this) {
            is DynamicString -> text
            is StringResource -> stringResource(id, *(formatArgs.toTypedArray()))
        }

    fun asUiString(context: Context): String =
        when(this) {
            is DynamicString -> text
            is StringResource -> context.getString(id, *(formatArgs.toTypedArray()))
        }
}