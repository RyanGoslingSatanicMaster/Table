package com.example.table.services

import com.example.table.model.db.Group
import com.example.table.model.requests.GroupRequest
import com.example.table.model.pojo.GroupWrapper


interface IGroupService {
    suspend fun getGroups(groupRequest: GroupRequest): GroupWrapper
    suspend fun isGroupInDb(group: Group): Group
    suspend fun deleteGroupData(group: Group)
    suspend fun getActiveGroup(): Group?
    suspend fun updateGroup(group: Group): Group
}