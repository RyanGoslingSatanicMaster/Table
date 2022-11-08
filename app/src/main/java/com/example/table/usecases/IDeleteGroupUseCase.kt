package com.example.table.usecases

import com.example.table.model.db.Group

interface IDeleteGroupUseCase {
    suspend fun deleteGroupData(group: Group)
}