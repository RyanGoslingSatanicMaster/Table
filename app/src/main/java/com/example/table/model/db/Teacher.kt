package com.example.table.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(indices = [
    Index(value = ["teacher_name", "url"], unique = true)
])
data class Teacher(
    @PrimaryKey(autoGenerate = true)
    val teacherId: Long = 0,
    @ColumnInfo(name = "teacher_name")
    val teacherName: String,
    @ColumnInfo(name = "url")
    val url: String
){
    override fun equals(other: Any?): Boolean {
        return if (other is Teacher)
            teacherName == other.teacherName &&
            url == other.url
        else
            false
    }
}
