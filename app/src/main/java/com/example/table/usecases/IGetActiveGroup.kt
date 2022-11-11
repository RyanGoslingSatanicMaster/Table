package com.example.table.usecases

import com.example.table.model.db.Group

interface IGetActiveGroup {
    suspend fun getActiveGroup(): Group?
}