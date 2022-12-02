package com.example.table.components.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.table.exceptions.TimeTableIsEmptyException
import com.example.table.model.LoadingState
import com.example.table.model.db.Group
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.model.requests.NextLessonRequest
import com.example.table.model.requests.TimeTableRequest
import com.example.table.usecases.*
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(private val getActiveGroup: IGetActiveGroup,
                                        private val getNextLessonTime: IGetNextLessonTime): ViewModel() {

    val activeGroup = MutableLiveData<Group>()

    val notificationSettings = MutableLiveData<Triple<Boolean, Boolean, String>>()

    val nextLessonTime = MutableLiveData<TimeTableWithLesson>()

    fun setGroup(group: Group){
        activeGroup.value = group
    }

    fun getActiveGroup(){
        viewModelScope.launch {
            activeGroup.postValue(getActiveGroup.getActiveGroup())
        }
    }

    fun getNextLessonTime(request: NextLessonRequest){
        viewModelScope.launch {
            try {
                nextLessonTime.postValue(getNextLessonTime.getNextLessonTime(request))
            }
            catch (ex: TimeTableIsEmptyException){

            }
        }
    }

}