package com.example.table.di.modules

import com.example.table.repositories.ITimeTableRepository
import com.example.table.repositories.TimeTableRepository
import com.example.table.services.ITimeTableService
import com.example.table.services.TimeTableService
import com.example.table.usecases.*
import dagger.Binds
import dagger.Module

@Module
abstract class GetDayWidgetModule {

    @Binds
    abstract fun providesRepository(repository: TimeTableRepository): ITimeTableRepository

    @Binds
    abstract fun proviesService(service: TimeTableService): ITimeTableService

    @Binds
    abstract fun providesGetUseCase(useCase: GetDayWidget): IGetDayWidget
}