package com.doggystyle.table.usecases

import com.doggystyle.table.model.db.Group
import com.doggystyle.table.repositories.IGroupRepository
import javax.inject.Inject

class IsGroupInDb @Inject constructor(val repository: IGroupRepository): UseCase<Group, Group>(), IIsGroupInDb {

    override suspend fun isGroupInDb(group: Group): Group {
        return run(group)
    }

    override suspend fun run(p: Group): Group {
        return repository.isGroupInDb(p)
    }
}
