package com.example.table.services

import com.example.table.model.db.Group
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.model.requests.TimeTableRequest

interface ITimeTableService {
    suspend fun getTimeTable(request: TimeTableRequest)
    suspend fun getIndexOfWeek(request: TimeTableRequest)
    suspend fun getTimeTableActiveGroup(): List<TimeTableWithLesson>
    suspend fun getTimeTableGroup(group: Group): List<TimeTableWithLesson>
}