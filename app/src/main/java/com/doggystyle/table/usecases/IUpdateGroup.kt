package com.doggystyle.table.usecases

import com.doggystyle.table.model.db.Group

interface IUpdateGroup {
    suspend fun updateGroup(group: Group): Group
}
