package com.doggystyle.table.repositories

import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.requests.GroupRequest

interface IGroupRepository {
    suspend fun getGroups(groupRequest: GroupRequest): List<Group>
    suspend fun isGroupInDb(group: Group): Group
    suspend fun deleteGroupData(group: Group)
    suspend fun getActiveGroup(): Group?
    suspend fun updateGroup(group: Group): Group
    suspend fun getSavedGroups(): List<Group>
}
