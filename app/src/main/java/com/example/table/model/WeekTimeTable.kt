package com.example.table.model

import androidx.compose.runtime.Stable
import com.example.table.model.pojo.TimeTableWithLesson

@Stable
data class WeekTimeTable(
    val days: List<DayTimeTable>
)
