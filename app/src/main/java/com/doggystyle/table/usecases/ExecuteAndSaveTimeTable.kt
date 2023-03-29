package com.doggystyle.table.usecases


import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.requests.TimeTableRequest
import com.doggystyle.table.repositories.ITimeTableRepository
import javax.inject.Inject

class ExecuteAndSaveTimeTable @Inject constructor(private val repository: ITimeTableRepository): IExecuteAndSaveTimeTable,
    UseCase<Group, TimeTableRequest>() {

    override suspend fun run(p: TimeTableRequest): Group {
        return repository.getTimeTable(p.group, p.typeSchedule)
    }

    override suspend fun getTimeTable(timeTable: TimeTableRequest): Group {
        return run(timeTable)
    }

}
