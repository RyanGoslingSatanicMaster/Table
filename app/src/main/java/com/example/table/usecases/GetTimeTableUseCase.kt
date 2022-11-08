package com.example.table.usecases

import com.example.table.model.DayTimeTable
import com.example.table.model.WeekTimeTable
import com.example.table.model.db.Group
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.repositories.ITimeTableRepository
import javax.inject.Inject

class GetTimeTableUseCase @Inject constructor(private val repository: ITimeTableRepository, private val dayWeek: List<String>): IGetTimeTableUseCase,
    UseCase<Pair<WeekTimeTable, WeekTimeTable>, Group>() {

    override suspend fun run(p: Group): Pair<WeekTimeTable, WeekTimeTable> {
        return repository.getTimeTableGroup(p).splitWeek()
    }

    override suspend fun getTimeTableGroup(group: Group): Pair<WeekTimeTable, WeekTimeTable> {
        return run(group)
    }

    private fun List<TimeTableWithLesson>.splitWeek(): Pair<WeekTimeTable, WeekTimeTable>{
        return this.filter { it.timeTable.isFirstWeek == true }.toList().splitDays() to
        this.filter { it.timeTable.isFirstWeek == false }.toList().splitDays()
    }

    private fun List<TimeTableWithLesson>.splitDays(): WeekTimeTable{
        var remainder = this
        val result = mutableListOf<DayTimeTable>()
        var middleList: Pair<List<TimeTableWithLesson>, List<TimeTableWithLesson>>
        while (remainder.isNotEmpty()){
            middleList = remainder.partition { it.timeTable.time.day == remainder[0].timeTable.time.day }
            result.add(DayTimeTable(dayWeek[remainder[0].timeTable.time.day], middleList.first))
            remainder = middleList.second
        }
        return WeekTimeTable(result)
    }


}