package com.example.table.di.modules

import com.example.table.repositories.ITimeTableRepository
import com.example.table.repositories.TimeTableRepository
import com.example.table.services.ITimeTableService
import com.example.table.services.TimeTableService
import com.example.table.usecases.GetTimeTableUseCase
import com.example.table.usecases.IGetTimeTableUseCase
import com.example.table.usecases.ITimeTableUseCase
import com.example.table.usecases.TimeTableUseCase
import dagger.Binds
import dagger.Module

@Module
abstract class TimeTableModule {

    @Binds
    abstract fun providesUseCase(useCase: TimeTableUseCase): ITimeTableUseCase

    @Binds
    abstract fun providesRepository(repository: TimeTableRepository): ITimeTableRepository

    @Binds
    abstract fun proviesService(service: TimeTableService): ITimeTableService

    @Binds
    abstract fun providesGetUseCase(useCase: GetTimeTableUseCase): IGetTimeTableUseCase
}