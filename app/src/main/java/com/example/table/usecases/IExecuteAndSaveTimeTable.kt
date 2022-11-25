package com.example.table.usecases


import com.example.table.model.LoadingState
import com.example.table.model.db.Group
import com.example.table.model.requests.TimeTableRequest

interface IExecuteAndSaveTimeTable {

    suspend fun getTimeTable(timeTable: TimeTableRequest): Group

}