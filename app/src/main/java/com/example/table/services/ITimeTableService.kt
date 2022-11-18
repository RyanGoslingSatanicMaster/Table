package com.example.table.services

import com.example.table.model.db.Group
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.model.requests.NextLessonRequest
import com.example.table.model.requests.TimeTableRequest
import java.util.*

interface ITimeTableService {
    suspend fun getTimeTable(request: TimeTableRequest)
    suspend fun getNextLessonTime(request: NextLessonRequest): Date
    suspend fun getTimeTableActiveGroup(): List<TimeTableWithLesson>
    suspend fun getTimeTableGroup(group: Group): List<TimeTableWithLesson>
}