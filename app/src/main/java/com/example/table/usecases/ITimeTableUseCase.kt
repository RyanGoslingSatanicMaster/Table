package com.example.table.usecases

import com.example.table.model.LoadingState
import com.example.table.model.requests.TimeTableRequest

interface ITimeTableUseCase {

    suspend fun getTimeTable(timeTable: TimeTableRequest)

}