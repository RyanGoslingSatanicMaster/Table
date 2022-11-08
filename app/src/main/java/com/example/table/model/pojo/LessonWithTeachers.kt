package com.example.table.model.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.table.model.db.Group
import com.example.table.model.db.Lesson
import com.example.table.model.db.Teacher
import com.example.table.model.db.LessonTeacherCrossRef

data class LessonWithTeachers(
    @Embedded
    val lesson: Lesson,
    @Relation(
        parentColumn = "lessonId",
        entityColumn = "teacherId",
        associateBy = Junction(LessonTeacherCrossRef::class)
    )
    val teachers: List<Teacher>,
    @Relation(
        entity = Group::class,
        parentColumn = "group",
        entityColumn = "groupId"
    )
    val group: Group? = null
)
