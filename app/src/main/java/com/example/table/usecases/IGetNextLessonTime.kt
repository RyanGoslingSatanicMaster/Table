package com.example.table.usecases

import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.model.requests.NextLessonRequest
import java.util.*

interface IGetNextLessonTime {
    suspend fun getNextLessonTime(request: NextLessonRequest): TimeTableWithLesson
}