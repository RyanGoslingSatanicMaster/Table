package com.example.table.components.broadcasts

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.table.components.RefreshCallback
import com.example.table.components.TimeTableWidget
import com.example.table.di.components.DaggerWidgetComponent
import com.example.table.di.components.WidgetComponent
import com.example.table.di.modules.RoomModule
import com.example.table.di.modules.WidgetModule
import com.example.table.usecases.IGetDayWidget
import com.example.table.utils.Constant
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TimeTableWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = TimeTableWidget()

    @Inject
    lateinit var getDayWidget: IGetDayWidget

    @Inject
    lateinit var gson: Gson

    lateinit var widgetComponent: WidgetComponent

    private val scope = MainScope()

    companion object{
        val dayName = stringPreferencesKey("day")
        val listTimeTable = stringPreferencesKey("timeTableList")
        val indexKey = stringPreferencesKey("indexKey")
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        widgetComponent = DaggerWidgetComponent.builder()
            .widgetModule(WidgetModule(context.applicationContext))
            .roomModule(RoomModule(context.applicationContext))
            .build()
        widgetComponent.inject(this)
        observeData(-1, context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        widgetComponent = DaggerWidgetComponent.builder()
            .widgetModule(WidgetModule(context.applicationContext))
            .roomModule(RoomModule(context.applicationContext))
            .build()
        widgetComponent.inject(this)
        if (intent.action == RefreshCallback.UPDATE_ACTION) {
            val index = intent.getIntExtra("index", -1)
            observeData(index, context)
        }
    }

    private fun observeData(index: Int, context: Context){
        scope.launch {
            val day = getDayWidget.getActiveDayWidget(index)
            val glanceId =
                GlanceAppWidgetManager(context).getGlanceIds(TimeTableWidget::class.java).firstOrNull()
            val today = try {
                day?.timeTableList?.get(0)?.timeTable?.time?.day
            }catch (ex: Exception){
                null
            }
            if (today==null && index == -1)
                observeData(1, context)
            glanceId?.let {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                    if (day != null)
                        pref.toMutablePreferences().apply {
                            this[dayName] = day.let { it.day }
                            this[listTimeTable] = gson.toJson(day?.let { it.timeTableList }?:"")
                            this[indexKey] = if (index == -1) today.toString() else index.toString()
                        }
                    else
                        pref.toMutablePreferences().apply {
                            this[dayName] = ""
                            this[listTimeTable] = ""
                            this[indexKey] = if (index == -1) today.toString() else index.toString()
                        }
                }
                glanceAppWidget.update(context, it)
            }
        }
    }
}
