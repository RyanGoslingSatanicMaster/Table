package com.example.table

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChangeIgnoreConsumed
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.table.di.DaggerViewModelFactory
import com.example.table.model.DayTimeTable
import com.example.table.model.WeekTimeTable
import com.example.table.model.db.Group
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.ui.AnimatedBackgroundGradient
import com.example.table.ui.PositionState
import com.example.table.ui.progressBar
import com.example.table.ui.theme.Primary
import com.example.table.ui.theme.TableTheme
import com.example.table.ui.theme.Typography
import com.example.table.ui.theme.blue
import com.example.table.utils.Constant
import com.example.table.utils.ConverterUtils
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import okhttp3.internal.toImmutableList
import org.intellij.lang.annotations.JdkConstants
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TimeTableFragment @Inject constructor() : Fragment() {

    @Inject
    lateinit var factory: DaggerViewModelFactory

    @Inject
    lateinit var currentDate: Date

    private lateinit var viewModel: TimeTableViewModel

    private lateinit var activityViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, factory).get(TimeTableViewModel::class.java)
        activityViewModel = (activity as MainActivity).viewModel
        return ComposeView(requireContext()).apply {
            setContent {
                TableTheme {
                    TimeTableLayout()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //viewModel.getTimeTable(activityViewModel.activeGroup.value!!)
        viewModel.getTimeTable(activityViewModel.activeGroup.value!!)
    }

    @Composable
    fun TimeTableLayout(){
        val list = viewModel.timeTable.observeAsState()
        if (list.value != null)
            Box(){
                AnimatedBackgroundGradient(
                    duration = 1200,
                    colors = Primary to blue
                ){ position, isTop ->
                    Crossfade(targetState = isTop) {
                        val startIndex by remember {
                            derivedStateOf {
                                if (it)
                                    list.value!!.first.days.indexOf(list.value!!.first.days.first { it.day == "Сегодня" }?:null)?:0
                                else
                                    list.value!!.second.days.indexOf(list.value!!.second.days.first { it.day == "Сегодня" }?:null)?:0
                            }
                        }

                        ShowTimeTable(
                            fullTimeTable = if (it) list.value!!.first else list.value!!.second,
                            positionState = position,
                            isFirstWeek = it,
                            startIndex = startIndex
                        )
                    }

                }
            }

    }
}


@Composable
fun ShowTimeTable(fullTimeTable: WeekTimeTable, positionState: PositionState, isFirstWeek: Boolean, startIndex: Int){
    val currentIndex = remember { mutableStateOf(
        startIndex
    ) }
    val upgrated = remember {
        mutableStateOf(false)
    }
    val element = remember {
        mutableStateOf(fullTimeTable.days.get(currentIndex.value))
    }
    when{
        positionState == PositionState.Previous && !upgrated.value -> {
            currentIndex.value =
                if (currentIndex.value == 0)
                    fullTimeTable.days.size - 1
                else
                    currentIndex.value - 1
            upgrated.value = true
        }

        positionState == PositionState.Next && !upgrated.value -> {
            currentIndex.value =
                if (currentIndex.value == fullTimeTable.days.size - 1)
                    0
                else
                    currentIndex.value + 1
            upgrated.value = true
        }

        positionState == PositionState.Current -> {
            upgrated.value = false
            element.value = fullTimeTable.days.get(currentIndex.value)
        }
    }
    AnimatedVisibility(
        visible = !upgrated.value,
        enter = slideInVertically(animationSpec = tween(1200)) + fadeIn(animationSpec = tween(1200)),
        exit = slideOutHorizontally(animationSpec = tween(1200)) + fadeOut(animationSpec = tween(1200))
    ) {

        ShowCurrentDay(element = element.value, day = element.value.day)
    }
}

@Composable
fun ShowCurrentDay(element: DayTimeTable, day: String){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp), horizontalAlignment = Alignment.Start) {
        Text(text = day, style = Typography.h1, color = Color.White)
        Spacer(modifier = Modifier.height(20.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            element.timeTableList.forEach {
                ShowTimeTableItem(timeTableWithLesson = it)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun ShowTimeTableItem(timeTableWithLesson: TimeTableWithLesson){
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Center ) {
        Text(text = ConverterUtils.formatterTime.format(timeTableWithLesson.timeTable.time), style = Typography.h3, color = Color.White)
        Spacer(modifier = Modifier.width(5.dp))
        Column(verticalArrangement = Arrangement.Top, modifier = Modifier.width(200.dp)) {
            Text(text = timeTableWithLesson.lesson.lesson.lessonName, style = Typography.h3, color = Color.White)
            Row() {
                timeTableWithLesson.lesson.teachers.forEach {
                    Text(text = it.teacherName, style = Typography.body1, color = Color.White)
                    Spacer(modifier = Modifier.width(2.dp))
                }
            }
        }
        Text(text = timeTableWithLesson.timeTable.cabinet?:"", style = Typography.body1, color = Color.White)

    }
}