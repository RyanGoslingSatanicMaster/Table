package com.example.table.usecases

import com.example.table.model.WeekTimeTable
import com.example.table.model.db.Group
import com.example.table.model.pojo.TimeTableWithLesson
import kotlinx.coroutines.flow.Flow

interface IGetTimeTableUseCase {
    suspend fun getTimeTableGroup(group: Group): Pair<WeekTimeTable, WeekTimeTable>
}