package com.example.table.model.db

import androidx.annotation.Nullable
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(indices = [
        Index(value = ["cabinet", "time", "lesson", "is_first_week"],
            unique = true
    )],
    foreignKeys = [
    ForeignKey(entity = Lesson::class,
        parentColumns = ["lessonId"],
        childColumns = ["lesson"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
])
data class TimeTable(
    @PrimaryKey(autoGenerate = true)
    val timeTableId: Long = 0,
    @ColumnInfo(name ="cabinet")
    val cabinet: String? = null,
    @ColumnInfo(name = "time")
    val time: Date,
    @ColumnInfo(name = "lesson")
    val lessonId: Long? = null,
    @ColumnInfo(name = "is_first_week")
    val isFirstWeek: Boolean,
){
    override fun equals(other: Any?): Boolean {
        return if (other is TimeTable)
            cabinet == other.cabinet &&
            time == other.time &&
            isFirstWeek == other.isFirstWeek
        else
            false
    }
}
