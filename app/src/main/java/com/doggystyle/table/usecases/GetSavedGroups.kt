package com.doggystyle.table.usecases

import com.doggystyle.table.model.db.Group
import com.doggystyle.table.repositories.IGroupRepository
import javax.inject.Inject

class GetSavedGroups @Inject constructor(private val repository: IGroupRepository): IGetSavedGroups, UseCase<List<Group>, Unit>() {

    override suspend fun getSavedGroups(): List<Group> {
        return run(Unit) ?: listOf()
    }

    override suspend fun run(p: Unit): List<Group>? {
        return repository.getSavedGroups()
    }
}
