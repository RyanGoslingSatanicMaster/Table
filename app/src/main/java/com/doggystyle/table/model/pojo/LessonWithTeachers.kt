package com.doggystyle.table.model.pojo

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.db.Lesson
import com.doggystyle.table.model.db.Teacher
import com.doggystyle.table.model.db.LessonTeacherCrossRef

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
){
    override fun equals(other: Any?): Boolean {
        return if (other is LessonWithTeachers)
            lesson == other.lesson &&
            teachers.compare(other.teachers) &&
            group == other.group
        else
            false
    }

    fun List<Teacher>.compare(other: List<Teacher>): Boolean{
        forEach { teacher ->
            if (other.filter { it == teacher }.isEmpty())
                return false
        }
        return true
    }
}
