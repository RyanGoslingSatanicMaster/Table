package com.doggystyle.table.model.requests

import com.doggystyle.table.model.db.Group

data class TimeTableRequest(
    val typeSchedule: Int,
    val group: Group)
