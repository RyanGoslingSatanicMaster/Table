package com.example.table.usecases

import com.example.table.model.db.Group

interface IIsGroupInDbUseCase {
    suspend fun isGroupInDb(group: Group): Group
}