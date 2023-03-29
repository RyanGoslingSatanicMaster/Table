package com.doggystyle.table.services

import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.requests.GroupRequest
import com.doggystyle.table.model.pojo.GroupWrapper


interface IGroupService {
    suspend fun getGroups(groupRequest: GroupRequest): GroupWrapper
    suspend fun isGroupInDb(group: Group): Group
    suspend fun deleteGroupData(group: Group)
    suspend fun getActiveGroup(): Group?
    suspend fun updateGroup(group: Group): Group
    suspend fun getSavedGroups(): List<Group>
}
