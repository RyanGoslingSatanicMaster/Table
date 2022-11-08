package com.example.table.datasource.local.dao

import android.util.Log
import androidx.room.*
import com.example.table.model.db.*
import com.example.table.model.pojo.LessonWithTeachers
import com.example.table.model.pojo.TimeTableWithLesson
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeTableDao {

    /** Group */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroup(group: Group): Long

    @Query("DELETE FROM `group` WHERE groupId LIKE :groupId")
    suspend fun deleteGroup(groupId: Long)

    @Update
    suspend fun updateGroup(group: Group): Int

    @Query("SELECT groupId FROM `group` WHERE group_name LIKE :groupName")
    suspend fun getGroupId(groupName: String): Long

    @Query("SELECT * FROM `Group` WHERE is_active LIKE 1")
    suspend fun getActiveGroup(): Group

    @Query("SELECT * FROM `Group` WHERE group_name = :groupName")
    suspend fun getGroupByName(groupName: String): Group

    @Query("UPDATE `Group` SET is_active = 0 WHERE is_active = 1")
    suspend fun deactivateAllGroup()

    @Transaction
    suspend fun saveGroup(group: Group): Long{
        val oldGroup = getGroupByName(group.groupName)
        if (oldGroup == null){
            if (group.isActive == true)
                deactivateAllGroup()
            return insertGroup(group)
        }
        else{
            if (!oldGroup.isActive && group.isActive)
                deactivateAllGroup()
            updateGroup(group)
            return oldGroup.groupId
        }
    }

    /** Teacher */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTeacher(teacher: Teacher): Long

    @Update
    suspend fun updateTeacher(teacher: Teacher): Int

    @Query("SELECT teacherId FROM Teacher WHERE teacher_name LIKE :teacherName AND url LIKE :url")
    suspend fun getTeacherId(teacherName: String, url: String): Long

    @Delete
    suspend fun deleteTeacher(vararg teacher: Teacher)

    @Transaction
    suspend fun saveTeacher(teacher: Teacher, withUpdate: Boolean): Long {
        val id = insertTeacher(teacher)
        return if (id == -1L)
                if (withUpdate)
                    updateTeacher(teacher).toLong()
                else
                    getTeacherId(teacher.teacherName, teacher.url)
            else
                id
    }

    /** Lesson */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLesson(lesson: Lesson): Long

    @Update
    suspend fun updateLesson(lesson: Lesson): Int

    @Query("SELECT lessonId FROM lesson WHERE lesson_name LIKE :lessonName AND is_lection LIKE :isLection")
    suspend fun getLessonId(lessonName: String, isLection: Int): Long

    @Query("SELECT lessonId FROM lesson WHERE `group` LIKE :groupId")
    suspend fun getLessonIdByGroupId(groupId: Long): List<Long>

    @Transaction
    suspend fun saveLesson(lesson: Lesson, withUpdate: Boolean): Long {
        val id = insertLesson(lesson)
        return if (id == -1L)
            if (withUpdate)
                updateLesson(lesson).toLong()
            else
                getLessonId(lesson.lessonName, if (lesson.isLection) 1 else 0)
        else
            id
    }

    /** TimeTable */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimeTable(timeTable: TimeTable): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTimeTable(list: List<TimeTable>)

    @Query("SELECT * FROM timetable WHERE lesson IN (:lessonIds)")
    suspend fun getTimeTableByLessonId(lessonIds: List<Long>): List<TimeTableWithLesson>

    @Query("DELETE FROM timetable WHERE lesson IN (:lessonId)")
    suspend fun deleteTimeTable(lessonId: List<Long>)

    /** LessonTeacherCrossRef */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLessTeachCrossRef(crossRef: LessonTeacherCrossRef): Long

    @Update
    suspend fun updateLessTeacherCrossRef(crossRef: LessonTeacherCrossRef): Int

    @Query("SELECT teacherId FROM LessonTeacherCrossRef WHERE lessonId LIKE :lessonId AND teacherId LIKE :teacherId")
    suspend fun getCrossRefId(lessonId: Long, teacherId: Long): Long

    @Transaction
    suspend fun saveLessonTeacherCrossRef(crossRef: LessonTeacherCrossRef, withUpdate: Boolean): Long {
        val id = insertLessTeachCrossRef(crossRef)
        return if (id == -1L)
            if (withUpdate)
                updateLessTeacherCrossRef(crossRef).toLong()
            else
                getCrossRefId(crossRef.lessonId, crossRef.teacherId)
        else
            id
    }

    /** TimeTableWithLesson */

    @Transaction
    suspend fun getTimeTable(groupId: Long): List<TimeTableWithLesson>{
       val Ids = getLessonIdByGroupId(groupId)
        return getTimeTableByLessonId(Ids)
    }

    @Transaction
    suspend fun deleteTimeTableOfGroup(groupId: Long){
        val Ids = getLessonIdByGroupId(groupId)
        deleteTimeTable(Ids)
    }

    @Transaction
    suspend fun getActiveTimeTable(): List<TimeTableWithLesson>{
        val group = getActiveGroup()
        return getTimeTable(group.groupId)
    }

    @Transaction
    suspend fun getGroupTimeTable(groupRequest: Group): List<TimeTableWithLesson>{
        val group = getGroupByName(groupRequest.groupName)
        return getTimeTable(group.groupId)
    }

    @Transaction
    suspend fun saveAllTimeTableWithLesson(list: List<TimeTableWithLesson>){
        list.forEach {
            saveTimeTableWithLesson(it)
        }
    }

    @Transaction
    suspend fun saveTimeTableWithLesson(timeTableWithLesson: TimeTableWithLesson){
        var groupId =  saveGroup(timeTableWithLesson.lesson.group!!)
        val lessonId = insertLessonWithTeachers(timeTableWithLesson.lesson, groupId = groupId)
        insertTimeTable(TimeTable(
            cabinet = timeTableWithLesson.timeTable.cabinet,
            time = timeTableWithLesson.timeTable.time,
            lessonId = lessonId,
            isFirstWeek = timeTableWithLesson.timeTable.isFirstWeek
        ))
    }

    @Transaction
    suspend fun insertLessonWithTeachers(lessonWithTeachers: LessonWithTeachers, groupId: Long): Long{
        val lessonId = saveLesson(
            Lesson(
                lessonName = lessonWithTeachers.lesson.lessonName,
                isLection = lessonWithTeachers.lesson.isLection,
                group = groupId
        ), false)
        lessonWithTeachers.teachers.forEach {
            val teacherId = saveTeacher(it, false)
            saveLessonTeacherCrossRef(LessonTeacherCrossRef(lessonId, teacherId), false)
        }
        return lessonId
    }
}