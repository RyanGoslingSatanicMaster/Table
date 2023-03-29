package com.doggystyle.table.usecases

import com.doggystyle.table.model.db.Group

interface IIsGroupInDb {
    suspend fun isGroupInDb(group: Group): Group
}
