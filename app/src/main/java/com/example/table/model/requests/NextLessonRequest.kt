package com.example.table.model.requests

import com.example.table.model.db.Group
import java.util.*

data class NextLessonRequest(
    val notify: Triple<Boolean, Boolean, Int>,
    val group: Group,
    val date: Date = Date()
)
