package com.example.table.usecases

import com.example.table.model.LoadingState
import com.example.table.model.requests.TimeTableRequest
import com.example.table.repositories.ITimeTableRepository
import javax.inject.Inject

class TimeTableUseCase @Inject constructor(private val repository: ITimeTableRepository): ITimeTableUseCase,
    UseCase<Unit, TimeTableRequest>() {

    override suspend fun run(p: TimeTableRequest) {
        return repository.getTimeTable(p)
    }

    override suspend fun getTimeTable(timeTable: TimeTableRequest) {
        return run(timeTable)
    }

}