package com.example.table.repositories

import com.example.table.model.LoadingState
import com.example.table.model.db.Group
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.model.requests.NextLessonRequest
import com.example.table.model.requests.TimeTableRequest
import java.util.*

interface ITimeTableRepository {
    suspend fun getTimeTable(group: Group, typeSchedule: Int): Group
    suspend fun getTimeTableActiveGroup(): List<TimeTableWithLesson>
    suspend fun getTimeTableGroup(group: Group): List<TimeTableWithLesson>
    suspend fun getDayTimeTable(groupName: String, isFirstWeek: Boolean, day: String): List<TimeTableWithLesson>
}