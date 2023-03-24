package com.example.table.usecases

import com.example.table.model.db.Group
import com.example.table.repositories.IGroupRepository
import javax.inject.Inject

class GetSavedGroups @Inject constructor(private val repository: IGroupRepository): IGetSavedGroups, UseCase<List<Group>, Unit>() {

    override suspend fun getSavedGroups(): List<Group> {
        return run(Unit) ?: listOf()
    }

    override suspend fun run(p: Unit): List<Group>? {
        return repository.getSavedGroups()
    }
}
