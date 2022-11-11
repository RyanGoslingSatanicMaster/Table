package com.example.table.usecases

import com.example.table.model.db.Group
import com.example.table.repositories.IGroupRepository
import javax.inject.Inject

class GetActiveGroup @Inject constructor(val repository: IGroupRepository): IGetActiveGroup, UseCase<Group?, Unit>() {

    override suspend fun run(p: Unit): Group? {
        return repository.getActiveGroup()
    }

    override suspend fun getActiveGroup(): Group? {
        return run(Unit)
    }
}