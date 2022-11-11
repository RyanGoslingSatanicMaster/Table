package com.example.table.model.pojo

import androidx.compose.runtime.Stable
import com.example.table.model.pojo.TimeTableWithLesson

@Stable
data class DayTimeTable(
    val day: String,
    val timeTableList: List<TimeTableWithLesson>
)
