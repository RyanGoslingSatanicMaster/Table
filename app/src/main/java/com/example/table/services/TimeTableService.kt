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

    override suspend fun getTimeTable(group: Group, typeSchedule: Int): Group{
        return executeAndSave(null, suspend {
            api.getTimeTable(group.groupName, typeSchedule)
        } to {
            dao.saveAllTimeTableWithLesson(timeTableDeserialization(it.string(), group))
        })
    }

    override suspend fun getTimeTableActiveGroup(): List<TimeTableWithLesson> {
        return dao.getActiveTimeTable()
    }

    override suspend fun getTimeTableGroup(group: Group): List<TimeTableWithLesson> {
        println(dao.getTimeTable(1))
        return dao.getGroupTimeTable(group)
    }

    override suspend fun getDayTimeTable(groupName: String, isFirstWeek: Boolean, day: String): List<TimeTableWithLesson> {
        return dao.getNextDayLessonsTime(groupName, isFirstWeek, day)
    }
}