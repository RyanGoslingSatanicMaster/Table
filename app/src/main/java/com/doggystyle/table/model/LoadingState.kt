package com.doggystyle.table.model

import androidx.compose.runtime.Stable
import java.lang.ClassCastException

@Stable
sealed class LoadingState {
    data class Error(val ex: Exception) : LoadingState()
    data class Success(val tag: Int): LoadingState()
    object Loading: LoadingState()
    object Stopped: LoadingState()
}
