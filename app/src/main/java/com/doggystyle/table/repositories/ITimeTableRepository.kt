package com.doggystyle.table.repositories

import com.doggystyle.table.model.LoadingState
import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.pojo.TimeTableWithLesson
import com.doggystyle.table.model.requests.NextLessonRequest
import com.doggystyle.table.model.requests.TimeTableRequest
import java.util.*

interface ITimeTableRepository {
    suspend fun getTimeTable(group: Group, typeSchedule: Int): Group
    suspend fun getTimeTableActiveGroup(): List<TimeTableWithLesson>
    suspend fun getTimeTableGroup(group: Group): List<TimeTableWithLesson>
    suspend fun getDayTimeTable(groupName: String, isFirstWeek: Boolean, day: String): List<TimeTableWithLesson>
}
