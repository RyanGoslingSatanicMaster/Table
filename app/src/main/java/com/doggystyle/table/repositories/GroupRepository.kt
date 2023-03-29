package com.doggystyle.table.repositories

import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.requests.GroupRequest
import com.doggystyle.table.services.IGroupService
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GroupRepository @Inject constructor(private val service: IGroupService): IGroupRepository {
    override suspend fun getGroups(groupRequest: GroupRequest): List<Group> {
        return service.getGroups(groupRequest).list.take(7)
    }

    override suspend fun isGroupInDb(group: Group): Group {
        return service.isGroupInDb(group)
    }

    override suspend fun deleteGroupData(group: Group){
        return service.deleteGroupData(group)
    }

    override suspend fun getActiveGroup(): Group? {
        return service.getActiveGroup()
    }

    override suspend fun updateGroup(group: Group): Group {
        return service.updateGroup(group)
    }

    override suspend fun getSavedGroups(): List<Group> {
        return service.getSavedGroups()
    }
}
