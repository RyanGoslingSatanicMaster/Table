package com.example.table.di.modules

import com.example.table.repositories.GroupRepository
import com.example.table.repositories.IGroupRepository
import com.example.table.services.GroupService
import com.example.table.services.IGroupService
import com.example.table.usecases.*
import dagger.Binds
import dagger.Module

@Module
abstract class GroupModule {

    @Binds
    abstract fun providesGroupUseCase(useCase: GetGroups): IGetGroups

    @Binds
    abstract fun providesGroupRepository(repo: GroupRepository): IGroupRepository

    @Binds
    abstract fun provideGroupService(service: GroupService): IGroupService

    @Binds
    abstract fun providesIsGroupInDbUseCase(useCase: IsGroupInDbUseCase): IIsGroupInDbUseCase

    @Binds
    abstract fun providesDeleteGroupUseCase(useCase: DeleteGroupUseCase): IDeleteGroupUseCase

    @Binds
    abstract fun providesGetActiveGroupUseCase(useCase:GetActiveGroup): IGetActiveGroup

    @Binds
    abstract fun providesUpdateGroupUseCase(usecase: UpdateGroup): IUpdateGroup
}