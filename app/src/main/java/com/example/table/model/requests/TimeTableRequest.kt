package com.example.table.model.requests

import com.example.table.model.db.Group

data class TimeTableRequest(
    val typeSchedule: Int,
    val group: Group)
