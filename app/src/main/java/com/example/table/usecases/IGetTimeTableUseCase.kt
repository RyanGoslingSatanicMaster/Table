package com.example.table.usecases

import com.example.table.model.pojo.WeekTimeTable
import com.example.table.model.db.Group

interface IGetTimeTableUseCase {
    suspend fun getTimeTableGroup(group: Group): Pair<WeekTimeTable, WeekTimeTable>
}