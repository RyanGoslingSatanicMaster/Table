package com.doggystyle.table.components

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.text.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.doggystyle.table.R
import com.doggystyle.table.components.broadcasts.TimeTableWidgetReceiver
import com.doggystyle.table.model.pojo.TimeTableWithLesson
import com.doggystyle.table.utils.Constant
import com.doggystyle.table.utils.ConverterUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TimeTableWidget: GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition
    
    private val gson = Gson()

    private val type = object : TypeToken<List<TimeTableWithLesson>>() {}.type

    companion object{
        val countParamKey = ActionParameters.Key<Int>("count-key")
    }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val prefs = currentState<Preferences>()
        val index = prefs[TimeTableWidgetReceiver.indexKey]
        val day = prefs[TimeTableWidgetReceiver.dayName]
        val list = prefs[TimeTableWidgetReceiver.listTimeTable]
        WidgetLayout(day?:"", gson.fromJson(list, type)?: listOf(), index?.let { it.toInt() }?:0 )
    }
}

@Composable
fun WidgetLayout(day: String, list: List<TimeTableWithLesson>, index: Int){
    Column(modifier = GlanceModifier.fillMaxSize().background(ImageProvider(R.drawable.widget_background))) {
        WidgetHeader(day = day, index = index)
        WidgetBody(list = list)
    }
}

@Composable
fun WidgetHeader(day: String, index: Int){
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically) {
        Image(provider = ImageProvider(R.drawable.ic_left_arrow),
            contentDescription = null,
            modifier = GlanceModifier.size(40.dp).clickable(actionRunCallback<RefreshCallback>(
                parameters = actionParametersOf(TimeTableWidget.countParamKey to index - 1))
            )
        )
        Text(text = day, style = TextStyle(color = ColorProvider(Color.White), fontSize = 15.sp))
        Image(provider = ImageProvider(R.drawable.ic_right_arrow),
            contentDescription = null,
            modifier = GlanceModifier.size(40.dp).clickable(actionRunCallback<RefreshCallback>(
                parameters = actionParametersOf(TimeTableWidget.countParamKey to index + 1))
            )
        )
    }
}

@Composable
fun WidgetBody(list: List<TimeTableWithLesson>){
    LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
        items(list) {
            Row(modifier = GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalAlignment = Alignment.CenterVertically) {
                val rowWidth = with(LocalSize.current){
                    width
                }
                Text(modifier = GlanceModifier.width((rowWidth.value * 0.2).dp), text = ConverterUtils.formatterTime.format(it.timeTable.time), style = TextStyle(color = ColorProvider(Color.White), fontSize = 10.sp))
                Text(modifier = GlanceModifier.width((rowWidth.value * 0.5).dp), text = it.lesson.lesson.lessonName, style = TextStyle(color = ColorProvider(Color.White), fontSize = 10.sp))
                Text(modifier = GlanceModifier.width((rowWidth.value * 0.1).dp), text = if(it.lesson.lesson.isLection) "Л" else "П", style = TextStyle(color = ColorProvider(Color.White), fontSize = 10.sp))
                it.timeTable.cabinet?.let { it1 -> Text(modifier = GlanceModifier.width((rowWidth.value * 0.2).dp), text = it1, style = TextStyle(color = ColorProvider(Color.White), fontSize = 10.sp)) }
            }    
        }
    }
}

class RefreshCallback : ActionCallback {

    companion object {
        const val UPDATE_ACTION = "UPDATE_ACTION_WIDGET_TIMETABLE"
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val index = parameters[TimeTableWidget.countParamKey]?:0
        val intent = Intent(context, TimeTableWidgetReceiver::class.java).apply {
            action = UPDATE_ACTION
            putExtra("index", when{
                index < 0 -> 6
                index > 6 -> 0
                else -> {
                    index
                }
            })
        }
        context.sendBroadcast(intent)
    }
}
