package com.doggystyle.table.di.modules

import com.doggystyle.table.repositories.ITimeTableRepository
import com.doggystyle.table.repositories.TimeTableRepository
import com.doggystyle.table.services.ITimeTableService
import com.doggystyle.table.services.TimeTableService
import com.doggystyle.table.usecases.*
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
