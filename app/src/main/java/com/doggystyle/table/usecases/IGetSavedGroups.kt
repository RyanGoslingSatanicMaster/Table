package com.doggystyle.table.usecases

import com.doggystyle.table.model.db.Group

interface IGetSavedGroups {
    suspend fun getSavedGroups(): List<Group>
}
