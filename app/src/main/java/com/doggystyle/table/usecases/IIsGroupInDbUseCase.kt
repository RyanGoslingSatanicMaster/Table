package com.doggystyle.table.usecases

import com.doggystyle.table.model.db.Group

interface IIsGroupInDbUseCase {
    suspend fun isGroupInDb(group: Group): Group
}
