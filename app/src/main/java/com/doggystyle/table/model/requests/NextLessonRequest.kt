package com.doggystyle.table.model.requests

import com.doggystyle.table.model.db.Group
import java.util.*

data class NextLessonRequest(
    val notify: Triple<Boolean, Boolean, Int>,
    val group: Group,
    val date: Date = Date()
)
