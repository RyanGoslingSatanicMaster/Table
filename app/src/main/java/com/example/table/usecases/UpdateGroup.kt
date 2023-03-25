package com.example.table.usecases

import com.example.table.model.db.Group
import com.example.table.repositories.IGroupRepository
import javax.inject.Inject

class UpdateGroup @Inject constructor(private val repo: IGroupRepository): UseCase<Group, Group>(), IUpdateGroup {

    override suspend fun run(p: Group): Group {
        return repo.updateGroup(p)
    }

    override suspend fun updateGroup(group: Group): Group {
        return run(group)
    }
}
