package com.doggystyle.table.usecases

import com.doggystyle.table.model.db.Group
import com.doggystyle.table.repositories.IGroupRepository
import javax.inject.Inject

class DeleteGroup @Inject constructor(val repository: IGroupRepository): IDeleteGroup, UseCase<Unit, Group>() {

    override suspend fun run(p: Group) {
        return repository.deleteGroupData(p)
    }

    override suspend fun deleteGroupData(group: Group) {
        return run(group)
    }

}
