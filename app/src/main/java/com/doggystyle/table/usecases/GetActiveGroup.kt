package com.doggystyle.table.usecases

import com.doggystyle.table.model.db.Group
import com.doggystyle.table.repositories.IGroupRepository
import javax.inject.Inject

class GetActiveGroup @Inject constructor(val repository: IGroupRepository): IGetActiveGroup, UseCase<Group?, Unit>() {

    override suspend fun run(p: Unit): Group? {
        return repository.getActiveGroup()
    }

    override suspend fun getActiveGroup(): Group? {
        return run(Unit)
    }
}
