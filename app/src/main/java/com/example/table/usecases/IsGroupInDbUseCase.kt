package com.example.table.usecases

import com.example.table.model.db.Group
import com.example.table.repositories.GroupRepository
import com.example.table.repositories.IGroupRepository
import javax.inject.Inject

class IsGroupInDbUseCase @Inject constructor(val repository: IGroupRepository): UseCase<Group, Group>(), IIsGroupInDbUseCase {

    override suspend fun isGroupInDb(group: Group): Group {
        return run(group)
    }

    override suspend fun run(p: Group): Group {
        return repository.isGroupInDb(p)
    }
}