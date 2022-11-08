package com.example.table.repositories

import com.example.table.model.LoadingState
import com.example.table.model.db.Group
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.model.requests.TimeTableRequest
import com.example.table.services.ITimeTableService
import com.example.table.utils.Constant
import java.lang.Exception
import javax.inject.Inject

class TimeTableRepository @Inject constructor(private val service: ITimeTableService): ITimeTableRepository {

    override suspend fun getTimeTable(timeTableRequest: TimeTableRequest){
            service.getTimeTable(timeTableRequest)
    }

    override suspend fun getTimeTableActiveGroup(): List<TimeTableWithLesson> {
        return service.getTimeTableActiveGroup()
    }

    override suspend fun getTimeTableGroup(group: Group): List<TimeTableWithLesson> {
        return service.getTimeTableGroup(group)
    }
}