package com.example.table.di.modules

import com.example.table.datasource.remote.Api
import com.example.table.repositories.GroupRepository
import com.example.table.repositories.IGroupRepository
import com.example.table.services.GroupService
import com.example.table.services.IGroupService
import com.example.table.usecases.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class GroupModule {

    @Binds
    abstract fun providesGroupUseCase(useCase: GroupUseCase): IGroupUseCase

    @Binds
    abstract fun providesGroupRepository(repo: GroupRepository): IGroupRepository

    @Binds
    abstract fun provideGroupService(service: GroupService): IGroupService

    @Binds
    abstract fun providesIsGroupInDbUseCase(useCase: IsGroupInDbUseCase): IIsGroupInDbUseCase

    @Binds
    abstract fun providesDeleteGroupUseCase(useCase: DeleteGroupUseCase): IDeleteGroupUseCase

    @Binds
    abstract fun proivdesGetActiveGroupUseCase(useCase:GetActiveGroup): IGetActiveGroup
}