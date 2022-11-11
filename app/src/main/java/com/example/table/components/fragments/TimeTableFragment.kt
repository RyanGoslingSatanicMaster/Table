package com.example.table.components.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.table.R
import com.example.table.components.activity.MainActivity
import com.example.table.components.activity.MainViewModel
import com.example.table.di.DaggerViewModelFactory
import com.example.table.model.pojo.DayTimeTable
import com.example.table.model.pojo.WeekTimeTable
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.ui.AnimatedBackgroundGradient
import com.example.table.ui.PositionState
import com.example.table.ui.theme.Primary
import com.example.table.ui.theme.TableTheme
import com.example.table.ui.theme.Typography
import com.example.table.ui.theme.blue
import com.example.table.utils.ConverterUtils
import javax.inject.Inject

class TimeTableFragment @Inject constructor() : Fragment() {

    @Inject
    lateinit var factory: DaggerViewModelFactory

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
        activityViewModel.activeGroup.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            viewModel.getTimeTable(it)
        })
    }

    @Composable
    fun TimeTableLayout(){
        val list = viewModel.timeTable.observeAsState()
        if (list.value != null) {

            Box(contentAlignment = Alignment.BottomEnd) {
                TimeTableNavigationBar({
                    (activity as MainActivity).startGroupSelectionFragment()
                })
                AnimatedBackgroundGradient(
                    duration = 1200,
                    colors = Primary to blue,
                    topPosition = list.value!!.first.isCurrent
                ) { position, isTop ->
                    Crossfade(targetState = isTop) {
                        val startIndex by remember {
                            derivedStateOf {
                                if (it) {
                                    val index = list.value!!.first.days.indexOf(list.value!!.first.days.firstOrNull { it.day == "Сегодня" })
                                    return@derivedStateOf if(index == -1) 0 else index
                                }
                                else {
                                    val index = list.value!!.second.days.indexOf(list.value!!.second.days.firstOrNull { it.day == "Сегодня" })
                                    return@derivedStateOf if(index == -1) 0 else index
                                }
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

}

@Composable
fun TimeTableNavigationBar(onSearchClick: () -> Unit = {}, onSettingsClick: () -> Unit = {}){
    Row(
        modifier = Modifier
            .background(
                shape = RoundedCornerShape(topStart = 30.dp),
                color = Color.White
            )
            .zIndex(1f)
    ) {
        Icon(
            modifier = Modifier
                .defaultMinSize(minHeight = 50.dp, minWidth = 50.dp)
                .padding(4.dp)
                .clickable {
                    onSearchClick()
                },
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = null)
        Icon(modifier = Modifier.defaultMinSize(minHeight = 50.dp, minWidth = 50.dp).padding(4.dp),
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = null)
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