package com.example.table.usecases

import com.example.table.model.requests.NextLessonRequest
import com.example.table.repositories.ITimeTableRepository
import com.example.table.utils.ConverterUtils
import java.util.*
import javax.inject.Inject

class GetNextLessonTime @Inject constructor(val repository: ITimeTableRepository): IGetNextLessonTime, UseCase<Date, NextLessonRequest>() {

    override suspend fun run(p: NextLessonRequest): Date {
        return repository.getNextLessonTime(p)
    }

    override suspend fun getNextLessonTime(request: NextLessonRequest): Date {
        return run(request)
    }

}