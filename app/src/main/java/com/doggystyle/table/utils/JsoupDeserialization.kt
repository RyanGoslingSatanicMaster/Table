package com.doggystyle.table.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.doggystyle.table.model.db.Group
import com.doggystyle.table.model.db.Lesson
import com.doggystyle.table.model.db.Teacher
import com.doggystyle.table.model.db.TimeTable
import com.doggystyle.table.model.pojo.LessonWithTeachers
import com.doggystyle.table.model.pojo.TimeTableWithLesson
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*
import kotlin.streams.toList

@RequiresApi(Build.VERSION_CODES.O)
fun timeTableDeserialization(html: String, group: Group): List<TimeTableWithLesson> {
    val doc = Jsoup.parse(html)
    val firstWeek = doc.getElementById("first")
    val secondWeek = doc.getElementById("second")
    val updatedGroup = group.copy(dateOfFirstWeek = doc.getElementsByClass("h2-responsive").first()?.getDateOfFirstWeek())
    return firstWeek?.timeTableWeek(true, updatedGroup)!!.toMutableList().apply {
        secondWeek?.timeTableWeek(false, updatedGroup)?.let { addAll(it) }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun Element.getDateOfFirstWeek(): Date {
    if(this.text().contains("1-я неделя"))
        return Date()
    else {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.DATE, -7)
        return cal.time
    }
}


fun Element.timeTableWeek(isFirstWeek: Boolean, group: Group): List<TimeTableWithLesson>{
    val list = this.getElementsByClass("card-block")
    val timeTableList = mutableListOf<TimeTableWithLesson>()
    list.forEach {
        timeTableList.addAll(timeTableDay(it, group, isFirstWeek))
    }
    return timeTableList
}


fun timeTableDay(el: Element, group: Group, isFirstWeek: Boolean): List<TimeTableWithLesson>{

    val day = el.select("h4").first()?.text()?.substringBefore('|')?.replace(" ", "")

    val timeTableList = mutableListOf<TimeTableWithLesson>()
    val lessonList: List<Element>
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        lessonList = el.select("tr").stream().filter { it.getElementsByClass("diss").text().isNotEmpty() }.toList()

    else {
        val intermList = el.select("tr").toList()
        lessonList = mutableListOf<Element>()
        intermList.forEach { lesson ->
            if(lesson.getElementsByClass("diss").text().isNotEmpty())
                lessonList.add(lesson)
        }
    }

    lessonList.forEach {
        val date = ConverterUtils.parseDateWithPrefix(day + ", " + it.getElementsByClass("time").text().substringBefore('<'))
        var link : String? = null
        var link2: String? = null
        if (it.getElementsByClass("webex").isNotEmpty())
            link = it.getElementsByClass("webex").first()?.attr("href")
        if (it.getElementsByClass("webex").size > 1)
            link2 = it.getElementsByClass("webex").component2()?.attr("href")
        val lesson = getLesson(it.getElementsByClass("diss").first()!!,
            it.getElementsByClass("yes").first(), group)
        val cabinet = it.getElementsByClass("who-where").first()?.text()
        timeTableList.add(TimeTableWithLesson(lesson = lesson,
            timeTable = TimeTable(cabinet = cabinet,
                time = date,
                isFirstWeek = isFirstWeek,
                lessonId = null,
                firstLink = link,
                secondLink = link2
            )
        ))
    }
    return timeTableList.toList()
}

fun getLesson(el: Element, isLection: Element?, group: Group): LessonWithTeachers {
    val name = if (el.ownText().isNotEmpty()) el.ownText() else el.select("strong").text()
    val teachers = getTeachers(el.select("span").select("a").toList())
    return LessonWithTeachers(Lesson(lessonName = name, isLection = isLection != null, group = 0), teachers, group)
}

fun getTeachers(els: List<Element>): List<Teacher>{
    val teacherList = mutableListOf<Teacher>()
    els.forEach {
        teacherList.add(Teacher(teacherName = it.text(), url = it.attr("href")))
    }
    return teacherList.toList()
}
