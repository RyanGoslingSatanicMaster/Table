package com.example.table

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.table.model.LoadingState
import com.example.table.model.db.Group
import com.example.table.model.requests.TimeTableRequest
import com.example.table.usecases.IGroupUseCase
import com.example.table.usecases.ITimeTableUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(): ViewModel() {

    val activeGroup = MutableLiveData<Group>()

    fun setGroup(group: Group){
        activeGroup.value = group
    }

}