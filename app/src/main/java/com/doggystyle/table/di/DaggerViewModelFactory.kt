package com.doggystyle.table.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class DaggerViewModelFactory @Inject constructor(val viewModelMap: Map<Class<out ViewModel>, @JvmSuppressWildcards(true) Provider<ViewModel>>): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val entity = viewModelMap.get(modelClass)
        if (entity != null)
            return if (modelClass.isAssignableFrom(entity.get().javaClass)) entity.get() as T
            else throw IllegalArgumentException("Invalid argument ViewModel")
        else
            throw IllegalArgumentException("Invalid argument ViewModel")
    }
}
