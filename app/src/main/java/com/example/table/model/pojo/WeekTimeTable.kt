package com.example.table.model.pojo

import androidx.compose.runtime.Stable
import com.example.table.model.pojo.DayTimeTable

@Stable
data class WeekTimeTable(
    val days: List<DayTimeTable>,
    val isCurrent: Boolean = false
)
