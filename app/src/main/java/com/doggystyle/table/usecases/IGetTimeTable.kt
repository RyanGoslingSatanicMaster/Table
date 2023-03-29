package com.doggystyle.table.usecases

import com.doggystyle.table.model.pojo.WeekTimeTable
import com.doggystyle.table.model.db.Group

interface IGetTimeTable {
    suspend fun getTimeTableGroup(group: Group): Pair<WeekTimeTable, WeekTimeTable>
}
