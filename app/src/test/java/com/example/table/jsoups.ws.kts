import androidx.annotation.Nullable
import androidx.room.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.BufferedReader
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.streams.toList

timeTableDeserialization(Group(groupName = "ИТ1901")).forEach {
    println(it)
}

val formatter = SimpleDateFormat("EEE, HH:mm", Locale("ru"))

fun timeTableDeserialization(group: Group): List<TimeTableWithLesson> {
    val bufferedReader: BufferedReader = File("C:\\Users\\vince\\AndroidStudioProjects\\Table\\app\\src\\main\\java\\com\\example\\table\\test.html").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }
    val doc = Jsoup.parse(inputString)
    val firstWeek = doc.getElementById("first")
    val secondWeek = doc.getElementById("second")
    return firstWeek?.timeTableWeek(true, group)!!.toMutableList().apply {
        secondWeek?.timeTableWeek(false, group)?.let { addAll(it) }
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

    val lessonList = el.select("tr").stream().filter { it.getElementsByClass("diss").text().isNotEmpty() }.toList()

    lessonList.forEach {
        val date = formatter.parse(day + ", " + it.getElementsByClass("time").text().substringBefore('<'))
        val lesson = getLesson(it.getElementsByClass("diss").first()!!,
            it.getElementsByClass("yes").first())
        val cabinet = it.getElementsByClass("who-where").first()?.text()
        timeTableList.add(TimeTableWithLesson(lesson = lesson, timeTable = TimeTable(cabinet = cabinet, time = date!!, isFirstWeek = isFirstWeek), group = group))
    }
    return timeTableList.toList()
}

fun getLesson(el: Element, isLection: Element?): LessonWithTeachers{
    val name = el.ownText()
    println(name)
    val teachers = getTeachers(el.select("span").select("a").toList())
    return LessonWithTeachers(Lesson(lessonName = name, isLection = isLection != null), teachers)
}

fun getTeachers(els: List<Element>): List<Teacher>{
    val teacherList = mutableListOf<Teacher>()
    els.forEach {
        teacherList.add(Teacher(teacherName = it.text(), url = it.attr("href")))
    }
    return teacherList.toList()
}

data class TimeTable(
    val timeTableId: Long = 0,
    val cabinet: String? = null,
    val time: Date,
    val lessonId: Long? = null,
    val isFirstWeek: Boolean,
    val group: Long = 0
)

data class TimeTableWithLesson(
    val lesson: LessonWithTeachers,
    val timeTable: TimeTable,
    val group: Group? = null
)

data class Lesson(
    val lessonId: Long = 0,
    val lessonName: String,
    val isLection: Boolean
)

data class LessonWithTeachers(
    val lesson: Lesson,
    val teachers: List<Teacher>
)

data class Teacher(
    val teacherId: Long = 0,
    val teacherName: String,
    val url: String
)

data class Group(
    val groupId: Long = 0,
    val groupName: String
)

fun comp(a: IntArray?, b: IntArray?): Boolean{
    var flag = true
    b?.forEach { el ->
        if (a?.filter { el == it*it }?.isEmpty() == true)
            flag = false
    }
    if (b == null)
        flag = false
    return flag
}

