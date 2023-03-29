package com.doggystyle.table.usecases

import com.doggystyle.table.model.pojo.TimeTableWithLesson
import com.doggystyle.table.model.requests.NextLessonRequest
import java.util.*

interface IGetNextLessonTime {
    suspend fun getNextLessonTime(request: NextLessonRequest): TimeTableWithLesson
}
