package com.doggystyle.table.usecases

import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.requests.GroupRequest

interface IGetGroups {
    suspend fun getGroup(groupRequest: GroupRequest): List<Group>?
}
