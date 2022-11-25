package com.example.table.components.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.table.exceptions.TimeTableIsEmptyException
import com.example.table.model.LoadingState
import com.example.table.model.db.Group
import com.example.table.model.requests.NextLessonRequest
import com.example.table.model.requests.TimeTableRequest
import com.example.table.usecases.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(private val getActiveGroup: IGetActiveGroup,
                                        private val getNextLessonTime: IGetNextLessonTime): ViewModel() {

    val activeGroup = MutableLiveData<Group>()

    val notificationSettings = MutableLiveData<Pair<Boolean, Boolean>>()

    val nextLessonTime = MutableLiveData<Date>()

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