package com.doggystyle.table.usecases

import com.doggystyle.table.model.db.Group
import com.doggystyle.table.repositories.GroupRepository
import com.doggystyle.table.repositories.IGroupRepository
import javax.inject.Inject

class IsGroupInDbUseCase @Inject constructor(val repository: IGroupRepository): UseCase<Group, Group>(), IIsGroupInDbUseCase {

    override suspend fun isGroupInDb(group: Group): Group {
        return run(group)
    }

    override suspend fun run(p: Group): Group {
        return repository.isGroupInDb(p)
    }
}
