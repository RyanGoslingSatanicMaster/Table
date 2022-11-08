package com.example.table.usecases

import com.example.table.model.db.Group
import com.example.table.model.requests.GroupRequest

interface IGroupUseCase {
    suspend fun getGroup(groupRequest: GroupRequest): List<Group>?
}