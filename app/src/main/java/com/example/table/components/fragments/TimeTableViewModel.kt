package com.example.table.components.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.table.model.pojo.WeekTimeTable
import com.example.table.model.db.Group
import com.example.table.usecases.IGetTimeTable
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimeTableViewModel @Inject constructor(
    private val getTimeTableUseCase: IGetTimeTable) : ViewModel() {

    val timeTable = MutableLiveData<Pair<WeekTimeTable, WeekTimeTable>>()

    // TODO Group must be with id from db
    fun getTimeTable(group: Group){
        viewModelScope.launch {
            timeTable.postValue(getTimeTableUseCase.getTimeTableGroup(group))
        }
    }
}