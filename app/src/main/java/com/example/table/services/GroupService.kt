package com.example.table.services

import com.example.table.model.db.Group
import com.example.table.model.requests.GroupRequest
import com.example.table.model.pojo.GroupWrapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupService @Inject constructor(): ApiService(), IGroupService {
    override suspend fun getGroups(request: GroupRequest): GroupWrapper {
        return execute{
            api.getGroups(request.query, request.typeSchedule)
        }
    }


    override suspend fun isGroupInDb(group: Group): Group {
        return dao.getGroupByName(groupName = group.groupName)
    }

    override suspend fun deleteGroupData(group: Group){
        return dao.deleteGroup(group.groupId)
    }

    override suspend fun getActiveGroup(): Group?{
        return dao.getActiveGroup()
    }

    override suspend fun updateGroup(group: Group): Group {
        return dao.updateActiveGroup(group)
    }
}