package com.doggystyle.table.usecases

import com.doggystyle.table.model.db.Group

interface IGetActiveGroup {
    suspend fun getActiveGroup(): Group?
}
