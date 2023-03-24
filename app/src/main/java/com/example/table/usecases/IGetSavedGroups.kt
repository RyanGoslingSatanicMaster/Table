package com.example.table.usecases

import com.example.table.model.db.Group

interface IGetSavedGroups {
    suspend fun getSavedGroups(): List<Group>
}
