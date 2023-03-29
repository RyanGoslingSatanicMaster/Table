package com.doggystyle.table.usecases


import com.doggystyle.table.model.LoadingState
import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.requests.TimeTableRequest

interface IExecuteAndSaveTimeTable {

    suspend fun getTimeTable(timeTable: TimeTableRequest): Group

}
