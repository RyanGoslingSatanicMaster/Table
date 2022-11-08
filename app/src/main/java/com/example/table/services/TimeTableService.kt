package com.example.table.services

import android.util.Log
import com.example.table.model.db.*
import com.example.table.model.pojo.LessonWithTeachers
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.model.requests.TimeTableRequest
import com.example.table.utils.timeTableDeserialization
import javax.inject.Inject

class TimeTableService @Inject constructor(): ITimeTableService, ApiService() {

    override suspend fun getTimeTable(request: TimeTableRequest){
        return executeAndSave(null, suspend {
            api.getTimeTable(request.group.groupName, request.typeSchedule)
        } to {
            val timeTable = timeTableDeserialization(it.string(), request.group)
            dao.saveAllTimeTableWithLesson(timeTable)
        })
    }

    override suspend fun getTimeTableActiveGroup(): List<TimeTableWithLesson> {
        return dao.getActiveTimeTable()
    }

    override suspend fun getTimeTableGroup(group: Group): List<TimeTableWithLesson> {
        return dao.getGroupTimeTable(group)
    }

    override suspend fun getIndexOfWeek(request: TimeTableRequest) {

    }
    /*
    // I know it's not beautiful, but for me important work with as less as possible count of models
    // and i don't want change fields of data class to var!!!!!!!!!!
    private suspend fun TimeTableWithLesson.save(): TimeTable{
        val groupId = this.group?.save()
        val lessonId = this.lesson?.save()
        return TimeTable(cabinet = this.timeTable.cabinet,
            time = this.timeTable.time,
            lessonId = lessonId!!,
            isFirstWeek = this.timeTable.isFirstWeek,
            group = groupId!!
        )
    }

    private suspend fun Group.save(): Long{
        return dao.saveGroup(this)
    }

    private suspend fun LessonWithTeachers.save(): Long{
        val lessonId = this.lesson?.save()
        this.teachers?.forEach {
            dao.insertLessTeachCrossRef(LessonTeacherCrossRef(lessonId!!, it.save()))
        }
        return lessonId!!
    }

    private suspend fun Teacher.save(): Long{
        return dao.saveTeacher(this)

    }

    private suspend fun Lesson.save(): Long{
        return dao.saveLesson(this)
    }
*/

}