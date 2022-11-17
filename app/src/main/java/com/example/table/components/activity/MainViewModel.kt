package com.example.table.components.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.table.model.LoadingState
import com.example.table.model.db.Group
import com.example.table.model.requests.TimeTableRequest
import com.example.table.usecases.GetActiveGroup
import com.example.table.usecases.IGetActiveGroup
import com.example.table.usecases.IGroupUseCase
import com.example.table.usecases.ITimeTableUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val getActiveGroup: IGetActiveGroup): ViewModel() {

    val activeGroup = MutableLiveData<Group>()

    val notificationSettings = MutableLiveData<Pair<Boolean, Boolean>>()

    fun setGroup(group: Group){
        activeGroup.value = group
    }

    fun getActiveGroup(){
        viewModelScope.launch {
            activeGroup.postValue(getActiveGroup.getActiveGroup())
        }
    }

}