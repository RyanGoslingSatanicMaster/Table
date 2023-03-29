package com.doggystyle.table.components.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doggystyle.table.model.pojo.WeekTimeTable
import com.doggystyle.table.model.db.Group
import com.doggystyle.table.usecases.IGetTimeTable
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
