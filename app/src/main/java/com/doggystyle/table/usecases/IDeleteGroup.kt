package com.doggystyle.table.usecases

import com.doggystyle.table.model.db.Group

interface IDeleteGroup {
    suspend fun deleteGroupData(group: Group)
}
