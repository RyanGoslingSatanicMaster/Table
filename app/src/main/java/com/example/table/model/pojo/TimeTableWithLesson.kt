package com.example.table.model.pojo

import androidx.room.Embedded
import androidx.room.Relation
import com.example.table.model.db.Group
import com.example.table.model.db.Lesson
import com.example.table.model.db.TimeTable
import java.util.*

data class TimeTableWithLesson(
    @Relation(
        entity = Lesson::class,
        parentColumn = "lesson",
        entityColumn = "lessonId"
    )
    val lesson: LessonWithTeachers,
    @Embedded
    val timeTable: TimeTable,
){
    override fun equals(other: Any?): Boolean {
        return if (other is TimeTableWithLesson)
            lesson == other.lesson &&
            timeTable == other.timeTable
        else
            false
    }
}
