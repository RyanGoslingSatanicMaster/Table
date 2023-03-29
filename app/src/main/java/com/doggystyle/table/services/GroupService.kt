package com.doggystyle.table.services

import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.requests.GroupRequest
import com.doggystyle.table.model.pojo.GroupWrapper
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

    override suspend fun getSavedGroups(): List<Group> {
        return dao.getAllGroup()
    }
}
