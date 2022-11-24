package com.example.table.services

import android.util.Log
import com.example.table.model.db.*
import com.example.table.model.pojo.LessonWithTeachers
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.model.requests.NextLessonRequest
import com.example.table.model.requests.TimeTableRequest
import com.example.table.utils.timeTableDeserialization
import java.util.*
import javax.inject.Inject

class TimeTableService @Inject constructor(): ITimeTableService, ApiService() {

    override suspend fun getTimeTable(request: TimeTableRequest){
        return executeAndSave(null, suspend {
            api.getTimeTable(request.group.groupName, request.typeSchedule)
        } to {
            val timeTable = timeTableDeserialization(it.string(), request.group)
            dao.saveAllTimeTableWithLesson(timeTable)
        })
    }

    override suspend fun getTimeTableActiveGroup(): List<TimeTableWithLesson> {
        return dao.getActiveTimeTable()
    }

    override suspend fun getTimeTableGroup(group: Group): List<TimeTableWithLesson> {
        return dao.getGroupTimeTable(group)
    }

    override suspend fun getNextLessonTime(request: NextLessonRequest): Date {
        return dao.getNextDayLessonsTime(request).get(0)
    }
}