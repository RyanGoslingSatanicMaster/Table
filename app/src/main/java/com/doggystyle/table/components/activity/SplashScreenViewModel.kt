package com.doggystyle.table.components.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doggystyle.table.model.LoadingState
import com.doggystyle.table.usecases.IGetActiveGroup
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashScreenViewModel @Inject constructor(private val getActiveGroup: IGetActiveGroup): ViewModel() {

    val loading = MutableLiveData<LoadingState>(LoadingState.Stopped)

    companion object{
        const val SUCCESS_ACTIVE_GROUP = 1
        const val SUCCESS_ACTIVE_GROUP_EMPTY = 0
    }

    fun getActiveGroup(){
        loading.value = LoadingState.Loading
        viewModelScope.launch {
            if (getActiveGroup.getActiveGroup() == null)
                loading.postValue(LoadingState.Success(SUCCESS_ACTIVE_GROUP_EMPTY))
            else
                loading.postValue(LoadingState.Success(SUCCESS_ACTIVE_GROUP))
        }
    }

}
