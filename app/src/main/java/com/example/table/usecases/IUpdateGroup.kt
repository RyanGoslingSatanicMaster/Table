package com.example.table.usecases

import com.example.table.model.db.Group

interface IUpdateGroup {
    suspend fun updateGroup(group: Group): Group
}