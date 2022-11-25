package com.example.table.datasource.local.dao

import android.util.Log
import androidx.room.*
import com.example.table.model.db.*
import com.example.table.model.pojo.LessonWithTeachers
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.model.requests.NextLessonRequest
import kotlinx.coroutines.flow.Flow
import java.util.*

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

    @Query("SELECT * FROM `Group`")
    suspend fun getAllGroup(): List<Group>

    @Query("SELECT * FROM `Group` WHERE groupId LIKE :groupId")
    suspend fun getGroupById(groupId: Long): Group

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

    @Transaction
    suspend fun updateActiveGroup(group: Group): Group{
        val oldGroup = getGroupByName(group.groupName)
        if (!oldGroup.isActive && group.isActive) {
            deactivateAllGroup()
            updateGroup(oldGroup.copy(isActive = true))
            return oldGroup.copy(isActive = true)
        }
        else
            return oldGroup
    }
    /** Teacher */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTeacher(teacher: Teacher): Long

    @Update
    suspend fun updateTeacher(teacher: Teacher): Int

    @Query("SELECT teacherId FROM Teacher WHERE teacher_name LIKE :teacherName AND url LIKE :url")
    suspend fun getTeacherId(teacherName: String, url: String): Long

    @Query("SELECT * FROM Teacher")
    suspend fun getAllTeacher(): List<Teacher>

    @Query("DELETE FROM Teacher WHERE teacherId IN (:teacherIds)")
    suspend fun deleteTeacherByTeacherId(teacherIds: List<Long>)

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

    @Query("DELETE FROM lesson WHERE lessonId IN (:lessonIds)")
    suspend fun deleteLessonsById(lessonIds: List<Long>)

    @Query("SELECT lessonId FROM lesson WHERE lesson_name LIKE :lessonName AND is_lection LIKE :isLection")
    suspend fun getLessonId(lessonName: String, isLection: Int): Long

    @Query("SELECT lessonId FROM lesson WHERE `group` LIKE :groupId")
    suspend fun getLessonIdByGroupId(groupId: Long): List<Long>

    @Query("SELECT * FROM lesson")
    suspend fun getAllLesson(): List<Lesson>

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

    @Query("SELECT * FROM timetable")
    suspend fun getAllTimeTable(): List<TimeTable>

    @Query("DELETE FROM timetable WHERE lesson IN (:lessonId)")
    suspend fun deleteTimeTable(lessonId: List<Long>)

   /* @Query("SELECT time, is_lection FROM timetable, lesson" +
            " WHERE timetable.time LIKE '%' || :day || '%' AND timetable.is_first_week LIKE :isFirstWeek " +
            "AND timetable.lesson LIKE lesson.lessonId AND lesson.`group` LIKE :groupId AND lesson.is_lection LIKE 1")
    suspend fun getNextLectionTime(day: String, groupId: Long, isFirstWeek: Boolean): List<Pair<Date, Boolean>>

    @Query("SELECT time FROM timetable, lesson" +
            " WHERE timetable.time LIKE '%' || :day || '%' AND timetable.is_first_week LIKE :isFirstWeek " +
            "AND timetable.lesson LIKE lesson.lessonId AND lesson.`group` LIKE :groupId AND lesson.is_lection LIKE 0")
    suspend fun getNextPracticeTime(day: String, groupId: Long, isFirstWeek: Boolean): List<Date>*/

    @Query("SELECT * FROM timetable, lesson" +
            " WHERE timetable.time LIKE '%' || :day || '%' AND timetable.is_first_week LIKE :isFirstWeek " +
            "AND timetable.lesson LIKE lesson.lessonId AND lesson.`group` LIKE :groupId")
    suspend fun getNextAllLessonTime(day: String, groupId: Long, isFirstWeek: Boolean): List<TimeTableWithLesson>

    @Transaction
    suspend fun getNextDayLessonsTime(groupName: String, isFirstWeek: Boolean, day: String): List<TimeTableWithLesson>{
        val group = getGroupByName(groupName)
        return getNextAllLessonTime(day, group.groupId, isFirstWeek)
    }

    /** LessonTeacherCrossRef */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLessTeachCrossRef(crossRef: LessonTeacherCrossRef): Long

    @Update
    suspend fun updateLessTeacherCrossRef(crossRef: LessonTeacherCrossRef): Int

    @Query("DELETE FROM LessonTeacherCrossRef WHERE lessonId IN (:lessonId)")
    suspend fun deleteCrossRefByLessonId(lessonId: List<Long>)

    @Query("SELECT teacherId FROM LessonTeacherCrossRef WHERE lessonId IN (:lessonId)")
    suspend fun getTeacherIdsByLessonId(lessonId: List<Long>): List<Long>

    @Query("SELECT teacherId FROM LessonTeacherCrossRef WHERE lessonId LIKE :lessonId AND teacherId LIKE :teacherId")
    suspend fun getCrossRefId(lessonId: Long, teacherId: Long): Long

    @Query("SELECT * FROM LessonTeacherCrossRef")
    suspend fun getAllCrossRef(): List<LessonTeacherCrossRef>

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
        val lessonId = getLessonIdByGroupId(groupId)
        val teacherId = getTeacherIdsByLessonId(lessonId)
        deleteTimeTable(lessonId)
        deleteCrossRefByLessonId(lessonId)
        deleteTeacherByTeacherId(teacherId)
        deleteLessonsById(lessonId)
        deleteGroup(groupId)
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
    suspend fun saveAllTimeTableWithLesson(list: List<TimeTableWithLesson>): Group?{
        list.forEach {
            saveTimeTableWithLesson(it)
        }
        return list.get(0).let {
            getGroupByName(it.lesson.group!!.groupName)
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