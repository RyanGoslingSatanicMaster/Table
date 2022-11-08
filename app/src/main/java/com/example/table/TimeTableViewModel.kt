package com.example.table

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.table.model.DayTimeTable
import com.example.table.model.WeekTimeTable
import com.example.table.model.db.Group
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.usecases.IGetTimeTableUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.streams.toList

class TimeTableViewModel @Inject constructor(
    private val getTimeTableUseCase: IGetTimeTableUseCase) : ViewModel() {

    val timeTable = MutableLiveData<Pair<WeekTimeTable,WeekTimeTable>>()

    // TODO Group must be with id from db
    fun getTimeTable(group: Group){
        viewModelScope.launch {
            timeTable.postValue(getTimeTableUseCase.getTimeTableGroup(group))
        }
    }
}