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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.table.R
import com.example.table.annotations.DayNightMode
import com.example.table.components.activity.MainActivity
import com.example.table.components.activity.MainViewModel
import com.example.table.di.DaggerViewModelFactory
import com.example.table.model.pojo.DayTimeTable
import com.example.table.model.pojo.WeekTimeTable
import com.example.table.model.pojo.TimeTableWithLesson
import com.example.table.ui.*
import com.example.table.ui.theme.*
import com.example.table.utils.ConverterUtils
import javax.inject.Inject

class TimeTableFragment @Inject constructor(@DayNightMode private val isNightMode: Boolean, private val factory: DaggerViewModelFactory) : Fragment() {


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
        activityViewModel.activeGroup.observe(viewLifecycleOwner) {
            viewModel.getTimeTable(it)
        }
    }

    override fun onPause() {
        activityViewModel.activeGroup.removeObservers(viewLifecycleOwner)
        super.onPause()
    }

    @Composable
    fun TimeTableLayout(){
        val list = viewModel.timeTable.observeAsState()
        if (list.value != null) {

            Box(modifier = Modifier.fillMaxSize()) {
                TimeTableNavigationBar({
                    (activity as MainActivity).startGroupSelectionFragment()
                },{
                    (activity as MainActivity).startSettingsFragment()
                },
                    modifier = Modifier.align(Alignment.BottomEnd), isNightMode)
                AnimatedBackgroundGradient(
                    duration = 1200,
                    colors = if (!isNightMode) blue to lightBlue else extraDarkBlue to darkBlue,
                    topPosition = list.value!!.first.isCurrent,
                    isNightMode = isNightMode
                ) { position, isTop ->
                    Crossfade(targetState = isTop) {
                        val startIndex by remember {
                            derivedStateOf {
                                if (it) {
                                    val index = list.value!!.first.days.indexOf(list.value!!.first.days.firstOrNull { it.day == "??????????????" })
                                    return@derivedStateOf if(index == -1) 0 else index
                                }
                                else {
                                    val index = list.value!!.second.days.indexOf(list.value!!.second.days.firstOrNull { it.day == "??????????????" })
                                    return@derivedStateOf if(index == -1) 0 else index
                                }
                            }
                        }

                        ShowTimeTable(
                            fullTimeTable = if (it) list.value!!.first else list.value!!.second,
                            positionState = position,
                            isFirstWeek = it,
                            startIndex = startIndex,
                            isNightMode = isNightMode
                        )
                    }

                }
            }
        }

    }

}

@Composable
fun TimeTableNavigationBar(onSearchClick: () -> Unit = {}, onSettingsClick: () -> Unit = {}, modifier: Modifier, isNightMode: Boolean){
    Row(
        modifier = modifier
            .background(
                shape = RoundedCornerShape(topStart = 30.dp),
                brush = Brush.linearGradient(colors = if (!isNightMode) listOf(Color.White, Color.White) else listOf(
                    Secondary, lightGreen))
            )
            .zIndex(1f)
    ) {
        val colors = if (!isNightMode) listOf(Secondary, Primary) else listOf(extraLightBlue, extraDarkBlue)
        Icon(
            modifier = modifier
                .defaultMinSize(minHeight = 50.dp, minWidth = 50.dp)
                .padding(4.dp)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            Brush.linearGradient(colors),
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                }
                .clickable {
                    onSearchClick()
                },
            imageVector = Icons.Default.Search,
            contentDescription = null
        )
        Icon(modifier = modifier
            .defaultMinSize(minHeight = 50.dp, minWidth = 50.dp)
            .padding(4.dp)
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    drawRect(
                        Brush.linearGradient(colors),
                        blendMode = BlendMode.SrcAtop
                    )
                }
            }
            .clickable {
                onSettingsClick()
            },
            imageVector = Icons.Default.Settings,
            contentDescription = null
        )
    }
}

@Composable
fun ShowTimeTable(fullTimeTable: WeekTimeTable, positionState: PositionState, isFirstWeek: Boolean, startIndex: Int, isNightMode: Boolean){
    if (fullTimeTable == null || fullTimeTable.days.isEmpty()){
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically(animationSpec = tween(1200)) + fadeIn(animationSpec = tween(1200)),
            exit = slideOutHorizontally(animationSpec = tween(1200)) + fadeOut(animationSpec = tween(1200))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text(text = "???? ???????? ???????????? ?????? ????????!", style = Typography.h1, color = Color.White)
            }
        }
        return
    }
    val currentIndex = remember(isFirstWeek) { mutableStateOf(
        startIndex
    ) }
    val upgraded = remember {
        mutableStateOf(false)
    }
    val element = remember {
        mutableStateOf(fullTimeTable.days.get(currentIndex.value))
    }
    when{
        positionState == PositionState.Previous && !upgraded.value -> {
            currentIndex.value =
                if (currentIndex.value == 0)
                    fullTimeTable.days.size - 1
                else
                    currentIndex.value - 1
            upgraded.value = true
        }

        positionState == PositionState.Next && !upgraded.value -> {
            currentIndex.value =
                if (currentIndex.value == fullTimeTable.days.size - 1)
                    0
                else
                    currentIndex.value + 1
            upgraded.value = true
        }

        positionState == PositionState.Current -> {
            upgraded.value = false
            element.value = fullTimeTable.days.get(currentIndex.value)
        }
    }
    AnimatedVisibility(
        visible = !upgraded.value,
        enter = slideInVertically(animationSpec = tween(1200)) + fadeIn(animationSpec = tween(1200)),
        exit = slideOutHorizontally(animationSpec = tween(1200)) + fadeOut(animationSpec = tween(1200))
    ) {
        ShowCurrentDay(element = element.value, day = element.value.day, isFirstWeek, isNightMode)
    }
}

@Composable
fun ShowCurrentDay(element: DayTimeTable, day: String, isFirstWeek: Boolean, isNightMode: Boolean){
    Column(modifier = Modifier
        .fillMaxSize(), horizontalAlignment = Alignment.Start) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(text = day, style = Typography.h1, color = Color.White, modifier = Modifier.padding(10.dp))
            Text(text = if (isFirstWeek) "1-???? ????????????" else "2-?? ????????????", style = Typography.h3, color = Color.White, modifier = Modifier.padding(10.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            element.timeTableList.forEach {
                ShowTimeTableItem(timeTableWithLesson = it, isNightMode)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun ShowTimeTableItem(timeTableWithLesson: TimeTableWithLesson, isNightMode: Boolean){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .drawColoredShadow(
                color = Color.Black,
                offsetX = 2.dp,
                offsetY = 5.dp,
                alpha = 0.6f
            )
            .background(
                brush = Brush.linearGradient(if (!isNightMode) listOf(Secondary, darkGreen) else listOf(
                    Secondary, lightGreen)),
                shape = Shapes.large
            )
            .padding(5.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = ConverterUtils.formatterTime.format(timeTableWithLesson.timeTable.time),
                style = Typography.h3, color = Color.White,
                )
            Text(
                text = if (timeTableWithLesson.lesson.lesson.isLection) "????????????" else "????????????????",
                style = Typography.body1, color = Color.White,
                )
        }
        Column(verticalArrangement = Arrangement.Top, modifier = Modifier.weight(4f)) {
            Text(text = timeTableWithLesson.lesson.lesson.lessonName, style = Typography.h3, color = Color.White)

            timeTableWithLesson.lesson.teachers.forEach {
                Text(text = it.teacherName, style = Typography.body1, color = Color.White)
            }

        }
        Text(text = timeTableWithLesson.timeTable.cabinet?:"", style = Typography.body1, color = Color.White, modifier = Modifier.weight(1f))

    }
}