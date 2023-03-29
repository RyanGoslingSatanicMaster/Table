package com.doggystyle.table.di.modules

import com.doggystyle.table.repositories.GroupRepository
import com.doggystyle.table.repositories.IGroupRepository
import com.doggystyle.table.services.GroupService
import com.doggystyle.table.services.IGroupService
import com.doggystyle.table.usecases.*
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
    abstract fun providesIsGroupInDbUseCase(useCase: IsGroupInDb): IIsGroupInDb

    @Binds
    abstract fun providesDeleteGroupUseCase(useCase: DeleteGroup): IDeleteGroup

    @Binds
    abstract fun providesGetActiveGroupUseCase(useCase:GetActiveGroup): IGetActiveGroup

    @Binds
    abstract fun providesUpdateGroupUseCase(usecase: UpdateGroup): IUpdateGroup

    @Binds
    abstract fun providesGetGroups(usecase: GetSavedGroups): IGetSavedGroups
}
