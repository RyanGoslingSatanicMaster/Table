package com.doggystyle.table.usecases

import com.doggystyle.table.model.db.Group

interface IDeleteGroupUseCase {
    suspend fun deleteGroupData(group: Group)
}
