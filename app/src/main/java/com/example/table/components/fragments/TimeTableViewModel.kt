package com.example.table.components.fragments

import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.table.model.pojo.WeekTimeTable
import com.example.table.model.db.Group
import com.example.table.usecases.IGetTimeTableUseCase
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

class TimeTableViewModel @Inject constructor(
    private val getTimeTableUseCase: IGetTimeTableUseCase, private val currentDate: Date) : ViewModel() {

    val timeTable = MutableLiveData<Pair<WeekTimeTable, WeekTimeTable>>()

    // TODO Group must be with id from db
    fun getTimeTable(group: Group){
        viewModelScope.launch {
            timeTable.postValue(getTimeTableUseCase.getTimeTableGroup(group))
        }
    }
}