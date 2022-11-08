package com.example.table.usecases

import com.example.table.model.db.Group
import com.example.table.model.requests.GroupRequest
import com.example.table.repositories.IGroupRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupUseCase @Inject constructor(private val repository: IGroupRepository): UseCase<List<Group>, GroupRequest>(), IGroupUseCase {

    override suspend fun run(p: GroupRequest): List<Group>? {
        return repository.getGroups(p)
    }

    override suspend fun getGroup(groupRequest: GroupRequest): List<Group>? {
        return run(groupRequest)
    }
}