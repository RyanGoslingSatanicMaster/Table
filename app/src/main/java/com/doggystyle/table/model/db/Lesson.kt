package com.doggystyle.table.model.db

import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.util.*
@Entity(indices = [
    Index(value = ["lesson_name", "is_lection", "group"], unique = true)
], foreignKeys = [
    ForeignKey(entity = Group::class,
        parentColumns = ["groupId"],
        childColumns = ["group"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
])
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    val lessonId: Long = 0,
    @ColumnInfo(name = "lesson_name")
    val lessonName: String,
    @ColumnInfo(name = "is_lection")
    val isLection: Boolean,
    @ColumnInfo(name = "group")
    val group: Long
){
    override fun equals(other: Any?): Boolean {
        return if (other is Lesson)
            lessonName == other.lessonName &&
            isLection == other.isLection
        else
            false
    }
}
