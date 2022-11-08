package com.example.table.repositories

import com.example.table.model.db.Group
import com.example.table.model.requests.GroupRequest
import com.example.table.services.IGroupService
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
}