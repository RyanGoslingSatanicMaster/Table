package com.doggystyle.table.components.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doggystyle.table.exceptions.TimeTableIsEmptyException
import com.doggystyle.table.model.LoadingState
import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.pojo.TimeTableWithLesson
import com.doggystyle.table.model.requests.NextLessonRequest
import com.doggystyle.table.model.requests.TimeTableRequest
import com.doggystyle.table.usecases.*
import kotlinx.coroutines.launch
import java.sql.Time
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getActiveGroup: IGetActiveGroup,
    private val getNextLessonTime: IGetNextLessonTime,
) : ViewModel() {

    val activeGroup = MutableLiveData<Group>()

    val notificationSettings = MutableLiveData<Triple<Boolean, Boolean, Int>>()

    fun setGroup(group: Group) {
        activeGroup.value = group
    }

    fun getActiveGroup() {
        viewModelScope.launch {
            activeGroup.postValue(getActiveGroup.getActiveGroup())
        }
    }

    fun getNextLessonTime(
        request: NextLessonRequest,
        callback: (lesson: TimeTableWithLesson, settings: Triple<Boolean, Boolean, Int>) -> Unit,
    ) {
        viewModelScope.launch {
            var lesson: TimeTableWithLesson? = null
            try {
                lesson = getNextLessonTime.getNextLessonTime(request)
            } catch (ex: TimeTableIsEmptyException) {

            }
            lesson?.let {
                callback(lesson, request.notify)
            }
        }
    }

}
