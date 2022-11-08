package com.example.table.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Index

@Entity(primaryKeys = ["lessonId", "teacherId"], foreignKeys = [
    ForeignKey(entity = Lesson::class, parentColumns = ["lessonId"], childColumns = ["lessonId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
    ForeignKey(entity = Teacher::class, parentColumns = ["teacherId"], childColumns = ["teacherId"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
], indices = [
    Index(value = ["lessonId", "teacherId"], unique = true)
])
data class LessonTeacherCrossRef(
    val lessonId: Long,
    val teacherId: Long
)
